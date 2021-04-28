package com.pulmuone.mrtina.comm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ServerRequest extends Thread{
	private HttpClient http = null;
	private HttpPost post = null;
	private String url = null;
	
	private ResponseHandler<String> mResHandler = null;
	private Handler mHandler = null;
	
	private HashMap<Object, Object> param = null;	
	
	/**
	 * @param url
	 * @param param
	 * @param mResHandler
	 */
	public ServerRequest(String url ,HashMap<Object, Object> param , ResponseHandler<String> mResHandler , Handler mHandler){
		this.url = url;
		this.param = param;
		this.mResHandler = mResHandler;
		this.mHandler = mHandler;
	}
	
	public void run() {

		try{
			http = new DefaultHttpClient();
			HttpParams params = http.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			
			Log.v(Comm.LOG_TAG,"RUN URL:: "+url);
			post = new HttpPost(url);
			setParameter(param);
			http.execute(post , mResHandler);
			Log.v(Comm.LOG_TAG,"execute");
			
		}catch (Exception e) {
			Log.v(Comm.LOG_TAG,"ERROR RUN: "+e);
			Message message = mHandler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putString("RESULT", "ERROR");
			message.setData(bundle);
			mHandler.sendMessage(message);

		}
	}
	

	public void setParameter(HashMap<Object , Object> param) throws UnsupportedEncodingException{
		if(param == null){
			Log.d(Comm.LOG_TAG,"Grid Null");
			return ;
		}
		List<NameValuePair> nameValueParis = null;	
		
		String hashKey = null;
		Iterator<Object> iter = null;	
		nameValueParis = new ArrayList<NameValuePair>();
		
		iter = param.keySet().iterator();
		
		while(iter.hasNext()){
			hashKey = (String)iter.next();
			
			nameValueParis.add(new BasicNameValuePair(hashKey , param.get(hashKey).toString()));
		}
		UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(nameValueParis, "UTF-8");
		post.setEntity(entityRequest);
	}
}

