package com.mzusman.bluetooth.model;

import android.util.Log;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.mzusman.bluetooth.utils.Constants;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public class WifiManager implements Manager {

	ArrayList<String> readings = new ArrayList<>();
	long              time     = System.currentTimeMillis();
	Socket socket;

	public WifiManager(Factory factory) {
		factory.setCommandsFactory(commandsFactory);
	}

	@Override public void connect(String address) {
		try {
			socket = new Socket(address.split(",")[0], Integer.parseInt(address.split(",")[1]));

			new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
			new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
			new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
			new SelectProtocolCommand(ObdProtocols.AUTO)
					.run(socket.getInputStream(), socket.getOutputStream());

		}
		catch (IOException e) {
			e.printStackTrace();
			Log.d(Constants.RUN_TAG, "getReadings Interrupt");
		}
		catch (InterruptedException e) {
			e.printStackTrace();
			Log.d(Constants.RUN_TAG, "getReadings Interrupt");
		}

	}

	@Override public List<String> getReadings() {

		if (readings.size() > 0) readings.clear();
		try {
			for (String command : commandsFactory.keySet()) {
				ObdCommand obdCommand = commandsFactory.get(command);

				obdCommand.run(socket.getInputStream(), socket.getOutputStream());
				readings.add(command + "," + Long.toString(time) + "," +
							 obdCommand.getFormattedResult());
			}

			return readings;

		}
		catch (InterruptedException e) {
			e.printStackTrace();
			Log.d(Constants.RUN_TAG, "getReadings Interrupt");
		}
		catch (IOException e) {
			e.printStackTrace();
			Log.d(Constants.IO_TAG, "getReadings IO Error");
		}


		return null;
	}

	@Override public void stop() {

	}

	@Override public String getReading(String READ) {

		time = System.currentTimeMillis();
		ObdCommand command = commandsFactory.get(READ);

		try {
			command.run(socket.getInputStream(), socket.getOutputStream());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}


		return READ + "," + Long.toString(time) + "," + command.getFormattedResult();
	}

}
