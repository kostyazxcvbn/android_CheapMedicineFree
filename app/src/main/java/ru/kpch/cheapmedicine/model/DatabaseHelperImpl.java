package ru.kpch.cheapmedicine.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.interfaces.IDatabaseManager;

public class DatabaseHelperImpl extends SQLiteOpenHelper implements IDatabaseManager {

    private static String DB_PATH;
    private static String DB_NAME="cheapmed.db";
    private static final int DB_VER=7;

    public static final String TABLE_DRUGS_1 ="drugs";
    public static final String TABLE_ACTIVE_SUBSTANCE_2 ="active_substances";
    public static final String TABLE_DRUGSTORE_3 ="drugstores";
    public static final String TABLE_CFG_4 ="cm_cfg";
    public static final String TABLE_DB_VER_5 ="db_ver";

    //columns in the table drugs
    public static final String COLUMN_T1_ID="_id";
    public static final String COLUMN_T1_DRUGNAME ="drug_name";
    public static final String COLUMN_T1_NOTICE="notice";
    public static final String COLUMN_T1_FIND="find_name";
    public static final String COLUMN_T1_ANALOGS="analogs_ids";
    public static final String COLUMN_T1_ACTIVE="active_substance_id";
    public static final String COLUMN_T1_BARCODE="barcode";

    //columns in the table active_substances
    public static final String COLUMN_T2_ID="_id";
    public static final String COLUMN_T2_SUBSNAME ="substance_name";

    //columns in the table drugstores
    public static final String COLUMN_T3_ID ="_id";
    public static final String COLUMN_T3_NAME ="drugstore_name";
    public static final String COLUMN_T3_LINK ="searchLink";
    public static final String COLUMN_T3_LINK2 ="link_part2";
    public static final String COLUMN_T3_SELECT_REQ ="select_req";
    public static final String COLUMN_T3_CHARSET ="charset";
    public static final String COLUMN_T3_SEARCH ="simple_search";
    public static final String COLUMN_T3_PHONE ="drugstore_phone";
    public static final String COLUMN_T3_SITE ="drugstore_site";
    public static final String COLUMN_T3_CITY ="drugstore_city";

    //columns in the table cm_cfg
    public static final String COLUMN_T4_ID ="_id";
    public static final String COLUMN_T4_ABOUT ="info_about";
    public static final String COLUMN_T4_DEMOCOUNT ="demo_count";

    //columns in the table db_ver
    public static final String COLUMN_T5_ID ="_id";
    public static final String COLUMN_T5_DRUGS ="drugs";
    public static final String COLUMN_T5_ACTIVE_SUBSTANCE ="active_substance";
    public static final String COLUMN_T5_DRUGSTORE ="drugstores";


    private Context appContext;
    private static DatabaseHelperImpl instance;
    private SQLiteDatabase connection;



    public static void init(Context context) {
        if(instance==null){
            instance=new DatabaseHelperImpl(context);
        }
    }

    private DatabaseHelperImpl(Context context){
        super(context,DB_NAME,null,DB_VER);
        this.appContext = context;
        DB_PATH=this.appContext.getDatabasePath(DB_NAME).getPath();
        File file=new File(DB_PATH);
        if(!file.exists()) {
            new File(file.getParent()).mkdirs();
            copyDatabase();
            setNewVersion();
        }
    }

    public static DatabaseHelperImpl getInstance(){
        return instance;
    }

