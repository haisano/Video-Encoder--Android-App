package com.sano.videoparser;


import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.sano.videoparser.VideoParserActivity;
import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
//import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.VideoView;

public class VideoParserActivity extends Activity implements
SurfaceHolder.Callback, OnInfoListener, OnErrorListener {

	  private static final String TAG = "RecordVideo";
	  private MediaRecorder mRecorder = null;
	  public static String mOutputFileName;
	  private VideoView mVideoView = null;
	  private SurfaceHolder mHolder = null;
	  private Button mInitBtn = null;
	  private Button mStartBtn = null;
	  private Button mStopBtn = null;
	  private Button mPlayBtn = null;
	  private Button mStopPlayBtn = null;
	  private Camera mCamera = null;
	//  private TextView mRecordingMsg = null;

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.v(TAG, "in onCreate");
	        setContentView(R.layout.main);

	        mInitBtn = (Button) findViewById(R.id.initBtn);
	        mStartBtn = (Button) findViewById(R.id.beginBtn);
	        mStopBtn = (Button) findViewById(R.id.stopBtn);
	        mPlayBtn = (Button) findViewById(R.id.playRecordingBtn);
	        mStopPlayBtn = (Button) findViewById(R.id.stopPlayingRecordingBtn);
	        //mRecordingMsg = (TextView) findViewById(R.id.recording);

	        mVideoView = (VideoView)this.findViewById(R.id.videoView);

	        
	        /*int duration = Toast.LENGTH_LONG;

	    	Toast toast = Toast.makeText(this,mVideoView.toString(), duration);
	    	toast.show();*/
	      
	      try{
	        mStopBtn.setOnClickListener(new View.OnClickListener(){
	    	  	public void onClick(View view){
	    	  		Intent intent = new Intent(VideoParserActivity.this, VideoParser.class);
	            	startActivity(intent);
	    		}
	        });
	      }
	      catch (Exception e){
	          e.printStackTrace();
	      }
	    }

	    private boolean initCamera() {
	        try {
	            mCamera  = Camera.open();
	            Camera.Parameters camParams = mCamera.getParameters();
	            mCamera.lock();
	            mCamera.setDisplayOrientation(90);
	            mCamera.setParameters(camParams);
	            mHolder = mVideoView.getHolder();
	            mHolder.addCallback(this);
	            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        }
	        catch(RuntimeException re) {
	          re.printStackTrace();
	          return false;
	        }
	        return true;
	    }
	    private void releaseRecorder() {
	      if(mRecorder != null) {
	        mRecorder.release();
	        mRecorder = null;
	      }
	    }
	    private void releaseCamera() {
	      if(mCamera != null) {
	        try {
	        mCamera.reconnect();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	        mCamera.release();
	        mCamera = null;
	      }
	    }
	    @Override
	    protected void onResume() {
	      super.onResume();
	        mInitBtn.setEnabled(false);
	        mStartBtn.setEnabled(false);
	        mStopBtn.setEnabled(false);
	        mPlayBtn.setEnabled(false);
	        mStopPlayBtn.setEnabled(false);
	      if(!initCamera())
	        finish();
	    }
	    @Override
	    protected void onPause() {
	        Log.v(TAG, "in onPause");
	      super.onPause();
	      releaseRecorder();
	      releaseCamera();
	    }
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	    }
	    public void doClick(View view) {
	      switch(view.getId()) {
	      case R.id.initBtn:
	            initRecorder();
	            break;
	      case R.id.beginBtn:
	            beginRecording();
	            break;
	      case R.id.stopBtn:
	            stopRecording();
	            break;
	      case R.id.playRecordingBtn:
	            playRecording();
	            break;
	      case R.id.stopPlayingRecordingBtn:
	            stopPlayingRecording();
	            break;
	      }
	    }

	    @Override
	    public void surfaceCreated(SurfaceHolder holder) {
	        try {
	        mCamera.setPreviewDisplay(mHolder);
	          mCamera.startPreview();
	        } catch (IOException e) {
	      e.printStackTrace();
	    }
	        mInitBtn.setEnabled(true);
	    }
	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder) {
	    }

	  @Override
	  public void surfaceChanged(SurfaceHolder holder, int format, int width,
	      int height) {
	  }

	    private void initRecorder() {
	        if(mRecorder != null) return;
	        Random generator = new Random();
	        int randomno;
	        String fname;	        	        
	        File outFile;
		        do {
		        	randomno = generator.nextInt( 1000 );
		        	fname="Video"+randomno;
			        mOutputFileName = Environment.getExternalStorageDirectory()+"/" +fname+".mp4";	                               
			        Log.v("Filename:",mOutputFileName);
			        outFile = new File(mOutputFileName);

		        }while(outFile.exists());
		   try{
	          mCamera.stopPreview();
	          mCamera.unlock();
	            mRecorder = new MediaRecorder();
	            mRecorder.setCamera(mCamera);

	            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	            mRecorder.setVideoSize(176, 144);
	            mRecorder.setVideoFrameRate(30);
	            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	            //Log.v(TAG, "MediaRecorder initialized");

	            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	            mRecorder.setMaxDuration(70000); // limit to 100 seconds
	            mRecorder.setPreviewDisplay(mHolder.getSurface());
	            mRecorder.setOutputFile(mOutputFileName);

	            mRecorder.prepare();
	            Log.v(TAG, "MediaRecorder initialized");
	            
	            mStartBtn.setEnabled(true);
	            mInitBtn.setEnabled(false);
	        }
	        catch(Exception e) {
	            Log.v(TAG, "MediaRecorder failed to initialize");
	            e.printStackTrace();

	        }
	    }

	    private void beginRecording() {
	    	try
	    	{
	    		Log.v("BeginRecording", "Beginning Recording");    	
	      mRecorder.setOnInfoListener(this);
	      mRecorder.setOnErrorListener(this);
	        mRecorder.start();
	        mStartBtn.setEnabled(false);
	        mStopBtn.setEnabled(true);
	    	}
	    	catch(Exception e)
	    	{
	    		Log.v("BeginRecording", "Unable to begin Recording");
	    		e.printStackTrace();
	    	}
	    }

	    private void stopRecording() {
			Log.v("Begin stop", "Beginning stop");    	

	    	if (mRecorder != null) {
	          mRecorder.setOnErrorListener(null);
	          mRecorder.setOnInfoListener(null);
	          try {
	                mRecorder.stop();
	          }
	          catch(IllegalStateException e) {
	          }
	          releaseRecorder();
	          //  mRecordingMsg.setText("");
	            releaseCamera();
	            mStartBtn.setEnabled(false);
	            mStopBtn.setEnabled(false);
	            mPlayBtn.setEnabled(true);
	        }
			Log.v("End Stop", "End Stop");    	

	    }

	  public void playRecording() {
	        MediaController mc = new MediaController(this);
	        mVideoView.setMediaController(mc);
	        mVideoView.setVideoPath(mOutputFileName);
	        mVideoView.start();
	        mStopPlayBtn.setEnabled(true);
	    }

	    private void stopPlayingRecording() {
	      mVideoView.stopPlayback();
	    }

	  @Override
	  public void onInfo(MediaRecorder mr, int what, int extra) {
	    if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
	      stopRecording();
	      Toast.makeText(this, "Recording limit has been reached. Stopping the recording",
	          Toast.LENGTH_SHORT).show();
	    }
	  }

	  @Override
	  public void onError(MediaRecorder mr, int what, int extra) {
	    stopRecording();
	    Toast.makeText(this, "Recording error has occurred. Stopping the recording",
	        Toast.LENGTH_SHORT).show();
	  }
	}



