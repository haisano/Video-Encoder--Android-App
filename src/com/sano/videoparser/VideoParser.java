package com.sano.videoparser;



import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import com.coremedia.iso.IsoBufferWrapperImpl;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoOutputStream;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class VideoParser extends Activity {
	
	private Button mUploadBtn = null;
	int count,cnt,j;
	String filenames[]=new String[50];
  

	public void onCreate(Bundle savedInstanceState) {
		 VideoParserActivity t1= new VideoParserActivity();
			String recordedvideo=  t1.mOutputFileName;
			
			
		        super.onCreate(savedInstanceState);
		        Log.v("Videoparser", "in onCreate");
		        		// TODO Auto-generated method stub
		        String o_file = recordedvideo;
				File f = new File(o_file);
				System.out.println(f.length());										
		        Movie movie0 = null;
				try {
					movie0 = new MovieCreator().build(new IsoBufferWrapperImpl(f));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					      		      		      
			       
		        List<Track> tracks0 = movie0.getTracks();
		        movie0.setTracks(new LinkedList<Track>());
		        
		        IsoFile isoFile = null;
				try {
					isoFile = new IsoFile(new IsoBufferWrapperImpl(f));
					isoFile.parse();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        double sec = (double)
		                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
		                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
		        count=(int)((sec%10)!=0?(sec/10)+1:(sec/10));
		        cnt=count;
		        System.out.println(tracks0.size());   
		        System.out.println("The start and the endtimes are " );
		      System.out.println( "End:"+sec  + "   ");
		      int i=0;
		      double start=0.00,end=start+10;
		            
		      do{      
		    	  if(count==1)
		    	  {	  
		    		  end=sec-0.5;
		    		  
		    	  }		  
		    	  try {
		    		  	cropvideo(f,start,end,i);
		    		  	String mParsedOutput = Environment.getExternalStorageDirectory() + "/Part"+i+"-"+f.getName();
		    		    filenames[i]=mParsedOutput;		    		  	
		    		  	start+=10;
		    		  	end+=10;
		    		  	i++;
		    		  	count--;
		    	  } 
		    	  catch (IOException e) {
		    		  // TODO Auto-generated catch block
		    		  e.printStackTrace();
		    	  }
		      
		      }while(count>0);
		        
		 	 setContentView(R.layout.upload);
		     mUploadBtn = (Button) findViewById(R.id.uploadBtn);
 				      		      		      
		}
	
	public void doClick(View view) {
	      switch(view.getId()) {
	      case R.id.uploadBtn:
	    	  for(j=0;j<cnt;j++){
	            uploadFile(filenames[j]);
	    	  } 
	    	 
	            break;
	      }
	}	      
	      
	      private void uploadFile(String f_name) {
	    	  HttpURLConnection conn = null;
              DataOutputStream dos = null;
              DataInputStream inStream = null;
              //String existingFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ParsedVideo-1.mp4";
              String existingFileName=f_name;
              String lineEnd = "\r\n";
              String twoHyphens = "--";
              String boundary =  "*****";
              int bytesRead, bytesAvailable, bufferSize;
              byte[] buffer;
              int maxBufferSize = 1*1024*1024;
              String responseFromServer = "";
              String urlString = "http://cervino.ddns.comp.nus.edu.sg/~a0082193/index.php";
              try
              {
               //------------------ CLIENT REQUEST
              FileInputStream fileInputStream = new FileInputStream(new File(existingFileName) );
               // open a URL connection to the Servlet
               URL url = new URL(urlString);
               // Open a HTTP connection to the URL
               conn = (HttpURLConnection) url.openConnection();
               // Allow Inputs
               conn.setDoInput(true);
               // Allow Outputs
               conn.setDoOutput(true);
               // Don't use a cached copy.
               conn.setUseCaches(false);
               // Use a post method.
               conn.setRequestMethod("POST");
               conn.setRequestProperty("Connection", "Keep-Alive");
               conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
               dos = new DataOutputStream( conn.getOutputStream() );
               dos.writeBytes(twoHyphens + boundary + lineEnd);
               dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" +";count=\"" +4 + "\"" + lineEnd);
               dos.writeBytes(lineEnd);
               // create a buffer of maximum size
               bytesAvailable = fileInputStream.available();
               bufferSize = Math.min(bytesAvailable, maxBufferSize);
               buffer = new byte[bufferSize];
               // read file and write it into form...
               bytesRead = fileInputStream.read(buffer, 0, bufferSize);
               
                              
               while (bytesRead > 0)
               {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
               }
               // send multipart form data necesssary after file data...
               dos.writeBytes(lineEnd);
               dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
               // close streams
               Log.e("Debug","File is written");
               
               WebView webview=new WebView(this);
      			//webview= (WebView) findViewById(R.id.myWebView);
      				webview.setWebViewClient(new WebViewClient() {
                  	public boolean shouldOverrideUrlLoading(WebView view, String url){ 
                  //	return super.shouldOverrideUrlLoading(view,url);
                  		view.loadUrl(url);
                  		return true;

                  	}
                  }); 
                  setContentView(webview);
               //	webview.loadData(responseFromServer, "text/html", null);
           	//webview.loadData(responseFromServer, "text/html", null)                               
               webview.getSettings().setJavaScriptEnabled(true);
               webview.getSettings().setLoadsImagesAutomatically(true);
               webview.getSettings().setLightTouchEnabled(true);
               webview.loadUrl("http://cervino.ddns.comp.nus.edu.sg/~a0082193/index.php");                    
               if(j==cnt-1)
                   webview.loadUrl("http://cervino.ddns.comp.nus.edu.sg/~a0082193/encode_new.php");                    


               fileInputStream.close();
               dos.flush();
               dos.close();
              }
              catch (MalformedURLException ex)
              {
                   Log.e("Debug", "error: " + ex.getMessage(), ex);
              }
              catch (IOException ioe)
              {
                   Log.e("Debug", "error: " + ioe.getMessage(), ioe);
              }
              //------------------ read the SERVER RESPONSE
              try {
            	  String str;
            	    //private WebView webview;
                    inStream = new DataInputStream ( conn.getInputStream() );
                    	
                    while (( str = inStream.readLine()) != null)
                    {
                    	 System.out.println("str:"+str);
                         Log.e("Debug","Server Response "+str);
                         responseFromServer=responseFromServer+str;
                         if(responseFromServer.contains("uploaded"))
                         {
                        	 int duration = Toast.LENGTH_LONG;
                        	 
                        	 Toast toast = Toast.makeText(this,"File:"+f_name+ "is uploaded", duration);
                 	    	 toast.show();
                         }
                    }
                    inStream.close();                    
                    Log.v("Response from server",responseFromServer);
                   
              }
              catch (IOException ioex){
                   Log.e("Debug", "error: " + ioex.getMessage(), ioex);
              }
            
	      }
			private static void cropvideo(File mp4, double startTime, double endTime, int number) throws IOException
			{
				Movie movie = new MovieCreator().build(new IsoBufferWrapperImpl(mp4));
		        List<Track> tracks = movie.getTracks();
		        movie.setTracks(new LinkedList<Track>());		        
		        IsoFile isoFile = new IsoFile(new IsoBufferWrapperImpl(mp4));
		        isoFile.parse();
		        double sec = (double)
		                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
		                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();;
				
		        boolean timeCorrected = false;
			       // Here we try to find a track that has sync samples. Since we can only start decoding
			       // at such a sample we SHOULD make sure that the start of the new fragment is exactly
			       // such a frame
			       for (Track track : tracks) {
			       	System.out.println("inside track loop");
			           if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
			           	System.out.println("inside if loop");
			               if (timeCorrected) {
			                   // This exception here could be a false positive in case we have multiple tracks
			                   // with sync samples at exactly the same positions. E.g. a single movie containing
			                   // multiple qualities of the same video (Microsoft Smooth Streaming file)


			                   throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
			               }
			               if(startTime != 0.0 && Math.floor(endTime)!= Math.floor(sec)) {
			               startTime = correctTimeToNextSyncSample(track, startTime); 
			               System.out.println(startTime);
			               endTime = correctTimeToNextSyncSample(track, endTime);
			               System.out.println(endTime);
			               }
			               timeCorrected = true;
			           }
			       }


			       if(startTime != endTime) {
			           for (Track track : tracks) {
			           long currentSample = 0;
			           double currentTime = 0;
			           long startSample = -1;
			           long endSample = -1;
			           for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
			               TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
			               for (int j = 0; j < entry.getCount(); j++) {
			                   // entry.getDelta() is the amount of time the current sample covers.
			                   if (currentTime <= startTime) {
			                       // current sample is still before the new starttime
			                       startSample = currentSample;
			                   }
			                   if (currentTime <= endTime) {
			                       // current sample is after the new start time and still before the new endtime
			                       endSample = currentSample;
			                   } else {
			                       // current sample is after the end of the cropped video
			                       break;
			                   }
			                   currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
			                   currentSample++;
			               }
			           }
			           if(startSample != endSample) {
			        	   movie.addTrack(new CroppedTrack(track, startSample, endSample));
			           }
			       }
			       System.out.println("error after this");
			       IsoFile out = new DefaultMp4Builder().build(movie);
			       String mParsedOutput = Environment.getExternalStorageDirectory() + "/Part"+"%d-"+mp4.getName();
			   
			       FileOutputStream fos = new FileOutputStream(new File(String.format(mParsedOutput, number)));
			       out.getBox(new IsoOutputStream(fos));
			       fos.close(); 
			      }
			}
		        		  	 			
			private static double correctTimeToNextSyncSample(Track track, double cutHere) {
				System.out.println("Entered correcttime");
			       double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
			       long currentSample = 0;
			       double currentTime = 0;
			       for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
			           TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
			           for (int j = 0; j < entry.getCount(); j++) {
			               if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
			                   // samples always start with 1 but we start with zero therefore +1
			                   timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
			               }
			               currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
			               currentSample++;
			           }
			       }
			       for (double timeOfSyncSample : timeOfSyncSamples) {
			           if (timeOfSyncSample > cutHere) {
			               return timeOfSyncSample;
			           }
			       }
			       return timeOfSyncSamples[timeOfSyncSamples.length - 1];
			   }

			}