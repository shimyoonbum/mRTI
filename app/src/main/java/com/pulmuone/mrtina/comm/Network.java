package com.pulmuone.mrtina.comm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulmuone.mrtina.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

public class Network extends AsyncTask<LinkedHashMap<String, String>, Void, String> {

	private ProgressDialog progressDialog;
	private Activity ct;
	private String api;
	private NetworkEvent cb;
	public Network(Activity ct,NetworkEvent cb,String api) {
		this.ct = ct;
		this.cb = cb;
		this.api = api;
	}
	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(this.ct, "", ct.getString(R.string.querying));
	}

	@Override
	protected String doInBackground(LinkedHashMap<String, String>... params) {
/*
		// [[
		ObjectMapper om = new ObjectMapper();
		try {
			String jsonData = om.writeValueAsString(params[0].get("json"));
			URL url = new URL(Comm.URL + api);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			os.write(jsonData.getBytes("utf-8"));
			os.flush();

			StringBuilder sb = new StringBuilder();
			int responseCode = conn.getResponseCode();
			if(responseCode == 200){
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line =  "";

				while((line = br.readLine())!=null){
					sb.append(line);
				}

			}
			return sb.toString();
		}catch(Exception e){
			return "";
		}
		// ]]

 */
		HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+api);
		http.addAllParameters(params[0]);
		HttpClient post = http.create();
		post.request();

		int statusCode = post.getHttpStatusCode();
		Log.v(Comm.LOG_TAG, "statusCode: "+ statusCode);
		String body = post.getBody();
		Log.v(Comm.LOG_TAG, "body: "+ body);
		return body;

	}

	@Override
	protected void onPostExecute(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			if(!obj.getString("status").equals("S") && !obj.getString("status").equals("T")){
				AlertDialog.Builder builder = new AlertDialog.Builder(this.ct)
						.setTitle(ct.getString(R.string.warning))
						.setMessage(ct.getString(R.string.server_error))
						.setPositiveButton(ct.getString(R.string.okay), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
				builder.show();

			}else{
				Log.v(Comm.LOG_TAG, "NETWORK SUCCESS :: " );
				cb.onPostExecute(obj,this.api);
			}
		} catch (JSONException e) {
			Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
		} catch (Exception e1) {
			Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
		}

		progressDialog.dismiss();
	}
}
