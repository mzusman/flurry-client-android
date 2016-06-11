package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;
import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.activities.MainActivity;
import com.mzusman.bluetooth.model.Network.NetworkManager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.dialogs.RegisterDialog;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;

import org.apache.log4j.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by amitmu on 04/15/2016.
 */
public class LoginFragment extends Fragment {
    ActionProcessButton actionProcessButton;
    String response;
    Button registerButton;
    Logger log = Log4jHelper.getLogger("LoginFragment");

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
                hideKeyboard();
                loginUser(userText.getText().toString(), passText.getText().toString());
                actionProcessButton.setProgress(1);

            }

        });
        registerButton = (Button) view.findViewById(R.id.register_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterDialog registerDialog = RegisterDialog.newInstance(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        int id = ((NetworkManager.UserCreditials) response.body()).driver_id;
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        log.debug("onResponse: id:" + id);
                        Model.getInstance().setDriverId(id);//important
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });
                registerDialog.show(getFragmentManager(), Constants.REGISTER_TAG);
            }
        });

        return view;
    }

    private void hideKeyboard() {
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    public void loginUser(String username, String password) {

        Model.getInstance().setNetworkManager(username, password).loginUser(new Callback<NetworkManager.UserCreditials>() {
            @Override
            public void onResponse(Call<NetworkManager.UserCreditials> call, Response<NetworkManager.UserCreditials> response) {
                LoginFragment.this.response = response.message();
                if (response.isSuccessful()) {
                    actionProcessButton.setProgress(100);
                    actionProcessButton.setText(R.string.welc);
                    int id = (response.body().driver_id);
                    log.debug("onResponse: id:" + id);
                    Model.getInstance().setDriverId(id); //important
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    actionProcessButton.setProgress(-1);
                    actionProcessButton.setText(R.string.cred);
                    Model.getInstance().setNetworkManager(null);
                }
            }

            @Override
            public void onFailure(Call<NetworkManager.UserCreditials> call, Throwable t) {
                actionProcessButton.setProgress(-1);
                actionProcessButton.setText(R.string.try_again);
                Model.getInstance().setNetworkManager(null);
            }
        });


    }


}
