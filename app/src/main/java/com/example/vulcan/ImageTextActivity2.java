package com.example.vulcan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class ImageTextActivity2 extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView i1;
    ImageView imgFavorite;
    TextView t1;

    Camera mCamera;
    Parameters parameters;
    boolean safeToCapture = true;
    SurfaceHolder sHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text2);
        //i1 = (ImageView)findViewById(R.id.imageView3Read);
        t1 = (TextView)findViewById(R.id.textViewContent);
        //getTextFromImage();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        //StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        //StrictMode.setVmPolicy(builder.build());
        //dispatchTakePictureIntent();


        mCamera = getAvailableFrontCamera();     // globally declared instance of camera
        if (mCamera == null){
            mCamera = Camera.open();    //Take rear facing camera only if no front camera available
        }
        SurfaceView sv = new SurfaceView(getApplicationContext());
        SurfaceTexture surfaceTexture = new SurfaceTexture(10);
        Log.d("image9099","20");
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            //mCamera.setPreviewDisplay(sv.getHolder());
            parameters = mCamera.getParameters();

            //set camera parameters
            mCamera.setParameters(parameters);


            //This boolean is used as app crashes while writing images to file if simultaneous calls are made to takePicture

            if(safeToCapture) {

                mCamera.startPreview();
                mCamera.takePicture(null, null, mCall);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //Get a surface
        sHolder = sv.getHolder();
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(3);

        Log.d("image9099","3");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });

    }



    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {

        public void onPictureTaken(byte[] data, Camera camera)
        {
            safeToCapture = false;
            //decode the data obtained by the camera into a Bitmap

            FileOutputStream outStream = null;
            try{
                Log.d("CAMERA", "picture click");


                // create a File object for the output file
                outStream = new FileOutputStream(createImageFile());
                outStream.write(data);
                outStream.close();
                mCamera.release();
                mCamera = null;

                String strImagePath = mCurrentPhotoPath;
                sendEmailWithImage(strImagePath);
                Log.d("CAMERA", "picture clicked - "+strImagePath);
                Intent i = new Intent(getApplicationContext(), OCRActivity2.class);
                startActivity(i);
                finish();
            } catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }

            safeToCapture = true;    //Set a boolean to true again after saving file.

        }
    };

    private Camera getAvailableFrontCamera (){

        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("CAMERA", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }


    //Send Email using javamail API as user will not be allowed to choose available
// application using a Chooser dialog for intent.
    public void sendEmailWithImage(String imageFile){
    }

    public void imageClick(View view){

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        dispatchTakePictureIntent();
//        imgFavorite =(ImageView)findViewById(R.id.imageView1);
//        open();
    }
    String mCurrentPhotoPath;
    private void dispatchTakePictureIntent() {
        Log.d("image","1");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("image","2");
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



    private File createImageFile() throws IOException, IOException {

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "example",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void open(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //IMAGE CAPTURE CODE
        startActivityForResult(intent, 0);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            galleryAddPic();
        }
//        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        Intent i = new Intent(getApplicationContext(), OCRActivity2.class);
        startActivity(i);
        finish();
    }

    public void getTextFromImage(){
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ggg);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
            Toast.makeText(getApplicationContext(), "No Text Found",Toast.LENGTH_SHORT).show();

        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            StringBuilder sb = new StringBuilder();

            for(int i=0; i<items.size();++i){
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }

            t1.setText(sb.toString());
        }
    }
}