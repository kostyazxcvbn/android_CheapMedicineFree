package ru.kpch.cheapmedicine.view_controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import static ru.kpch.cheapmedicine.model.AppEnums.*;

import ru.kpch.cheapmedicine.R;

public class HelpWithAnalogActivity extends AppActivity {

    EditText newDrug;
    Button addNewDrugButton;

    boolean isRequestToServerInProgress =false;

    private static String classNameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            classNameFrom=getIntent().getExtras().getString(IntentKeys.PARENT);
        } catch (NullPointerException e) {

        }

        setActivityActionBar(this, R.raw.doc_back);

        setContentView(R.layout.activity_help_with_analog);

        addNewDrugButton = (Button) findViewById(R.id.b_addNewDrug);
        newDrug = (EditText) findViewById(R.id.et_add_newDrug);

        newDrug.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start,
                                               int end, Spanned dest, int dstart, int dend) {
                        if (source.equals(" ")) { // for backspace
                            return source;
                        }
                        if (source.toString().matches("[a-zA-Zа-яА-Я, ]+")) {
                            return source;
                        }
                        return "";
                    }
                }
        });

        DrugTextWatcher drugTextWatcher=new DrugTextWatcher(newDrug, newDrug, addNewDrugButton);
        newDrug.addTextChangedListener(drugTextWatcher);
        newDrug.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (isAppWillBeClosed) {
            appLogic.clearAppCache();
        }
        super.onDestroy();
    }

    public void onClickNeedHelp(View view) {

        UsersDrugForAnalogHelper usersDrugForAnalogHelper=new UsersDrugForAnalogHelper();
        usersDrugForAnalogHelper.execute(newDrug.getText().toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && !isRequestToServerInProgress){
            onBackPressed();
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    public void onClickHomeButton(View view) {
        if(!isRequestToServerInProgress){
            onBackPressed();
        }
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

    public class UsersDrugForAnalogHelper extends AsyncTask<String, Void, RequestToServerState>
    {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            isRequestToServerInProgress =true;
            addNewDrugButton.setEnabled(false);
            newDrug.setEnabled(false);

            TextView resultMessageContainer=(TextView)findViewById(R.id.tv_addNew_state);
            resultMessageContainer.setText("");

            ProgressBar progBar_update=(ProgressBar)findViewById(R.id.progBar_needHelp);
            progBar_update.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected RequestToServerState doInBackground(String... params){

            return appLogic.helpWithDrug(params[0]);
        }

        @Override
        protected void onPostExecute(RequestToServerState result){
            super.onPostExecute(result);

            TextView resultMessageContainer=(TextView)findViewById(R.id.tv_addNew_state);

            switch (result) {
                case SENDING_SUCCESSFUL:{
                    resultMessageContainer.setText(getString(R.string.messageRequestAccepted));
                    break;
                }
                case SENDING_ERROR:
                default:{
                    resultMessageContainer.setText(getString(R.string.messageRequestSendingError));
                }
            }
            addNewDrugButton.setEnabled(true);
            newDrug.setEnabled(true);
            ProgressBar AddingNewDrugProgressBar=(ProgressBar)findViewById(R.id.progBar_needHelp);
            AddingNewDrugProgressBar.setVisibility(ProgressBar.INVISIBLE);
            isRequestToServerInProgress = false;
        }
    }
}
