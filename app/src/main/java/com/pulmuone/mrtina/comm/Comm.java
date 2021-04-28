package com.pulmuone.mrtina.comm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pulmuone.mrtina.utils.Util;

public class Comm {
	public static final String SVR = "TEST"; //LOCAL,TEST,LIVE
	//개발
//	public static final String URL = "http://namrtit.pulmuone.com:9142";
	//운영
	public static final String URL = "http://namrti.pulmuone.com";
	//로컬
//	public static final String URL = "http://192.168.135.69:8080";

	//개발
//	public static final String DL_URL = "http://namrtit.pulmuone.com:9142/download/download.html";
    //운영
	public static final String DL_URL = "http://namrti.pulmuone.com/download/download.html";

	public static final String TCP_ADDRESS = "192.168.132.100";//192.168.132.100
	public static final String TCP_PORT_NUMBER = "9100";//9100,6101

	public static final String[] TCP_ADDR = { "192.168.132.100", "10.111.13.166", "10.111.13.167" };
	public static final String[] TCP_NAME = { "Seoul F4 Printer", "FT1: TEST1 Printer", "FT1: TEST2 Printer" };

	public static final int PO 			= 1;
	public static final int TRO 		= 2;
	public static final int SUBINV 		= 3;
	public static final int LOC 		= 4;
	public static final int LOT 		= 5;

	public static final int BATCH 		= 6;
	public static final int SUBINV_FROM = 7;
	public static final int SUBINV_TO 	= 8;
	public static final int LOC_FROM 	= 9;
	public static final int LOC_TO 		= 10;

	public static final int PALLET 		= 11;
	public static final int TRANPALLET 	= 12;
	public static final int TRANTRO 	= 13;
	public static final int TRANITEM 	= 14;
	public static final int TRANLOT 	= 15;

	public static final int DETAIL 		= 16;
	public static final int ORG 		= 17;
	public static final int MOVE_DTL 	= 18;

	public static final int	CONNECTION_TIME = 30000;
	public static final String LOG_TAG = "mRTI";
	public static final int ACTIVITY_FOR_RESULT_REDISPLAY = 1;
	public static final int ACTIVITY_RECEIVER_RESULT = 2;
    public static final String PACKAGE_INFO = "com.pulmuone.mrtina";

	public static final int DIALOG_VIEW_BLUETOOTH = 99;
	
	public static final int DIALOG_EXIT = 99;
	public static final int DIALOG_SEND_ALERT = 98;
	
	public static final int TOAST_TIME_NOMAL = 5;
	public static final int TOAST_TIME_LONG = 7;
	
	public static final String STATUS = "STATUS";
	public static final int DB_OK = 1;
	public static final int DB_FAIL = 0;
    //블루투스 연결 관련
	public static boolean IS_BT_CONNECT = false;
	public static final int REQUEST_ENABLE_BT = 1;
	public static boolean BT_CONNECT_LOGIN = false;

	public static final int VIBRA = 1;
	public static final int NOTIF = 2;
	public static final int SOUND = 3;
	//
	public static final int ACTIVITYFORRESULT_EMERGENCY_NOTICE = 1;
	
	// QR Queue
	public static LinkedList<String> QRQUEUE = new LinkedList<String>(); 
	
	// 속도체크용
	public static String TIMELOG(long start, long end, String str){
		
		return str+" 총 수행시간 = "+Long.toString((end-start)/1000)+"."+Long.toString((end-start)%1000)+"초";
	}
	
	public static void makeToast(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
	
	public static void makeToast(Context context, String str, int time) {
		Toast.makeText(context, str, time).show();
	}

	public static Bitmap makeQrCode(String str, int width, int height){
		Bitmap bmp = null;
		com.google.zxing.Writer c9 = new QRCodeWriter();
		try {

			String text = new String(str.getBytes("UTF-8"), "ISO-8859-1");
			BitMatrix bm = c9.encode(text, BarcodeFormat.QR_CODE,width, height);
			bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
				}
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}

		return bmp;
	}

    public static Bitmap makeDataMetrix(String str, int width, int height){
        Bitmap bmp = null;
        com.google.zxing.Writer c9 = new DataMatrixWriter();//new QRCodeWriter();
        try {

            String text = new String(str.getBytes("UTF-8"), "ISO-8859-1");
            BitMatrix bm = c9.encode(text, BarcodeFormat.DATA_MATRIX,width, height);
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        return bmp;
    }

	public static boolean isLibraryInstalled(Context ctx, String libraryName) {
		boolean isLibraryInstalled = false;
		if (!Util.isNull(libraryName)) {
			String[] installedLibraries = ctx.getPackageManager().getSystemSharedLibraryNames();
			if (installedLibraries != null) {
				for (String s : installedLibraries) {
					if (libraryName.equals(s)) {
						isLibraryInstalled = true;
						break;
					}
				}
			}
		}
		return isLibraryInstalled;
	}
}
