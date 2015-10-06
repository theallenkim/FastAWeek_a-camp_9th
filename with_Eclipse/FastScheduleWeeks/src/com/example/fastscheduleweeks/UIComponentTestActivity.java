package com.example.fastscheduleweeks;

import android.util.DisplayMetrics;
import android.util.Log;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;



public class UIComponentTestActivity extends Activity {
	
	DisplayMetrics mMetrics;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uicomponent_test);
		
		GridView localGridView = (GridView)findViewById(R.id.gridView);
		localGridView.setAdapter(new TimeTableAdapter(this));
		localGridView.setOnItemClickListener(gridviewOnItemClickListener);
		
		mMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
	}
	
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
			if ((position % 8) == 0) {
				int sub = (position / 8) + 1;
				return String.valueOf(sub);
			} else {
				
			}
			return "0";
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
				subButton.setBackgroundColor(0xFFCCEEFF);
				subButton.setPadding(1, 1, 1, 1);
			
			} else {
				subButton = (Button)convertView;
			}
			subButton.setText(""+getItem(position));
			
			return subButton;
		}
	}
}
