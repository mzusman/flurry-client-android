package com.mzusman.bluetooth.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.utils.Profile;

import java.util.ArrayList;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 4/20/16.
 */
public class ProfileAdapter extends BaseAdapter{
    ArrayList<Profile> profiles;
    private static LayoutInflater inflater ;
    Context context;

    public ProfileAdapter(Context context, ArrayList<Profile> profiles){
        this.context = context;
        this.profiles = profiles;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Object getItem(int position) {
        return profiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(vi == null)
            vi = inflater.inflate(R.layout.profile_list,null,false);
        Profile profile = profiles.get(position);
        TextView date = (TextView) vi.findViewById(R.id.tv_date);
        TextView time = (TextView) vi.findViewById(R.id.tv_time);
        time.setText(profile.getTime());
        date.setText(profile.getDate());


        return vi;
    }
}
