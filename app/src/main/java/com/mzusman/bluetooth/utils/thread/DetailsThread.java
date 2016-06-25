package com.mzusman.bluetooth.utils.thread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonWriter;
import android.widget.ListView;
import android.widget.TextView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.fragments.FragmentDetailsList;
import com.mzusman.bluetooth.fragments.FragmentProfile;
import com.mzusman.bluetooth.model.Managers.GpsManager;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.adapters.DetailsAdapter;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

/*
 * Class : DetailsThread.
 * Created by mzusman - morzusman@gmail.com on 4/19/16.
 */

/**
 * Extension to thread class - runnable that updates the fragment screen and update the data .
 */
public class DetailsThread extends Thread {


    private Callback callback;
    private final DetailsThread.Event event;
    private final static int SLEEP_TIME = 100;
    private DetailsAdapter detailsAdapter;
    private Activity activity;
    private ArrayList<String> readings;
    private SpotsDialog spotsDialog;
    private TextView textView;
    private Logger log = Log4jHelper.getLogger("Details Thread");
    private volatile boolean run = true;
    private long time = 0;
    private FragmentDetailsList.CallBack fragCallBack;

    /**
     * ListView in order to post it with the main loop
     */
    public DetailsThread(FragmentDetailsList.CallBack fragcallBack, ListView listView, @NonNull Activity activity, TextView timeView) {
        this.fragCallBack = fragcallBack;
        this.activity = activity;
        this.detailsAdapter = (DetailsAdapter) listView.getAdapter();
        this.textView = timeView;
        ((AppCompatActivity) activity).getSupportActionBar().setTitle("Details");
        showDialog("Loading...");
        event = new Event();
    }


    private void showDialog(String message) {
        this.spotsDialog = new SpotsDialog(activity, message);
        this.spotsDialog.setCancelable(false);
        this.spotsDialog.show();
    }

    private void disposeDialog() {
        if (spotsDialog == null)
            return;
        if (spotsDialog.isShowing()) {
            spotsDialog.dismiss();
            spotsDialog = null;
        }
    }

    private class Event {
        volatile boolean finish = false;

        void onEvent() throws IOException, InterruptedException {
            if (!finish)
                getConnectionReadings();
        }
    }

    /**
     * updates the activity's tool bar and the list view with strings that are given him from the
     * managers inside the model component
     */
    public void run() {
        while (run) {
            getConnectionReadings();
            if (event.finish)
                break;
            time = System.currentTimeMillis();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    disposeDialog();
                    detailsAdapter.setArray(readings);
                    detailsAdapter.notifyDataSetChanged();
                    textView.setText("time: " + Long.toString(time));
                }
            });
            Model.getInstance().writeToFile(readings, time);
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignored) {
            }
        }
        Model.getInstance().endJsonWrite();
    }

    private void getConnectionReadings() {
        try {
            readings = Model.getInstance().getReading(activity);
        } catch (IOException e) {
            disposeDialog();
            tryAgainDialog();
            synchronized (event) {
                try {
                    event.wait();
                } catch (InterruptedException e1) {//ignored
                    e1.printStackTrace();
                }
                try {
                    event.onEvent();
                } catch (IOException e1) {
                    getConnectionReadings();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            getConnectionReadings();
        } catch (InterruptedException ignored) {
        }
    }


    private void tryAgainDialog() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                CharSequence[] items = {"Try Again(OBD)", "Send Cloud(3G)", "Cancel"};
                if (time == 0)
                    items = new CharSequence[]{"Try Again(OBD)", "Cancel"};
                final CharSequence[] finalItems = items;
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (finalItems[which].equals("Try Again(OBD)")) {
                            synchronized (event) {
                                showDialog("Connecting..");
                                event.notify();
                            }
                        } else if (finalItems[which].equals("Send Cloud(3G)")) {
                            event.finish = true;
                            synchronized (event) {
                                event.notify();
                            }
                            DetailsThread.this.fragCallBack.onStop();
                        } else if (finalItems[which].equals("Cancel")) {
                            run = false;
                            activity.getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new FragmentProfile(),
                                            Constants.DETAILS_TAG).commit();
                        }
                    }
                }).setTitle("Unable to connect,\nChoose an Action:").setCancelable(false).show();

            }

        });

    }


    /*
    waits till the thread will stop - shows loading dialog meanwhile
     */
    public void stopRunning(Callback callback) {
        this.callback = callback;
        showDialog("Stopping...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DetailsThread.this.run = false;
                    if (DetailsThread.this.isAlive())
                        DetailsThread.this.join();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DetailsThread.this.disposeDialog();
                            DetailsThread.this.callback.ThreadDidStop();
                        }
                    });
                } catch (InterruptedException e) {
                    log.debug(e.getMessage());
                }
            }
        }).start();
    }

    public interface Callback {
        void ThreadDidStop();
    }


}
