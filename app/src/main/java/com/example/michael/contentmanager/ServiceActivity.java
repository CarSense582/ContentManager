package com.example.michael.contentmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class ServiceActivity extends ActionBarActivity {
    private ListView lv;
    private PairArrayAdapter arrayAdapter;
    private String serviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        Intent intent = getIntent();
        DataServiceInformation ds = new DataServiceInformation(intent.getStringExtra("service_id"),
                (HashMap<String,Object>) intent.getSerializableExtra("service_map"));
        //Setup list
        lv = (ListView) findViewById(R.id.serviceFieldList);
        ArrayList<Pair<String, Object>> services = new ArrayList<Pair<String, Object>>();
        Set<String> keys = ds.fieldInfo.keySet();
        for(String k: keys) {
            services.add(new Pair<String, Object>(k, ds.fieldInfo.get(k)));
        }
        arrayAdapter = new PairArrayAdapter(
                this,
                R.layout.service_field_item,
                services );
        lv.setAdapter(arrayAdapter);
        TextView sId = (TextView) findViewById(R.id.serviceId);
        sId.setText(ds.serviceId);
        serviceId = ds.serviceId;
        //Setup get button
        final Button button = (Button) findViewById(R.id.getValue);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Bind to service
        Intent bindIntent = getIntentByName(serviceId);
        if(bindIntent != null) {
            bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
        } else {
            System.out.println("Service not found");
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Messenger for communicating with the service. */
    Messenger mService = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void sayHello(View v) {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, 1, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        Message msg = Message
                .obtain(null, 3);

        msg.replyTo = new Messenger(new DataServerHandler() {
            @Override
            public void useMap() {
                ArrayList<Pair<String,Object>> serviceFields = new ArrayList<Pair<String,Object>>();
                Set<String> keys = map.keySet();
                for(String k: keys) {
                    serviceFields.add(new Pair<String, Object>(k, map.get(k)));
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(serviceFields);
                arrayAdapter.notifyDataSetChanged();
            }
        });
        // We pass the value
        Bundle b = new Bundle();
        b.putString("data", "content manager data");

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
