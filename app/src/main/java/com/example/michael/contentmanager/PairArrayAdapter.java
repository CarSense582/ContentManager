package com.example.michael.contentmanager;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by michael on 4/22/15.
 */
public class PairArrayAdapter extends ArrayAdapter<Pair<String,Object>> {
    Context context;
    int layoutResourceId;
    ArrayList<Pair<String,Object>> data = null;
    HashMap<String,Object> dataValues = new HashMap<String,Object>();

    public int size() {
        return data.size();
    }

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
        boolean rowWasNull = false;
        if(row == null)
        {
            rowWasNull = true;
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
        if(rowWasNull){
            //be aware that you shouldn't do this for each call on getView, just once by listItem when convertView is null
            holder.value.addTextChangedListener(new GenericTextWatcher(holder.value));
        }
        Pair<String,Object> pair = data.get(position);
        holder.value.setTag(pair.first);
        holder.key.setText(pair.first);
        holder.value.setText(pair.second.toString());

        return row;
    }

    private class GenericTextWatcher implements TextWatcher {
        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            String key  = view.getTag().toString();
            //save the value for the given tag :
            Object val = PairArrayAdapter.this.dataValues.get(key);
            PairArrayAdapter.this.dataValues.put(key, text);
            for(int i = 0; i < PairArrayAdapter.this.data.size(); ++i) {
                Pair<String, Object> p = PairArrayAdapter.this.data.get(i);
                if(p.first == key) {
                    Pair<String,Object> newP = new Pair<String,Object>(p.first, text);
                    PairArrayAdapter.this.data.set(i, newP);
                }
            }
        }
    }
    //you can implement a method like this one for each EditText with the list position as parameter :
    public String getValueFromEditText(int position){
        //here you need to recreate the id for the first editText
        String result = dataValues.get("value:"+position).toString();
        if(result ==null)
            result = "default value";
        return result;
    }

    static class PairHolder
    {
        EditText value;
        TextView key;
    }
}
