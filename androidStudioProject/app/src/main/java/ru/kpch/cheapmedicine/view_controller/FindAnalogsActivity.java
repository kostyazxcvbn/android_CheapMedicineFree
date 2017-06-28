package ru.kpch.cheapmedicine.view_controller;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;

import java.util.ArrayList;

import ru.kpch.cheapmedicine.model.ActiveSubstance;
import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.model.Drug;

public class FindAnalogsActivity extends AppActivity {

    ListView drugsList;
    Spinner activeSubstanceSpinner;
    CheckBox activeSubstanceFilterCheckBox;

    ArrayAdapter<Drug> drugsListAdapter;
    ArrayAdapter<ActiveSubstance> activeSubstanceAdapter;

    private static String classNameFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            classNameFrom=getIntent().getExtras().getString(IntentKeys.PARENT);
        } catch (NullPointerException e) {

        }

        setActivityActionBar(this, R.raw.doc_back);

        setContentView(R.layout.activity_find_analogs);

        drugsList =(ListView)findViewById(R.id.lv_names_in_DB);

        activeSubstanceFilterCheckBox =(CheckBox)findViewById(R.id.cb_filterSusp);

        activeSubstanceSpinner =(Spinner)findViewById(R.id.sp_active_susp);
        activeSubstanceSpinner.setEnabled(false);


        try {
            activeSubstanceAdapter=new ArrayAdapter<>(this,R.layout.list_item_active_substance,R.id.tv_itemForSpinner,appLogic.getActiveSubstances());
        } catch (NullPointerException e) {
            closeWithFatalError(this);
        }
        activeSubstanceSpinner.setAdapter(activeSubstanceAdapter);


        drugsListAdapter=new ArrayAdapter<>(this,R.layout.list_item_new_drug,R.id.mi_newDrug);
        try {
            drugsListAdapter.addAll(appLogic.getDrugs());
        } catch (NullPointerException e) {
            closeWithFatalError(this);
        }
        drugsList.setAdapter(drugsListAdapter);

        activeSubstanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(activeSubstanceFilterCheckBox.isChecked()){
                    ArrayList<Drug>filteredDrugsList=new ArrayList<Drug>();
                    try {
                        for (Drug drug : appLogic.getDrugs()) {
                            if (drug.getActiveSubstance().equals(parent.getSelectedItem())) {
                                filteredDrugsList.add(drug);
                            }
                        }
                    } catch (NullPointerException e) {
                        closeWithFatalError(FindAnalogsActivity.this);
                    }
                    drugsListAdapter.clear();
                    drugsListAdapter.addAll(filteredDrugsList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        activeSubstanceFilterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    activeSubstanceSpinner.setEnabled(true);

                    ArrayList<Drug>filteredDrugsList=new ArrayList<Drug>();
                    try {
                        for (Drug drug : appLogic.getDrugs()) {
                            if (drug.getActiveSubstance().equals(activeSubstanceSpinner.getSelectedItem())) {
                                filteredDrugsList.add(drug);
                            }
                        }
                    } catch (NullPointerException e) {
                        closeWithFatalError(FindAnalogsActivity.this);
                    }
                    drugsListAdapter.clear();
                    drugsListAdapter.addAll(filteredDrugsList);
                }
                else{
                    activeSubstanceSpinner.setEnabled(false);
                    drugsListAdapter.clear();
                    try {
                        drugsListAdapter.addAll(appLogic.getDrugs());
                    } catch (NullPointerException e) {
                        closeWithFatalError(FindAnalogsActivity.this);
                    }
                }
            }
        });


        drugsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                appLogic.setSelectedDrug((Drug)parent.getAdapter().getItem(position));
                Intent intent = new Intent(FindAnalogsActivity.this, AnalogsActivity.class);
                intent.putExtra(IntentKeys.PARENT,clearClassName(getClass().getName()));
                startActivity(intent);
                finish();
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
    public void onDestroy(){
        super.onDestroy();

        if (isAppWillBeClosed) {
            appLogic.clearAppCache();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView  drugsSearch = (SearchView) MenuItemCompat.getActionView(searchItem);
        drugsSearch.setQueryHint(getString(R.string.textDrugSearhFieldTip));
        drugsSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        drugsSearch.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        drugsSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                drugsListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }


    public void onClickHomeButton(View view) {
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
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
