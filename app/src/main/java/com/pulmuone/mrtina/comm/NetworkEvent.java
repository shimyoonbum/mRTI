package com.pulmuone.mrtina.comm;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public interface NetworkEvent {
	void doInBackground(LinkedHashMap<String, String>... params);
	void onPostExecute(JSONObject obj,String api);
}