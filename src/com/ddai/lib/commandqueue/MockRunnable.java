package com.ddai.lib.commandqueue;

import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public interface MockRunnable {

	String getFlag();

	void addListener(ArrayList<ICommandHandler> listener);

	ArrayList<ICommandHandler> getListener();

	void run() throws ClientProtocolException, JSONException, Exception;

	void timeoutCallback();
}
