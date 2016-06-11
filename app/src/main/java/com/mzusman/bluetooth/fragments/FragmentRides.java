package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.adapters.RidesAdapter;

/**
 * Created by Asaf on 11/06/2016.
 */
public class FragmentRides extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides, container, false);
        RidesAdapter ridesAdapter = new RidesAdapter(Model.getInstance().getAllDriverRides(), getActivity());
        ListView listView = (ListView) view.findViewById(R.id.rides_lv);
        listView.setAdapter(ridesAdapter);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!menu.hasVisibleItems()) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.rides_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentProfile()).commit();
        return true;
    }
}
