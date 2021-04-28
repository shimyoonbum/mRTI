package com.pulmuone.mrtina.comm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKManager.FEATURE_TYPE;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.BarcodeManager.ConnectionState;
import com.symbol.emdk.barcode.BarcodeManager.ScannerConnectionListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.ScanDataCollection.ScanData;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.Scanner.TriggerType;
import com.symbol.emdk.barcode.StatusData.ScannerStates;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Scan implements EMDKListener, DataListener, StatusListener, ScannerConnectionListener{
	private Context ct;
	private MyEvent cb;
	private EMDKManager emdkManager = null;
	private BarcodeManager barcodeManager = null;
	private Scanner scanner = null;
	private String statusString = "";
	private final Object lock = new Object();
	private boolean bExtScannerDisconnected = false;
	private List<ScannerInfo> deviceList = null;
	private int defaultIndex = 0; // Keep the default scanner

	public Scan(Activity ct) {
		this.ct = ct;

		EMDKResults results = EMDKManager.getEMDKManager(ct, this);
		if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
			updateStatus("EMDKManager object request failed!");
			return;
		}
	}

	public void setEvent(MyEvent ev) {
		this.cb = ev;
	}

	@Override
	public void onOpened(EMDKManager emdkManager) {
		updateStatus("EMDK open success!");
		this.emdkManager = emdkManager;
		initBarcodeManager();
		enumerateScannerDevices();
		initScanner();
		setDecoders();
	}

	public void onResume() {
		if (emdkManager != null) {
			initBarcodeManager();
			enumerateScannerDevices();
			initScanner();
		}
	}

	public void onPause() {
		deInitScanner();
		deInitBarcodeManager();
	}

	@Override
	public void onClosed() {
		if (emdkManager != null) {
			emdkManager.release();
			emdkManager = null;
		}
		updateStatus("EMDK closed unexpectedly! Please close and restart the application.");
	}

	public void onDestroy() {
		if (emdkManager != null) {
			emdkManager.release();
			emdkManager = null;
		}
	}

	@Override
	public void onData(ScanDataCollection scanDataCollection) {
		if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
			ArrayList <ScanData> scanData = scanDataCollection.getScanData();
			for(ScanData data : scanData) {
				//updateData(data.getLabelType() + " : " + data.getData());
				cb.onEvent(data.getData());
			}
		}
	}

	@Override
	public void onStatus(StatusData statusData) {
		ScannerStates state = statusData.getState();
		switch(state) {
			case IDLE:
				statusString = statusData.getFriendlyName()+" is enabled and idle...";
				updateStatus(statusString);

				if(!scanner.isReadPending() && !bExtScannerDisconnected) {
					try {
						scanner.read();
					} catch (ScannerException e) {
						updateStatus(e.getMessage());
					}
				}
				break;

			case WAITING:
				statusString = "Scanner is waiting for trigger press...";
				updateStatus(statusString);
				break;
			case SCANNING:
				statusString = "Scanning...";
				updateStatus(statusString);
				break;
			case DISABLED:
				statusString = statusData.getFriendlyName()+" is disabled.";
				updateStatus(statusString);
				break;
			case ERROR:
				statusString = "An error has occurred.";
				updateStatus(statusString);
				break;
			default:
				break;
		}
	}

	@Override
	public void onConnectionChange(ScannerInfo scannerInfo, ConnectionState connectionState) {
		String statusExtScanner = connectionState.toString();
		String scannerNameExtScanner = scannerInfo.getFriendlyName();

		switch(connectionState) {
			case CONNECTED:
				synchronized (lock) {
					initScanner();
					bExtScannerDisconnected = false;
				}
				break;
			case DISCONNECTED:
				bExtScannerDisconnected = true;
				synchronized (lock) {
					deInitScanner();
				}
				break;
		}
		updateStatus(scannerNameExtScanner + ":" + statusExtScanner);

	}

	private void initScanner() {
		if (scanner == null) {
			if (barcodeManager != null) {
				//scanner = barcodeManager.getDevice(deviceList.get(scannerIndex));
				scanner = barcodeManager.getDevice(barcodeManager.getSupportedDevicesInfo().get(defaultIndex));
			}
			if (scanner != null) {
				scanner.addDataListener(this);
				scanner.addStatusListener(this);
				try {
					scanner.enable();
				} catch (ScannerException e) {
					updateStatus(e.getMessage());
					deInitScanner();
				}
			}else{
				updateStatus("Failed to initialize the scanner device.");
			}
		}
	}

	private void deInitScanner() {
		if (scanner != null) {
			try{
				scanner.disable();
			} catch (Exception e) {
				updateStatus(e.getMessage());
			}

			try {
				scanner.removeDataListener(this);
				scanner.removeStatusListener(this);
			} catch (Exception e) {
				updateStatus(e.getMessage());
			}

			try{
				scanner.release();
			} catch (Exception e) {
				updateStatus(e.getMessage());
			}
			scanner = null;
		}
	}

	private void initBarcodeManager(){
		barcodeManager = (BarcodeManager) emdkManager.getInstance(FEATURE_TYPE.BARCODE);
		// Add connection listener
		if (barcodeManager != null) {
			barcodeManager.addConnectionListener(this);
		}
	}

	private void deInitBarcodeManager(){
		if (emdkManager != null) {
			emdkManager.release(FEATURE_TYPE.BARCODE);
		}
	}

	private void setDecoders() {
		if (scanner != null) {
			try {
				ScannerConfig config = scanner.getConfig();
				config.decoderParams.ean8.enabled = true;
				config.decoderParams.ean13.enabled = true;
				config.decoderParams.code39.enabled= true;
				config.decoderParams.code128.enabled = true;
				scanner.setConfig(config);
				scanner.triggerType = TriggerType.HARD;
			} catch (ScannerException e) {
				updateStatus(e.getMessage());
			}
		}
	}

	private void enumerateScannerDevices() {
		if (barcodeManager != null) {
			List<String> friendlyNameList = new ArrayList<String>();
			int spinnerIndex = 0;
			deviceList = barcodeManager.getSupportedDevicesInfo();
			if ((deviceList != null) && (deviceList.size() != 0)) {
				Iterator<ScannerInfo> it = deviceList.iterator();
				while(it.hasNext()) {
					ScannerInfo scnInfo = it.next();
					friendlyNameList.add(scnInfo.getFriendlyName());
					if(scnInfo.isDefaultScanner()) {
						defaultIndex = spinnerIndex;
					}
					++spinnerIndex;
				}
			}
			else {
				updateStatus("Failed to get the list of supported scanner devices! Please close and restart the application.");
			}
		}
	}

	private void updateStatus(final String status){
        //new AsyncStatusUpdate().execute("stat:"+status);
    }

	private void updateData(final String result){
        //new AsyncStatusUpdate().execute("data:"+result);
	}

    private class AsyncStatusUpdate extends AsyncTask<String, Void, String> {

        @Override
        public String doInBackground(String... params) {

            return params[0];
        }

        @Override
        public void onPostExecute(String result) {
            Log.v(Comm.LOG_TAG, "status:"+result);
            Comm.makeToast(ct, result);
        }
    }
}
