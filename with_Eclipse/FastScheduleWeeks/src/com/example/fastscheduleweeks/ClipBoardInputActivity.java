package com.example.fastscheduleweeks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;

public class ClipBoardInputActivity extends Activity {

	EditText inputTextBox;
	Button forceInputButton;
	
    EditText dateTextBox;
    EditText timeTextBox;
    EditText whereTextBox;
    EditText whoTextBox;
    EditText otherTextBox;
    
    Button addItemButton;
    Button clipSubmitButton;
      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clipboard_input);
		
		inputTextBox = (EditText) findViewById(R.id.clipStringEditText);
		
        dateTextBox = (EditText) findViewById(R.id.clip_dateTextField);
        timeTextBox = (EditText) findViewById(R.id.clip_timeTextField);
        whereTextBox = (EditText) findViewById(R.id.clip_whereTextField);
        whoTextBox = (EditText) findViewById(R.id.clip_whoTextField);
        otherTextBox = (EditText) findViewById(R.id.clip_otherText);
        
		forceInputButton = (Button)findViewById(R.id.forceInputMessage);
		clipSubmitButton = (Button)findViewById(R.id.clip_submit);
		
		forceInputButton.setOnClickListener(mClickListener);
		clipSubmitButton.setOnClickListener(mClickListener);
		
		findViewById(R.id.clip_cancel).setOnClickListener(mClickListener);
		
		showClipData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent serviceIntent = new Intent(this, WidgetService.class);
		stopService(serviceIntent);
	}
	
	protected void showClipData() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData cd = cm.getPrimaryClip();

        if(cd != null){
            ClipData.Item item = cd.getItemAt(0);
            inputTextBox.setText("" + item.getText());
        } else {
        	inputTextBox.setText("클립보드에 정보가 없습니다.");
        }
    }
	
	Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.forceInputMessage: {
                    startParsing();
                } break;
                
                case R.id.clip_submit : { 
                	saveDateBase();
                	onBackPressed();
                } break;
                case R.id.clip_cancel : {
                	onBackPressed();
                } break;
            }
        }
    };
    
    public void startParsing() {
    	parseToString(inputTextBox.getText().toString());
    }
    
    public void parseToString(String lowString) {
        Log.i("MyActivity", "lowString is  item number " + lowString);
        StringTokenizer tokenizer = new StringTokenizer(lowString, " ");

        int tokenCount = tokenizer.countTokens();

        if (tokenCount < 4 ) {
            // not parsing
            // print other text box string
            otherTextBox.setText(lowString);
            return;
        }

        if (tokenCount > 4) {
            // over count case
            parseItem4(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
            // other text is over string

            StringBuffer overStr = new StringBuffer(tokenizer.nextToken());
            if ( tokenizer.hasMoreTokens() ) {
                overStr.append(tokenizer.nextToken());
            }

            otherTextBox.setText(overStr);

            return;
        }

        parseItem4(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
    }

    private void parseItem4(String dateStr, String timeStr, String whereStr, String whoStr) {
    	
    	CommonParseStringModule CPSM = new CommonParseStringModule();
    	
    	Calendar c = Calendar.getInstance(); 
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	
    	// 날짜 분석 부분 
    	String dateStringValue = CPSM.getParseFromDateString(dateStr);
    	
    	// 시간 분석 부분 
    	String timeStringValue = CPSM.getParseFromTimeString(timeStr);
    	
    	// 장소 분석 부분 
    	
    	String whereStringValue = CPSM.getParseFromWhereString(whereStr);
    	
    	// 누구랑 분석 부분 
    	String whoStringValue = CPSM.getParseFromWithWhoString(whoStr);
    	
        dateTextBox.setText(dateStringValue);
        timeTextBox.setText(timeStringValue);
        whereTextBox.setText(whereStringValue);
        whoTextBox.setText(whoStringValue);
    }
    
    private void saveDateBase() {
    	LocalDataBaseManagerHelper localDBMH = new LocalDataBaseManagerHelper(this);
    	
    	Calendar c = Calendar.getInstance(); 
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
    	String target_date = dateTextBox.getText().toString();
    	String regist_date = df.format(c.getTime());
    	String v_time = timeTextBox.getText().toString();
    	String v_day = "day";
    	String duration = "1";
    	String address = whereTextBox.getText().toString();
    	String whoData = whoTextBox.getText().toString();
    	String others = "no others";
    	
    	localDBMH.insert(target_date, regist_date, v_time, v_day, duration, address, whoData ,others);
    }
}
