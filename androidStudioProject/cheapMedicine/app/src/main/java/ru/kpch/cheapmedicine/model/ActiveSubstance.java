package ru.kpch.cheapmedicine.model;

public class ActiveSubstance{
    private int id;
    private String name;

    public ActiveSubstance(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (name.equals(((ActiveSubstance) o).name) && id == ((ActiveSubstance) o).id) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
