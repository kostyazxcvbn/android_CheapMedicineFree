package ru.kpch.cheapmedicine.interfaces;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import ru.kpch.cheapmedicine.model.ActiveSubstance;
import ru.kpch.cheapmedicine.model.Drug;
import ru.kpch.cheapmedicine.model.Drugstore;

import static ru.kpch.cheapmedicine.model.AppEnums.*;

public interface IAppLogic {
    UpdateState checkDatabaseUpdates();
    int getRawsCount(String tableName);
    Drug getSelectedDrug();
    ArrayList<Drug> getDrugs();
    ArrayList<Drugstore> getDrugstores();
    ArrayList<ActiveSubstance> getActiveSubstances();
    int getDemoCountForFreeVersion();
    boolean decrementDemoCountForFreeVersion();
    Drug findByBarcode(String barcode);
    void setSelectedDrug(Drug selectedDrug);
    RequestToServerState sendBarcodeToFindAnalogs(String barcode);
    RequestToServerState helpWithDrug(String drug);
    RequestToServerState addNewDrugsPairOnServer(String drug, String analog);
    void clearAppCache();
    String[]getFullDrugsList();
    String getDrugNotice(Drug drug);
    String getAboutInfo();
}
