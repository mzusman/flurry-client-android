package com.mzusman.bluetooth.model;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by mzeus on 4/25/16.
 */
public class WifiManagerTest {

    SpeedCommand speedCommand = Mockito.mock(SpeedCommand.class);
    HashMap commandFactory = new HashMap();
    HashMap spy = spy(commandFactory);

    WifiManager wifiManager = new WifiManager(new Manager.Factory() {
        @Override
        public void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory) {
            commandsFactory.put("speed", new SpeedCommand());
        }
    });


    @Test
    public void testGetReadings() throws Exception {

        doReturn(speedCommand).when(spy).get("speed");
        when(speedCommand.getFormattedResult()).thenReturn(null);
//        when(Manager.commandsFactory.keySet().iterator().next()).thenReturn(Constants.REQUEST_SPEED_READING);
//        when(Long.toString(anyLong())).thenReturn("0");

        assertNotNull(wifiManager.getReadings());

//        when(wifiManager.readings.add(anyString())).thenReturn()

    }

    @Rule
    public ExpectedException exceptedException = ExpectedException.none();

    @Test(expected=NullPointerException.class)
    public void testGetReading() throws Exception {//without sockets
        doReturn(speedCommand).when(spy).get("speed");
        when(speedCommand.getFormattedResult()).thenReturn("");

        assertEquals("speed" + "," + System.currentTimeMillis() + "," + "0", wifiManager.getReading("speed"));


    }

    @Test
    public void testConnect() throws Exception {



    }


    @Test
    public void testStop() throws Exception {

    }

}