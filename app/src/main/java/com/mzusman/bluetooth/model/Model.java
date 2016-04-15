package com.mzusman.bluetooth.model;

import java.util.List;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public class Model {
    Manager manager;

    private static Model instance = new Model();

    public static Model getInstance() {
        return instance;
    }

    public void init(Manager manager){
        this.manager = manager;
        manager.connect();
    }
    public void setManager(Manager manager){
        init(manager);
    }
    public Manager getManager(){
        return this.manager;
    }
    public List<String> getReading(int READINGS){
        return manager.getReadings(READINGS);
    }
    public String getRead(int READINGS){
        return manager.getReading(READINGS);
    }
    public void drop(){
        manager.stop();
    }





}
