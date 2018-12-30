package www.cvit.leafrecognizer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

//import android.support.v8.renderscript.*;

/**
 * Created by vamsidhar on 4/12/17.
 */

public class CameraActivityInbuilt extends AppCompatActivity  implements View.OnClickListener, OnShowcaseEventListener {
    private Bitmap queryImage;
    private String LOGTAG = "CameraActivityInbuilt";
    public static Menu menu;
    public static MenuItem menuItem;
    private CropImageView cropImageView;
    private int MAX_SIZE = 640;
    private int MIN_SIZE = 448;
    private Boolean runOffline = true;
    private ImageClassifier classifier;


    private String[] resultString = new String[10];
    private String[] resultName = new String[10];
    private ArrayList<LeafInfo> leafInfo;

    private String queryLocation;
    private ImageView resultImageView;

    private Button editButton;
    private Button confirmButton;

    private SessionManager sessionManager;
    private ShowcaseView showcaseView;
    private String showcaseKey = "demo_crop_screen";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_inbuilt);

        Intent intent = getIntent();
        runOffline = (Boolean)intent.getExtras().getBoolean("runOffline");
        queryLocation = intent.getStringExtra("queryLocation");

        classifier = MainActivity.classifier;

        getBitmap(queryLocation);

        setViews();
        setListeners();

        setShowCaseViews();


