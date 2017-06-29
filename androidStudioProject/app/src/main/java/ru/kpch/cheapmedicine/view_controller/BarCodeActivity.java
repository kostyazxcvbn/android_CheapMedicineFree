package ru.kpch.cheapmedicine.view_controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.model.Drug;

import static ru.kpch.cheapmedicine.model.AppEnums.*;

public class BarCodeActivity extends AppActivity {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private FrameLayout preview;
    private ImageView barcodeImage;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private String lastScannedCode;
    private Image codeImage;

    private TextView barcodeTextView;

    boolean isSendingOfBarcodeInProccess =false;

    private static String classNameFrom;

    static {
        System.loadLibrary( "iconv" );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            classNameFrom=getIntent().getExtras().getString(IntentKeys.PARENT);
        } catch (NullPointerException e) {

        }

        setActivityActionBar(this, R.raw.doc_back);

        setContentView(R.layout.activity_barcode);

        barcodeTextView =(TextView)findViewById(R.id.field_ScannedBarcode);

        preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.setEnabled(false);

        showDemoVersionDialog();

    }

    private void showDemoVersionDialog(){
        final int demoCountForFreeVersion=appLogic.getDemoCountForFreeVersion();

        LayoutInflater windowCloseContent = getLayoutInflater();
        View barcodeNotFoundView = windowCloseContent.inflate(R.layout.window_bar_code_not_found,null);
        TextView notice=(TextView) barcodeNotFoundView.findViewById(R.id.tv_barcodeNotFound);

        notice.setText(getString(R.string.textBarcodeDemoVersionPart1)+ Integer.toString(demoCountForFreeVersion)+getString(R.string.textBarcodeDemoVersionPart2));

        AlertDialog.Builder quitDialog = new android.support.v7.app.AlertDialog.Builder(BarCodeActivity.this);
        quitDialog.setView(barcodeNotFoundView);

        if(demoCountForFreeVersion>0){
            quitDialog.setPositiveButton(R.string.buttonContinue, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (appLogic.decrementDemoCountForFreeVersion()) {
                        autoFocusHandler = new Handler();

                        scanner = new ImageScanner();
                        scanner.setConfig(0, Config.X_DENSITY, 3);
                        scanner.setConfig(0, Config.Y_DENSITY, 3);

                        barcodeImage = (ImageView) findViewById(R.id.iv_barcode);
                        resumeCamera();
                        preview.setEnabled(true);
                    } else {
                        closeWithFatalError(BarCodeActivity.this);
                    }
                }
            });
        }

        quitDialog.setNegativeButton(R.string.buttonReturn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });

        quitDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(preview.isEnabled()){
            resumeCamera();
        }
    }

    public void onPause() {
        super.onPause();
        if(mCamera!=null){
            releaseCamera();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mCamera!=null){
            releaseCamera();
        }
        if (isAppWillBeClosed) {
            appLogic.clearAppCache();
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void resumeCamera() {
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        preview.removeAllViews();
        preview.setEnabled(true);
        preview.addView(mPreview);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            Size size = parameters.getPreviewSize();
            codeImage = new Image(size.width, size.height, "Y800");
            previewing = true;
            mPreview.refreshDrawableState();
        }
        barcodeTextView.setText("");
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing && mCamera != null) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            codeImage.setData(data);
            int result = scanner.scanImage(codeImage);
            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    lastScannedCode = sym.getData();
                    if (lastScannedCode != null) {

                        barcodeScanned = true;

                        releaseCamera();
                        preview.setEnabled(false);
                        barcodeTextView.setText(lastScannedCode);

                        ProgressBar FindByBarcodeProgressBar=(ProgressBar)findViewById(R.id.progBar_findByBarcode);
                        FindByBarcodeProgressBar.setVisibility(ProgressBar.VISIBLE);

                        Drug foundDrug=appLogic.findByBarcode(lastScannedCode);

                        if(foundDrug!=null){
                            appLogic.setSelectedDrug(foundDrug);
                            Intent intent = new Intent(BarCodeActivity.this, AnalogsActivity.class);
                            intent.putExtra(IntentKeys.SELECTED_DRUG, appLogic.getSelectedDrug());
                            intent.putExtra(IntentKeys.PARENT, clearClassName(getClass().getName()));
                            startActivity(intent);
                            finish();
                        }
                        else{
                            FindByBarcodeProgressBar.setVisibility(ProgressBar.INVISIBLE);

                            LayoutInflater windowCloseContent = getLayoutInflater();
                            View barcodeNotFoundView = windowCloseContent.inflate(R.layout.window_bar_code_not_found, null);
                            TextView notice=(TextView) barcodeNotFoundView.findViewById(R.id.tv_barcodeNotFound);

                            notice.setText(getString(R.string.textNotFoundByBarcode));

                            AlertDialog.Builder quitDialog = new android.support.v7.app.AlertDialog.Builder(BarCodeActivity.this);
                            quitDialog .setView(barcodeNotFoundView);

                            quitDialog.setPositiveButton(R.string.buttonYes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UsersBarcodeForAnalogHelper UsersBarcodeForAnalogHelper =new UsersBarcodeForAnalogHelper();
                                    UsersBarcodeForAnalogHelper.execute(lastScannedCode);
                                }
                            });

                            quitDialog.setNegativeButton(R.string.buttonNo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    resumeCamera();
                                }
                            });

                            quitDialog.show();
                        }
                    }
                }
            }
            camera.addCallbackBuffer(data);
        }
    };

    public class UsersBarcodeForAnalogHelper extends AsyncTask<String, Void, RequestToServerState>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            isSendingOfBarcodeInProccess =true;

            ProgressBar sendingBarcodeProgressBar=(ProgressBar)findViewById(R.id.progBar_findByBarcode);
            sendingBarcodeProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected RequestToServerState doInBackground(String... params){
            return appLogic.sendBarcodeToFindAnalogs(params[0]);
        }

        @Override
        protected void onPostExecute(RequestToServerState result){
            super.onPostExecute(result);

            Toast resultMessageContainer;

            ProgressBar sendingBarcodeProgressBar=(ProgressBar)findViewById(R.id.progBar_findByBarcode);
            sendingBarcodeProgressBar.setVisibility(ProgressBar.INVISIBLE);

            switch (result) {
                case SENDING_SUCCESSFUL:{
                    resultMessageContainer = Toast.makeText(getApplicationContext(),
                            getString(R.string.messageRequestAccepted), Toast.LENGTH_LONG);
                    break;
                }
                case SENDING_ERROR:
                default:{
                    resultMessageContainer = Toast.makeText(getApplicationContext(),
                            getString(R.string.errorRequestSendingError), Toast.LENGTH_LONG);
                    break;
                }
            }

            resultMessageContainer.show();
            isSendingOfBarcodeInProccess =false;
            resumeCamera();
        }
    }

    // Mimic continuous auto-focusing
    final AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    public void onClickHomeButton(View view){
        if(!isSendingOfBarcodeInProccess) {
            onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK &&  !isSendingOfBarcodeInProccess){
            onBackPressed();
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName(classNameFrom));
        } catch (ClassNotFoundException e) {
            closeWithFatalError(this);
            return;
        }
        startActivity(intent);
        finish();
    }
}
