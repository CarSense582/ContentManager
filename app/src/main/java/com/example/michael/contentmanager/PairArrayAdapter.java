package com.example.michael.contentmanager;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by michael on 4/22/15.
 */
public class PairArrayAdapter extends ArrayAdapter<Pair<String,Object>> {
    Context context;
    int layoutResourceId;
    ArrayList<Pair<String,Object>> data = null;

    public PairArrayAdapter(Context context, int layoutResourceId, ArrayList<Pair<String,Object>> data) {
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
            holder.key = (TextView)row.findViewById(R.id.key);
            holder.value = (EditText)row.findViewById(R.id.value);
            row.setTag(holder);
        }
        else
        {
            holder = (PairHolder)row.getTag();
        }

        Pair<String,Object> pair = data.get(position);
        holder.key.setText(pair.first);
        holder.value.setText(pair.second.toString());

        return row;
    }

    static class PairHolder
    {
        EditText value;
        TextView key;
    }
}
