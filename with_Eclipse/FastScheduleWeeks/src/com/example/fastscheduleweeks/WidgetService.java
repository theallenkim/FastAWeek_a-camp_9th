package com.example.fastscheduleweeks;
// 소스 출처. (in English - Reference from : )
//https://github.com/fouady/SpotifyTray-Android/blob/master/src/com/droidprojects/spotifytray/PlayerService.java
// 추가 설명 :
// In Korean : 기존 소스는 위젯이며 하나 재생,일시정지,이전곡,다음곡 버튼과 곡의 섬네일 이미지를 제공해주는 기능이 많은 코드 였다.
//            해당 코드에서 필요로 한 부분만 제거 하고 Layout code를 조정 하여 적용 하였다. 
//            여기에서 보여지는 버튼 UI 컴포넌트는 크게 토글기능과 드래깅 기능을 지윈 하기 위한 버튼, 토글에서 enable/disable 되는 
//            버튼 여러개가 존재한다. 
// In English : The original source code is an android widget. but, means & support type to different.

/* In Korean. :
 * 이 소스 코드가 하는일은 크게 다음과 같다. 
 * 1. 바탕화면 최상단 무빙 위젯 
 * 2. 알림바 영역 위젯 사라지기 기능 제공 
 * 3. 위젯에 특정 버튼들이 보여졌다가 사라지게 하는 토글 기능을 제공.
 * 
 * In English : 
 * Below the working items in this code.
 * 1. top moving widget in Android main screen
 * 2. Using event about disable to widget, in notification area.
 * 3.  
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.example.fastscheduleweeks.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class WidgetService extends Service {

	// 
	private static final int TRAY_HIDDEN_FRACTION 			= 6; 	// Controls fraction of the tray hidden when open
	private static final int TRAY_MOVEMENT_REGION_FRACTION 	= 6;	// Controls fraction of y-axis on screen within which the tray stays.
	private static final int TRAY_CROP_FRACTION 			= 12;	// Controls fraction of the tray chipped at the right end.
	private static final int ANIMATION_FRAME_RATE 			= 30;	// Animation frame rate per second.
	private static final int TRAY_DIM_X_DP 					= 140;	// Width of the tray in dps
	private static final int TRAY_DIM_Y_DP 					= 140; 	// Height of the tray in dps
	private static final int BUTTONS_DIM_Y_DP 				= 27;	// Height of the buttons in dps
	
	// Layout containers for various widgets
	private WindowManager 				mWindowManager;			// Reference to the window
	private WindowManager.LayoutParams 	mRootLayoutParams;		// Parameters of the root layout
	private RelativeLayout 				mRootLayout;			// Root layout
	private RelativeLayout 				mContentContainerLayout;// Contains everything other than buttons and song info
	
	// Widgets
	private ImageButton mCenterButton;
	private ImageButton mAppCallButton;
	private ImageButton mClipButton;
	private ImageButton mCamButton;
	private ImageButton mVoiceButton;
	
	// Variables that control drag
	private int mStartDragX;
	//private int mStartDragY; // Unused as yet
	private int mPrevDragX;
	private int mPrevDragY;
	
	private boolean mIsTrayOpen = true;
	
	// Controls for animations
	private Timer 					mTrayAnimationTimer;
	private Handler 				mAnimationHandler = new Handler();
	
	@Override
	public IBinder onBind(Intent intent) {
		// Not used
		return null;
	}

	@Override
	public void onCreate() {
		// Get references to all the views and add them to root view as needed.
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		mRootLayout = (RelativeLayout) LayoutInflater.from(this).
				inflate(R.layout.service_player, null);
		mContentContainerLayout = (RelativeLayout) mRootLayout.findViewById(R.id.content_container);
		//mContentContainerLayout.setOnTouchListener(new TrayTouchListener());
		
		mCenterButton =  (ImageButton) mRootLayout.findViewById(R.id.overay_button_center);
		mAppCallButton = (ImageButton) mRootLayout.findViewById(R.id.overay_button_app);
		mClipButton = (ImageButton) mRootLayout.findViewById(R.id.overay_button_typing);
		mCamButton = (ImageButton) mRootLayout.findViewById(R.id.overay_button_camera);
		mVoiceButton = (ImageButton) mRootLayout.findViewById(R.id.overay_button_voice);
			
		mCenterButton.setOnTouchListener(new TrayTouchListener());
		
		mRootLayoutParams = new WindowManager.LayoutParams(
				Utils.dpToPixels(TRAY_DIM_X_DP, getResources()),
				Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()),
				WindowManager.LayoutParams.TYPE_PHONE, 
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 
				PixelFormat.TRANSLUCENT);

		mRootLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowManager.addView(mRootLayout, mRootLayoutParams);
		// Post these actions at the end of looper message queue so that the layout is
		// fully inflated once these functions execute
		mRootLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				// Reusable variables
		
				// Make everything visible
				mRootLayout.setVisibility(View.VISIBLE);

			}
		}, ANIMATION_FRAME_RATE);
	}

	// The phone orientation has changed. Update the widget's position.
	// The phone orientation has changed. Update the widget's position.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mIsTrayOpen)
			mRootLayoutParams.x = -mRootLayout.getWidth() / TRAY_HIDDEN_FRACTION;
		else
			mRootLayoutParams.x = -mRootLayout.getWidth();
		mRootLayoutParams.y = (getResources().getDisplayMetrics().heightPixels - mRootLayout.getHeight()) / 2;
		mWindowManager.updateViewLayout(mRootLayout, mRootLayoutParams);
		animateButtons();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		if (intent.getBooleanExtra("stop_service", false)){
			// If it's a call from the notification, stop the service.
			stopSelf();
		}else{
			// Make the service run in foreground so that the system does not shut it down.
			Intent notificationIntent = new Intent(this, WidgetService.class);
			notificationIntent.putExtra("stop_service", true);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);
			Notification notification = new Notification(
					R.drawable.ic_launcher, 
					"제때해 바탕화면 위젯",
			        System.currentTimeMillis());
			notification.setLatestEventInfo(
					this, 
					"제때해 바탕화면 위젯",
			        "누르면 위젯이 사라집니다.", 
			        pendingIntent);
			startForeground(86, notification);
		}
		
		return 0;
	}

	// The app is closing.
	@Override
	public void onDestroy() {
//		mPlaylist.stopCurrentSong();
		if (mRootLayout != null)
			mWindowManager.removeView(mRootLayout);
	}

	// Drags the tray as per touch info
	private void dragTray(int action, int x, int y){
		switch (action){
		case MotionEvent.ACTION_DOWN:
			
			// Cancel any currently running animations/automatic tray movements.
//			if (mTrayTimerTask!=null){
//				mTrayTimerTask.cancel();
//				mTrayAnimationTimer.cancel();
//			}
			
			// Store the start points
			mStartDragX = x;
			//mStartDragY = y;
			mPrevDragX = x;
			mPrevDragY = y;
			break;
			
		case MotionEvent.ACTION_MOVE:
			
			// Calculate position of the whole tray according to the drag, and update layout.
			float deltaX = x-mPrevDragX;
			float deltaY = y-mPrevDragY;
			mRootLayoutParams.x += deltaX;
			mRootLayoutParams.y += deltaY;
			mPrevDragX = x;
			mPrevDragY = y;
			animateButtons();
			mWindowManager.updateViewLayout(mRootLayout, mRootLayoutParams);
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			
			// When the tray is released, bring it back to "open" or "closed" state.
			if ((mIsTrayOpen && (x-mStartDragX)<=0) ||
				(!mIsTrayOpen && (x-mStartDragX)>=0))
				mIsTrayOpen = !mIsTrayOpen;
			
			break;
		}
	}

	// Listens to the touch events on the tray.
	private class TrayTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			final int action = event.getActionMasked();

			switch (action) {
			case MotionEvent.ACTION_DOWN:{ 
				toggleSubEvent();
			}break;
			//case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_MOVE: {
				toggleInVisible();
				// Filter and redirect the events to dragTray()
				dragTray(action, (int)event.getRawX(), (int)event.getRawY());
			} break;
			default:
				return false;
			}
			return true;

		}
	}
	
	// This function animates the buttons based on the position of the tray.
	private void animateButtons(){
		
		// Animate only if the tray is between open and close state.
		if (mRootLayoutParams.x < -mRootLayout.getWidth()/TRAY_HIDDEN_FRACTION){
			
			
//			// Limit it to 0-1 if it goes beyond 0-1 for any reason.
//			relativeDistance=Math.max(relativeDistance, 0);
//			relativeDistance=Math.min(relativeDistance, 1);
//			
//			// Setup animations
//			AnimationSet animations = new AnimationSet(true);
//			animations.setFillAfter(true);
//			Animation animationAlpha = new AlphaAnimation(
//					relativeDistance, 
//					relativeDistance);
//			animations.addAnimation(animationAlpha);
//
//			Animation animationScale = new ScaleAnimation(
//					relativeDistance, 
//					relativeDistance, 
//					relativeDistance, 
//					relativeDistance);
//			animations.addAnimation(animationScale);
			
			// Play the animations
//			mPlayerButtonsLayout.startAnimation(animations);
//			mSongInfoLayout.startAnimation(animations);
//			mAlbumCoverLayout.startAnimation(animationAlpha);
		}else{
			
			// Clear all animations if the tray is being dragged - that is, when it is beyond the
			// normal open state.
//			mPlayerButtonsLayout.clearAnimation();
//			mSongInfoLayout.clearAnimation();
//			mAlbumCoverLayout.clearAnimation();
		}
	}
	
	// Load new album cover image
	
	/**************************** Callbacks **************************************/
	
	/*
	 * 	private ImageButton mCenterButton;
	private ImageButton mAppCallButton;
	private ImageButton mClipButton;
	private ImageButton mCamButton;
	private ImageButton mVoiceButton;
	 */
	// Play current song
	public void toggleEvent(View view){
		toggleSubEvent();
	}
	
	public void toggleInVisible() {
		mAppCallButton.setVisibility(View.INVISIBLE);
		mClipButton.setVisibility(View.INVISIBLE);
		mCamButton.setVisibility(View.INVISIBLE);
		mVoiceButton.setVisibility(View.INVISIBLE);
	}
	
	public void toggleSubEvent() {
		if (mAppCallButton.getVisibility() == View.VISIBLE) {
			toggleInVisible();
		} else {
			mAppCallButton.setVisibility(View.VISIBLE);
			mClipButton.setVisibility(View.VISIBLE);
			mCamButton.setVisibility(View.VISIBLE);
			mVoiceButton.setVisibility(View.VISIBLE);
		}
	}
	
	public void showAppMain(View view) {
		//Context context = getApplicationContext();
//		Intent voiceInputIntent = new Intent(this, VoiceInputActivity.class);
//		//voiceInputIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		//context.startActivity(voiceInputIntent);
//		startActivity(voiceInputIntent);
		
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		//i.setComponent(new ComponentName(getApplicationContext().getPackageName(), MainActivity.class.getName()));
		i.setComponent(new ComponentName(getApplicationContext().getPackageName(), MainActivity.class.getName()));
		startActivity(i);
	}
	
	public void showClipInput(View view) {
		//Context context = getApplicationContext();
//		Intent voiceInputIntent = new Intent(this, VoiceInputActivity.class);
//		//voiceInputIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		//context.startActivity(voiceInputIntent);
//		startActivity(voiceInputIntent);
		
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		//i.setComponent(new ComponentName(getApplicationContext().getPackageName(), MainActivity.class.getName()));
		i.setComponent(new ComponentName(getApplicationContext().getPackageName(), ClipBoardInputActivity.class.getName()));
		startActivity(i);
	}
	
	public void showCameraInput(View view) {
		//Context context = getApplicationContext();
//		Intent voiceInputIntent = new Intent(this, VoiceInputActivity.class);
//		//voiceInputIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		//context.startActivity(voiceInputIntent);
//		startActivity(voiceInputIntent);
		
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		//i.setComponent(new ComponentName(getApplicationContext().getPackageName(), MainActivity.class.getName()));
		i.setComponent(new ComponentName(getApplicationContext().getPackageName(), CameraInputActivity.class.getName()));
		startActivity(i);
	}

	public void showVoiceInput(View view) {
		//Context context = getApplicationContext();
//		Intent voiceInputIntent = new Intent(this, VoiceInputActivity.class);
//		//voiceInputIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		//context.startActivity(voiceInputIntent);
//		startActivity(voiceInputIntent);
		
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		//i.setComponent(new ComponentName(getApplicationContext().getPackageName(), MainActivity.class.getName()));
		i.setComponent(new ComponentName(getApplicationContext().getPackageName(), VoiceInputActivity.class.getName()));
		startActivity(i);
	}

	public void showToHideWidget() {
		toggleInVisible();
		mRootLayout.setVisibility(View.INVISIBLE);
	}
	
	public void hideToShowWidget() {
		mRootLayout.setVisibility(View.VISIBLE);
	}
	

}
