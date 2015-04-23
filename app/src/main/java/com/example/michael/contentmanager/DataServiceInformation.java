package com.example.michael.contentmanager;

import java.util.HashMap;

/**
 * Created by michael on 4/22/15.
 */
public class DataServiceInformation {
    public String serviceId;
    public HashMap<String,Object> fieldInfo;
    DataServiceInformation() {
        serviceId = "DefaultId";
        fieldInfo = new HashMap<String,Object>();
    }
    DataServiceInformation(String id, HashMap<String,Object> map) {
        serviceId = id;
        fieldInfo = map;
    }
}
