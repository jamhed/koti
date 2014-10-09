package com.ncom.videoplayer;

import io.vov.vitamio.LibsChecker;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	
	final private String videoUri = "rtsp://smarthouse.ncom-ufa.ru";
	
	private Player camPlayer;
	private ViewGroup vg;
	private String cam = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        LibsChecker.manual(this);
        
        vg = (ViewGroup) this.findViewById(R.id.page_fragment);
        camPlayer = new Player(vg, this);
        
        camPlayer.Play(videoUri + "/cam" + cam );
 
    }
}
