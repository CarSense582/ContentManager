/**
 * Created by michael on 4/23/15.
 */
package com.example.michael.contentmanager;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.example.michael.appcmlib.AppCMLibConstants;
import com.example.michael.appcmlib.AppData;
import com.example.michael.dataserverlib.DataServerLibConstants;
import com.example.michael.dataserverlib.SensorData;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContentManagerService extends Service {
    AppData sensor;
    HashMap<String,HashMap<String,Object>> appMap;
    private ArrayList<DataServiceInformation> services;
    HashMap<String,Object> map;
    HashMap<String,HashMap<String,Object>> distrMap;
    ArrayList<ServiceConnection> connections;
    ArrayList<Messenger> messengers;
    ArrayList<HashMap<String,Object>> messengerMapList;
    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int msgType = msg.what;
            switch (msgType) {
                case AppCMLibConstants.WRITE_MSG: {
                    //try {
                    // Incoming data
                    Message resp = Message.obtain(null, AppCMLibConstants.WRITE_REPLY_MSG);
                    Bundle bResp = new Bundle();
                    boolean fresh = false;
                    sensor.setFields((HashMap<String, Object>) msg.getData().getSerializable(AppCMLibConstants.WRITE_MAP));
                    //msg.replyTo.send(resp);
                    //}
                }
                break;
                case AppCMLibConstants.READ_MSG:
                    try {
                        // Incoming data
                        Message resp = Message.obtain(null, 5);
                        Bundle bResp = new Bundle();
                        boolean fresh = false;
                        bResp.putSerializable(AppCMLibConstants.READ_MAP, sensor.getFields());
                        resp.setData(bResp);
                        msg.replyTo.send(resp);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    //super.handleMessage(msg);
                    break;
                default:
                    System.out.println("Unhandled Message");
                    break;
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        appMap = new HashMap<String,HashMap<String,Object>>();
        Toast.makeText(getApplicationContext(), "CM binding", Toast.LENGTH_SHORT).show();
        Bundle extras = intent.getExtras();
        map = (HashMap<String,Object>) extras.getSerializable(AppCMLibConstants.QUERY_MAP);
        String appId = extras.getString(AppCMLibConstants.QUERY_APP_ID);
        appMap.put(appId,map);
        //Figure out who I can get stuff from
        Intent notifyIntent = new Intent();
        notifyIntent.setAction(DataServerLibConstants.CM_BROADCAST_ID);
        services = new ArrayList<DataServiceInformation>();
        sendOrderedBroadcast(notifyIntent, null, (BroadcastReceiver) new DataServerReceiver() {
            @Override
            protected void onNewServices() {
                distrMap = new HashMap<String,HashMap<String,Object>>();
                services = serviceList;
                Set<String> keysLeft = map.keySet();
                for(DataServiceInformation dI : services) {
                    Set<String> matchingKeys = keysLeft;
                    matchingKeys.retainAll(dI.fieldInfo.keySet());
                    HashMap<String,Object> matchingFields = new HashMap<String, Object>();
                    for(String f : matchingKeys) {
                        matchingFields.put(f,dI.fieldInfo.get(f));
                    }
                    //Make mapping of serviceId and fields
                    distrMap.put(dI.serviceId, matchingFields);
                    keysLeft.removeAll(matchingKeys);
                    if(keysLeft.isEmpty()) {
                        break;
                    }
                }
                if(!keysLeft.isEmpty()) {
                    Log.i("ContentManagerService","Couldn't find right setups");
                } else {
                    Log.i("ContentManagerService","Found matches");
                    connections = new ArrayList<ServiceConnection>();
                    messengers  = new ArrayList<Messenger>();
                    messengerMapList = new ArrayList<HashMap<String, Object>>();
                    //Bind to these services
                    for(String sId : distrMap.keySet()) {
                        connections.add(new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                messengers.add(new Messenger(service));
                            }
                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        });
                        messengerMapList.add(distrMap.get(sId));
                        Intent bindIntent = getIntentByName(sId);
                        if(bindIntent != null) {
                            bindService(bindIntent, connections.get(connections.size()-1), Context.BIND_AUTO_CREATE);
                        } else {
                            System.out.println("Service not found");
                        }
                    }
                }
            }
        }, null, Activity.RESULT_OK,null,null);

        return mMessenger.getBinder();
    }
    public Intent getIntentByName(String name) {
        PackageManager packageManager = getPackageManager();
        Intent serviceIntent = new Intent(name);
        List<ResolveInfo> services = packageManager.queryIntentServices(serviceIntent, 0);
        if (services.size() > 0) {
            ResolveInfo service = services.get(0);
            Intent intent = new Intent();
            intent.setClassName(service.serviceInfo.packageName, service.serviceInfo.name);
            return intent;
        }
        return null;
    }

    public void sendMessage(int what) {
        for(int i = 0; i < messengers.size(); ++i) {
            Messenger msgr = messengers.get(i);
            HashMap<String,Object> msgMapping = messengerMapList.get(i);
            Message msg = Message
                    .obtain(null, what);
            Bundle b = new Bundle();
            if(what == DataServerLibConstants.READ_MSG) {
                msg.replyTo = new Messenger(new DataServerHandler() {
                    @Override
                    public void useMap() {
                        ArrayList<Pair<String, Object>> serviceFields = new ArrayList<Pair<String, Object>>();
                        Set<String> keys = map.keySet();
                        for (String k : keys) {
                            //messengerMapList.set(i,map)
                            serviceFields.add(new Pair<String, Object>(k, map.get(k)));
                        }
                    }
                });
            } else { //Writing
                HashMap<String, Object> map = new HashMap<String, Object>();
                for(int i = 0; i < arrayAdapter.size(); ++i) {
                    Pair<String,Object> p = arrayAdapter.getItem(i);
                    map.put(p.first,p.second);
                }
                b.putSerializable(DataServerLibConstants.WRITE_MAP,map);
            }
            msg.setData(b);
            try {
                if(mBound) {
                    mService.send(msg);
                    System.out.println("sent message");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //Stop when unbound
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getApplicationContext(), "CM unbinding", Toast.LENGTH_SHORT).show();
        //Unbind to Services
        for(int i = 0; i < distrMap.size(); ++i) {
            unbindService(connections.get(i));
        }
        connections = new ArrayList<ServiceConnection>();
        messengers  = new ArrayList<Messenger>();
        messengerMapList = new ArrayList<HashMap<String, Object>>();
        return false; //Don't use rebind
    }
}