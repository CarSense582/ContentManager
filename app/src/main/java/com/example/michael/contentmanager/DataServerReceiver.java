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

public class DataServerReceiver extends BroadcastReceiver {

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
        System.out.println(results);
    }

}