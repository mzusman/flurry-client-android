package com.mzusman.bluetooth.model;

import com.github.pires.obd.commands.ObdCommand;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public interface Manager {


	void connect(String deviceAddress);

	List<String> getReadings();

	void stop();

	String getReading(String READ);


	HashMap<String, ObdCommand> commandsFactory = null;


	interface Factory {
		void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory);
	}

	UUID uuid = UUID.fromString("667d60d3-981e-41c8-befc-ba931ebaa385");

}
