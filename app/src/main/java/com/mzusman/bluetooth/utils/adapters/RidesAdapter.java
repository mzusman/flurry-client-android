package com.mzusman.bluetooth.utils.adapters;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.RideDescription;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Asaf on 11/06/2016.
 */
public class RidesAdapter extends BaseAdapter {
    ArrayList<RideDescription> rideDescriptions;
    int id;
    Context context;
    private static LayoutInflater inflater = null;

    public RidesAdapter(int id, List<RideDescription> rideDescriptionList, Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.rideDescriptions = (ArrayList<RideDescription>) rideDescriptionList;
        Collections.sort(rideDescriptionList, new Comparator<RideDescription>() {
            @Override
            public int compare(RideDescription rideDescription, RideDescription t1) {
                DateFormat dateFormat = Model.getInstance().getDateFormat();
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = dateFormat.parse(rideDescription.getFileName());
                    date2 = dateFormat.parse(t1.getFileName());
                } catch (ParseException e) {
                    //ignored
                }
                return date1.compareTo(date2);
            }
        });
    }

    @Override
    public int getCount() {
        return rideDescriptions.size();
    }

    @Override
    public Object getItem(int i) {
        return rideDescriptions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (vi == null)
            vi = inflater.inflate(R.layout.adapter_rides, null);

        final RideDescription currentRide = rideDescriptions.get(i);
        final ActionProcessButton button = (ActionProcessButton) vi.findViewById(R.id.send_btn);
        if (rideDescriptions.get(i).isSent()) {
            button.setText("Done");
            button.setProgress(100);
            button.setClickable(false);
        } else {
            button.setText("Send");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setProgress(1);
                Model.getInstance().sendRemote(Integer.parseInt(currentRide.getDriverID()), new Model.OnEvent() {
                    @Override
                    public void onSuccess() {
                        button.setText("Done");
                        button.setProgress(100);
                    }

                    @Override
                    public void onFailure() {
                        button.setProgress(-1);
                        button.setText("Error");
                    }
                });
            }
        });

        TextView textView = (TextView) vi.findViewById(R.id.tv_file_name);
        textView.setText("Ride from: " + currentRide.getFileName());

        return vi;
    }

}
