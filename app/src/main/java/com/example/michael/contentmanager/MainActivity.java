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
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ListView lv;
    private ArrayList<DataServiceInformation> services;
    private ArrayAdapter<DataServiceInformation> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setup list
        lv = (ListView) findViewById(R.id.serviceList);
        services = new ArrayList<DataServiceInformation>();
        arrayAdapter = new ArrayAdapter<DataServiceInformation>(
                this,
                android.R.layout.simple_list_item_1,
                services );

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DataServiceInformation s = services.get(position);
                Toast toast=Toast.makeText(getApplicationContext(), s.serviceId, Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(view.getContext(), ServiceActivity.class);
                intent.putExtra("msg","bye");
                Bundle b = new Bundle();
                b.putSerializable("service_map", s.fieldInfo);
                b.putString("service_id", s.serviceId);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        final Button button = (Button) findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent notifyIntent = new Intent();
                notifyIntent.setAction("com.example.michael.contentmanager.broadcaster");
                sendOrderedBroadcast(notifyIntent, null, (BroadcastReceiver) new DataServerReceiver() {
                    @Override
                    protected void onNewServices() {
                        services = serviceList;
                        arrayAdapter.clear();
                        arrayAdapter.addAll(serviceList);
                        arrayAdapter.notifyDataSetChanged();
                        System.out.println("got New Services");
                    }
                }, null, Activity.RESULT_OK,null,null);
                /*
                // Perform action on click
                Message msg = Message
                        .obtain(null, 3);

                msg.replyTo = new Messenger(new DataServerHandler());
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
                */
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    //Messenger with Data Servers
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        /*
        bindService(new Intent(this, MessengerService.class), mConnection,
                Context.BIND_AUTO_CREATE);

        PackageManager packageManager = getPackageManager();
        Intent serviceIntent = new Intent("com.example.michael.dataserver.service");
        List<ResolveInfo> services = packageManager.queryIntentServices(serviceIntent, 0);
        for(int i = 0; i < services.size(); ++i) {
            System.out.println(services.get(i));
        }
        if (services.size() > 0) {
            ResolveInfo service = services.get(0);
            Intent intent = new Intent();
            intent.setClassName(service.serviceInfo.packageName, service.serviceInfo.name);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            System.out.println("Bound to service");
        }
        */
    }


}
