package ru.kpch.cheapmedicine.model;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;

import ru.kpch.cheapmedicine.R;

public class Drugstore {
    private int id;
    private String name;
    private String searchLinkPart1;
    private String searchLinkPart2;
    private String selectForJSoup;
    private String City;
    private String Phone;
    private String Site;
    private String charset;
    private boolean isSimpleSearchName;

    public Drugstore(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSearchLinkPart1() {
        return searchLinkPart1;
    }

    public void setSearchLinkPart1(String searchLinkPart1) {
        this.searchLinkPart1 = searchLinkPart1;
    }

    public String getSearchLinkPart2() {
        return searchLinkPart2;
    }

    public void setSearchLinkPart2(String searchLinkPart2) {
        this.searchLinkPart2 = searchLinkPart2;
    }

    public String getSelectForJSoup() {
        return selectForJSoup;
    }

    public void setSelectForJSoup(String selectForJSoup) {
        this.selectForJSoup = selectForJSoup;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getSite() {
        return Site;
    }

    public void setSite(String site) {
        Site = site;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isSimpleSearchName() {
        return isSimpleSearchName;
    }

    public void setSimpleSearchName(boolean simpleSearchName) {
        isSimpleSearchName = simpleSearchName;
    }

    public String getPrice(Drug drug){
        Context context = AppLogicImpl.getInstance().getAppContext();
        Document doc=null;
        String price=null;
        try{
            System.setProperty("http.agent","");
            String httpReq = searchLinkPart1 + URLEncoder.encode(drug.getNameForDrugstores(), charset);
            doc= Jsoup.connect(httpReq).timeout(10000).get();
        }
        catch(IOException e){
            return context.getString(R.string.messageNoData);
        }

        if(doc!=null ){
            if(doc.select(selectForJSoup).hasText()) {
                price = doc.select(selectForJSoup).first().text();
                price = correctPrice(price);
                drug.setPrice(price);
                return price;
            }
        }
        return context.getString(R.string.messageNoData);
    }

    private String correctPrice(String price){

        Context context = AppLogicImpl.getInstance().getAppContext();
        StringBuilder correctedPrice = new StringBuilder();
        if(price.length()>1){
            for (int i=0; i<price.length(); i++){
                char ch=price.charAt(i);

                if((ch >= '0') && (ch <= '9')){
                    correctedPrice.append(ch);
                }
                else if((i+1)<price.length()){
                    char next_ch=price.charAt(i+1);
                    if(((ch=='.')||(ch==',')||(ch==' '))&&((next_ch>='0')&&(next_ch<='9'))){
                        correctedPrice.append(ch);
                    }
                }
                else{
                    return correctedPrice.toString();
                }
            }
            return correctedPrice.toString();
        }
        return context.getString(R.string.messageNoData);
    }

    @Override
    public String toString() {
        return name;
    }
}
