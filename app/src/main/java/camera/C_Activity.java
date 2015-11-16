package camera;

/**
 * Created by Loso on 2015-11-11.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.anesa.test.R;
import com.example.anesa.test.meny;

import activity.RegisterRecept;

public class C_Activity extends Activity {

    ImageView image;
    Activity context;
    C_Preview preview;
    Camera camera;
    Button nyBildButton;
    Button spara_btn;
    ImageView fotoButton;
    LinearLayout progressLayout;

    static String tempImgPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);

        context=this;

        fotoButton = (ImageView) findViewById(R.id.imageView_foto);
        nyBildButton = (Button) findViewById(R.id.ok_btn);
        spara_btn = (Button) findViewById(R.id.spara_btn);



        image = (ImageView) findViewById(R.id.imageView_granska);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);

        preview = new C_Preview(this, (SurfaceView) findViewById(R.id.CameraFragment));
        FrameLayout frame = (FrameLayout) findViewById(R.id.preview);
        frame.addView(preview);
        preview.setKeepScreenOn(false);

        nyBildButton.setVisibility(View.INVISIBLE);
        spara_btn.setVisibility(View.INVISIBLE);

        //Starta kamera
        startCamera();


        fotoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                nyBildButton.setVisibility(View.VISIBLE);
                spara_btn.setVisibility(View.VISIBLE);

                try {
                    camera.takePicture(null, null, mPicture);
                    //takeFocusedPicture();
                } catch (Exception e) {
                }
                progressLayout.setVisibility(View.VISIBLE);
            }
        });

        nyBildButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    image.setImageResource(0);
                    nyBildButton.setVisibility(View.INVISIBLE);
                    spara_btn.setVisibility(View.INVISIBLE);
                    taBortBild();
                } catch (Exception e) {

                }

            }
        });

        spara_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    image.setImageResource(0);
                    nyBildButton.setVisibility(View.INVISIBLE);
                    spara_btn.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), R.string.pic_save, Toast.LENGTH_LONG).show();
                } catch (Exception e) {

                }
            }
        });


    }//slut onCreate

    private void startCamera(){

        if (camera == null) {
            camera = Camera.open();
            camera.startPreview();
            preview.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= 14)
                setCameraDisplayOrientation(context,
                        CameraInfo.CAMERA_FACING_BACK, camera);
            preview.setCamera(camera);
        }
    }

    private void stopCamera(){

        if (camera != null) {

            camera.release();
        }
    }

//TODO kamera krashar om man tar upp bakgrunds program onResume krävs??.

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
       startCamera();
    }

    //TODO kamera krashar om man är inne i den och lägger den i bakgrunden.
    //LÖST?
    @Override
    protected void onStop() {
        super.onStop();
        stopCamera();
        finish();
        Intent startaMeny = new Intent(C_Activity.this, RegisterRecept.class);
        C_Activity.this.startActivity(startaMeny); //Kamera avslutas och när man återupptar går den till meny igen
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId,
                                             android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

            try{
                camera.takePicture(mShutterCallback, null, mPicture);
            }catch(Exception e){

            }

        }
    };

    Camera.ShutterCallback mShutterCallback = new ShutterCallback() {

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };
    public void takeFocusedPicture() {
        camera.autoFocus(mAutoFocusCallback);

    }


    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputPicFile();
                if (pictureFile == null) {
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                }

                //cacheBild
                Bitmap realImage;
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 5;

                options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

                options.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

                realImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                ExifInterface exif = null;


            int counter =0;

                try {
                    exif = new ExifInterface(getOutputCachePath() + "IMG_Cache" + counter + ".jpg");
                    counter++;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    Log.d("EXIF value",
                            exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                    if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                            .equalsIgnoreCase("1")) {
                        realImage = rotate(realImage, 90);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                            .equalsIgnoreCase("8")) {
                        realImage = rotate(realImage, 90);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                            .equalsIgnoreCase("3")) {
                        realImage = rotate(realImage, 90);
                    } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                            .equalsIgnoreCase("0")) {
                        realImage = rotate(realImage, 90);
                    }
                } catch (Exception e) {

                }

                image.setImageBitmap(realImage);

                fotoButton.setClickable(true);
                camera.startPreview();
                progressLayout.setVisibility(View.GONE); //////////////progress bar slut
                nyBildButton.setClickable(true);

            }

    };

    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, false);
    }


    private static File getOutputPicFile() {
        //Skapar en ny map för appens applikationer                 //TODO om användare inte har SD-kort
        File picStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "EatWit");
        if (!picStorageDir.exists()) {
            if (!picStorageDir.mkdirs()) {
                Log.d("EatWit", "failed to create directory");
                return null;
            }
        }
            // skapar bild
            String tidKodBild = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            mediaFile = new File(picStorageDir.getPath() + File.separator + "IMG_" + tidKodBild + ".jpg");
            // Sparar nuvarande bildens sökväg
            tempImgPath = mediaFile.toString();

            return mediaFile;

    }

    private static String getOutputCachePath() { //TODO Behöver kollas upp. Ska detta användas

        File cacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/EatWit","Cache");
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                Log.d("CacheDir", "failed to create directory");
                return null;
            }
        }
        return cacheDir.getPath()+"/";
    }

    //metod som går till den senast tagna bilden och tar bort den
    private static File taBortBild() {

        File file = new File(tempImgPath);
        boolean deleted = file.delete();
        Log.v("log_tag","deleted: " + deleted);
        return null;
    }

    // TODO Spara Bild till server


}