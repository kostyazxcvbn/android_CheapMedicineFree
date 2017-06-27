package ru.kpch.cheapmedicine.model;

import android.content.Context;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.interfaces.IAppLogic;
import ru.kpch.cheapmedicine.interfaces.IDatabaseManager;
import ru.kpch.cheapmedicine.interfaces.IHttpManager;

import static ru.kpch.cheapmedicine.model.AppEnums.*;

public final class AppLogicImpl implements IAppLogic{

    public static final String KEY_ANALOGNAME = "analogName";
    public static final String KEY_PRICE = "price";
    public static final String KEY_ANALOG_IN = "analogIn";

    private static AppLogicImpl instance;
    private Context appContext;

    private IHttpManager httpReqManager;
    private IDatabaseManager databaseManager;
    private ArrayList<Drug>drugs;
    private ArrayList<Drugstore>drugstores;
    private ArrayList<ActiveSubstance>activeSubstances;
    private Drug selectedDrug;

    private AppLogicImpl(Context appContext, ExecutorService appThreadPool) {
        this.appContext=appContext;
        HttpReqManagerImpl.init(appThreadPool);
        DatabaseHelperImpl.init(appContext);
        httpReqManager = HttpReqManagerImpl.getInstance();
        databaseManager = DatabaseHelperImpl.getInstance();
        activeSubstances=databaseManager.getActiveSubstances();
        drugs=databaseManager.getDrugs();
        drugstores=databaseManager.getDrugstores();

    }

    public static AppLogicImpl getInstance() {
        return instance;
    }

    public static void createInstance(Context appContext, ExecutorService appThreadPool) {
        if (instance == null) {
            instance = new AppLogicImpl(appContext,appThreadPool);
        }
    }

    protected Context getAppContext() {
        return appContext;
    }

    @Override
    public Drug getSelectedDrug() {
        return selectedDrug;
    }

    @Override
    public void setSelectedDrug(Drug selectedDrug) {
        this.selectedDrug = selectedDrug;
    }

    @Override
    public ArrayList<Drug> getDrugs() {
        return drugs;
    }

    @Override
    public ArrayList<Drugstore> getDrugstores() {
        return drugstores;
    }

    @Override
    public ArrayList<ActiveSubstance> getActiveSubstances() {
        return activeSubstances;
    }

    @Override
    public int getRawsCount(String tableName) {
        return databaseManager.getRawsCount(tableName);
    }

    @Override
    public int getDemoCountForFreeVersion() {
        return databaseManager.getDemoCountForFreeVersion();
    }

    @Override
    public RequestToServerState sendBarcodeToFindAnalogs(String barcode) {
        return addNewPhpFunc(barcode,null);
    }

    @Override
    public RequestToServerState helpWithDrug(String drug) {
        return addNewPhpFunc(drug,null);
}

    @Override
    public RequestToServerState addNewDrugsPairOnServer(String drug, String analog) {
        return addNewPhpFunc(drug,analog);
    }

