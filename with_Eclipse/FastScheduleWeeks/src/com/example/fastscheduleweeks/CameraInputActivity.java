package com.example.fastscheduleweeks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class CameraInputActivity extends Activity {

	private ImageView mImageView;
	private Bitmap profileBitmap;
	private EditText parseStrTextView;
	
    EditText dateTextBox;
    EditText timeTextBox;
    EditText whereTextBox;
    EditText whoTextBox;
    EditText otherTextBox;
    
	
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/tesseract/";
	
	
	//public static final String DATA_PATH = "/mnt/sdcard/tesseract/";
	public static final String lang = "kor"; // origin is eng
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_input);
		mImageView = (ImageView) findViewById(R.id.inputCameraImageView);
		parseStrTextView = (EditText)findViewById(R.id.cameraToStringTextView);
		
        dateTextBox = (EditText) findViewById(R.id.camera_dateTextField);
        timeTextBox = (EditText) findViewById(R.id.camera_timeTextField);
        whereTextBox = (EditText) findViewById(R.id.camera_whereTextField);
        whoTextBox = (EditText) findViewById(R.id.camera_whoTextField);
        otherTextBox = (EditText) findViewById(R.id.camera_otherText);

		
		findViewById(R.id.startCamera).setOnClickListener(photoPickClick);
		findViewById(R.id.camera_cancel).setOnClickListener(mClickListener);
		findViewById(R.id.camera_submit).setOnClickListener(mClickListener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent serviceIntent = new Intent(this, WidgetService.class);
		stopService(serviceIntent);
	}

	
	Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.startCamera: {
                	
                }break;
                case R.id.camera_cancel : {
                    onBackPressed();
                }break;
                case R.id.camera_submit : {
                	saveDateBase();
                	onBackPressed();
                	
                } break;
            }
        }
	};
	
	protected void onParseImageToString() {
    	Bitmap bitmap = profileBitmap;
// 2015.09.18 rotation 기능도 살려야 함.
//    	ExifInterface exif = new ExifInterface(_path);
//    	int exifOrientation = exif.getAttributeInt(
//    	        ExifInterface.TAG_ORIENTATION,
//    	        ExifInterface.ORIENTATION_NORMAL);
//
//    	int rotate = 0;
//
//    	switch (exifOrientation) {
//    	case ExifInterface.ORIENTATION_ROTATE_90:
//    	    rotate = 90;
//    	    break;
//    	case ExifInterface.ORIENTATION_ROTATE_180:
//    	    rotate = 180;
//    	    break;
//    	case ExifInterface.ORIENTATION_ROTATE_270:
//    	    rotate = 270;
//    	    break;
//    	}
//
//    	if (rotate != 0) {
//    	    int w = bitmap.getWidth();
//    	    int h = bitmap.getHeight();
//
//    	    // Setting pre rotate
//    	    Matrix mtx = new Matrix();
//    	    mtx.preRotate(rotate);
//
//    	    // Rotating Bitmap & convert to ARGB_8888, required by tess
//    	    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
//    	}
//    	bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    	
    	TessBaseAPI baseApi = new TessBaseAPI();
    	// DATA_PATH = Path to the storage
    	// lang = for which the language data exists, usually "eng"
    	Log.i("DataFilePath : ",(DATA_PATH + "tessdata/" + lang + ".traineddata"));
    	baseApi.init(DATA_PATH, "kor");

    	baseApi.setImage(bitmap);
    	String recognizedText = baseApi.getUTF8Text();
    	Log.i("PhosoCaptureExample",recognizedText);
    	baseApi.end();
    	parseStrTextView.setText("" + recognizedText);
    	parseToString(parseStrTextView.getText().toString());
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!= 0){
			if(requestCode==1){
				mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
				try{
					if( data.getExtras() != null ) {
						Bundle extra = data.getExtras();
						profileBitmap = extra.getParcelable("mData");
						mImageView.setImageBitmap(profileBitmap);
						
					}
					if (data.getData() != null) {
						Uri imageUri = data.getData();
						Bitmap bitmap = Images.Media.getBitmap(getContentResolver(), imageUri);
						int height = bitmap.getHeight();
						int width = bitmap.getWidth();
						// Toast.makeText(this, width + " , " + height, Toast.LENGTH_SHORT).show();
						profileBitmap = null;
						while (height > 320) {
							profileBitmap = Bitmap.createScaledBitmap(bitmap, (width * 320) / height, 320, true);
						        height = profileBitmap.getHeight();
						        width = profileBitmap.getWidth();
						}
						mImageView.setImageBitmap(profileBitmap);
						// Call the after flow 
						onParseImageToString();
					}
				} catch(Exception e){	
						e.printStackTrace();;
				}
			} else {
				Log.i("ERROR", "Not image data");
			}
		}
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

	
	// 카메라 세번째 
	private OnClickListener photoPickClick = new OnClickListener() {
	
		public void onClick(View v) {
		// TODO Auto-generated method stub
			try {
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				//cameraIntent.putExtra("crop", "true");
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
				cameraIntent.putExtra("return-data", true);
				startActivityForResult(cameraIntent,1);
			} catch (ActivityNotFoundException e) {
				
			}
		}
	};

}
