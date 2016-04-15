package com.mzusman.bluetooth;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created on 04/15/2016.
 */
public class FragmentDeviceList extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.device_list_frag,container,false);
        final ListView listOfDevices=(ListView)view.findViewById(R.id.device_list);
        final ArrayList<String> devices  = new ArrayList<>();
        final ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getActivity(), R.layout.device_name);
        getActivity().setTitle("Select Device");
        adapter.add("asdasd");
        listOfDevices.setAdapter(adapter);
        listOfDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            	String deviceAdress = adapter.getItem(position);
                Fragment details = new FragmentDetailsList();
                Bundle bundle=new Bundle();
                bundle.putString("DEVICE",deviceAdress);
                details.setArguments(new Bundle());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, details);
                transaction.commit();
                //Intent intent = new Intent(getActivity(), DetailsActivity.class);
//				intent.putExtra("address",deviceAdress);
                //startActivity(intent);

            }
        });

        return view;
    }
}
