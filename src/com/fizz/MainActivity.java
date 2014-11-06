package com.fizz ;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
 	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		final VideoView videoview = (VideoView)findViewById(R.id.videoView);
	    videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.win)); 
	    //final Button button = (Button) findViewById(R.id.loginButton);
	    
	       videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	            @Override
	            public void onPrepared(MediaPlayer arg0) {
	                videoview.requestFocus();
	                videoview.start();
	           }
	        });
	       videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	            public void onCompletion(MediaPlayer mp) {
	            	startActivity(new Intent(MainActivity.this, SplashActivity.class)) ;
	            	finish() ;
					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	           }
	        });
	}
}
