package com.pulmuone.mrtina.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.pulmuone.mrtina.R;

import java.text.DecimalFormat;

import static android.content.Context.VIBRATOR_SERVICE;

public class Util {

	public static String nullString(String str,String def){
		if(str == null || str.length() == 0 || str == "null")return def;
		return str;
	}
	
	public static boolean isNull(String str){

        return str == null || str.length() == 0 || str == "null";
    }

	public static String byteArrayToString(byte[] b){
		if(b == null){
			return "";
		}
		String str = "";
		try{
			str = new String(b);
		}catch(Exception e){
			str = "";
		}
		return str;
	}
	
	public static String appVersionName(Context context){
		String version = "1.0";
		try {
			PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		    version = i.versionName;
		} catch(NameNotFoundException e) {
			  
		}
		return version;
	}
	
	public static int stringToInt(String number){
		int num = 0;
		try{
			num = Integer.parseInt(number);
		} catch(Exception e){
			num = 0;
		}
		return num;
	}

	public static boolean checkQty(String chkqty) {
		try{
			if(chkqty.length() == 0 || "0".equals(chkqty)){
				return false;
			}

		}catch (Exception e) {    
			return false;
		}
		return true;
	}
	
	public static String makeComma(String str) {
		if (str.length() == 0)
			return "";
		//long value = Long.parseLong(str);
		float value = Float.valueOf(str).floatValue();
		DecimalFormat format = new DecimalFormat("###,###,###");
		return format.format(value);
	}

	public static String[] subStrLen(String str, int len) {
		String[] array = new String[3];
		int leng = 0;
		for(int i = 0; i < str.length(); i++){
			if (Character.isUpperCase(str.charAt(i)) == true) leng+=16;
			else leng+=10;
		}
		leng = leng/10;
		int diff = len-(leng-str.length());
		if (str.length() > diff) {
			int val = str.length() / diff;
			if (val == 1) {
				array[0] = str.substring(0,diff);
				array[1] = str.substring(diff);
				array[2] = "";
			}else if(val == 2){
				array[0] = str.substring(0,diff);
				array[1] = str.substring(diff,diff*2);
				array[2] = str.substring(diff*2);
			}else if(val > 2){
				array[0] = str.substring(0,diff);
				array[1] = str.substring(diff,diff*2);
				array[2] = str.substring(diff*2,diff*3);
			}else{
				array[0] = str;
				array[1] = "";
				array[2] = "";
			}

			return array;
		}else{
			array[0] = str;
			array[1] = "";
			array[2] = "";
			return array;
		}
	}

	public static String makePeriod(String str) {
		if (str.length() == 0)
			return "";
		//long value = Long.parseLong(str);
		float value = Float.valueOf(str).floatValue();
		DecimalFormat format = new DecimalFormat("0.##");
		return format.format(value);
	}	
	
