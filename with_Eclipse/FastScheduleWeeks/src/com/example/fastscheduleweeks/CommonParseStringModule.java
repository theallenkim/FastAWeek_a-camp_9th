package com.example.fastscheduleweeks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class CommonParseStringModule {
	public String getParseFromDateString(String lowData) {
		//parseItem4(String dateStr, String timeStr, String whereStr, String whoStr) {
	
    	Calendar c = Calendar.getInstance(); 
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	
    	// 날짜 분석 부분 
    	String dateStringValue = null;
    	if (lowData.trim().equals("내일") == true) {
    		long now = System.currentTimeMillis();
    		Date date = new Date(now);

    		//[date 날짜에서 하루 이전 날짜로 설정하기] 
    		date.setDate(date.getDate()+1);
    		dateStringValue = df.format(date);
    	} else if (lowData.trim().equals("모레") == true || lowData.trim().equals("모래") == true) {
    		long now = System.currentTimeMillis();
    		Date date = new Date(now);
    		//[date 날짜에서 하루 이전 날짜로 설정하기] 
    		date.setDate(date.getDate()+2);
    		dateStringValue = df.format(date);
    	} else {
    		dateStringValue = lowData;
    	}
    	
    	return dateStringValue;
	}
	
	 // 시간 분석 부분...
	public String getParseFromTimeString(String lowData) {
	   	String timeStringValue = "시간 인식 불가";
	   	if (lowData.indexOf("시") > 0) {
	   		timeStringValue = lowData;
	   	}
	    return timeStringValue;
	}
	 
	// 장소 문자열 분석 부분.
	// 장소는 끝에 "에" 또는 "으로" 로 해야 함. 
	public String getParseFromWhereString(String lowData) {
		Log.i("MyActivity", lowData + "||"+ String.valueOf(lowData.indexOf("에")) +"||" + String.valueOf(lowData.length()-1) );
	    String whereStringValue = "장소 인식 불가"; 
	    if (lowData.indexOf("에") >= (lowData.length()-1)) {
	    	whereStringValue = lowData.substring(0, (lowData.length()-1)); 
	    } else if (lowData.indexOf("으로") >= (lowData.length()-2)) {
	    	whereStringValue = lowData.substring(0, (lowData.length()-2));
	    }
	    return whereStringValue;
	}
	
	// 누구랑 분석 부분.... "누구와는 "와" 나 "랑"이 들어가야함 
	public String getParseFromWithWhoString(String lowData) {
		String whoStringValue = "참여자 인식 불가";
		
		// validation Check
		if (lowData == null) {
			return whoStringValue;
		}
		if (lowData.length() < 2) {
			return whoStringValue;
		}
		
		// parsing
		if (lowData.indexOf("이랑") >= (lowData.length()-2)) {
			whoStringValue = lowData.substring(0, (lowData.length()-2));
		} else if (lowData.indexOf("랑") >= (lowData.length()-1) || lowData.indexOf("와") >= (lowData.length()-1)) {
    		whoStringValue = lowData.substring(0, (lowData.length()-1));
    	}
    	return whoStringValue;
	}
}
