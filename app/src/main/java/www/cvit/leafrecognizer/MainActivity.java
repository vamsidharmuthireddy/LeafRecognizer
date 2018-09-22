package www.cvit.leafrecognizer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.ExifInterface;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    private static final int Click = 5;
    private static final int PERMISSIONS_REQUEST_CAMERA = 6;
    public static Menu menu;
    public static MenuItem menuItem;
    private int totalPermissions = 0;
    private boolean storageRequested = false;
    private boolean cameraRequested = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    public static ImageClassifier classifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);

        checkAllPermissions();



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu mMenu) {
        menu = mMenu;
        menuItem = menu.findItem(R.id.openCamera);
//        menuItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.openCamera) {
//            Intent takePicture = new Intent(MainActivity.this, CameraActivity.class);
//            startActivity(takePicture);

            File saveFile = new File(Environment.getExternalStorageDirectory(),
                    getString(R.string.save_name));
//            String saveName = Environment.getExternalStorageDirectory().toString()
//                    + File.separator + getString(R.string.save_name);


            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (saveFile != null){
//                Uri photoURI = Uri.fromFile(saveFile);
                Log.v(LOGTAG,BuildConfig.APPLICATION_ID);
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        saveFile);


                takePicture.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

                startActivityForResult(takePicture,PERMISSIONS_REQUEST_CAMERA);

                Log.v(LOGTAG,Uri.fromFile(saveFile).toString());
                Log.v(LOGTAG,"Called an intent");
                return true;

            }

            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAllPermissions() {
        //Setting Camera permissions
        if (checkCameraPermission()) {
            cameraRequested = true;
            Log.v(LOGTAG, "MainActivity has Camera permission");
            try {
                classifier = new ImageClassifier(MainActivity.this);
                Log.v(LOGTAG,"Intialized the Classifier");
            } catch (IOException e) {
                Log.e(LOGTAG, "Failed to initialize an image classifier.");
            }
        } else {
            Log.v(LOGTAG, "MainActivity Requesting Camera permission");
            requestCameraPermission();
        }
        //Setting Storage permissions
        if (checkStoragePermission()) {
            storageRequested = true;
            Log.v(LOGTAG, "MainActivity has storage permission");
        } else {
            Log.v(LOGTAG, "MainActivity Requesting storage permission");
            requestStoragePermission();
        }

        if (totalPermissions == 2 & cameraRequested & storageRequested){
            menuItem.setVisible(true);
        }

    }

    /**
     * Checking if read/write permissions are set or not
     *
     * @return
     */
    protected boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    protected void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //Toast.makeText(this, getString(R.string.storage_permission_request), Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            /*ImageButton button = (ImageButton) toolbarCard.findViewById(R.id.openCamera);
            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);*/
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            Log.v(LOGTAG, "requestStoragePermission if");

        } else {
            Log.v(LOGTAG, "requestStoragePermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    protected void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //toast to be shown while requesting permissions
            //Toast.makeText(this, getString(R.string.gps_permission_request), Toast.LENGTH_LONG).show();
            Log.v(LOGTAG, "requestCameraPermission if");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);

        } else {
            Log.v(LOGTAG, "requestCameraPermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode,Intent data)//Called after the intent
    {
        super.onActivityResult(requestCode, resultCode,data);
        Bitmap thumbnail = null;
        Log.v(LOGTAG,"requestCode: "+requestCode);
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {

            if (resultCode == RESULT_OK) {

//                thumbnail = (Bitmap) data.getExtras().get("data");
//                if(thumbnail != null){
//                    Log.v(LOGTAG,"data sent is not null");
//                }else{
//                    Log.v(LOGTAG,"data sent is null");
//                }

                Intent i = new Intent(this, CameraActivityInbuilt.class);
//                i.putExtra("outImage", thumbnail);
                i.putExtra("from","MainActivity");
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("classifier", classifier);
//                i.putExtras(bundle);
//                i.putExtra("classifier",classifier);
                startActivity(i);
                finish();



            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                storageRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    totalPermissions = totalPermissions + 1;
                    //LoadMyData(Environment.getExternalStorageDirectory().getAbsolutePath());
                } else {

                    Log.v("value", "Permission Denied, You cannot use local drive .");
                    totalPermissions = totalPermissions - 1;
                }
                break;

            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                storageRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "MainActivity has WRITE storage permissions");
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "MainActivity does not have WRITE storage permissions");
                    totalPermissions = totalPermissions - 1;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
                        //openApplicationPermissions();
                    } else {
                        //openApplicationPermissions();
                    }
                }
                break;

            case PERMISSIONS_REQUEST_CAMERA:
                cameraRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "MainActivity has Camera permissions");
                    try {
                        classifier = new ImageClassifier(MainActivity.this);
                        Log.v(LOGTAG,"Intialized the Classifier");
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Failed to initialize an image classifier.");
                    }
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "MainActivity does not have Camera permissions");
                    totalPermissions = totalPermissions - 1;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                        //Log.v(LOGTAG,"4 if");
                        //openApplicationPermissions();
                    } else {
                        //Log.v(LOGTAG,"4 else");
                        //openApplicationPermissions();
                    }
                }
                break;


        }

        Log.v(LOGTAG, "totalPermissions = " + totalPermissions + " storageRequested = " + storageRequested + " cameraRequested = " + cameraRequested);
        if (totalPermissions <= 0 & storageRequested & cameraRequested) {
            //Log.v(LOGTAG, "5");
            Log.v(LOGTAG, "openApplicationPermissions");
            openApplicationPermissions();
        }

        if (totalPermissions == 2 & cameraRequested & storageRequested){
            menuItem.setVisible(true);
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
