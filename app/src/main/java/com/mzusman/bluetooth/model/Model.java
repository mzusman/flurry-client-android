package com.mzusman.bluetooth.model;

import java.util.List;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public class Model {
	Manager manager;

	private static Model instance = new Model();

	public static Model getInstance() {
		return instance;
	}

	public void setManager(Manager manager, String deviceAddress) {

		this.manager = manager;
//		manager.connect(deviceAddress);
	}

	public Manager getManager() {

		return this.manager;
	}

	public List<String> getReading() {

		return manager.getReadings();
	}

	public String getRead(String READINGS) {

		return manager.getReading(READINGS);
	}

	public void drop() {
		manager.stop();
	}


}
