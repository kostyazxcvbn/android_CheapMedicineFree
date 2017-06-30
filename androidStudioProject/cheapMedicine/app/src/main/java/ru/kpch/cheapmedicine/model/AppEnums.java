package ru.kpch.cheapmedicine.model;

/**
 * Created by kostyazxcvbn on 18.06.2017.
 */

public final class AppEnums {
    public enum UpdateState {
        NO_UPDATES,
        UPDATE_SUCCESSFUL,
        UPDATE_ERROR,
    }

    public enum RequestToServerState {
        SENDING_SUCCESSFUL,
        SENDING_ERROR
    }
}
