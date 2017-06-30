package ru.kpch.cheapmedicine.view_controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ru.kpch.cheapmedicine.model.AppLogicImpl;
import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.model.Drug;
import ru.kpch.cheapmedicine.model.Drugstore;

public class AnalogsActivity extends AppActivity {

    private ListView analogsList;
    private Spinner drugstoresList;
    private boolean isJsoupInProccess;
    private SimpleAdapter analogsListAdapter;
    private ArrayAdapter<Drugstore> drugstoresListAdapter;

    private Drugstore selectedDrugstore;

    private static String classNameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            classNameFrom=getIntent().getExtras().getString(IntentKeys.PARENT);
        } catch (NullPointerException e) {

        }

        setActivityActionBar(this, R.raw.doc_back);

        setContentView(R.layout.activity_analogs);

        WebView view = (WebView) findViewById(R.id.tv_analogsHelpText);
        String text = "<html><body bgcolor=\"#fafafa\"><div align=\"justify\" style=\"color:#444444; font-size:14px\">" +
        getString(R.string.textComparePrices) +
        "</div></body></html>";
        WebSettings settings = view.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        view.loadDataWithBaseURL(null, text, "text/html","en_US", null);

        TextView drugNameField=(TextView)findViewById(R.id.tv_selectedDrugName);
        drugstoresList =(Spinner)findViewById(R.id.s_drugstores);

        drugNameField.setText(appLogic.getSelectedDrug().getName());

        analogsList =(ListView)findViewById(R.id.lv_analogsList);
        try {
            analogsListAdapter = new SimpleAdapter(this, appLogic.getSelectedDrug().getAnalogsListForAdapter(), R.layout.list_item_analogs, new String[]{AppLogicImpl.KEY_ANALOGNAME,AppLogicImpl.KEY_PRICE,AppLogicImpl.KEY_ANALOG_IN},new int[]{R.id.mi_analogName, R.id.mi_price, R.id.tv_analogIn});
        } catch (NullPointerException e) {
            closeWithFatalError(this);
        }
        analogsList.setAdapter(analogsListAdapter);

        try {
            drugstoresListAdapter=new ArrayAdapter(this, R.layout.list_item_active_substance, R.id.tv_itemForSpinner, appLogic.getDrugstores().toArray());
        } catch (NullPointerException e) {
            closeWithFatalError(this);
        }
        drugstoresList.setAdapter(drugstoresListAdapter);

        analogsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AnalogsActivity.this, WebViewInfoActivity.class);
                HashMap<String,Object> selectedItemMap = (HashMap<String,Object>)parent.getAdapter().getItem(position);
                intent.putExtra(IntentKeys.SELECTED_DRUG,(Drug)selectedItemMap.get(AppLogicImpl.KEY_ANALOGNAME));
                intent.putExtra(IntentKeys.PARENT, clearClassName(getClass().getName()));
                startActivity(intent);
                finish();
            }
        });

        drugstoresList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDrugstore = (Drugstore)parent.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDrugstore = (Drugstore)parent.getItemAtPosition(0);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isAppWillBeClosed) {
            appLogic.clearAppCache();
        }
    }

    public void onClickDrugNotice(View view) {
        try {
            Intent intent = new Intent(AnalogsActivity.this, WebViewInfoActivity.class);
            intent.putExtra(IntentKeys.PARENT, clearClassName(getClass().getName()));
            intent.putExtra(IntentKeys.SELECTED_DRUG, appLogic.getSelectedDrug());
            startActivity(intent);
            finish();
        } catch (NullPointerException e) {
            closeWithFatalError(this);
        }
    }

    public void onClickComparePrices(View view) {
        (new PriceParcer()).execute();
    }

    public void onClickDrugstoreInfo(View view) {

        LayoutInflater drugstoreInfoContent = getLayoutInflater();

        View drugstoreInfoView = drugstoreInfoContent.inflate(R.layout.window_drugstore_info,null);
        TextView drugStoreName = (TextView)drugstoreInfoView.findViewById(R.id.tv_drugstoreName);
        TextView drugstoreCity = (TextView)drugstoreInfoView.findViewById(R.id.field_drugstoreCity);
        TextView drugStoreSite = (TextView)drugstoreInfoView.findViewById(R.id.field_drugStoreSite);
        TextView drugstorePhone = (TextView)drugstoreInfoView.findViewById(R.id.field_drugstorePhone);

        drugStoreName.setText(selectedDrugstore.getName());
        drugstoreCity.setText(selectedDrugstore.getCity());
        drugStoreSite.setText(selectedDrugstore.getSite());
        drugstorePhone.setText(selectedDrugstore.getPhone());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(drugstoreInfoView);

        builder.setCancelable(false)
                .setNegativeButton(R.string.buttonClose,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onClickHomeButton(View view){
        if(!isJsoupInProccess) {
            onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if(!isJsoupInProccess) {
                onBackPressed();
                return super.onKeyDown(keyCode, event);
            }
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

    public class PriceParcer extends AsyncTask<Void, Void, Void>
    {
        Button comparePricesButton =(Button)findViewById(R.id.b_comparePrices);
        Spinner drugstoresList =(Spinner)findViewById(R.id.s_drugstores);
        ProgressBar progressBarComparePrices =(ProgressBar)findViewById(R.id.progBar_comparePrices);
        int i;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            isJsoupInProccess =true;

            comparePricesButton.setEnabled(false);
            drugstoresList.setEnabled(false);
            progressBarComparePrices.setVisibility(ProgressBar.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params){

            try {
                Drug drug = appLogic.getSelectedDrug();
                ArrayList<Drug>analogs=drug.getAnalogs();

                drug.clearPrice();
                selectedDrugstore.getPrice(drug);

                for (Drug analog : analogs) {
                    selectedDrugstore.getPrice(analog);
                }
            } catch (NullPointerException e) {
                closeWithFatalError(AnalogsActivity.this);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            TextView drugPriceField=(TextView)findViewById(R.id.tv_drugPrice);
            drugPriceField.setText(appLogic.getSelectedDrug().getPrice());

            analogsListAdapter=new SimpleAdapter(AnalogsActivity.this, appLogic.getSelectedDrug().getAnalogsListForAdapter(), R.layout.list_item_analogs, new String[]{AppLogicImpl.KEY_ANALOGNAME,AppLogicImpl.KEY_PRICE,AppLogicImpl.KEY_ANALOG_IN},new int[]{R.id.mi_analogName, R.id.mi_price, R.id.tv_analogIn});
            analogsList.setAdapter(analogsListAdapter);

            progressBarComparePrices.setVisibility(ProgressBar.INVISIBLE);
            comparePricesButton.setEnabled(true);
            drugstoresList.setEnabled(true);
            isJsoupInProccess =false;
        }
    }
}