/**
 * Created by michael on 4/24/15.
 */
package com.example.michael.contentmanager;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;


abstract public class DataServiceConnection implements ServiceConnection {
    public String connectionId;
    DataServiceConnection(String id) {
        super();
        connectionId = id;
    }
}