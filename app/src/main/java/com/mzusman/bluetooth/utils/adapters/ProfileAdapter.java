package com.mzusman.bluetooth.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mzusman.bluetooth.R;

import java.util.ArrayList;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 4/20/16.
 */
public class ProfileAdapter extends BaseAdapter {
    private static LayoutInflater inflater;
    Context context;
    ArrayList<String> tabs;

    public ProfileAdapter(Context context) {
        this.context = context;
        tabs = new ArrayList<>();
        tabs.add("Wifi");
        tabs.add("Bluetooth");
        tabs.add("Options");
        tabs.add("View Data");
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public Object getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.adapter_profile, null, false);
        String s = tabs.get(position);
        TextView textView = (TextView) vi.findViewById(R.id.tab_tv);
        textView.setText(s);
        return vi;
    }
}
