package ru.kpch.cheapmedicine.interfaces;

/**
 * Created by user on 20.06.2017.
 */

public interface IHttpManager {
    String getDataFromServer(String urlReq, int timeToWait);
}
