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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import com.sky.Main.myasync;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.provider.MediaStore;
import android.net.Uri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Environment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Button;

/**
 * This example shows how to create and handle image picker in Android.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class ImageSelect extends Activity {
	
	private static final int CAMERA_REQUEST = 1888;
	 
	ImageView mImageView;
	boolean done = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setContentView(R.layout.uploadimage);
        
        //MOVING PREVIOUS OUTPUT
        String cmds[] = {"mv /mnt/sdcard/debian/cropped_face.png /mnt/sdcard/data/cropped_face.png",
        				  "rm /mnt/sdcard/debian/face_img.png"};
        try {
			doCmds(cmds);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		//GETTING PICTURE FROM CAMERA
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        
        
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

	            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	            //mImageView.setImageBitmap(thumbnail);
	            //3
	            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
	            //4
	            File file = new File("/mnt/sdcard/debian/face_img.png");
	            Toast.makeText(ImageSelect.this, file.getAbsolutePath(), 10000).show();
	            try {
	                file.createNewFile();
	                FileOutputStream fo = new FileOutputStream(file);
	                //5
	                fo.write(bytes.toByteArray());
	                fo.close();
	            
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            finally{
	            	ImageSelect.this.finish();
	            }
        }
        ImageSelect.this.finish();
        
    }	
    public void doCmds(String[] cmds) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        
        for (String cmd : cmds) {
                os.writeBytes(cmd+"\n");
        }
        
        os.writeBytes("exit\n");
        os.flush();
        os.close();
        process.waitFor();
    } 
}