    public SQLiteDatabase getSqLiteConnection(){
        //return getWritableDatabase();
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void setConnectionForTranzaction() throws InitializationException{
        if (this.connection == null) {
            this.connection = getSqLiteConnection();
        }
        if (this.connection != null) {
            throw new InitializationException(appContext.getString(R.string.initialization_exception_connection));
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db){

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < newVersion) {
            if (oldVersion < 5) {
                try {
                    File file = new File(DB_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                copyDatabase();
            }
            if (oldVersion < 7) {
                SQLiteDatabase sqLiteConnection=null;
                try{
                    sqLiteConnection = getSqLiteConnection();

                    sqLiteConnection.beginTransaction();

                    sqLiteConnection.execSQL("update " +
                            TABLE_DB_VER_5 + " set " +
                            COLUMN_T5_DRUGS + "=160302");
                    sqLiteConnection.execSQL("update " +
                            TABLE_DB_VER_5 + " set " +
                            COLUMN_T5_ACTIVE_SUBSTANCE + "=160302");
                    sqLiteConnection.execSQL("update " +
                            TABLE_DB_VER_5 + " set " +
                            COLUMN_T5_DRUGSTORE + "=160302");

                    sqLiteConnection.setTransactionSuccessful();

                } catch(SQLException e){
                    e.printStackTrace();
                } finally{
                    if(sqLiteConnection!=null){
                        sqLiteConnection.endTransaction();
                        sqLiteConnection.close();
                    }
                }
            }
        }
    }

    private void setNewVersion(){
        SQLiteDatabase sqLiteConnection=null;
        try {
            sqLiteConnection = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            sqLiteConnection.setVersion(DB_VER);
        }
        catch (SQLiteException e){
            e.printStackTrace();
        } finally{
            if(sqLiteConnection!=null){
                sqLiteConnection.close();
            }
        }
    }

    private void copyDatabase(){
        InputStream inputStream=null;
        OutputStream outputStream=null;
        try{
            inputStream= appContext.getAssets().open(DB_NAME);
            //String outputFileName=DB_PATH+DB_NAME;

            outputStream=new FileOutputStream(DB_PATH);

            byte[] buffer=new byte[1024];
            int length;
            while ((length=inputStream.read(buffer))>0){
                outputStream.write(buffer,0,length);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            try {
                if(inputStream!=null && outputStream!=null){
                    outputStream.close();
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<Drug> getDrugs() {
        String sqlReq = "select " +
                TABLE_DRUGS_1 + "." + COLUMN_T1_ID + " as " +
                COLUMN_T1_ID + "_" + TABLE_DRUGS_1 +"," +
                TABLE_DRUGS_1 + "." + COLUMN_T1_DRUGNAME + "," +
                TABLE_DRUGS_1 + "." + COLUMN_T1_ANALOGS + "," +
                TABLE_DRUGS_1 + "." + COLUMN_T1_FIND  + "," +
                TABLE_ACTIVE_SUBSTANCE_2 + "." + COLUMN_T2_ID + " as " +
                COLUMN_T2_ID + "_" + TABLE_ACTIVE_SUBSTANCE_2 +"," +
                TABLE_ACTIVE_SUBSTANCE_2 + "." + COLUMN_T2_SUBSNAME + " from " +
                TABLE_DRUGS_1 + " inner join " +
                TABLE_ACTIVE_SUBSTANCE_2 + " on " +
                COLUMN_T1_ACTIVE + " = " +
                TABLE_ACTIVE_SUBSTANCE_2 + "." + COLUMN_T2_ID + " order by " +
                COLUMN_T1_DRUGNAME;

        ArrayList<Drug> drugs = null;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        ArrayList<ActiveSubstance> activeSubstances = getActiveSubstances();

        int rawsCount=getRawsCount(TABLE_DRUGS_1);

        if (rawsCount > 0) {
            try {
                connection = getSqLiteConnection();
                dataCursor = connection.rawQuery(sqlReq, null);
                if (dataCursor.moveToFirst()) {
                    drugs=new ArrayList<>(rawsCount);

                    do {
                        Drug drug = new Drug(dataCursor.getInt(dataCursor.getColumnIndex(COLUMN_T1_ID + "_" + TABLE_DRUGS_1)), dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T1_DRUGNAME)));
                        drug.setNameForDrugstores(dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T1_FIND)));
                        ActiveSubstance newActiveSubstance = new ActiveSubstance(dataCursor.getInt(dataCursor.getColumnIndex(COLUMN_T2_ID + "_" + TABLE_ACTIVE_SUBSTANCE_2)), dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T2_SUBSNAME)));
                        for (ActiveSubstance substance : activeSubstances) {
                            if(substance.equals(newActiveSubstance)) {
                                drug.setActiveSubstance(substance);
                                break;
                            }
                        }

                        drugs.add(drug);
                    } while (dataCursor.moveToNext());

                    dataCursor.moveToFirst();

                    for (Drug drug : drugs) {
                        String[]analogIds=dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T1_ANALOGS)).split(",");
                        ArrayList<Drug>analogs=new ArrayList<>(analogIds.length);
                        for (int i = 0; i < analogIds.length; i++) {
                            int analogId = Integer.valueOf(analogIds[i].trim());
                            for (Drug analog : drugs) {
                                if (analog.getId() == analogId) {
                                    analogs.add(analog);
                                    break;
                                }
                            }
                        }
                        drug.setAnalogs(analogs);
                        dataCursor.moveToNext();
                    }
                }
            } finally{
                dataCursor.close();
                connection.close();
            }
        }

        return drugs;
    }

