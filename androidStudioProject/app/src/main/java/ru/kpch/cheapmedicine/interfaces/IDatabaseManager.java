package ru.kpch.cheapmedicine.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ru.kpch.cheapmedicine.model.ActiveSubstance;
import ru.kpch.cheapmedicine.model.Drug;
import ru.kpch.cheapmedicine.model.Drugstore;
import ru.kpch.cheapmedicine.model.InitializationException;

public interface IDatabaseManager {
    ArrayList<Drug> getDrugs();
    ArrayList<ActiveSubstance> getActiveSubstances();
    ArrayList<Drugstore> getDrugstores();
    int getDemoCountForFreeVersion();
    boolean decrementDemoCountForFreeVersion();
    String getDrugNotice(Drug drug);
    String getAboutInfo();
    int getRawsCount(String tableName);
    HashMap<String,Integer> getLocalVersion();
    HashSet<String> getBarcodes(Drug drug);
    boolean execSQL(String sqlRequest, boolean withTransaction);
    void beginTransaction() throws InitializationException;
    void endTransaction() throws InitializationException;
    void setTransactionSuccessful() throws InitializationException;
    void setConnectionForTranzaction() throws InitializationException;
}
