package com.mzusman.bluetooth.utils.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;

import org.apache.log4j.Logger;

import java.io.IOException;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;
import retrofit2.Callback;

/*
 * Class : RegisterDialog.
 * Created by mzusman - morzusman@gmail.com on 4/25/16.
 */
public class RegisterDialog extends BlurDialogFragment {

    Logger log = Log4jHelper.getLogger("register");
    static Callback callback;

    public static RegisterDialog newInstance(Callback callback) {
        RegisterDialog.callback = callback;
        Bundle args = new Bundle();

        RegisterDialog fragment = new RegisterDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.info("opened register dialog");
        View view = inflater.inflate(R.layout.register_dialog, container, false);
        final EditText name = (EditText) view.findViewById(R.id.name_register_edit);
        final EditText username = (EditText) view.findViewById(R.id.username_register_edit);
        final EditText password = (EditText) view.findViewById(R.id.password_register_edit);
        Button button = (Button) view.findViewById(R.id.register_btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Model.getInstance().getNetworkManager().registerUser(username.getText().toString(),
                            name.getText().toString(), password.getText().toString(), callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }
}
