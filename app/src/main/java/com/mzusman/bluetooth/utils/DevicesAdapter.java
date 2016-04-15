package com.mzusman.bluetooth.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mzusman.bluetooth.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Class : DevicesAdapter.
 * Created by mzusman - morzusman@gmail.com on 4/15/16.
 */
public class DevicesAdapter extends BaseAdapter {

	ArrayList<String> strings;
	Context           context;
	LayoutInflater    inflater;

	public DevicesAdapter(List<String> list, Context context) {
		this.strings = (ArrayList<String>) list;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override public int getCount() {
		return strings.size();

	}

	@Override public Object getItem(int position) {
		return strings.get(position);
	}

	@Override public long getItemId(int position) {
		return position;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			vi = inflater.inflate(R.layout.device_view, null);
		}
		TextView nameView     = (TextView) vi.findViewById(R.id.name);
		TextView addressView  = (TextView) vi.findViewById(R.id.address);
		String   stringToView = strings.get(position);
		nameView.setText(stringToView.split(",")[0]);
		addressView.setText(stringToView.split(",")[1]);


		return vi;
	}

	public String getDeviceAddress(int position) {
		return strings.get(position).split(",")[1];
	}

}
