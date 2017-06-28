package ru.kpch.cheapmedicine.view_controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import ru.kpch.cheapmedicine.R;
import static ru.kpch.cheapmedicine.model.AppEnums.*;

public class UsersAnalogActivity extends AppActivity {

    Button addNewPairButton;
    ListView drugsList;
    EditText addDrugTextView;
    EditText addAnalogTextView;
    ArrayAdapter<String> drugsListAdapter;
    ProgressBar loadingDrugLisrProgressbar;

    boolean isLoadingFullDrugListInProccess =false;
    boolean isAddingNewDrugsPairInProccess =false;

    private static String classNameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            classNameFrom=getIntent().getExtras().getString(IntentKeys.PARENT);
        } catch (NullPointerException e) {

        }

        setActivityActionBar(this, R.raw.doc_back);

        setContentView(R.layout.activity_users_analog);

        addAnalogTextView =(EditText)findViewById(R.id.et_add_analogAnalog);

        addDrugTextView =(EditText)findViewById(R.id.et_add_analogDrug);

        InputFilter drugTextFieldFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)  {
                    if (source.equals(" ") || source.toString().matches("[a-zA-Zа-яА-Я, -]+") ) { // for backspace
                        return source;
                    }
                    return "";
            }
        };

        addAnalogTextView.setFilters(new InputFilter[]{drugTextFieldFilter});
        addDrugTextView.setFilters(new InputFilter[]{drugTextFieldFilter});;

        addNewPairButton =(Button)findViewById(R.id.b_addNewPair);
        addNewPairButton.setEnabled(false);

        DrugTextWatcher drugTextWatcher=new DrugTextWatcher(addDrugTextView, addAnalogTextView, addNewPairButton);
        addDrugTextView.addTextChangedListener(drugTextWatcher);

        DrugTextWatcher analogTextWatcher=new DrugTextWatcher(addAnalogTextView, addDrugTextView, addNewPairButton);
        addAnalogTextView.addTextChangedListener(analogTextWatcher);

        loadingDrugLisrProgressbar =(ProgressBar)findViewById(R.id.progBar_NewDrugList);

        drugsList=(ListView)findViewById(R.id.lv_add_analog);

        FullDrugListLoader fullDrugListLoader =new FullDrugListLoader();
        fullDrugListLoader.execute();

    }

    public void onClickAddNew(View view) {
        NewDrugsPairLoader newDrugsPairLoader =new NewDrugsPairLoader();
        newDrugsPairLoader.execute(addDrugTextView.getText().toString(), addAnalogTextView.getText().toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && !isLoadingFullDrugListInProccess && !isAddingNewDrugsPairInProccess){
            onBackPressed();
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    public void onClickHomeButton(View view) {
        if(!isLoadingFullDrugListInProccess && !isAddingNewDrugsPairInProccess){
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

    public class FullDrugListLoader extends AsyncTask<Void, Void, String[]>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            isLoadingFullDrugListInProccess =true;
            loadingDrugLisrProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(Void... params){
            return appLogic.getFullDrugsList();
        }

        @Override
        protected void onPostExecute(String[] result){
            super.onPostExecute(result);
            loadingDrugLisrProgressbar.setVisibility(View.INVISIBLE);

            if (result.length > 1) {
                drugsListAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item_new_drug, R.id.mi_newDrug, result);
                drugsList.setAdapter(drugsListAdapter);

                drugsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        addDrugTextView.setText(parent.getAdapter().getItem(position).toString());

                        if (addNewPairButton.isEnabled() == false && (addAnalogTextView.getText().length() != 0)) {
                            addNewPairButton.setEnabled(true);
                        }

                    }
                });
                isLoadingFullDrugListInProccess = false;
            } else {
                isLoadingFullDrugListInProccess =false;
                closeWithFatalError(UsersAnalogActivity.this);
            }
        }
    }

    public class NewDrugsPairLoader extends AsyncTask<String, Void, RequestToServerState>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            isAddingNewDrugsPairInProccess =true;
            addNewPairButton.setEnabled(false);
            addAnalogTextView.setEnabled(false);
            addDrugTextView.setEnabled(false);
            loadingDrugLisrProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected RequestToServerState doInBackground(String... params){
            return appLogic.addNewDrugsPairOnServer(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(RequestToServerState result){
            super.onPostExecute(result);

            Toast resultMessageContainer;

            switch (result) {
                case SENDING_SUCCESSFUL:{
                    resultMessageContainer = Toast.makeText(getApplicationContext(),
                            getString(R.string.messageRequestAccepted), Toast.LENGTH_SHORT);
                    break;
                }
                case SENDING_ERROR:
                default:{
                    resultMessageContainer = Toast.makeText(getApplicationContext(),
                            getString(R.string.messageRequestSendingError), Toast.LENGTH_LONG);
                }
            }

            resultMessageContainer.show();

            addNewPairButton.setEnabled(true);
            addAnalogTextView.setEnabled(true);
            addDrugTextView.setEnabled(true);
            loadingDrugLisrProgressbar.setVisibility(View.INVISIBLE);
            isAddingNewDrugsPairInProccess =false;
        }
    }
}
