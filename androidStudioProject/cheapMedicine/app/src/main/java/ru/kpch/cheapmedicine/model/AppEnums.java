package ru.kpch.cheapmedicine.model;

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