    @Override
    public String[] getFullDrugsList() {
        String content;
        String[] fullDrugsList = null;

        String serverName="http://www.kpch.ru/app1/";
        String phpFunc="getNewDrugList.php?";

        String urlRequest = serverName + phpFunc;
        content=httpReqManager.getDataFromServer(urlRequest, 10);
        try {
            if (content!=null && content.length() > 4) {
                String[] newDrugsFromServer = content.split("\n");
                fullDrugsList = new String[newDrugsFromServer.length + drugs.size()];
                System.arraycopy(newDrugsFromServer, 0, fullDrugsList, 0, newDrugsFromServer.length);
                System.arraycopy(drugs.toArray(), 0, fullDrugsList, newDrugsFromServer.length, drugs.size());
                Arrays.sort(fullDrugsList);
            } else {
                fullDrugsList= new String[drugs.size()];
                for (int i = 0; i < drugs.size(); i++) {
                    fullDrugsList[i]=drugs.get(i).toString();
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return new String[]{null};
        } catch (NullPointerException e) {
            return new String[]{null};
    }
        return fullDrugsList;
    }

    @Override
    public boolean decrementDemoCountForFreeVersion() {
        return databaseManager.decrementDemoCountForFreeVersion();
    }

    @Override
    public Drug findByBarcode(String barcode) {
        Drug foundDrug=null;

        for (Drug drug : drugs) {
            if (databaseManager.getBarcodes(drug).contains(barcode)) {
                foundDrug=drug;
                break;
            }
        }
        return foundDrug;
    }

    @Override
    public String getDrugNotice(Drug drug) {
        return databaseManager.getDrugNotice(drug);
    }

    @Override
    public String getAboutInfo() {
        return databaseManager.getAboutInfo();
    }

    @Override
    public UpdateState checkDatabaseUpdates() {
        String content;
        UpdateState updateState= UpdateState.NO_UPDATES;

        String serverName="http://www.kpch.ru/app1/";
        String phpFunc="getDb.php?bc=1&t=";

        try {
            databaseManager.setConnectionForTranzaction();
        } catch (InitializationException e) {
            updateState= UpdateState.UPDATE_ERROR;
        }
        Set<Map.Entry<String,Integer>> localVersions = databaseManager.getLocalVersion().entrySet();

        try{
            databaseManager.beginTransaction();

            for (Map.Entry currentTable : localVersions) {
                String urlRequest=serverName + phpFunc + currentTable.getKey() + "&date=" + currentTable.getValue();
                content= httpReqManager.getDataFromServer(urlRequest,20);

                if(content!=null && content.length()>10) {
                    String req = content.substring(content.indexOf("INS"), content.indexOf("UPD"));
                    req.replace("'", "\'");
                    databaseManager.execSQL(req, true);
                    req = content.substring(content.indexOf("UPD"), content.lastIndexOf(";"));
                    databaseManager.execSQL(req, true);

                    drugs=null;
                    activeSubstances=null;
                    drugstores=null;
                    System.gc();

                    activeSubstances=databaseManager.getActiveSubstances();
                    drugs=databaseManager.getDrugs();
                    drugstores=databaseManager.getDrugstores();

                    updateState= UpdateState.UPDATE_SUCCESSFUL;
                }
                if(content=="err" || content.contains("app_err")){
                    updateState= UpdateState.UPDATE_ERROR;
                    break;
                }
            }
            if(updateState== UpdateState.UPDATE_SUCCESSFUL || updateState == UpdateState.NO_UPDATES){
                databaseManager.setTransactionSuccessful();
            }
        }catch(StringIndexOutOfBoundsException ex){
            updateState= UpdateState.UPDATE_ERROR;
        }
        catch(InitializationException e){
            updateState= UpdateState.UPDATE_ERROR;
        }
        catch(NullPointerException e){
            updateState= UpdateState.UPDATE_ERROR;
        }
        finally{
            try {
                databaseManager.endTransaction();
            } catch (InitializationException e) {
                updateState= UpdateState.UPDATE_ERROR;
            }
        }
        return updateState;
    }

    private RequestToServerState addNewPhpFunc(String addingItem, String addingItem2) {
        String content;
        RequestToServerState sendingState= RequestToServerState.SENDING_ERROR;

        String serverName="http://www.kpch.ru/app1/";
        String phpFunc="addNew.php?";

        try {
            String urlRequest = serverName + phpFunc + "d=" + URLEncoder.encode(addingItem, "utf-8") + "&a=" + ((addingItem2!=null)?URLEncoder.encode(addingItem2, "utf-8"):"");
            content=httpReqManager.getDataFromServer(urlRequest, 10);

            if(content!=null && content.contains("app_ok")) {
                sendingState= RequestToServerState.SENDING_SUCCESSFUL;
            }
        } catch (UnsupportedEncodingException e) {
            sendingState= RequestToServerState.SENDING_ERROR;
        }
        return sendingState;
    }

    @Override
    public void clearAppCache() {
        try {
            trimCache(appContext);
        } catch (Exception e) {
            return;
        }
    }

    private void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            return;
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }



}