    @Override
    public HashSet<String> getBarcodes(Drug drug) {
        String sqlReq = "select " +
                COLUMN_T1_BARCODE + " from " +
                TABLE_DRUGS_1 + " where " +
                COLUMN_T1_ID + " like '" + drug.getId() + "'";

        SQLiteDatabase connection=null;
        Cursor dataCursor=null;
        HashSet<String> barcodes=null;

        try {
            connection = getSqLiteConnection();
            dataCursor = connection.rawQuery(sqlReq, null);
            if (dataCursor.moveToFirst()) {
                do {
                    String[] barcodesArray = dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T1_BARCODE)).split(",");
                    barcodes = new HashSet<>(barcodesArray.length);
                    for (String barcode : barcodesArray) {
                        barcodes.add(barcode);
                    }
                } while (dataCursor.moveToNext());
            }
        }finally{
            dataCursor.close();
            connection.close();
        }
        return barcodes;
    }

    @Override
    public ArrayList<ActiveSubstance> getActiveSubstances() {
        String sqlReq = "select " +
                COLUMN_T2_ID + "," +
                COLUMN_T2_SUBSNAME + " from " +
                TABLE_ACTIVE_SUBSTANCE_2 + " order by " +
                COLUMN_T2_SUBSNAME;

        ArrayList<ActiveSubstance> activeSuspencies=null;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        int rawsCount=getRawsCount(TABLE_ACTIVE_SUBSTANCE_2);

        if (rawsCount > 0) {
            try {
                connection = getSqLiteConnection();
                dataCursor = connection.rawQuery(sqlReq, null);
                if (dataCursor.moveToFirst()) {
                    activeSuspencies = new ArrayList<>(rawsCount);
                    do {
                        ActiveSubstance activeSubstance = new ActiveSubstance(dataCursor.getInt(dataCursor.getColumnIndex(COLUMN_T2_ID)), dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T2_SUBSNAME)));
                        activeSuspencies.add(activeSubstance);
                    } while (dataCursor.moveToNext());
                }
            }finally{
                dataCursor.close();
                connection.close();
            }
        }

        return activeSuspencies;
    }

    @Override
    public ArrayList<Drugstore> getDrugstores() {
        String sqlReq = "select " +
                COLUMN_T3_ID + "," +
                COLUMN_T3_NAME + "," +
                COLUMN_T3_CHARSET + "," +
                COLUMN_T3_CITY + "," +
                COLUMN_T3_LINK + "," +
                COLUMN_T3_LINK2 + "," +
                COLUMN_T3_PHONE + "," +
                COLUMN_T3_SEARCH + "," +
                COLUMN_T3_SELECT_REQ + "," +
                COLUMN_T3_SITE + " from " +
                TABLE_DRUGSTORE_3 + " order by " +
                COLUMN_T3_NAME;

        ArrayList<Drugstore> drugstores=null;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        int rawsCount=getRawsCount(TABLE_DRUGSTORE_3);

        if (rawsCount > 0) {
            try {
                connection = getSqLiteConnection();
                dataCursor = connection.rawQuery(sqlReq, null);

                if (dataCursor.moveToFirst()) {
                    drugstores = new ArrayList<>();
                    do {
                        Drugstore drugstore = new Drugstore(dataCursor.getInt(dataCursor.getColumnIndex(COLUMN_T3_ID)), dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T3_NAME)));

                        int choiceValue = dataCursor.getInt(dataCursor.getColumnIndex(COLUMN_T3_CHARSET));
                        drugstore.setCharset((choiceValue==1)?"windows-1251":"utf-8");

                        choiceValue=dataCursor.getInt(dataCursor.getColumnIndex(COLUMN_T3_SEARCH));
                        drugstore.setSimpleSearchName((choiceValue==1)?true:false);

                        drugstore.setCity(dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T3_CITY)));
                        drugstore.setPhone(dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T3_PHONE)));
                        drugstore.setSearchLinkPart1(dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T3_LINK)));
                        drugstore.setSearchLinkPart2(dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T3_LINK2)));
                        drugstore.setSelectForJSoup(dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T3_SELECT_REQ)));
                        drugstore.setSite(dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T3_SITE)));
                        drugstores.add(drugstore);
                    } while (dataCursor.moveToNext());
                }
            }finally{
                dataCursor.close();
                connection.close();
            }
        }

        return drugstores;
    }

    @Override
    public int getDemoCountForFreeVersion() {
        String sqlReq = "select " +
                COLUMN_T4_DEMOCOUNT + " from " +
                TABLE_CFG_4;

        int count=-1;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        try {
            connection = getSqLiteConnection();
            dataCursor = connection.rawQuery(sqlReq, null);

            if (dataCursor.moveToFirst()) {
                count = dataCursor.getInt(dataCursor.getColumnIndex(COLUMN_T4_DEMOCOUNT));
            }
        }finally{
            dataCursor.close();
            connection.close();
        }
        return count;
    }

    @Override
    public boolean decrementDemoCountForFreeVersion() {

        int count=getDemoCountForFreeVersion();
        boolean isSuccessful=false;

        if (count!=-1) {
            String sqlReq = "update " +
                    DatabaseHelperImpl.TABLE_CFG_4 + " set " +
                    DatabaseHelperImpl.COLUMN_T4_DEMOCOUNT + "=" + (count - 1);
            isSuccessful = execSQL(sqlReq, false);
        }
        return isSuccessful;
    }

    @Override
    public String getDrugNotice(Drug drug) {
        String sqlReq = "select " +
                COLUMN_T1_NOTICE + " from " +
                TABLE_DRUGS_1 + " where " +
                COLUMN_T1_ID + " like '" +
                drug.getId() + "'";

        String notice=null;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        try {
            connection = getSqLiteConnection();
            dataCursor = connection.rawQuery(sqlReq, null);

            if (dataCursor.moveToFirst()) {
                notice = dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T1_NOTICE));
            }
        }finally{
            dataCursor.close();
            connection.close();
        }
        return notice;
    }

    @Override
    public String getAboutInfo() {
        String sqlReq = "select " +
                COLUMN_T4_ABOUT + " from " +
                TABLE_CFG_4;

        String aboutInfo=null;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        try {
            connection = getSqLiteConnection();
            dataCursor = connection.rawQuery(sqlReq, null);

            if (dataCursor.moveToFirst()) {
                aboutInfo = dataCursor.getString(dataCursor.getColumnIndex(COLUMN_T4_ABOUT));
            }
        }finally{
            dataCursor.close();
            connection.close();
        }
        return aboutInfo;
    }

    @Override
    public int getRawsCount(String tableName) {
        String sqlReq = "select count(*) as count from " +
                tableName;

        int count=-1;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        try {
            connection = getSqLiteConnection();
            dataCursor = connection.rawQuery(sqlReq, null);

            if (dataCursor.moveToFirst()) {
                count = dataCursor.getInt(dataCursor.getColumnIndex("count"));
            }
        }finally{
            if (dataCursor != null) {
                dataCursor.close();
            }
            connection.close();
        }
        return count;
    }

    @Override
    public HashMap<String, Integer> getLocalVersion() {
        String sqlReq = "select " +
                DatabaseHelperImpl.COLUMN_T5_DRUGS + "," +
                DatabaseHelperImpl.COLUMN_T5_ACTIVE_SUBSTANCE + "," +
                DatabaseHelperImpl.COLUMN_T5_DRUGSTORE + " from " +
                DatabaseHelperImpl.TABLE_DB_VER_5;

        HashMap<String, Integer> versions = null;
        SQLiteDatabase connection=null;
        Cursor dataCursor=null;

        try {
            connection = getSqLiteConnection();
            dataCursor = connection.rawQuery(sqlReq, null);

            if (dataCursor.moveToFirst()) {
                versions=new HashMap<>();
                versions.put(DatabaseHelperImpl.COLUMN_T5_DRUGS,dataCursor.getInt(dataCursor.getColumnIndex(DatabaseHelperImpl.COLUMN_T5_DRUGS)));
                versions.put(DatabaseHelperImpl.COLUMN_T5_ACTIVE_SUBSTANCE,dataCursor.getInt(dataCursor.getColumnIndex(DatabaseHelperImpl.COLUMN_T5_ACTIVE_SUBSTANCE)));
                versions.put(DatabaseHelperImpl.COLUMN_T5_DRUGSTORE,dataCursor.getInt(dataCursor.getColumnIndex(DatabaseHelperImpl.COLUMN_T5_DRUGSTORE)));

            }
        }finally{
            dataCursor.close();
            connection.close();
        }
        return versions;
    }

    @Override
    public boolean execSQL(String sqlRequest, boolean withTransaction){
        SQLiteDatabase connection=null;
        boolean isSuccessful=true;
        try {
            connection=(withTransaction)?this.connection:getSqLiteConnection();
            connection.execSQL(sqlRequest);
        } catch (SQLException e) {
            isSuccessful=false;
        } finally {
            if (!withTransaction) {
                connection.close();
            }
        }
        return isSuccessful;
    }

    @Override
    public void beginTransaction() throws NullPointerException{
        if(this.connection==null){
            throw new NullPointerException(appContext.getString(R.string.null_pointer_exception_connection));
        }
        if (this.connection != null) {
            this.connection.beginTransaction();
        }
    }

    @Override
    public void endTransaction() {
        if (this.connection == null) {
            throw new NullPointerException(appContext.getString(R.string.null_pointer_exception_connection));
        }
        if(this.connection!=null){
            this.connection.endTransaction();
            this.connection.close();
            this.connection=null;
        }
    }

    @Override
    public void setTransactionSuccessful() {
        if(this.connection==null){
            throw new NullPointerException(appContext.getString(R.string.null_pointer_exception_connection));
        }
        if (this.connection != null) {
            this.connection.setTransactionSuccessful();
        }
    }
}
