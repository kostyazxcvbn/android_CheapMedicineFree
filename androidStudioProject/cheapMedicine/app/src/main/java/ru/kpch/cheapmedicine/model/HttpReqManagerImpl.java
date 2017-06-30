package ru.kpch.cheapmedicine.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ru.kpch.cheapmedicine.interfaces.IHttpManager;

public class HttpReqManagerImpl implements IHttpManager {

    private static HttpReqManagerImpl instance= null;
    private ExecutorService appThreadPool=null;

    private HttpReqManagerImpl(ExecutorService appThreadPool) {
        if (appThreadPool != null) {
            this.appThreadPool = appThreadPool;
        }
    }

    public HttpReqManagerImpl() {
    }

    public static void init(ExecutorService appThreadPool) {
        if (instance == null) {
            instance=new HttpReqManagerImpl(appThreadPool);
        }
        System.setProperty("http.agent","");
    }

    public static HttpReqManagerImpl getInstance(){
        return instance;
    }

    public String getDataFromServer(String urlReq, int timeToWait) {

        final String urlRequest = urlReq;
        String result = null;
        Future<String> responceBody = appThreadPool.submit((new Callable<String>() {
            @Override
            public String call() throws Exception {
                String responceBody=null;
                BufferedReader reader = null;
                URL url = new URL(urlRequest);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();

                if (c.getContentLength() > 0) {
                    try {
                        reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder buf = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            buf.append(line + "\n");
                        }
                        responceBody = buf.toString();
                    } catch (IOException ex) {
                        responceBody = "err";
                    } finally {
                        if (reader != null) {
                            reader.close();
                        }
                        c.disconnect();
                    }
                }
                return responceBody;
            }
        }));

        try {
            result = responceBody.get(timeToWait, TimeUnit.SECONDS);
        } catch (Exception e) {
            return "err";
        }
        return result;
    }
}
