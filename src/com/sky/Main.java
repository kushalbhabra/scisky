package com.sky;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    /** Called when the activity is first created. */
	TextView mylog;
	Button runbtn;
	private static final int CAMERA_REQUEST = 1888;
	
	String[] mount_cmds = {"sh /mnt/sdcard/debian/debinit"};
	
	String[] clear_images_cmds = {"mv /mnt/sdcard/debian/cropped_face.png /mnt/sdcard/data/cropped_face.png",
	   				              "rm /mnt/sdcard/debian/face_img.png"};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        //GETTING UI REFERENCES
        mylog = (TextView) findViewById(R.id.tv_log);
        runbtn = (Button) findViewById(R.id.button1);
                
        //TAKING PICTURE & PROCESSING
        runbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				//CLEAR IMAGES -> ip and op
				try {
					doCmds(clear_images_cmds);
					
				} catch (Exception e) {
					mylog.append("\nError:"+e.toString());
					Log.d("SKY",e.toString());
				}
				
				//GETTING PICTURE FROM CAMERA
		        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		        startActivityForResult(cameraIntent, CAMERA_REQUEST);
		        
			}
		});
    }
    
    //PERFORMS SHELL COMMANDS
    public void doCmds(String[] cmds) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        DataInputStream is = new DataInputStream(process.getInputStream());
        InputStream in = process.getInputStream();
        Reader reader_ = new InputStreamReader(in);
        myasync sread = new myasync(reader_);
        sread.execute(reader_);
        
        for (String cmd : cmds) {
        		Log.d("SKYSHELL", cmd);
        		mylog.append("\n# "+cmd);
                os.writeBytes(cmd+"\n");
        }
        
        os.writeBytes("exit\n");
        os.flush();
        os.close();
        
        sread.cancel(true);
        
        in.close();
        is.close();
        process.waitFor();
    }
    
    //WRITES IMAGE FROM CAMERA TO SDCARD ; INITIATES MOUNT COMMANDS
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

	            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

	            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);

	            File file = new File("/mnt/sdcard/debian/face_img.png");
	            
	            try {
	                file.createNewFile();
	                FileOutputStream fo = new FileOutputStream(file);
	                fo.write(bytes.toByteArray());
	                fo.close();
	           
	            } catch (IOException e) {
	                mylog.append(e.toString());
	            }
	            
	            finally{
	            	//PROCESS IMAGE
					try {
						doCmds(mount_cmds);
						
					} catch (Exception e) {
						mylog.append("\nError:"+e.toString());
					}
					
					//DISPLAY OUTPUT
					startActivity(new Intent(Main.this,DisplayOutput.class));
	            }
        }
        
        else{
        	Toast.makeText(Main.this, "Had a problem saving image!", 5000).show();
        }
    }
    
    
    //SHELL OUTPUT READER (LOGGER)
    class myasync extends AsyncTask
    {
    	StringBuffer buf ;
    	Reader reader_;
        TextView tv;
        myasync(Reader reader) {
            this.reader_=reader; 
            this.tv = (TextView) findViewById(R.id.tv_log);
        }

		@Override
		protected Object doInBackground(Object... params) {
			Log.d("SKY", "shell op reader doInbackground");
        	buf = new StringBuffer();
            try {
                while( reader_ != null ) {
                    int c = reader_.read();
                    if( c==-1 ) break;
                    buf.append((char) c);
                }
                runOnUiThread(new Runnable() {
      			     public void run() {
      			    
      			    	 tv.append( new StringBuffer(buf) );
      			    }
      			 });
            } catch ( Exception ioe ) {
                buf.append("\n\nERROR:\n"+ioe.toString());
                Log.d("SKY", "shell op reader error"+ioe.toString());
                runOnUiThread(new Runnable() {
      			     public void run() {
      			    	 tv.append( new StringBuffer(buf) );
      			    }
      			 });
            }
			return null;
		}
		
		@Override
        protected void onPostExecute(Object result) {
        	// TODO Auto-generated method stub
        	super.onPostExecute(result);
        	try {
				reader_.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				Log.d("SKY", "shell op reader postexecute");
			}
        }
    }   
}