package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.activities.MainActivity;
import com.mzusman.bluetooth.model.NetworkManager;
import com.mzusman.bluetooth.utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by amitmu on 04/15/2016.
 */
public class LoginFragment extends Fragment {
    ActionProcessButton actionProcessButton;
    String response;
    int id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        final EditText driverText = (EditText) view.findViewById(R.id.drivername_edit);
        final EditText userText = (EditText) view.findViewById(R.id.username_edit);
        actionProcessButton = (ActionProcessButton) view.findViewById(R.id.btnLogin);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        actionProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDriverID(userText.getText().toString(), driverText.getText().toString());
                actionProcessButton.setProgress(1);

            }

        });
        return view;
    }

    public void getDriverID(String username, String drivername) {
        NetworkManager networkManager = new NetworkManager();

        try {
            networkManager.getDriverID(username, drivername, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    LoginFragment.this.response = response.message();
                    if (response.isSuccessful()) {
                        actionProcessButton.setProgress(100);
                        LoginFragment.this.id = Integer.parseInt(response.body());
                        Log.d(Constants.IO_TAG, "onResponse: " + id);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("ID", id);
                        startActivity(intent);
                        getActivity().finish();
                    } else actionProcessButton.setProgress(-1);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void flipCard() {
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.card_flip_right_in, R.animator.card_flip_right_out
                , R.animator.card_flip_left_in, R.animator.anim_flip_left_out).
                replace(R.id.fragment_container_login, new FragmentChooseManager()).addToBackStack(null).commit();
    }


}
