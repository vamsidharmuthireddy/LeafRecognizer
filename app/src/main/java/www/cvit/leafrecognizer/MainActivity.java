package www.cvit.leafrecognizer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "MainActivity";

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    private static final int Click = 5;
    private static final int PERMISSIONS_REQUEST_CAMERA = 6;
    private static final int PERMISSIONS_PICK_PICTURE_REQUEST = 7;
    public static Menu menu;
    public static MenuItem menuItem;
    private int totalPermissions = 0;
    private boolean storageRequested = false;
    private boolean cameraRequested = false;
    public static ImageClassifier classifier;
    private Button openCamera;
    private Button selectPicture;
    private Switch modeSwitch;
    private TextView modeSwitchText;
    public static String queryLocation;
    private String userId;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setViews();
        setDummyListeners();

        try {
            classifier = new ImageClassifier(MainActivity.this);
            Log.v(LOGTAG,"Intialized the Classifier");
        } catch (IOException e) {
            Log.e(LOGTAG, "Failed to initialize an image classifier.");
        }
//        setListeners();
        setUserId();

        checkAllPermissions();

    }

    private void setUserId(){
        sessionManager = new SessionManager();
        userId = giveUserId(8);
        if (sessionManager.checkSessionPreferences(this, "userId")){
            userId = sessionManager.getStringSessionPreferences(this, "userId",userId);
        }else{
            sessionManager.setSessionPreferences(this,"userId",userId);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(hasPermissions(this, PERMISSIONS)) {
            Log.v(LOGTAG, "Permissions Granted");
            setListeners();

        }
    }

    private void setViews(){
        openCamera = (Button)findViewById(R.id.openCamera);
        modeSwitch = (Switch) findViewById(R.id.modeSwitch);
        modeSwitchText = (TextView) findViewById(R.id.modeSwitchText);
        selectPicture = (Button) findViewById(R.id.selectPicture);

        if(modeSwitch.isChecked()){
            modeSwitchText.setText("Online Mode");
        }else{
            modeSwitchText.setText("Offline Mode");
        }

        openCamera.setClickable(true);
        selectPicture.setClickable(true);
        modeSwitch.setClickable(true);
        Log.d(LOGTAG,"Set Views");

    }

    private void setListeners(){
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = generateFileName();

//                File saveFile = new File(Environment.getExternalStorageDirectory(),
//                        fileName);

                String saveFolder = Environment.getExternalStorageDirectory().toString()
                        +File.separator+BuildConfig.APPLICATION_ID+File.separator;
                if(!new File(saveFolder).exists()){
                    new File(saveFolder).mkdirs();
                }

                File saveFile = new File(saveFolder+fileName);


                queryLocation = saveFile.getAbsolutePath();
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (saveFile != null){
                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            saveFile);

                    takePicture.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);

                    startActivityForResult(takePicture,PERMISSIONS_REQUEST_CAMERA);

                    Log.d(LOGTAG,Uri.fromFile(saveFile).toString());

                }
            }
        });


        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, PERMISSIONS_PICK_PICTURE_REQUEST);
            }
        });


        CompoundButton.OnCheckedChangeListener modeSwitchListener =
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Log.d(LOGTAG, "Switch = " + isChecked + " button press =" + modeSwitch.isPressed());

                if (isChecked) {
//                    modeSwitch.setChecked(false);
//                    modeSwitch.invalidate();
                    modeSwitchText.setText("Online Mode");
                } else {
//                    modeSwitch.setChecked(true);
//                    modeSwitch.invalidate();
                    modeSwitchText.setText("Offline Mode");
                }
            }
        };

        modeSwitch.setOnCheckedChangeListener(modeSwitchListener);

        Log.d(LOGTAG,"Set Listeners");

    }

    private void setDummyListeners(){
        final String permissionResuestText = "Please give permissions in Settings";

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,permissionResuestText,Toast.LENGTH_LONG).show();
            }
        });

        CompoundButton.OnCheckedChangeListener modeSwitchListener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        Log.d(LOGTAG, "Switch = " + isChecked + " button press =" + modeSwitch.isPressed());

                        if (isChecked) {
                            modeSwitchText.setText("Online Mode");
                        } else {
                            modeSwitchText.setText("Offline Mode");
                        }
                    }
                };

        modeSwitch.setOnCheckedChangeListener(modeSwitchListener);

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
        String s = String.valueOf(System.currentTimeMillis());
        String time = s.substring(5, s.length());

        String fileName = userId+"_"+time+".jpg";
        return fileName;

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu mMenu) {
//        menu = mMenu;
//        menuItem = menu.findItem(R.id.openCamera);
////        menuItem.setVisible(false);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.openCamera) {
//
//            File saveFile = new File(Environment.getExternalStorageDirectory(),
//                    getString(R.string.save_name));
//
//
//            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            if (saveFile != null){
//                Log.v(LOGTAG,BuildConfig.APPLICATION_ID);
//                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
//                        BuildConfig.APPLICATION_ID + ".provider",
//                        saveFile);
//
//
//                takePicture.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
//                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                takePicture.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
//                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//
//                startActivityForResult(takePicture,PERMISSIONS_REQUEST_CAMERA);
//
//                Log.v(LOGTAG,Uri.fromFile(saveFile).toString());
//                Log.v(LOGTAG,"Called an intent");
//                return true;
//
//            }
//
//            return false;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkAllPermissions(){
        if(!hasPermissions(this, PERMISSIONS)){
            Log.v(LOGTAG, "Permissions Missing and Requested");
//            openCamera.setClickable(false);
//            selectPicture.setClickable(false);
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else{
            Log.v(LOGTAG, "Permissions Granted");
            setListeners();
//            openCamera.setClickable(true);
//            selectPicture.setClickable(true);
//            modeSwitch.setClickable(true);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode,Intent data)//Called after the intent
    {
        super.onActivityResult(requestCode, resultCode,data);
        Bitmap thumbnail = null;
        Log.v(LOGTAG,"requestCode: "+requestCode);
        if (requestCode == PERMISSIONS_REQUEST_CAMERA && resultCode == RESULT_OK) {
            Log.d(LOGTAG,"clicked queryLocation: "+queryLocation);

            MediaScannerConnection.scanFile(this,
                    new String[]{queryLocation}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            Intent i = new Intent(this, CameraActivityInbuilt.class);
            i.putExtra("from","MainActivity");
            i.putExtra("runOffline",!modeSwitch.isChecked());
            i.putExtra("queryLocation",queryLocation);
            startActivity(i);
//            finish();
        }

        if (requestCode == PERMISSIONS_PICK_PICTURE_REQUEST  && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            queryLocation = cursor.getString(columnIndex);
            cursor.close();
            Log.d(LOGTAG,"selected queryLocation: "+queryLocation);

            Intent i = new Intent(this, CameraActivityInbuilt.class);
            i.putExtra("from","MainActivity");
            i.putExtra("runOffline",!modeSwitch.isChecked());
            i.putExtra("queryLocation",queryLocation);
            startActivity(i);

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Boolean openApplicationPermissions = false;
        if (grantResults.length > 0) {
            for(int grantResult: grantResults){
                if(grantResult != PackageManager.PERMISSION_GRANTED){
                    openApplicationPermissions = true;
                }
            }
        }

        if (openApplicationPermissions) {
            Log.v(LOGTAG, "openApplicationPermissions");
            openApplicationPermissions();
        }else{
//            openCamera.setClickable(true);
//            selectPicture.setClickable(true);
//            modeSwitch.setClickable(true);
            setListeners();
        }

    }


    private void openApplicationPermissions() {
        final Intent intent_permissions = new Intent();
        intent_permissions.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent_permissions.addCategory(Intent.CATEGORY_DEFAULT);
        intent_permissions.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));

        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        MainActivity.this.startActivity(intent_permissions);
    }








}
