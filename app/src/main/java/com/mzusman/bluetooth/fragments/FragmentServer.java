package com.mzusman.bluetooth.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;

/**
 * Created by mzeus on 7/15/16.
 */
public class FragmentServer extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fragment_server);
        dialog.setTitle("Change Server's Address");
        final EditText ip = (EditText) dialog.findViewById(R.id.address_et);
        Button save = (Button) dialog.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.getInstance().setServerIp(ip.getText().toString());
                dialog.dismiss();
                Toast.makeText(getActivity(), "Server's Address has been changed!", Toast.LENGTH_SHORT).show();
            }
        });
        ip.setText(Model.getInstance().getSeverIp());
        return dialog;
    }
}
