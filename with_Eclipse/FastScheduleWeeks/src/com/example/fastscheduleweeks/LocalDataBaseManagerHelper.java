package com.example.fastscheduleweeks;



/**
 * Created by AllenMBA on 15. 9. 4..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;


public class LocalDataBaseManagerHelper {
    private LocalDataBaseManager mDBManager;
    private SQLiteDatabase db;

    public LocalDataBaseManagerHelper (Context context){
        this.mDBManager = new LocalDataBaseManager(context);
    }
    //닫기
    public void close() {
        db.close();
    }
    //저장
    public void insert (String target_date, 
    					String regist_date, 
    					String v_time, 
    					String v_day,
    					String duration,
    					String address,
    					String whoData,
    					String others
    					) 
    {
        db = mDBManager.getWritableDatabase();
        ContentValues val = new ContentValues();
        
        val.put("target_date", target_date);
        val.put("regist_date", regist_date);
        val.put("time",v_time);
        val.put("day",v_day);
        val.put("duration", duration);
        val.put("address", address);
        val.put("withWho", whoData);
        val.put("others", others);

        db.insert("schedule", null, val);
    }
    
    //전체 가저오기
    public Cursor select(){
        
    	db = mDBManager.getReadableDatabase();
        String[] columns = {"_id", "target_date", "regist_date", "time", "day", "duration", "address","withWho","others"};
        
        Cursor cursor = db.query("schedule", columns, null, null, null, null, null);
        return cursor;
    }
    
    // 특정일 정보 가져 오기 
    public Cursor getObject(String target_date) {
    	db = mDBManager.getReadableDatabase();
    	String[] columns = {"_id","target_date", "regist_date", "time", "day", "duration", "address", "withWho","others"};
        String whereStr = "where target_date == ?";
        String[] whereDiffStr = {target_date};
        Cursor cursor = db.query("schedule", columns, whereStr, whereDiffStr, null, null, null);
        //db.close();
    	return cursor;
    }
    
 // 특정일 정보 가져 오기 
    public Cursor getTargetObject(String target_date, String target_time) {
    	db = mDBManager.getReadableDatabase();
    	String[] columns = {"_id","target_date", "regist_date", "time"};
        String whereStr = "target_date == ? AND time == ?";
        String[] whereDiffStr = {target_date, target_time};
        Cursor cursor = db.query("schedule", columns, whereStr, whereDiffStr, null, null, null);
    	//db.close();
    	return cursor;
    }
    
    public int getTargetDayNTime(String target_date, String target_time) {
    	Log.i("DBSection", "Parameter : " + target_date + " , target_time : "+ target_time);
    	Cursor tempCursor = getTargetObject(target_date, target_time);
    	Log.i("responce count is", "[ value : " + tempCursor.getCount() + " ]");
    	return tempCursor.getCount();
    }
    
    public Vector<StringBuffer> returnAllData() {
    	Vector<StringBuffer> dataArray = new Vector<StringBuffer>();
    	
    	Cursor tempCursor = select();
    	
    	if (tempCursor.getCount() < 1) {
    		return null;
    	}
    	
    	int resCursorCount = tempCursor.getCount();
    	
    	for (int i=0; i<resCursorCount;i++) {
    		tempCursor.moveToNext();
    		// id
    		StringBuffer resBufferStr = new StringBuffer(tempCursor.getString(0));
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(1)); // target_date
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(2)); // regist_date
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(3)); // time
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(4)); // day
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(5)); // duration
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(6)); // address
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(7)); // who???
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(8)); // others 
    		
    		dataArray.addElement(resBufferStr);
    	}
    	
    	return dataArray;
  
    }
    
    public Vector<StringBuffer> returnDataFromTargetDate(String target_date) {
    	Vector<StringBuffer> dataArray = new Vector<StringBuffer>();
    	
    	Cursor tempCursor = getObject(target_date);
    	if (tempCursor.getCount() < 1) {
    		return null;
    	}
    	
    	int resCursorCount = tempCursor.getCount();
    	
    	for (int i=0; i<resCursorCount;i++) {
    		tempCursor.moveToNext();
    		// id
    		StringBuffer resBufferStr = new StringBuffer(tempCursor.getString(0));
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(1)); // target_date
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(1)); // regist_date
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(2)); // time
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(3)); // day
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(4)); // duration
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(5)); // address
    		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(6)); // whoData
    		   		
    		resBufferStr.append("|");
    		resBufferStr.append(tempCursor.getString(7)); // others 
    		
    		dataArray.add(resBufferStr);
    	}
    	
    	return dataArray;
    }
}


/**  업데이트방법
 *     public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {}
 *
 * 삭제방법
 *     public int delete(String table, String whereClause, String[] whereArgs) {}
 */