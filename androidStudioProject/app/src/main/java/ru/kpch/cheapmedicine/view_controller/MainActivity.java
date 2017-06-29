package ru.kpch.cheapmedicine.view_controller;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import ru.kpch.cheapmedicine.model.DatabaseHelperImpl;
import ru.kpch.cheapmedicine.R;

import static ru.kpch.cheapmedicine.model.AppEnums.*;

public class MainActivity extends AppActivity {

    private ListView ListMainMenu;
    private Button buttonUpdateDB;
    private boolean isUpdateInProcess;
    private boolean isActivityClosed =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityActionBar(this, R.raw.doc);

        setContentView(R.layout.activity_main);

        final String[]menuItems=getResources().getStringArray(R.array.textMainMenuItems);

        ArrayAdapter<String> adapter_mainMenu=new  MainMenuAdapter(this, menuItems);
        ListMainMenu =(ListView)findViewById(R.id.lv_mainMenuItemsList);

        ListMainMenu.setAdapter(adapter_mainMenu);

        ListMainMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=null;

                switch (position) {
                    case 0: {
                        intent=new Intent(MainActivity.this, FindAnalogsActivity.class);
                    }
                    break;
                    case 1: {
                        intent=new Intent(MainActivity.this, BarCodeActivity.class);
                    }
                    ;
                    break;
                    case 2: {
                        intent=new Intent(MainActivity.this, HelpWithAnalogActivity.class);
                    }
                    break;
                    case 3: {
                        intent=new Intent(MainActivity.this, UsersAnalogActivity.class);
                    }
                    break;
                    case 4: {
                        intent = new Intent(MainActivity.this, WebViewInfoActivity.class);
                    }
                    break;
                    case 5: {
                        openQuitDialog();
                    }
                    break;
                    default:
                        break;
                }

                if (intent != null) {
                    intent.putExtra(IntentKeys.PARENT, clearClassName(getClass().getName()));
                    startActivity(intent);
                    finish();
                }
            }
        });

        buttonUpdateDB =(Button)findViewById(R.id.b_updateDatabase);
        isUpdateInProcess =true;

        updateCountInField(DatabaseHelperImpl.TABLE_DRUGS_1, (TextView)findViewById(R.id.tv_drugsCountField));
        updateCountInField(DatabaseHelperImpl.TABLE_DRUGSTORE_3, (TextView)findViewById(R.id.tv_drugstoresCountField));
    }

    @Override
    public void  onDestroy(){
        super.onDestroy();

        if(isActivityClosed){
            appLogic.clearAppCache();
        }

        if (isAppWillBeClosed) {
            appLogic.clearAppCache();
        }
    }

    @Override
    public void onBackPressed() {
        if(isUpdateInProcess){
            openQuitDialog();
        }
        else{
            return;
        }
    }

    public void onClickHomeButton(View view) {
    }

    private void openQuitDialog() {
        LayoutInflater windowCloseContent = getLayoutInflater();
        View v_window_close_content = windowCloseContent.inflate(R.layout.window_close,null);

        AlertDialog.Builder quitDialog = new android.support.v7.app.AlertDialog.Builder(this);
        quitDialog .setView(v_window_close_content);

        quitDialog.setPositiveButton(getString(R.string.buttonYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isActivityClosed =true;
                finish();
            }
        });

        quitDialog.setNegativeButton(getString(R.string.buttonNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        quitDialog.show();
    }

    public void onClickUpdateDB(View view) {
        AppDatabaseUpdater appDatabaseUpdater=new AppDatabaseUpdater();
        appDatabaseUpdater.execute();
    }

    private class AppDatabaseUpdater extends AsyncTask<Void, Void, UpdateState>
    {
        ProgressBar progBarUpdateProgress=(ProgressBar)findViewById(R.id.progBar_databaseUpdating);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            ListMainMenu.setEnabled(false);
            buttonUpdateDB.setEnabled(false);
            progBarUpdateProgress.setVisibility(ProgressBar.VISIBLE);

            isUpdateInProcess =false;
        }

        @Override
        protected UpdateState doInBackground(Void... params){
            return appLogic.checkDatabaseUpdates();
        }

        @Override
        protected void onPostExecute(UpdateState updateState){

            TextView drugsCount=(TextView)findViewById(R.id.tv_drugsCountField);
            TextView drugstoresCount=(TextView)findViewById(R.id.tv_drugstoresCountField);
            String updateResultMessage=null;

            super.onPostExecute(updateState);

            switch(updateState){
                case UPDATE_SUCCESSFUL:{
                    updateResultMessage=getString(R.string.messageUpdateSuccess);
                    break;
                }
                case NO_UPDATES:{
                    updateResultMessage=getString(R.string.messageNoUpdates);
                    break;
                }
                case UPDATE_ERROR:
                default: {
                    updateResultMessage=getString(R.string.errorUpdatingDatabase);
                    break;
                }
            }

            updateCountInField(DatabaseHelperImpl.TABLE_DRUGS_1, drugsCount);
            updateCountInField(DatabaseHelperImpl.TABLE_DRUGSTORE_3, drugstoresCount);

            ListMainMenu.setEnabled(true);
            buttonUpdateDB.setEnabled(true);
            isUpdateInProcess =true;
            progBarUpdateProgress.setVisibility(ProgressBar.INVISIBLE);

            Toast updateResultContainer = Toast.makeText(getApplicationContext(),
                    updateResultMessage, Toast.LENGTH_SHORT);
            updateResultContainer.show();
        }
    }

    private void updateCountInField(String tableName, TextView field) {
        int rawsCount=appLogic.getRawsCount(tableName);

        if (rawsCount !=-1) {
            field.setText(String.valueOf(rawsCount));
        }
        else{
            field.setText(R.string.messageNoData);
        }
    }

    private class MainMenuAdapter extends ArrayAdapter<String>{

        public MainMenuAdapter(Context context, String[]menuItems){
            super(context, R.layout.list_item_main_menu, menuItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_main_menu, null);
            }

            ImageView image = (ImageView)convertView.findViewById(R.id.iv_mainMenuImage);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;

            InputStream is = getResources().openRawResource(R.raw.mi_pic);

            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            image.setImageDrawable(drawable);

            ((TextView)convertView.findViewById(R.id.tv_mainMenuItem)).setText(getItem(position));
            return convertView;
        }
    }
}
