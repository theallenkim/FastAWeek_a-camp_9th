package com.example.fastscheduleweeks;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class VoiceInputActivity extends Activity {
    private static final int GOOGLE_STT = 1000;
    /// 인텐트, SpeechRecognizer, 텍스트뷰 삽입
    Intent i;
    SpeechRecognizer mRecognizer;
    TextView textView;
    EditText inputTextBox;

    // Input Voice Text to Parsing the Result UI Components
    EditText dateTextBox;
    EditText timeTextBox;
    EditText whereTextBox;
    EditText whoTextBox;
    EditText otherTextBox;
    
    Button addItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_input);
       // textView = (TextView) findViewById(R.id.textView);
        inputTextBox = (EditText) findViewById(R.id.editText);

        dateTextBox = (EditText) findViewById(R.id.dateTextField);
        timeTextBox = (EditText) findViewById(R.id.timeTextField);
        whereTextBox = (EditText) findViewById(R.id.whereTextField);
        whoTextBox = (EditText) findViewById(R.id.whoTextField);
        otherTextBox = (EditText) findViewById(R.id.otherText);
        
        addItemButton = (Button)findViewById(R.id.voice_submit);

        // added the event
        findViewById(R.id.startVoiceRecorder).setOnClickListener(mClickListener);
//        findViewById(R.id.b)
        findViewById(R.id.voice_submit).setOnClickListener(mClickListener);
        findViewById(R.id.voice_cancel).setOnClickListener(mClickListener);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(this, WidgetService.class);
    	stopService(serviceIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_voice_input, menu);
        return false;// true;
    }

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

    protected void recodingVoice() {
        //이하 추가 코드
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        // i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");

        Toast toast = Toast.makeText(getApplicationContext(), "입력하고 싶은 내용을 말씀해 주세요",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(i);


        //startActivityForResult(i, GOOGLE_STT);    //구글 음성인식 실행
    }

    private RecognitionListener listener = new RecognitionListener() {

        @Override
        public void onReadyForSpeech(Bundle params) {
        }
        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
            Toast toast = Toast.makeText(getApplicationContext(), "보이스를 분석 중입니다. 잠시만 기다려 주세요",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }

        @Override
        public void onError(int error) {
        }

        @Override
        public void onResults(Bundle results) {
            ///
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            //textView.setText("" + rs[0]);
            inputTextBox.setText("" + rs[0]);
            mRecognizer.stopListening();

            /// data to Parsing Method
            parseToString(rs[0]);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

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

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.startVoiceRecorder: {
                    recodingVoice();
                } break;
                
                case R.id.voice_submit : { 
                	saveDateBase();
                	onBackPressed();
                } break;
                case R.id.voice_cancel: {
                	onBackPressed();
                }
            }
        }
    };
}
