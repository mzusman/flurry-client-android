package com.mzusman.bluetooth;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * Created by amitmu on 04/15/2016.
 */
public class LoginFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.login_fragment,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