//        loadleaf();


    }

    private void setViews(){

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(Color.WHITE);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setTitle("");
//        toolbar.setSubtitle("");

        editButton = findViewById(R.id.edit_button);
        confirmButton = findViewById(R.id.confirm_button);
        resultImageView = (ImageView)findViewById(R.id.crop_result);
        resultImageView.setImageBitmap(queryImage);


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


        cropImageView.setVisibility(View.INVISIBLE);


    }

    private void setListeners(){
        cropImageView.setOnCropWindowChangedListener(new CropImageView.OnSetCropWindowChangeListener() {
            @Override
            public void onCropWindowChanged() {
                Log.v(LOGTAG,"crop window changed listener");
//                editButton.setVisibility(View.INVISIBLE);
//                confirmButton.setVisibility(View.INVISIBLE);
            }
        });

        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                Log.v(LOGTAG,"crop image complete listener");
            }
        });


        cropImageView.setOnSetCropOverlayMovedListener(new CropImageView.OnSetCropOverlayMovedListener() {
            @Override
            public void onCropOverlayMoved(Rect rect) {
                Log.v(LOGTAG,"crop overlay moved listener");
                editButton.setVisibility(View.INVISIBLE);
                confirmButton.setVisibility(View.INVISIBLE);
            }
        });

        cropImageView.setOnSetCropOverlayReleasedListener(new CropImageView.OnSetCropOverlayReleasedListener() {
            @Override
            public void onCropOverlayReleased(Rect rect) {
                Log.v(LOGTAG,"crop overlay released listener");
                editButton.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.VISIBLE);
            }
        });

        cropImageView.setOnSetImageUriCompleteListener(new CropImageView.OnSetImageUriCompleteListener() {
            @Override
            public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
                Log.v(LOGTAG,"crop image uri change listener");
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rect cropRect = cropImageView.getCropRect();
                Log.v(LOGTAG,cropRect.left+" "+cropRect.top
                        +" "+cropRect.width()+" "+cropRect.height());
                Bitmap croppedImage = cropImageView.getCroppedImage(cropRect.width(),cropRect.height());
                dispCropImage(croppedImage);
                saveImage(croppedImage);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cropImageView.getVisibility() == View.VISIBLE){
                    Log.v(LOGTAG,"Making crop rectangle invisible");
                    cropImageView.setVisibility(View.INVISIBLE);
                    editButton.setText(R.string.orig);
                    resultImageView.setImageBitmap(queryImage);
                    resultImageView.setVisibility(View.VISIBLE);
                }else if (cropImageView.getVisibility() == View.INVISIBLE){
                    Log.v(LOGTAG,"Making crop rectangle visible");
                    cropImageView.setVisibility(View.VISIBLE);
                    editButton.setText(R.string.edit);
                    resultImageView.setVisibility(View.INVISIBLE);
                }

            }
        });

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_crop, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu mMenu) {
//        menu = mMenu;
//        menuItem = menu.findItem(R.id.cropPicture);
//        menuItem.setVisible(true);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.cropPicture) {
//            Rect cropRect = cropImageView.getCropRect();
//            Log.v(LOGTAG,cropRect.left+" "+cropRect.top
//                    +" "+cropRect.width()+" "+cropRect.height());
//            Bitmap croppedImage = cropImageView.getCroppedImage(cropRect.width(),cropRect.height());
//
//            item.setVisible(false);
//            dispCropImage(croppedImage);
//
//            saveImage(croppedImage);
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    public void dispCropImage(Bitmap croppedImage){
        cropImageView.setVisibility(View.INVISIBLE);

        resultImageView.setImageBitmap(croppedImage);
        resultImageView.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.INVISIBLE);
        confirmButton.setVisibility(View.INVISIBLE);
//        MenuItem menuItem = findViewById(R.id.cropPicture);
//        menuItem.setVisible(false);

    }

    private Bitmap rotateImage(String path, Bitmap b){
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String orientString = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
//            int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(LOGTAG,"Orientation EXIF: "+orientation);
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                String cameraId = manager.getCameraIdList()[0];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

                Log.v(LOGTAG,"Orientation cameraChar: "+orientation);

                int degree = 0;

                switch (orientation) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        degree = 0;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                    case ExifInterface.ORIENTATION_UNDEFINED:
                        degree = 0;
                        break;
                    default:
                        degree = 90;
                }
                Log.v(LOGTAG,"Rotation degree: "+degree);

                Matrix matrix = new Matrix();
                if(b.getWidth()>b.getHeight()){
                    matrix.setRotate(degree);
                    b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                            matrix, true);
                }
            }
            catch (Exception e)
            {
            }


        }catch (IOException e){
            Log.e(LOGTAG,"IO exception: "+e.getMessage());
        }

        return b;

    }

    public void getBitmap(String path) {
        try {
            Bitmap bitmap=null;
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
//            queryImage = bitmap;

            queryImage = rotateImage(path,bitmap);


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static Bitmap getResizedBitmap2(RenderScript rs, Bitmap src, int minSize) {
        Bitmap.Config  bitmapConfig = src.getConfig();
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        float srcAspectRatio = (float) srcWidth / srcHeight;
        Log.v("CameraActivityInbuilt","Old: "+srcWidth+" X "+srcHeight);

        int dstWidth = src.getWidth();
        int dstHeight = src.getHeight();

        if (srcAspectRatio < 1) {
            dstWidth = minSize;
            dstHeight = (int) (dstWidth / srcAspectRatio);
        } else if(srcAspectRatio >= 1){
            dstHeight = minSize;
            dstWidth = (int) (dstHeight * srcAspectRatio);
        }

        Log.v("CameraActivityInbuilt","New: "+dstWidth+" X "+dstHeight);

        float resizeRatio = (float) srcWidth / dstWidth;

        /* Calculate gaussian's radius */
        float sigma = resizeRatio / (float) Math.PI;
        // https://android.googlesource.com/platform/frameworks/rs/+/master/cpu_ref/rsCpuIntrinsicBlur.cpp
        float radius = 2.5f * sigma - 1.5f;
        radius = Math.min(25, Math.max(0.0001f, radius));

        /* Gaussian filter */
        Allocation tmpIn = Allocation.createFromBitmap(rs, src);
        Allocation tmpFiltered = Allocation.createTyped(rs, tmpIn.getType());
        ScriptIntrinsicBlur blurInstrinsic = ScriptIntrinsicBlur.create(rs, tmpIn.getElement());

        blurInstrinsic.setRadius(radius);
        blurInstrinsic.setInput(tmpIn);
        blurInstrinsic.forEach(tmpFiltered);

        tmpIn.destroy();
        blurInstrinsic.destroy();

        /* Resize */
        Bitmap dst = Bitmap.createBitmap(dstWidth, dstHeight, bitmapConfig);
        Type t = Type.createXY(rs, tmpFiltered.getElement(), dstWidth, dstHeight);
        Allocation tmpOut = Allocation.createTyped(rs, t);
        ScriptIntrinsicResize resizeIntrinsic = ScriptIntrinsicResize.create(rs);

        resizeIntrinsic.setInput(tmpFiltered);
        resizeIntrinsic.forEach_bicubic(tmpOut);
        tmpOut.copyTo(dst);

        tmpFiltered.destroy();
        tmpOut.destroy();
        resizeIntrinsic.destroy();

        return dst;
    }

    public Bitmap getResizedBitmap(Bitmap image, int minSize) {

        float temp = Math.min(image.getHeight(),image.getWidth())/MIN_SIZE;
        int fact = (int) (Math.log(temp)/Math.log(2));

        int min_size = minSize ;

        int width = image.getWidth();
        int height = image.getHeight();
        Log.v(LOGTAG,"Old: "+width+" X "+height);

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio < 1) {
            width = minSize;
            height = (int) (width / bitmapRatio);
        } else if(bitmapRatio >= 1){
            height = minSize;
            width = (int) (height * bitmapRatio);
        }
        Log.v(LOGTAG,"New: "+width+" X "+height);
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private void saveImage(Bitmap croppedImage){

        String fileName = generateFileName();
        String saveFolder = Environment.getExternalStorageDirectory().toString()
                +File.separator+BuildConfig.APPLICATION_ID+File.separator;
        File croppedFile = new File(saveFolder+fileName);

        queryLocation = croppedFile.getAbsolutePath();
        if (croppedFile == null) {
            Log.w(LOGTAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {

            FileOutputStream fos = new FileOutputStream(croppedFile);
//            Bitmap resizedImage = getResizedBitmap(croppedImage, MIN_SIZE);
            RenderScript rs = RenderScript.create(CameraActivityInbuilt.this);
            Bitmap resizedImage = getResizedBitmap2(rs,croppedImage,MIN_SIZE);

            resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            Log.d(LOGTAG,"croppedFile: "+croppedFile.toString());
            fos.close();

            MediaScannerConnection.scanFile(this,
                    new String[]{queryLocation}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });


            if (runOffline){
               runOfflineClassifier(resizedImage);
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

        RunClassifier.AsyncResponse delegate = new RunClassifier.AsyncResponse(){
            @Override
            public void processFinish(String output) {
                String resultString = output;
//                Toast.makeText(CameraActivityInbuilt.this,resultString,Toast.LENGTH_LONG).show();

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
        String allChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder charBuilder = new StringBuilder();
        Random rnd = new Random();
        while (charBuilder.length() < MAX_LENGTH) { // length of the random string.
            int index = (int) (rnd.nextFloat() * allChars.length());
            charBuilder.append(allChars.charAt(index));
        }

        return charBuilder.toString();
    }

    private String generateFileName(){
        SessionManager sessionManager = new SessionManager();
        String userId = sessionManager.getStringSessionPreferences(this,"userId","");
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


    private Target viewTarget[];
    private String demoContent[];
    private String demoTitle[];
    private int demoNumber = 0;

    private void setShowCaseViews() {
        SessionManager sessionManager = new SessionManager();
        boolean showDemo = sessionManager.getBooleanSessionPreferences(CameraActivityInbuilt.this, showcaseKey, false);

//        boolean showDemo = false;

        if (!showDemo) {
            Log.v(LOGTAG, "Current demo number is initial");
            viewTarget = new ViewTarget[10];
            viewTarget[0] = new ViewTarget(findViewById(R.id.edit_button));
            viewTarget[1] = new ViewTarget(findViewById(R.id.confirm_button));

            demoContent = new String[10];
            demoContent[0] = getString(R.string.intro_inst6);
            demoContent[1] = getString(R.string.intro_inst7);

            demoTitle = new String[10];
            demoTitle[0] = getString(R.string.edit);
            demoTitle[1] = getString(R.string.confirm);

            String initialTitle = getString(R.string.editSection);
            String initialContent = getString(R.string.editSectionIntro);

            showcaseView = new ShowcaseView.Builder(CameraActivityInbuilt.this)
                    .blockAllTouches()
                    .setContentTitle(initialTitle)
                    .setContentText(initialContent)
                    .setTarget(Target.NONE)
                    .withNewStyleShowcase()
                    .setOnClickListener(this)
                    .setShowcaseEventListener(this)
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .build();

            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lps.addRule(RelativeLayout.CENTER_IN_PARENT);

            showcaseView.setButtonPosition(lps);
            showcaseView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            showcaseView.setButtonText(getString(R.string.next));
            showcaseView.setShowcase(Target.NONE, true);
            showcaseView.show();
        } else {
            Log.v(LOGTAG, "Demo already shown");
        }
    }

    @Override
    public void onClick(View v) {
        Log.v(LOGTAG, "onClick");
        if (viewTarget[demoNumber] != null && demoContent[demoNumber] != null && demoTitle[demoNumber] != null) {
            Log.v(LOGTAG, "Current demo number is " + demoNumber);
            showcaseView.setShowcase(viewTarget[demoNumber], true);
            showcaseView.show();
            showcaseView.setContentTitle(demoTitle[demoNumber]);
            showcaseView.setContentText(demoContent[demoNumber]);
            if (viewTarget[demoNumber + 1] == null) {
                showcaseView.setButtonText(getString(R.string.got_it));
            }
            //showcaseView.show();

            demoNumber++;
        } else {
            showcaseView.hide();
            SessionManager sessionManager = new SessionManager();
            sessionManager.setSessionPreferences(CameraActivityInbuilt.this, showcaseKey, true);
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView _showcaseView) {

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView _showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView _showcaseView) {
        Log.v(LOGTAG, "onShow");
        if (_showcaseView != null) {
            Log.v(LOGTAG, "Local is not null");
        } else {
            Log.v(LOGTAG, "Local is null");
        }
        if (showcaseView != null) {
            Log.v(LOGTAG, "global is not null");
        } else {
            Log.v(LOGTAG, "Local is null");
        }
    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent _motionEvent) {

    }


}
