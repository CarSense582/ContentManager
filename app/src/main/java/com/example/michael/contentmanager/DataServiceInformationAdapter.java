/**
 * Created by michael on 4/23/15.
 */
package com.example.michael.contentmanager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DataServiceInformationAdapter extends ArrayAdapter<DataServiceInformation> {
    Context context;
    int layoutResourceId;
    ArrayList<DataServiceInformation> data = null;
    public DataServiceInformationAdapter(Context context, int layoutResourceId, ArrayList<DataServiceInformation> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PairHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new PairHolder();
            holder.value = (TextView)row.findViewById(R.id.serviceInfo);
            row.setTag(holder);
        }
        else
        {
            holder = (PairHolder)row.getTag();
        }
        DataServiceInformation ds = data.get(position);
        String s = ds.serviceId + ": ";
        boolean first = true;
        for(String k:ds.fieldInfo.keySet()) {
            if(!first) {
                s = s.concat(", ");
            }
            s = s.concat("["+k + "(" + ds.fieldInfo.get(k).getClass().getSimpleName() +")" +
            "=" + ds.fieldInfo.get(k)+"]");
            first = false;
        }
        holder.value.setText(s);

        return row;
    }
    static class PairHolder
    {
        TextView value;
    }
}
