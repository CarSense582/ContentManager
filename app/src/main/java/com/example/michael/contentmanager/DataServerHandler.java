/**
 * Created by michael on 4/22/15.
 */

package com.example.michael.contentmanager;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;

// This class handles the Service response
abstract class DataServerHandler extends Handler {

    public HashMap<String, Object> map;
    @Override
    public void handleMessage(Message msg) {
        int respCode = msg.what;

        switch (respCode) {
            default: {
                String val = msg.getData().getString("respData");
                map = (HashMap<String,Object>) msg.getData().getSerializable("respMap");
                if(map != null) {
                    useMap();
                }
                System.out.println("CM got " + val + " from DS");
                break;
            }
        }
    }

    abstract public void useMap();
}