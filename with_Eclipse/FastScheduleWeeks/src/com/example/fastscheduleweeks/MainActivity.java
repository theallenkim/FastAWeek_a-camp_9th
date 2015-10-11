package com.example.fastscheduleweeks;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.example.fastscheduleweeks.UIComponentTestActivity.TimeTableAdapter;
import com.googlecode.tesseract.android.TessBaseAPI;

// 2015.09.11 added by allen.
// http://www.devblog.kr/r/8y0gFPAvJ2j8MWIVVXucyP9uYvQegfSVbY5XM3yK4 //그리드뷰 
/* In Korean.
 * 앱의 기본 화면 클래스이다.
 * 지원하는 기능은 다음과 같다.
 * 1. 알림바 영역 핫키 버튼 리스트 
 * 2. 메인 화면 
 * 
 */

public class MainActivity extends Activity {


	DisplayMetrics mMetrics;
	BroadcastReceiver buttonBroadcastReceiver;
	LocalDataBaseManagerHelper localDBMH;
    
	Vector<StringBuffer> loadDatas;
	
	Calendar c = Calendar.getInstance(); 
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
//	String target_date = dateTextBox.getText().toString();
//	String regist_date = df.format(c.getTime());
//	
	private GridView.OnItemClickListener gridviewOnItemClickListener = 
			new GridView.OnItemClickListener() {
		
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Log.i("Test", "Test message!!!!!");
		}
	};
	
	public class TimeTableAdapter extends BaseAdapter {
		private Context mContext;
		
		
		public TimeTableAdapter(Context c) {
			mContext = c;
		}
		
		public int getCount() {
			return 192; // 가로 8, 세로 24 
		}
		
		public Object getItem(int position) {
			Object retObject = null;
			String today = df.format(c.getTime());
			int tempInt = (position % 8);
			if (tempInt == 0) {
				int sub = (position / 8);
				return (String.valueOf(sub) + ":00");
			} else {
				int restValue = position / 8;
				switch(tempInt) {
					case 1: { // today
						retObject = getSubItem(today, restValue);
					} break;
					case 2: { // 내일 
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.add(Calendar.HOUR_OF_DAY, 24);
						String day = df.format(tempCalendar.getTime());
						retObject = getSubItem(day, restValue);
					}
					break;
					case 3: { // 그다음 
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.add(Calendar.HOUR_OF_DAY, 48);
						String day = df.format(tempCalendar.getTime());
						retObject = getSubItem(day, restValue);
					}
					break;
					case 4: { // 3일 뒤 
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.add(Calendar.HOUR_OF_DAY, 60);
						String day = df.format(tempCalendar.getTime());
						retObject = getSubItem(day, restValue);
					}
					break;
					case 5: { // 4일 뒤 
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.add(Calendar.HOUR_OF_DAY, 84);
						String day = df.format(tempCalendar.getTime());
						retObject = getSubItem(day, restValue);
					}
					break;
					case 6: { // 5일 뒤 
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.add(Calendar.HOUR_OF_DAY, 108);
						String day = df.format(tempCalendar.getTime());
						retObject = getSubItem(day, restValue);
					}
					break;
					case 7: { // 6일 뒤 
						Calendar tempCalendar = Calendar.getInstance();
						tempCalendar.add(Calendar.HOUR_OF_DAY, 132);
						String day = df.format(tempCalendar.getTime());
						retObject = getSubItem(day, restValue);
					}
					break;
				}
			}
			return retObject;
		}
		
		public Object getSubItem(String targetDay,int position) {
			Log.i("GetSubItemValue", "Target day = " + targetDay + ", position " + position);
			String convertTimeString = new String(position + ":00");
			int retCount = localDBMH.getTargetDayNTime(targetDay, convertTimeString);
			//Log.i("Ret step test", "[ret value : " + ret)		
			if (retCount == 0) {
				return "";
			}
			return String.valueOf(retCount);
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			int rowWidth = (mMetrics.widthPixels) / 8;
			
			Button subButton = null;
			
			if (convertView == null) {
				LayoutParams param = new GridView.LayoutParams(rowWidth, rowWidth);
				subButton = new Button(mContext);
				subButton.setLayoutParams(param);
				subButton.setBackgroundColor(0xFFEEEEFF);
				subButton.setPadding(1, 1, 1, 1);
			
			} else {
				subButton = (Button)convertView;
			}
			
			if (position % 8 == 0) {
				subButton.setBackgroundColor(0xFF474545);
				subButton.setTextColor(0xFFFFFFFF);
			} else {
				subButton.setBackgroundColor(0xFFEEEEFF);
				subButton.setTextColor(0xFF777777);
			}
			String retStr = getItem(position).toString();
			Log.i("Value", "[ " + position + " , " + retStr + " ]");
			subButton.setText("" + retStr);
			
			return subButton;
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRemoteView();
        loadLocalDatabaseData();
        
        findViewById(R.id.onVoiceRec).setOnClickListener(mClickListener);
        findViewById(R.id.getClipBoard).setOnClickListener(mClickListener);
        findViewById(R.id.onScreenShot).setOnClickListener(mClickListener);
        findViewById(R.id.onCameraShot).setOnClickListener(mClickListener);
        
        GridView localGridView = (GridView)findViewById(R.id.gridView);
		localGridView.setAdapter(new TimeTableAdapter(this));
		localGridView.setOnItemClickListener(gridviewOnItemClickListener);
		
		mMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        
		
        //TessBaseAPI baseApi = new TessBaseAPI();
    }
    
    public void loadLocalDatabaseData() {
    	localDBMH = new LocalDataBaseManagerHelper(this);
    	loadDatas = localDBMH.returnAllData();
    	if (loadDatas == null) {
    		return;
    	}
    	for (int i=0; i<loadDatas.size();i++) {
    		Log.i("LocalDatais:", "[- " + loadDatas.elementAt(i).toString() + " - ]");
    		// [- 2015년10월04일|2015-10-02|14:00|day|1|강남역|영어샘|no others - ]
    		//	
    	}	
    }
    
// 클립보드 데이터 가져오는 코드 .
//    protected void showClipData() {
//        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//
//        ClipData cd = cm.getPrimaryClip();
//
//        if(cd != null){
//            ClipData.Item item = cd.getItemAt(0);
//            textView.setText("" + item.getText());
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
            	//case R.id.overay_button_voice:
                case R.id.onVoiceRec: {
                    //recodingVoice();
                    Intent voiceInputIntent = new Intent(MainActivity.this, VoiceInputActivity.class);
                    startActivity(voiceInputIntent);
                }break;

                case R.id.getClipBoard: {
                    //showClipData();
                	Intent clipboardInputIntent = new Intent(MainActivity.this, ClipBoardInputActivity.class);
                    startActivity(clipboardInputIntent);
                } break;
                
                case R.id.onCameraShot : {
                	Intent cameraShotInputIntent = new Intent(MainActivity.this, CameraInputActivity.class);
                	startActivity(cameraShotInputIntent);
                } break;

                case R.id.onScreenShot: {
                   // setRemoteView();;
                	Intent testUIIntent = new Intent(MainActivity.this, UIComponentTestActivity.class);
                	startActivity(testUIIntent);
                } break;
                
                case R.id.remote_onVoiceRec: {
                    Intent voiceInputIntent = new Intent(MainActivity.this, VoiceInputActivity.class);
                    startActivity(voiceInputIntent);
                } break;
                
                case R.id.remote_onCamera: {
                	Intent cameraShotInputIntent = new Intent(MainActivity.this, CameraInputActivity.class);
                	startActivity(cameraShotInputIntent);
                } break;
            }
        }
    };

    public void startServiceMoveWidget() {
    	ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    	List<RunningServiceInfo> procInfos = am.getRunningServices(100);
    	int flag = 0;
        for (int i = 0; i < procInfos.size(); i++) {
        	Log.i("check process", "[" + procInfos.get(i).service.getClassName() + "]");
        	if(procInfos.get(i).service.getClassName().equals("com.example.fastscheduleweeks.PlayerService") == true) {
        		flag = 1;
        		break;
        	} 
        }
        if (flag == 0) {
            
            Intent serviceIntent = new Intent(this, WidgetService.class);
        	startService(serviceIntent);

        	//stopService(serviceIntent);
        }
    }
    public void stopServiceMoveWidget() {
//    	ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//    	List<RunningServiceInfo> procInfos = am.getRunningServices(100);
//        for (int i = 0; i < procInfos.size(); i++) {
//        	Log.i("check process", "[" + procInfos.get(i).service.getClassName() + "]");
//        	if(procInfos.get(i).service.getClassName().equals("com.example.fastscheduleweeks.PlayerService") == true) {
//
//        		//PlayerService tempService = (PlayerService)(procInfos.get(i).service);
//        		//am.restartPackage(procInfos.get(i).service.getClassName());
//        		am.killBackgroundProcesses(procInfos.get(i).service.getClassName());
//        		break;
//        	}
//        }
        Intent serviceIntent = new Intent(this, WidgetService.class);
    	stopService(serviceIntent);
    	
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        startServiceMoveWidget();
       
        unregisterReceiver(buttonBroadcastReceiver);
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopServiceMoveWidget();
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction("com.example.fastscheduleweeks.VoiceInputActivity");
        intentFilter.addAction("com.example.fastscheduleweeks.CameraInputActivity");
        registerReceiver(buttonBroadcastReceiver, intentFilter);
    }   
    
    public void setRemoteView() {
    
        // 2015.09/11
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
       
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker("Sample");
        builder.setWhen(System.currentTimeMillis());
        builder.setNumber(10);
        builder.setContentTitle("Title");
        builder.setContentText("");
        Notification noti = builder.build();
        
        Intent intent_ = new Intent("com.example.fastscheduleweeks.VoiceInputActivity");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 101, intent_,
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        Intent intent2_ = new Intent("com.example.fastscheduleweeks.CameraInputActivity");
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 102, intent2_,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews contentiew = new RemoteViews(getPackageName(), R.layout.remoteview);
        contentiew.setOnClickPendingIntent(R.id.remote_onVoiceRec, pendingIntent);
        contentiew.setOnClickPendingIntent(R.id.remote_onCamera, pendingIntent2);
        noti.contentView = contentiew;
        nm.notify(1, noti);
        
        buttonBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
            	String name = intent.getAction();
            	Log.i("Intent RES", "Message is " + name);
                // TODO Auto-generated method stub
            	if (name.trim().equals("com.example.fastscheduleweeks.VoiceInputActivity")) {
            		Intent voiceInputIntent = new Intent(MainActivity.this, VoiceInputActivity.class);
            		startActivity(voiceInputIntent);
            	}
            	if (name.trim().equals("com.example.fastscheduleweeks.CameraInputActivity")) {
            		Intent voiceInputIntent = new Intent(MainActivity.this, CameraInputActivity.class);
            		startActivity(voiceInputIntent);
            	}
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction("com.example.fastscheduleweeks.VoiceInputActivity");
        intentFilter.addAction("com.example.fastscheduleweeks.CameraInputActivity");
        registerReceiver(buttonBroadcastReceiver, intentFilter);
        
        
        
    }
}
