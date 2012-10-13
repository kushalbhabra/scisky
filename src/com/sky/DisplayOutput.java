package com.sky;

import java.io.File;

import com.sky.Main.myasync;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayOutput extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.output);
		
        ImageView jpgView = (ImageView)findViewById(R.id.imageView1);
        
        String myJpgPath = "/mnt/sdcard/debian/cropped_face.png"; //UPDATE WITH YOUR OWN JPG FILE
        File findFile = new File(myJpgPath);
        
	    if(findFile.isFile())
	    {
	    	setTitle("Face Detected!");
	    	BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 2;
	        Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
	        jpgView.setImageBitmap(bm);
	    }
	    
	    else
	    	setTitle("No face found!");
	  
	}

}
