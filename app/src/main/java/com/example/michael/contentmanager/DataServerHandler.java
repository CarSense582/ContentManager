/**
 * Created by michael on 4/22/15.
 */

package com.example.michael.contentmanager;

import android.os.Handler;
import android.os.Message;

// This class handles the Service response
class DataServerHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
        int respCode = msg.what;

        switch (respCode) {
            default: {
                String val = msg.getData().getString("respData");
                System.out.println("CM got " + val + " from DS");
                break;
            }
        }
    }

}