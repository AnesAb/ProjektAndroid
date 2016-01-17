package camera;

/**
 * Created by Loso on 2015-11-11.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Base64;
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

import activity.NyttReceptAct;
import helper.ConnectImg;


public class C_Activity extends Activity {

    ImageView image;
    Activity context;
    C_Preview preview;
    Camera camera;
    Button nyBildButton;
    Button spara_btn;
    ImageView fotoButton;
    LinearLayout progressLayout;

    public static String tempImgPath;
    public static final String UPLOAD_URL = "http://eatwit.se/android_pictures/upload.php";
    public static final String UPLOAD_KEY = "image";

    private Bitmap bitmap;

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
                    uploadImage();

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId,
                                             Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
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
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
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

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;

            options.inPurgeable = true;

            options.inInputShareable = true;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            ExifInterface exif = null;

            int counter =0;

            try {
                exif = new ExifInterface(tempImgPath);
                counter++;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Log.d("EXIF value",
                        exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("1")) {
                    bitmap = rotate(bitmap, 0);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("8")) {
                    bitmap = rotate(bitmap, 90);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("3")) {
                    bitmap = rotate(bitmap, 180);
                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                        .equalsIgnoreCase("0")) {
                    bitmap = rotate(bitmap, 270);
                }
            } catch (Exception e) {

            }

            image.setImageBitmap(bitmap);
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
        //Skapar en ny map för appens applikationer
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

    //metod som går till den senast tagna bilden och tar bort den
    private static File taBortBild() {

        File file = new File(tempImgPath);
        boolean deleted = file.delete();
        Log.v("log_tag","deleted: " + deleted);
        return null;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            ConnectImg rh = new ConnectImg();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(C_Activity.this, "Uploading Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(C_Activity.this, NyttReceptAct.class);
                myIntent.putExtra("bildKoll", 1);
                startActivity(myIntent);
                finish();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);

                String result = rh.sendPostRequest(UPLOAD_URL,data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

}