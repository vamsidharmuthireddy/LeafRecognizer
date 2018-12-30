package www.cvit.leafrecognizer;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ActivityOptions;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnShowcaseEventListener {

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
    private Button openWebsite;
    private SwitchCompat modeSwitch;
    private TextView modeSwitchText;
    public static String queryLocation;
    private String userId;

    private SessionManager sessionManager;
    private ShowcaseView showcaseView;
    private String showcaseKey = "demo_main_screen";

    private Boolean openWebsiteButtonDown = false;
    private Boolean openCameraButtonDown = false;
    private Boolean selectPictureButtonDown = false;
    final Float animationdownScale = 0.9f;
    final Float animationUpScale = 1.25f;
    final Float animationNormalScale = 1.0f;
    final int animationScaleTime = 250;

    private FloatingActionButton infoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();
        setShowCaseViews();
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
        modeSwitch = (SwitchCompat) findViewById(R.id.modeSwitch);
        modeSwitchText = (TextView) findViewById(R.id.modeSwitchText);
        selectPicture = (Button) findViewById(R.id.selectPicture);
        openWebsite = findViewById(R.id.open_website);
        infoButton = findViewById(R.id.info_button);


        if(modeSwitch.isChecked()){
            modeSwitchText.setText("Online Mode");
        }else{
            modeSwitchText.setText("Offline Mode");
        }

        openCamera.setClickable(true);
        selectPicture.setClickable(true);
        modeSwitch.setClickable(true);
        infoButton.setClickable(true);
        openWebsite.setClickable(true);

        Log.d(LOGTAG,"Set Views");

    }

    private void setListeners(){

        final View.OnTouchListener openCameraTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        openCameraButtonDown = true;
                        Log.v(LOGTAG, "openCamera Down Animation " + openCameraButtonDown);
                        openCamera.clearAnimation();
                        openCamera.animate().scaleX(animationdownScale).scaleY(animationdownScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation End " + openCameraButtonDown);
                                    }
                                })
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        openCameraButtonDown = false;
                        Log.v(LOGTAG, "openCamera UP Triggered " + openCameraButtonDown);
                        openCamera.animate().scaleX(animationUpScale).scaleY(animationUpScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "openCamera UP animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "openCamera UP animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "openCamera UP animation End " + openCameraButtonDown);
                                        if (!openCameraButtonDown) {
                                            Log.v(LOGTAG, "openCamera Last Animation " + openCameraButtonDown);
                                            Log.v(LOGTAG,"classifier "+classifier);
                                            String fileName = generateFileName();

//                                            File saveFile = new File(Environment.getExternalStorageDirectory(),
//                                                    fileName);

                                            String saveFolder = Environment.getExternalStorageDirectory().toString()
                                                    +File.separator+BuildConfig.APPLICATION_ID+File.separator;
                                            if(!new File(saveFolder).exists()){
                                                new File(saveFolder).mkdirs();
                                            }

                                            File saveFile = new File(saveFolder+fileName);


                                            queryLocation = saveFile.getAbsolutePath();
                                            final Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                            if (saveFile != null){
                                                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                                        BuildConfig.APPLICATION_ID + ".provider",
                                                        saveFile);

                                                takePicture.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
                                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                                takePicture.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);

