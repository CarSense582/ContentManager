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

import java.util.ArrayList;
import java.util.HashMap;

public abstract class DataServerReceiver extends BroadcastReceiver {

    public ArrayList<DataServiceInformation> serviceList;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("CM receiver:");
        System.out.println(intent);
        CharSequence text = "CM received broadcast from DS!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        Bundle results = getResultExtras(true);
        String s = results.getString("broadCastResp");
        System.out.println("CM DS Rx Final Result Receiver = " + s);
        ArrayList<String> serviceIds = results.getStringArrayList("dsServices");
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