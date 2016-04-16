package com.mzusman.bluetooth.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mzusman.bluetooth.R;

import java.util.ArrayList;

/*
 * Class : DetailsAdapter.
 * Created by mzusman - morzusman@gmail.com on 4/11/16.
 */
public class DetailsAdapter extends BaseAdapter {

    ArrayList<String> parametersList;
    private static LayoutInflater inflater = null;
    Context context;
    TextView name;
    TextView value;
    TextView time;

    public DetailsAdapter( Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    public void setArray(ArrayList<String> array){
        this.parametersList = array;
    }

    @Override
    public int getCount() {
        return parametersList.size();
    }

    @Override
    public Object getItem(int position) {
        return parametersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.car_details, null);
        }

        String paramter = parametersList.get(position);
        name = (TextView) vi.findViewById(R.id.name);
        name.setText(paramter.split(",")[0]);
        time = (TextView) vi.findViewById(R.id.time);
        time.setText(paramter.split(",")[1]);
        value = (TextView) vi.findViewById(R.id.val);
        value.setText(paramter.split(",")[2]);
        return vi;


    }
}