	public static void hideInput(Context context,EditText mEdit){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEdit.getWindowToken(),0);   
	}
	
	public static void showInput(Context context,EditText mEdit){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mEdit, 0);
	}

	//키보드 감추기 so.hwang
	public static void hideKeyboard(Activity activity){
		InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void alertAnim(LinearLayout layout){
		ObjectAnimator anim = ObjectAnimator.ofArgb(layout,"backgroundColor", Color.RED,Color.WHITE);
		anim.setDuration(700);
		anim.setRepeatCount(5);
		//anim.setRepeatMode(ValueAnimator.REVERSE);
		anim.start();
	}

	public static void alertNoti(Context ct,int val){

		if(val == 1){
			Vibrator vib = (Vibrator)ct.getSystemService(VIBRATOR_SERVICE);
			vib.vibrate(100);
		}else if(val == 2){
			Uri noti = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone ringtone = RingtoneManager.getRingtone(ct,noti);
			ringtone.play();
		}else if(val == 3){
			MediaPlayer player = MediaPlayer.create(ct, R.raw.error2);
			//MediaPlayer player = MediaPlayer.create(ct, R.raw.error2);
			player.start();
		}
	}

	public static String labelPalletNet(String palletNum,String itemCd,String itemNm,String lotNum,String expireDate,String batchNo){
		String[] itemDesc = Util.subStrLen(itemNm,26);

		String label =
				"^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI27^PA0,1,1,0^XZ\n" +
				"^XA^MMT^PW799^LL2004^LS0\n" +
				"^FT75,1972^A@B,31,31,TT0003M_^FH\\^CI28^FDItem^FS^CI27\n" +
				"^FT249,1971^A@B,31,31,TT0003M_^FH\\^CI28^FDItem Description^FS^CI27\n" +
				"^FT605,1971^A@B,31,31,TT0003M_^FH\\^CI28^FDLot^FS^CI27\n" +
				"^FT763,1199^A@B,31,31,TT0003M_^FH\\^CI28^FDExpiration Date^FS^CI27\n" +
				"^FT763,1971^A@B,31,31,TT0003M_^FH\\^CI28^FDBatch No^FS^CI27\n" +
				"^FT693,1972^A@B,31,31,TT0003M_^FH\\^CI28^FDPallet No^FS^CI27\n" +
				"^FT714,1818^A@B,85,85,TT0003M_^FH\\^CI28^FD^"+palletNum+"^FS^CI27\n" +
				"^FT213,1834^A@B,203,204,TT0003M_^FH\\^CI28^FD"+itemCd+"^FS^CI27\n" +
				"^FT336,1961^A@B,102,101,TT0003M_^FH\\^CI28^FD"+itemDesc[0]+"^FS^CI27\n" +
				"^FT621,1818^A@B,85,85,TT0003M_^FH\\^CI28^FD"+lotNum+"^FS^CI27\n" +
				"^FT763,961^A@B,34,34,TT0003M_^FH\\^CI28^FD"+expireDate+"^FS^CI27\n" +
				"^FT763,1818^A@B,34,34,TT0003M_^FH\\^CI28^FD"+batchNo+"^FS^CI27\n" +
				"^FT436,1961^A@B,102,103,TT0003M_^FH\\^CI28^FD"+itemDesc[1]+"^^FS^CI27\n" +
				"^FT742,45^BXI,49,200,0,0,1,_,1\n" +
				"^FH\\^FD"+palletNum+"^FS\n" +
				"^FT537,1961^A@B,102,103,TT0003M_^FH\\^CI28^FD"+itemDesc[2]+"^FS^CI27\n" +
				"^PQ1,0,1,Y^XZ";
/*

		String label =
				"^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ\n" +
				"^XA^MMT^PW799^LL2004^LS0\n" +
				"^FT75,1972^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDItem^FS^CI0\n" +
				"^FT249,1971^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDItem Description^FS^CI0\n" +
				"^FT589,1971^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDLot^FS^CI0\n" +
				"^FT758,1204^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDExpiration Date^FS^CI0\n" +
				"^FT758,1546^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDBatch No^FS^CI0\n" +
				"^FT758,1962^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDPallet No^FS^CI0\n" +
				"^FT762,1830^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+palletNum+"^FS^CI0\n" +
				"^FT213,1834^A@B,203,204,TT0003M_^FH\\^CI17^F8^FD"+itemCd+"^FS^CI0\n" +
				"^FT334,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[0]+"^FS^CI0\n" +
				"^FT714,1969^A@B,135,137,TT0003M_^FH\\^CI17^F8^FD"+lotNum+"^FS^CI0\n" +
				"^FT762,1020^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+expireDate+"^FS^CI0\n" +
				"^FT762,1414^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+batchNo+"^FS^CI0\n" +
				"^FT435,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[1]+"^FS^CI0\n" +
				"^FT536,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[2]+"^FS^CI0\n" +
				"^BY686,686^FT742,45^BXI,49,200,0,0,1,~\n" +
				"^FH\\^FD"+palletNum+"^FS^PQ1,0,1,Y^XZ";
*/

		return label;
	}

	public static String labelPalletBlt(String palletNum){
		String label =
				"^XA^POI^PW350^MNN^LL141^LH0,0\n" +
				"^FO0,20^BY2^BCN,70,Y,N,N\n" +
				"^FD" + palletNum + "^FS^XZ";
		return label;
	}

	public static String labelLocatorNet(String barCode,String subinvCd,String locatorCd){
		String label =
				"^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ\n" +
				"^XA^MMT^PW799^LL2004^LS0\n" +
				"^BY760,760^FT780,786^BXB,38,200,0,0,1,~\n" +
				"^FH\\^FD" + barCode + "^FS\n" +
				"^FT394,1911^A@B,135,135,TT0003M_^FH\\^CI17^F8^FD" + subinvCd + "^FS^CI0\n" +
				"^FT665,1947^A@B,226,227,TT0003M_^FH\\^CI17^F8^FD" + locatorCd.substring(0,8) + "^FS^CI0\n" +
				"^PQ1,0,1,Y^XZ";
		return label;
	}

	public static String labelLocatorNetS(String barCode1,String locatorCd1, String barCode2,String locatorCd2){

		String label =	"^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ\n" +
						"^XA^MMT^PW799^LL1421^LS0\n";
		if(!barCode1.equals("")) {
			label = label + "^BY2,3,112^FT641,434^BCR,,N,N\n" +
							"^FD" + barCode1 + "^FS\n";
		}
		if(!barCode2.equals("")) {
			label = label + "^BY2,3,112^FT121,434^BCR,,N,N\n" +
							"^FD" + barCode2 + "^FS\n";
		}
		label = label +	"^FT577,442^A@R,73,74,TT0003M_^FH\\^CI17^F8^FD"+locatorCd1+"^FS^CI0\n" +
						"^FT53,442^A@R,73,74,TT0003M_^FH\\^CI17^F8^FD"+locatorCd2+"^FS^CI0\n" +
						"^PQ1,0,1,Y^XZ";
		return label;
	}

	public static String labelLocatorBlt(String barCode){
		String label =
				"^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ\n" +
				"^XA^MMT^PW464^LL0144^LS0^BY4,6^FT439,45^B7I,6,2,,,N\n" +
				"^FH\\^FD"+barCode+"^FS\n" +
				"^FT355,23^A@I,14,13,TT0003M_^FH\\^CI17^F8^FD"+barCode+"^FS^CI0\n" +
				"^PQ1,0,1,Y^XZ";
		return label;
	}
}
