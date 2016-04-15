package com.mzusman.bluetooth.model;

import com.github.pires.obd.commands.ObdCommand;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public interface Manager {


	public void connect(String deviceAddress);

	public List<String> getReadings();

	public void stop();

	public String getReading(String READ);


	static HashMap<String, ObdCommand> commandsFactory = null;


	interface Factory {
		public void setCommandsFactory(
				HashMap<String, ObdCommand> commandsFactory);
	}


}
