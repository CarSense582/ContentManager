/**
 * Created by michael on 4/22/15.
 */

package com.example.michael.contentmanager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.michael.dataserverlib.DataServerLibConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public abstract class DataServerReceiver extends BroadcastReceiver {

    public ArrayList<DataServiceInformation> serviceList;
    public Set<String> fieldNames;

    DataServerReceiver() {
        super();
    }
    DataServerReceiver(Set<String> fields) {
        super();
        fieldNames = fields;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle results = getResultExtras(true);
        String s = results.getString(DataServerLibConstants.CM_BROADCAST_RESP_KEY);
        ArrayList<String> serviceIds = results.getStringArrayList(DataServerLibConstants.CM_BROADCAST_SERVICES);
        serviceList = new ArrayList<DataServiceInformation>();
        if(serviceIds != null) {
            for (int i = 0; i < serviceIds.size(); ++i) {
                String id = serviceIds.get(i);
                HashMap<String,Object> serviceOptions = (HashMap<String,Object>) results.getSerializable(id);
                serviceList.add(new DataServiceInformation(id,serviceOptions));
                System.out.print(id + ": ");
                System.out.println(serviceOptions);
            }
        }
        System.out.println(results);
        onNewServices();
    }

    protected abstract void onNewServices();
}