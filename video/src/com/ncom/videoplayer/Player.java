package com.ncom.videoplayer;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

public class Player implements Callback,
	OnBufferingUpdateListener,
	OnCompletionListener,
	OnPreparedListener,
	OnVideoSizeChangedListener {
	
	final String TAG = "Player";
	
	private MediaPlayer mMediaPlayer;
	private Activity main = null;
	private ViewGroup vg = null;
    private SurfaceView vp = null;

	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	
	private int mVideoWidth;
	private int mVideoHeight;
    
	private String cam;	
	
	Player(ViewGroup vg, Activity main) {
		this.vg = vg;
		this.main = main;
		vp = new SurfaceView(main);
	}
	
	public void Play(final String uri) {
		Log.d(TAG, "Play: " + uri);
		cam = uri;
		initSurface();
	}
	
	public void initSurface() {
        vg.removeAllViews();

		ViewGroup.MarginLayoutParams param = new ViewGroup.MarginLayoutParams(704, 576);
        vp.setLayoutParams(param);
        vg.addView(vp);
        
        SurfaceHolder sh = vp.getHolder();
        sh.setFormat(PixelFormat.RGBA_8888);
        sh.setFixedSize(640, 480);
        sh.addCallback(this);      
	}
	
	public void Stop() {
		if (mMediaPlayer == null) return;
		mMediaPlayer.stop();
		releaseMediaPlayer();
	}

	public void initMediaPlayer(final String uri, SurfaceHolder holder) {
    	try {
    		Toast.makeText(main, "Ожидайте подключения", Toast.LENGTH_LONG).show();
    		releaseMediaPlayer();
    		    		
    		mMediaPlayer = new MediaPlayer(main);
    		
    		// HashMap<String, String> options = new HashMap<String, String>();
    		// options.put("rtsp_transport", "tcp"); // udp
    		// mMediaPlayer.setDataSource(path + s, options);
    		
    		mMediaPlayer.setDisplay(holder);
    		mMediaPlayer.setDataSource(uri);
    		
    		mMediaPlayer.setBufferSize(0);
    		mMediaPlayer.setUseCache(false);
    		mMediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
    		mMediaPlayer.setAdaptiveStream(true);
    		
    		mMediaPlayer.prepareAsync();
    		
    		mMediaPlayer.setOnBufferingUpdateListener(this);
    		mMediaPlayer.setOnCompletionListener(this);
    		mMediaPlayer.setOnPreparedListener(this);
    		mMediaPlayer.setOnVideoSizeChangedListener(this);
    			
		} catch (Exception e) {
			Log.d(TAG, "initMediaPlayer exception");
			e.printStackTrace();
		}    	
    }	
	
	// Surface callbacks
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("VT", "surfaceCreated");
		initMediaPlayer(cam, holder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}

	// MediaPlayer callbacks
	
	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		mIsVideoSizeKnown = true;
		mVideoWidth = width;
		mVideoHeight = height;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}		
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mIsVideoReadyToBePlayed = true;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	private void startVideoPlayback() {
		mMediaPlayer.start();
	}
		
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {}

	@Override
	public void onCompletion(MediaPlayer mp) {}
	
	protected void onDestroy() {
		releaseMediaPlayer();
		doCleanUp();
	}

	private void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;
	}
}