package com.example.fastscheduleweeks;

/**
 * Created by AllenMBA on 15. 9. 4..
 */
// 참조 : http://ggari.tistory.com/235

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LocalDataBaseManager extends SQLiteOpenHelper {
    private final static String table_name = "schedule";
    public static final String database_name = "localDB.db";
    public static final int database_ver = 1;

    String quary ;

    //constructor
    public LocalDataBaseManager(Context context) {
        super(context, database_name, null, database_ver);

        // 일정에 필요 한게, 날짜, 시간, 요일, 소요시간, 누구랑, 장소, 알림여부, 메모, + a
        quary = "CREATE TABLE "+ table_name  
        		+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT "
                + "," + "target_date TEXT" // 약속일자 
        		+ "," + "regist_date TEXT" // 등록일자 
                + "," + "time TEXT"        // 시간 
                + "," + "day TEXT"         // 요일 
                + "," + "duration TEXT"    // 소요시간 
                + "," + "address TEXT "    // 주소 또는 장소  
                + "," + "withWho TEXT"     // 누구랑 
                + "," + "others TEXT"      // 그외 
                + "," + "buffers TEXT"         // 백업 항목 (이후 추가 될지 몰라서 항목을 포함해둠) 
                + ");";
    }

    //테이블을 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(quary);

    }
    //업그레이드
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
