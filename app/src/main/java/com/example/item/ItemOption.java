package com.example.item;

/**
 * Created by jonathan on 15/12/2014.
 */
public class ItemOption {
    private String name;
    private int resource;
    private int id;

    public ItemOption(){}

    public ItemOption(String name, int resource, int id) {
        this.name = name;
        this.resource = resource;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
