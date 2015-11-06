package com.example.anesa.test;

/**
 * Created by Loso on 2015-11-05.
 */


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;



public class kamera extends Activity {

    Button takePhoto_btn;
    ImageView imgTakenPhoto;
    private static final int CAM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kamera);

        takePhoto_btn = (Button) findViewById(R.id.camera_btn);
        imgTakenPhoto = (ImageView) findViewById(R.id.imageviewKamera);

        takePhoto_btn.setOnClickListener(new btnTakePhotoClicker());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST)
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imgTakenPhoto.setImageBitmap(thumbnail);
        }

    }

    class btnTakePhotoClicker implements Button.OnClickListener
    {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
           Intent came_intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


            startActivityForResult(came_intent, CAM_REQUEST);



        }

    }


}

