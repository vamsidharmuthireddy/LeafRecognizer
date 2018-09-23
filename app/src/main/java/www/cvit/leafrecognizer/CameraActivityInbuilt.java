package www.cvit.leafrecognizer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vamsidhar on 4/12/17.
 */

public class CameraActivityInbuilt extends AppCompatActivity {
    private Bitmap queryImage;
    private String LOGTAG = "CameraActivityInbuilt";
    public static Menu menu;
    public static MenuItem menuItem;
    private CropImageView cropImageView;
    private int MAX_SIZE = 640;
    private Boolean runOffline = true;
    private ImageClassifier classifier;


    private String[] resultString = new String[10];
    private String[] resultName = new String[10];
    private ArrayList<LeafInfo> leafInfo;

    private String queryLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_inbuilt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        runOffline = (Boolean)intent.getExtras().getBoolean("runOffline");
        queryLocation = intent.getStringExtra("queryLocation");

//        Bundle bundle = intent.getExtras();
//        classifier = (ImageClassifier) bundle.getSerializable("classifier");
//        classifier = (ImageClassifier)intent.getSerializableExtra("classifier");
//        classifier = new MainActivity().classifier;

        classifier = MainActivity.classifier;


//        String saveName = Environment.getExternalStorageDirectory().toString()
//                + File.separator + getString(R.string.save_name);

        getBitmap(queryLocation);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
//        cropImageView.setGuidelines(1);
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
//        cropImageView.setAspectRatio(1,1);
//        cropImageView.setFixedAspectRatio(true);
        cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        cropImageView.setAutoZoomEnabled(true);
        cropImageView.setShowProgressBar(true);
        cropImageView.setImageBitmap(queryImage);

//        cropImageView.setCropRect(new Rect(0, 0, 800, 500));

        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                Log.v(LOGTAG,"crop listener");
            }
        });


//        loadleaf();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu mMenu) {
        menu = mMenu;
        menuItem = menu.findItem(R.id.cropPicture);
        menuItem.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cropPicture) {
            Rect cropRect = cropImageView.getCropRect();
            Log.v(LOGTAG,cropRect.left+" "+cropRect.top
                    +" "+cropRect.width()+" "+cropRect.height());
            Bitmap croppedImage = cropImageView.getCroppedImage(cropRect.width(),cropRect.height());

            item.setVisible(false);
            dispCropImage(croppedImage);

            saveImage(croppedImage);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void dispCropImage(Bitmap croppedImage){
        cropImageView.setVisibility(View.INVISIBLE);
        ImageView cropImageView = (ImageView)findViewById(R.id.crop_result);
        cropImageView.setImageBitmap(croppedImage);
        cropImageView.setVisibility(View.VISIBLE);
//        MenuItem menuItem = findViewById(R.id.cropPicture);
//        menuItem.setVisible(false);

    }

    private void rotateImage(String path){
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String orientString = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
//            int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(LOGTAG,"Orientation : "+orientation);
//            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics("" + 0)

        }catch (IOException e){
            Log.e(LOGTAG,"IO exception: "+e.getMessage());
        }

    }

    public void getBitmap(String path) {
        try {
            rotateImage(path);
            Bitmap bitmap=null;
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            queryImage = bitmap;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        Log.v(LOGTAG,"Old: "+width+" X "+height);

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio >= 1 && width>=MAX_SIZE) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else if(bitmapRatio < 1 && height>=MAX_SIZE){
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        Log.v(LOGTAG,"New: "+width+" X "+height);
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void saveImage(Bitmap croppedImage){

        String fileName = generateFileName();
        File croppedFile = new File(Environment.getExternalStorageDirectory(),
                fileName);
        queryLocation = croppedFile.getAbsolutePath();
        if (croppedFile == null) {
            Log.w(LOGTAG, "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {

            FileOutputStream fos = new FileOutputStream(croppedFile);
//            cropped.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            Bitmap temp = getResizedBitmap(croppedImage, MAX_SIZE);
            temp.compress(Bitmap.CompressFormat.JPEG, 80, fos);

            Log.d(LOGTAG,"croppedFile: "+croppedFile.toString());
            fos.close();
            if (runOffline){
               runOfflineClassifier(croppedImage);
            }else {
                sendToServer(croppedFile);
            }
        } catch (FileNotFoundException e) {
            Log.v(LOGTAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.v(LOGTAG, "Error accessing file: " + e.getMessage());
        }
    }


    private void runOfflineClassifier(Bitmap croppedImage){
        if (classifier == null || croppedImage == null) {
            Toast.makeText(CameraActivityInbuilt.this,
                    "Uninitialized Classifier or null bitmap.",Toast.LENGTH_LONG).show();
            return ;
        }
//                int newWidth = 224;
//                int newHeight = 224;
//
//
//                Bitmap cropImg = Bitmap.createBitmap(croppedImage, 138, 0, 364, 364);
//                cropImg = Bitmap.createScaledBitmap(cropImg,newWidth,newHeight,true);
//
//                FileOutputStream fos1 = new FileOutputStream(
//                        new File(Environment.getExternalStorageDirectory(),"AA1.jpg"));
//                cropImg.compress(Bitmap.CompressFormat.JPEG,100,fos1);
//                fos1.close();

//                String resultString = classifier.classifyFrame(croppedImage);

        RunClassifier.AsyncResponse delegate = new RunClassifier.AsyncResponse(){
            @Override
            public void processFinish(String output) {
                String resultString = output;
                Toast.makeText(CameraActivityInbuilt.this,resultString,Toast.LENGTH_LONG).show();

                Log.v(LOGTAG,resultString);
                Intent callAnnotation = new Intent(CameraActivityInbuilt.this, ResultPrimaryActivity.class);

                callAnnotation.putExtra("resultString", resultString);
                callAnnotation.putExtra("runOffline",runOffline);
                callAnnotation.putExtra("queryLocation",queryLocation);

                startActivity(callAnnotation);
                finish();

            }
        };

        new RunClassifier(CameraActivityInbuilt.this,classifier,delegate)
                .execute(croppedImage);
    }

    private void sendToServer(File croppedFile){
        ContactServer contactServer = new ContactServer(this, this);
        contactServer.execute(croppedFile.toString());
    }

    private String giveUserId(int MAX_LENGTH){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
//        for (int i = 0; i < randomLength; i++){
        for (int i = 0; i < MAX_LENGTH; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private String generateFileName(){
        String userId = giveUserId(10);
        String s = String.valueOf(System.currentTimeMillis());
        String time = s.substring(5, s.length());

        String fileName = userId+"_"+time+".jpg";
        return fileName;

    }

    private void loadleaf(){
        PackageReader reader;

        reader = new PackageReader(CameraActivityInbuilt.this);

        leafInfo = reader.getLeafList();

        Log.v(LOGTAG, "interestPointsList size is " + leafInfo.size());

        LeafInfo leaf;
        for (int i = 0; i < resultString.length; i++) {
            leaf = leafInfo.get(Integer.parseInt(resultString[i]) - 1);
            resultName[i] = leaf.getLeaf(getString(R.string.scientific_name_tag));

            Log.v(LOGTAG, resultString[i]+" "+Integer.parseInt(resultString[i])+" Id " + leaf.getLeaf(getString(R.string.id_tag))+" " +resultName[i]);

        }

    }


}