//                                                startActivityForResult(takePicture,PERMISSIONS_REQUEST_CAMERA);

                                                Log.d(LOGTAG,Uri.fromFile(saveFile).toString());

                                            }
                                            int startX = (int) v.getX();
                                            int startY = (int) v.getY();
                                            int width = v.getWidth();
                                            int height = v.getHeight();
                                            final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
                                            PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
                                            PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
                                            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(openCamera, scalex, scaley);
                                            //anim.setRepeatCount(1);
                                            //anim.setRepeatMode(ValueAnimator.REVERSE);
                                            anim.setDuration(animationScaleTime / 2);
                                            anim.addListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);

                                                    //startActivity(openGallery, options.toBundle());
                                                    if (!openCamera.hasTransientState()) {
//                                                        startActivity(takePicture, options.toBundle());
                                                        startActivityForResult(takePicture,PERMISSIONS_REQUEST_CAMERA);

                                                    }
                                                }
                                            });
                                            anim.start();


                                        }
                                    }
                                })
                                .start();
                        return true;
                }

                return false;//does not recognise any other touch events
            }
        };

        openCamera.setOnTouchListener(openCameraTouchListener);

        final View.OnTouchListener selectPictureOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectPictureButtonDown = true;
                        Log.v(LOGTAG, "selectPicture Down Animation " + selectPictureButtonDown);
                        selectPicture.clearAnimation();
                        selectPicture.animate().scaleX(animationdownScale).scaleY(animationdownScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation End " + selectPictureButtonDown);
                                    }
                                })
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        selectPictureButtonDown = false;
                        Log.v(LOGTAG, "selectPicture UP Triggered " + selectPictureButtonDown);
                        selectPicture.animate().scaleX(animationUpScale).scaleY(animationUpScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "selectPicture UP animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "selectPicture UP animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "selectPicture UP animation End " + selectPictureButtonDown);
                                        if (!selectPictureButtonDown) {
                                            Log.v(LOGTAG, "selectPicture Last Animation " + selectPictureButtonDown);
                                            final Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                            int startX = (int) v.getX();
                                            int startY = (int) v.getY();
                                            int width = v.getWidth();
                                            int height = v.getHeight();
                                            final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
                                            PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
                                            PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
                                            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(selectPicture, scalex, scaley);
                                            //anim.setRepeatCount(1);
                                            //anim.setRepeatMode(ValueAnimator.REVERSE);
                                            anim.setDuration(animationScaleTime / 2);
                                            anim.addListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);

                                                    //startActivity(openGallery, options.toBundle());
                                                    if (!selectPicture.hasTransientState()) {
                                                        startActivityForResult(pickPhotoIntent, PERMISSIONS_PICK_PICTURE_REQUEST);
                                                    }
                                                }
                                            });
                                            anim.start();


                                        }
                                    }
                                })
                                .start();
                        return true;
                }

                return false;//does not recognise any other touch events
            }
        };

        selectPicture.setOnTouchListener(selectPictureOnTouchListener);





















        final View.OnTouchListener openWebsiteTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        openWebsiteButtonDown = true;
                        Log.v(LOGTAG, "openWebsite Down Animation " + openWebsiteButtonDown);
                        openWebsite.clearAnimation();
                        openWebsite.animate().scaleX(animationdownScale).scaleY(animationdownScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "openWebsite DOWN animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "openWebsite DOWN animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "openWebsite DOWN animation End " + openWebsiteButtonDown);
                                    }
                                })
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        openWebsiteButtonDown = false;
                        Log.v(LOGTAG, "openWebsite UP Triggered " + openWebsiteButtonDown);
                        openWebsite.animate().scaleX(animationUpScale).scaleY(animationUpScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "openWebsite UP animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "openWebsite UP animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "openWebsite UP animation End " + openWebsiteButtonDown);
                                        if (!openWebsiteButtonDown) {
                                            Log.v(LOGTAG, "openWebsite Last Animation " + openWebsiteButtonDown);


                                            final Intent openWebsiteIntent = new Intent(MainActivity.this, WebsiteActivity.class);

//                                             Uncomment these to open the website using a browser
//                                            final Intent openWebsiteIntent = new Intent(Intent.ACTION_VIEW);
//                                            String url = getString(R.string.leaf_portal_link);
//                                            openWebsiteIntent.setData(Uri.parse(url));

                                            int startX = (int) v.getX();
                                            int startY = (int) v.getY();
                                            int width = v.getWidth();
                                            int height = v.getHeight();
                                            final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
                                            PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
                                            PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
                                            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(openWebsite, scalex, scaley);
                                            //anim.setRepeatCount(1);
                                            //anim.setRepeatMode(ValueAnimator.REVERSE);
                                            anim.setDuration(animationScaleTime / 2);
                                            anim.addListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);

                                                    //startActivity(openGallery, options.toBundle());
                                                    if (!openWebsite.hasTransientState()) {
                                                        startActivity(openWebsiteIntent);
                                                    }
                                                }
                                            });
                                            anim.start();


                                        }
                                    }
                                })
                                .start();
                        return true;
                }

                return false;//does not recognise any other touch events
            }
        };

        openWebsite.setOnTouchListener(openWebsiteTouchListener);



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

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ActivityInstructions.class);
                startActivity(i);

            }
        });

        Log.d(LOGTAG,"Set Listeners");

    }

    private void setDummyListeners(){
        final String permissionRequestText = "Please give required permissions in Settings";

        final View.OnTouchListener openCameraTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        openCameraButtonDown = true;
                        Log.v(LOGTAG, "openCamera Down Animation " + openCameraButtonDown);
                        openCamera.clearAnimation();
                        openCamera.animate().scaleX(animationdownScale).scaleY(animationdownScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "openCamera DOWN animation End " + openCameraButtonDown);
                                    }
                                })
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        openCameraButtonDown = false;
                        Log.v(LOGTAG, "openCamera UP Triggered " + openCameraButtonDown);
                        openCamera.animate().scaleX(animationUpScale).scaleY(animationUpScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "openCamera UP animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "openCamera UP animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "openCamera UP animation End " + openCameraButtonDown);
                                        if (!openCameraButtonDown) {
                                            Log.v(LOGTAG, "openCamera Last Animation " + openCameraButtonDown);


                                            int startX = (int) v.getX();
                                            int startY = (int) v.getY();
                                            int width = v.getWidth();
                                            int height = v.getHeight();
                                            final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
                                            PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
                                            PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
                                            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(openCamera, scalex, scaley);
                                            //anim.setRepeatCount(1);
                                            //anim.setRepeatMode(ValueAnimator.REVERSE);
                                            anim.setDuration(animationScaleTime / 2);
                                            anim.addListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);

                                                    //startActivity(openGallery, options.toBundle());
                                                    if (!openCamera.hasTransientState()) {
                                                        Toast.makeText(MainActivity.this,permissionRequestText,Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                            anim.start();


                                        }
                                    }
                                })
                                .start();
                        return true;
                }

                return false;//does not recognise any other touch events
            }
        };

        openCamera.setOnTouchListener(openCameraTouchListener);

        final View.OnTouchListener selectPictureOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectPictureButtonDown = true;
                        Log.v(LOGTAG, "selectPicture Down Animation " + selectPictureButtonDown);
                        selectPicture.clearAnimation();
                        selectPicture.animate().scaleX(animationdownScale).scaleY(animationdownScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "selectPicture DOWN animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "selectPicture DOWN animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "selectPicture DOWN animation End " + selectPictureButtonDown);
                                    }
                                })
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        selectPictureButtonDown = false;
                        Log.v(LOGTAG, "selectPicture UP Triggered " + selectPictureButtonDown);
                        selectPicture.animate().scaleX(animationUpScale).scaleY(animationUpScale)
                                .setDuration(animationScaleTime / 2)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationCancel(animation);
                                        Log.v(LOGTAG, "selectPicture UP animation CANCEL");
                                    }

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        Log.v(LOGTAG, "selectPicture UP animation START");
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Log.v(LOGTAG, "selectPicture UP animation End " + selectPictureButtonDown);
                                        if (!selectPictureButtonDown) {
                                            Log.v(LOGTAG, "selectPicture Last Animation " + selectPictureButtonDown);

                                            int startX = (int) v.getX();
                                            int startY = (int) v.getY();
                                            int width = v.getWidth();
                                            int height = v.getHeight();
                                            final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
                                            PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
                                            PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
                                            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(selectPicture, scalex, scaley);
                                            //anim.setRepeatCount(1);
                                            //anim.setRepeatMode(ValueAnimator.REVERSE);
                                            anim.setDuration(animationScaleTime / 2);
                                            anim.addListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);

                                                    //startActivity(openGallery, options.toBundle());
                                                    if (!selectPicture.hasTransientState()) {
                                                        Toast.makeText(MainActivity.this,permissionRequestText,Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                            anim.start();


                                        }
                                    }
                                })
                                .start();
                        return true;
                }

                return false;//does not recognise any other touch events
            }
        };

        selectPicture.setOnTouchListener(selectPictureOnTouchListener);


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
        Toast.makeText(MainActivity.this, "Please give necessary permissions",Toast.LENGTH_LONG);
        final Intent intent_permissions = new Intent();
        intent_permissions.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent_permissions.addCategory(Intent.CATEGORY_DEFAULT);
        intent_permissions.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));

        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        MainActivity.this.startActivity(intent_permissions);
    }


    private Target viewTarget[];
    private String demoContent[];
    private String demoTitle[];
    private int demoNumber = 0;

    private void setShowCaseViews() {
        SessionManager sessionManager = new SessionManager();
        boolean showDemo = sessionManager.getBooleanSessionPreferences(MainActivity.this, showcaseKey, false);

//        boolean showDemo = false;

        if (!showDemo) {
            Log.v(LOGTAG, "Current demo number is initial");
            viewTarget = new ViewTarget[10];
            viewTarget[0] = new ViewTarget(findViewById(R.id.openCamera));
            viewTarget[1] = new ViewTarget(findViewById(R.id.selectPicture));
            viewTarget[2] = new ViewTarget(findViewById(R.id.modeSwitch));
            viewTarget[3] = new ViewTarget(findViewById(R.id.modeSwitch));
            viewTarget[4] = new ViewTarget(findViewById(R.id.open_website));


            demoContent = new String[10];
            demoContent[0] = getString(R.string.intro_inst4);
            demoContent[1] = getString(R.string.intro_inst5);
            demoContent[2] = getString(R.string.intro_inst2);
            demoContent[3] = getString(R.string.intro_inst3);
            demoContent[4] = getString(R.string.intro_inst8);

            demoTitle = new String[10];
            demoTitle[0] = getString(R.string.camera);
            demoTitle[1] = getString(R.string.gallery);
            demoTitle[2] = getString(R.string.online_mode);
            demoTitle[3] = getString(R.string.offline_mode);
            demoTitle[4] = getString(R.string.web);


            String initialTitle = getString(R.string.basepackagename);
            String initialContent = getString(R.string.intro_inst1);

            showcaseView = new ShowcaseView.Builder(MainActivity.this)
                    .blockAllTouches()
                    .setContentTitle(initialTitle)
                    .setContentText(initialContent)
                    .setTarget(Target.NONE)
                    .withNewStyleShowcase()
                    .setOnClickListener(this)
                    .setShowcaseEventListener(this)
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .build();

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
            sessionManager.setSessionPreferences(MainActivity.this, showcaseKey, true);
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
