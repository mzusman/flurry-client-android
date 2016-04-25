package com.mzusman.bluetooth.fragments;

import android.app.Activity;
import android.location.LocationListener;
import android.widget.ListView;

import com.mzusman.bluetooth.utils.DetailsTask;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by mzeus on 4/25/16.
 */
public class DetailsTaskTest {

    @Test
    public void testRun() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        LocationListener locationListener = Mockito.mock(LocationListener.class);
        ListView listView = Mockito.mock(ListView.class);

        DetailsTask task = new DetailsTask(locationListener,activity,listView);
        task.run();

    }

    @Test
    public void testStopRunning() throws Exception {

    }
}