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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
        int id = item.getItemId();
        System.out.println("item selected");
        Intent notifyIntent = new Intent();
        notifyIntent.setAction("com.example.michael.contentmanager.broadcaster");
        sendOrderedBroadcast(notifyIntent, null, (BroadcastReceiver) new DataServerReceiver(), null, Activity.RESULT_OK,null,null);
        System.out.println("Sent broadcast");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
        */
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
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
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

}
