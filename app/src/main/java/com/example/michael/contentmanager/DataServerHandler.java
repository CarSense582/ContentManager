/**
 * Created by michael on 4/22/15.
 */

package com.example.michael.contentmanager;

import android.os.Handler;
import android.os.Message;

import com.example.michael.dataserverlib.DataServerLibConstants;

import java.util.HashMap;

// This class handles the Service response
abstract class DataServerHandler extends Handler {

    public HashMap<String, Object> map;
    @Override
    public void handleMessage(Message msg) {
        int respCode = msg.what;
        switch (respCode) {
            default: {
                map = (HashMap<String,Object>) msg.getData().getSerializable(DataServerLibConstants.READ_REPLY_MAP);
                if(map != null) {
                    useMap();
                }
                break;
            }
        }
    }

    abstract public void useMap();
}
