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


    Callback callback;
    final DetailsThread.Event event;
    private final static int SLEEP_TIME = 500;
    private ListView listView;
    private DetailsAdapter detailsAdapter;
    private Activity activity;
    private ArrayList<String> readings;
    private JsonWriter jsonWriter;
    private FileOutputStream fileOutputStream;
    private LocationListener locationListener;
    private SpotsDialog spotsDialog;
    private TextView textView;
    private Logger log = Log4jHelper.getLogger("Details Thread");
    private boolean run = true;
    private long time = 0;
    private FragmentDetailsList.CallBack fragCallBack;

    /**
     * ListView in order to post it with the main loop
     */
    public DetailsThread(FragmentDetailsList.CallBack fragcallBack, @NonNull LocationListener locationListener, @NonNull Activity activity, @NonNull ListView listView, TextView timeView) {
        this.fragCallBack = fragcallBack;
        this.listView = listView;
        this.activity = activity;
        this.detailsAdapter = (DetailsAdapter) listView.getAdapter();
        this.locationListener = locationListener;
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

    private synchronized void disposeDialog() {
        if (spotsDialog == null)
            return;
        if (spotsDialog.isShowing()) {
            spotsDialog.dismiss();
            spotsDialog = null;
        }
    }

    private class Event {
        boolean finish = false;

        void onEvent() {
            if (!finish)
                tryConnectToObd();
        }
    }

    /**
     * updates the activity's tool bar and the list view with strings that are given him from the
     * managers inside the model component
     */
    public void run() {
        initJsonWriting();//initiates the json reading
        tryConnectToObd();
        disposeDialog();
        try {
            sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            log.debug("run: interrupted while sleeping\n" + e.getMessage());
        }
        while (run) {
            try {
                time = System.currentTimeMillis();
                readings = Model.getInstance().getReading();//assigned to the Manager .
            } catch (IOException e) {
                tryConnectToObd();
                disposeDialog();
            }
            if (event.finish)
                break;
            readings.add(((GpsManager) locationListener).getReading(Constants.GPS_TAG));
            writeToJson(readings);
            //run on the main thread to update the listview
            listView.post(new Runnable() {
                @Override
                public void run() {
                    detailsAdapter.setArray(readings);
                    detailsAdapter.notifyDataSetChanged();
                    textView.setText("time: " + Long.toString(time));
                }
            });
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                log.debug("run: interrupted while sleeping");
            }
        }
        endJsonWrite();
    }

    private void tryConnectToObd() {
        try {
            Model.getInstance().getManager().connect(Constants.WIFI_ADDRESS);
        } catch (IOException e) {
            log.debug("try to connect to obd:" + e.getMessage());
            disposeDialog();
            tryAgainDialog();
            synchronized (event) {
                try {
                    event.wait();
                } catch (InterruptedException e1) {//ignored
                    e1.printStackTrace();
                }
                event.onEvent();
            }
        } catch (InterruptedException e) {
            log.debug("interrupted:" + e.getMessage());
            disposeDialog();
            tryAgainDialog();
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
                                event.notify();
                            }
                            showDialog("Connecting..");
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


    /**
     * three methods that are writing the data into a json
     */
    private void initJsonWriting() {
        try {
            fileOutputStream = activity.openFileOutput(Constants.FILE_DATA, Context.MODE_PRIVATE);
            jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
            jsonWriter.beginArray();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    /**
     * writing to Json - hard coded to time-speed-rpm-gps
     */

    private void writeToJson(ArrayList<String> readings) {
        try {
            jsonWriter.beginObject();
            jsonWriter.name("time").value(time);
            /**
             * through all of the reading and write them to the json ,
             * all of the parameters are splitted by ',' -> name,time,value.
             */
            String[] reads;
            for (String read : readings) {
                reads = read.split(",");
                jsonWriter.name(reads[0]).value(reads[1]);
            }
            /**
             * gps reading - separated with latitude and longitude
             */
            jsonWriter.name(Constants.GPS_TAG);
            jsonWriter.beginObject();
            String[] strings = readings.get(readings.size() - 1).split(",");
            jsonWriter.name("lat").value(strings[1]);
            jsonWriter.name("lon").value(strings[2]);
            jsonWriter.endObject();
            jsonWriter.endObject();
            jsonWriter.flush();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    /**
     * this method is crucial . it writes down the json and ends the json array
     */
    private void endJsonWrite() {
        try {
            jsonWriter.endArray();
            jsonWriter.close();
            this.fileOutputStream.close();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
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
