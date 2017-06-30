package ru.kpch.cheapmedicine.model;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.kpch.cheapmedicine.R;

/**
 * Created by user on 19.06.2017.
 */

public class Drug implements Serializable {

    private int id;
    private String name;
    transient private String nameForDrugstores;
    transient private ActiveSubstance activeSubstance;
    transient private ArrayList<Drug> analogs;
    transient private String price;

    public Drug(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameForDrugstores() {
        return nameForDrugstores;
    }

    public void setNameForDrugstores(String nameForDrugstores) {
        this.nameForDrugstores = nameForDrugstores;
    }

    public ActiveSubstance getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(ActiveSubstance activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public ArrayList<Drug> getAnalogs() {
        return analogs;
    }

    public void setAnalogs(ArrayList<Drug> analogs) {
        this.analogs = analogs;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name;
    }

    public void clearPrice(){
        price = "";
        for (Drug analog : analogs) {
            analog.price="";
        }
    }

    public List<HashMap<String,?>> getAnalogsListForAdapter() {
        HashMap<String,Object> analogsMap = new HashMap<>();
        Context context = AppLogicImpl.getInstance().getAppContext();

        for (Drug analog : analogs) {
            analogsMap.put(AppLogicImpl.KEY_ANALOGNAME, analog);
            analogsMap.put(AppLogicImpl.KEY_PRICE,analog.getPrice());
            analogsMap.put(AppLogicImpl.KEY_ANALOG_IN, (analog.getActiveSubstance().equals(activeSubstance)?context.getString(R.string.textFarmgroupActiveSubstanceAnalog):context.getString(R.string.textFarmgroupAnalog)));
        }
        List<HashMap<String,?>> analogsList = new ArrayList<>();
        analogsList.add(analogsMap);
        return analogsList;
    }

    public String getNotice() {
        return AppLogicImpl.getInstance().getDrugNotice(this);
    }


}
