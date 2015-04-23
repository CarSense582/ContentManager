package com.example.michael.contentmanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;


public class ServiceActivity extends ActionBarActivity {
    private ListView lv;
    private PairArrayAdapter arrayAdapter;
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
}
