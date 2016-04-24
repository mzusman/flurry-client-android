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
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.model.NetworkManager;
import com.mzusman.bluetooth.utils.Constants;

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
        final EditText userText = (EditText) view.findViewById(R.id.username_edit);
        final EditText passText = (EditText) view.findViewById(R.id.password_edit);
        actionProcessButton = (ActionProcessButton) view.findViewById(R.id.btnLogin);
        actionProcessButton.setMode(ActionProcessButton.Mode.ENDLESS);
        actionProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(userText.getText().toString(), passText.getText().toString());
                actionProcessButton.setProgress(1);

            }

        });
        return view;
    }

    public void loginUser(String username, String password) {

        Model.getInstance().getNetworkManager(username, password).loginUser(new Callback<NetworkManager.UserCreditials>() {
            @Override
            public void onResponse(Call<NetworkManager.UserCreditials> call, Response<NetworkManager.UserCreditials> response) {
                LoginFragment.this.response = response.message();
                Log.d(Constants.IO_TAG, "onResponse: " + call.request().toString());
                Log.d(Constants.IO_TAG, "onResponse: " + response.raw().toString());
                if (response.isSuccessful()) {
                    actionProcessButton.setProgress(100);
                    LoginFragment.this.id = (response.body().driver_id);
                    Log.d(Constants.IO_TAG, "onResponse: " + id);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("ID", id);
                    startActivity(intent);
                    getActivity().finish();
                } else actionProcessButton.setProgress(-1);
            }

            @Override
            public void onFailure(Call<NetworkManager.UserCreditials> call, Throwable t) {
//                actionProcessButton.setProgress(-1);
            }
        });


    }


    private void flipCard() {
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.card_flip_right_in, R.animator.card_flip_right_out
                , R.animator.card_flip_left_in, R.animator.anim_flip_left_out).
                replace(R.id.fragment_container_login, new FragmentChooseManager()).addToBackStack(null).commit();
    }


}
