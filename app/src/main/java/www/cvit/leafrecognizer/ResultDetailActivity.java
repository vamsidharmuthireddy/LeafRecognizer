package www.cvit.leafrecognizer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by vamsidhar on 20/2/18.
 */

public class ResultDetailActivity extends AppCompatActivity{

    /**
     * When an interest point is clicked, this class is called.
     * It sets the data on the interest point activity
     */
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView textview_info;
    private CardView text_card;
    private LeafInfo leafInfo;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String language;
    private String interestPointName;

    private String packageName;
    private String packageName_en;

    public ArrayList<String> ImageNamesList = new ArrayList<String>();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;

    private int totalPermissions = 0;
    private boolean storageRequested = false;
    private boolean locationRequested = false;

    private static final String LOGTAG = "ResultDetailActivity";
    private Boolean visible = false;
    private Boolean revealButtonDown = false;
    private Boolean galleryButtonDown = false;
    private Boolean mapsButtonDown = false;

    final Float animationdownScale = 0.9f;
    final Float animationUpScale = 1.25f;
    final Float animationNormalScale = 1.0f;
    final int animationScaleTime = 250;

    private ShowcaseView showcaseView;
    private Target viewTarget[];
    private String demoContent[];
    private String demoTitle[];
    private int demoNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Loading the language preference
//        LocaleManager localeManager = new LocaleManager(ResultDetailActivity.this);
//        localeManager.loadLocale();
//        language = Locale.getDefault().getLanguage();
        setContentView(R.layout.activity_result_details);

//        //we are getting the name of the session(Heritage site that user initially clicked to see)
////        sessionManager = new SessionManager();
////        packageName = sessionManager.getStringSessionPreferences(ResultDetailActivity.this,
////                getString(R.string.package_name), getString(R.string.default_package_value));
//
//        //we are getting tha name of the interest point that was clicked
//        Intent intent = getIntent();
//        interestPointName = intent.getStringExtra(getString(R.string.interestpoint_name));
////        packageName_en = intent.getStringExtra(getString(R.string.package_name_en));
////        interestPointType = intent.getStringExtra(getString(R.string.interest_point_type));
//
//        //loading the relevant interest point
//        leafInfo = LoadInterestPoint(packageName_en, interestPointName);
//        Log.v(LOGTAG, "clicked interest point is " + interestPointName.toUpperCase());
//
//        checkAllPermissions();

    }
//
//    private void setViews() {
//
//        toolbar = (Toolbar) findViewById(R.id.coordinatorlayout_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        //setting up the interest point name as title on action bar in co-ordinator layout
//        toolbar.setTitle(interestPointName.toUpperCase());
//        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
//
//
//        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.coordinatorlayout_colltoolbar);
//        collapsingToolbarLayout.setTitle(interestPointName.toUpperCase());
//        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
//        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.colorWhite));
//        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarStyle);
//        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarStyle);
//        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
//        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorBlack));
//
//        //setting up the interest point image as image in image view in co-ordinator layout
//        imageView = (ImageView) findViewById(R.id.coordinatorlayout_imageview);
//
//        text_card = (CardView) findViewById(R.id.monument_details_card);
//        textview_info = (TextView) text_card.findViewById(R.id.cardview_text);
//
//
//    }
//
//
////    private void setListeners() {
////
////
////        if (interestPointType.equals(getString(R.string.monument))) {
////
////            Log.v(LOGTAG, "Entered Monuments");
////            ImageNamesList = leafInfo.getMonumentImagePaths(ResultDetailActivity.this, packageName_en, interestPointName);
////
////            Bitmap setBitmap = leafInfo.getMonumentTitleImage(packageName_en, interestPointName, ResultDetailActivity.this);
////            //Log.v(LOGTAG, "Title Image = " + leafInfo.getMonumentTitleImagePath(packageName_en, interestPointName, ResultDetailActivity.this));
////            if (setBitmap == null) {
////                imageView.setImageBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.monument)).getBitmap());
////                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
////            } else {
////                imageView.setImageBitmap(setBitmap);
////                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
////            }
////
////            textview_info.setText(leafInfo.getMonument(getString(R.string.interest_point_info)));
////            textview_info.setGravity(Gravity.LEFT);
////
////
////            final View.OnTouchListener galleryTouchListener = new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(final View v, MotionEvent event) {
////                    switch (event.getAction()) {
////                        case MotionEvent.ACTION_DOWN:
////                            galleryButtonDown = true;
////                            //Log.v(LOGTAG,"scale in up is "+galleryButton.getScaleX());
////                            Log.v(LOGTAG, "Gallary Down Animation " + galleryButtonDown);
////                            galleryButton.clearAnimation();
////                            galleryButton.animate().scaleX(animationdownScale).scaleY(animationdownScale)
////                                    .setDuration(animationScaleTime / 2)
////                                    .setListener(new AnimatorListenerAdapter() {
////                                        @Override
////                                        public void onAnimationCancel(Animator animation) {
////                                            super.onAnimationCancel(animation);
////                                            Log.v(LOGTAG, "Gallery DOWN animation CANCEL");
////                                        }
////
////                                        @Override
////                                        public void onAnimationStart(Animator animation) {
////                                            super.onAnimationStart(animation);
////                                            Log.v(LOGTAG, "Gallery DOWN animation START");
////                                        }
////
////                                        @Override
////                                        public void onAnimationEnd(Animator animation) {
////                                            super.onAnimationEnd(animation);
////                                            Log.v(LOGTAG, "Gallary DOWN animation End " + galleryButtonDown);
////                                        }
////                                    })
////                                    .start();
////                            return true;
////
////                        case MotionEvent.ACTION_UP:
////                            galleryButtonDown = false;
////                            //Log.v(LOGTAG,"scale in down is "+galleryButton.getScaleX());
////                            Log.v(LOGTAG, "Gallary UP Triggered " + galleryButtonDown);
////                            galleryButton.animate().scaleX(animationUpScale).scaleY(animationUpScale)
////                                    .setDuration(animationScaleTime / 2)
////                                    .setListener(new AnimatorListenerAdapter() {
////                                        @Override
////                                        public void onAnimationCancel(Animator animation) {
////                                            super.onAnimationCancel(animation);
////                                            Log.v(LOGTAG, "Gallery UP animation CANCEL");
////                                        }
////
////                                        @Override
////                                        public void onAnimationStart(Animator animation) {
////                                            super.onAnimationStart(animation);
////                                            Log.v(LOGTAG, "Gallery UP animation START");
////                                        }
////
////                                        @Override
////                                        public void onAnimationEnd(Animator animation) {
////                                            super.onAnimationEnd(animation);
////                                            Log.v(LOGTAG, "Gallary UP animation End " + galleryButtonDown);
////                                            if (!galleryButtonDown) {
////                                                Log.v(LOGTAG, "Gallary Last Animation " + galleryButtonDown);
////
////                                                final Intent openGallery = new Intent(ResultDetailActivity.this, GalleryActivity.class);
////                                                openGallery.putExtra(getString(R.string.package_name), packageName);
////                                                openGallery.putExtra(getString(R.string.package_name_en), packageName_en);
////                                                openGallery.putExtra(getString(R.string.image_count), getString(R.string.interestpoint_name));
////                                                openGallery.putExtra(getString(R.string.interestpoint_name), interestPointName);
////                                                openGallery.putExtra(getString(R.string.interest_point_type), interestPointType);
////                                                openGallery.putStringArrayListExtra(getString(R.string.imageNamesList), ImageNamesList);
////                                                int startX = (int) v.getX();
////                                                int startY = (int) v.getY();
////                                                int width = v.getWidth();
////                                                int height = v.getHeight();
////                                                final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
////                                                PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
////                                                PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
////                                                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(galleryButton, scalex, scaley);
////                                                //anim.setRepeatCount(1);
////                                                //anim.setRepeatMode(ValueAnimator.REVERSE);
////                                                anim.setDuration(animationScaleTime / 2);
////                                                anim.addListener(new AnimatorListenerAdapter() {
////                                                    @Override
////                                                    public void onAnimationEnd(Animator animation) {
////                                                        super.onAnimationEnd(animation);
////
////                                                        //startActivity(openGallery, options.toBundle());
////                                                        if (!galleryButton.hasTransientState()) {
////                                                            startActivity(openGallery, options.toBundle());
////                                                        }
////                                                    }
////                                                });
////                                                anim.start();
////
////
////                                            }
////                                        }
////                                    })
////                                    .start();
////                            return true;
////                    }
////
////
////                    return false;//does not recognise any other touch events
////                }
////            };
////
////            galleryButton.setOnTouchListener(galleryTouchListener);
////
////            final View.OnTouchListener mapTouchListener = new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(final View v, MotionEvent event) {
////                    switch (event.getAction()) {
////                        case MotionEvent.ACTION_DOWN:
////                            mapsButtonDown = true;
////                            //Log.v(LOGTAG,"scale in up is "+galleryButton.getScaleX());
////                            Log.v(LOGTAG, "Map Down Animation " + mapsButtonDown);
////                            mapsButton.clearAnimation();
////                            mapsButton.animate().scaleX(animationdownScale).scaleY(animationdownScale)
////                                    .setDuration(animationScaleTime / 2)
////                                    .setListener(new AnimatorListenerAdapter() {
////                                        @Override
////                                        public void onAnimationStart(Animator animation) {
////                                            super.onAnimationStart(animation);
////                                            Log.v(LOGTAG, "Map DOWN animation START");
////                                        }
////
////                                        @Override
////                                        public void onAnimationCancel(Animator animation) {
////                                            super.onAnimationCancel(animation);
////                                            Log.v(LOGTAG, "Map DOWN animation CANCEL");
////                                        }
////
////                                        @Override
////                                        public void onAnimationEnd(Animator animation) {
////                                            super.onAnimationEnd(animation);
////                                            Log.v(LOGTAG, "Map DOWN animation END " + mapsButtonDown);
////                                            //mapsButton.animate().cancel();
////                                        }
////                                    })
////                                    .start();
////                            return true;
////                        //break;
////
////                        case MotionEvent.ACTION_UP:
////                            mapsButtonDown = false;
////                            //Log.v(LOGTAG,"scale in down is "+galleryButton.getScaleX());
////                            Log.v(LOGTAG, "Map UP Animation " + mapsButtonDown);
////                            mapsButton.animate().scaleX(animationUpScale).scaleY(animationUpScale)
////                                    .setDuration(animationScaleTime / 2)
////                                    .setListener(new AnimatorListenerAdapter() {
////                                        @Override
////                                        public void onAnimationCancel(Animator animation) {
////                                            super.onAnimationCancel(animation);
////                                            Log.v(LOGTAG, "Map UP animation CANCEL");
////                                        }
////
////                                        @Override
////                                        public void onAnimationStart(Animator animation) {
////                                            super.onAnimationStart(animation);
////                                            Log.v(LOGTAG, "Map UP animation START");
////                                        }
////
////                                        @Override
////                                        public void onAnimationEnd(Animator animation) {
////                                            super.onAnimationEnd(animation);
////                                            Log.v(LOGTAG, "Map UP animation END " + mapsButtonDown);
////                                            if (!mapsButtonDown) {
////                                                Log.v(LOGTAG, "Map Last Animation " + mapsButtonDown);
////                                                final Intent openMap = new Intent(ResultDetailActivity.this, MapsActivityGoogle.class);
////                                                openMap.putExtra(getString(R.string.package_name), packageName);
////                                                openMap.putExtra(getString(R.string.package_name_en), packageName_en);
////                                                openMap.putExtra(getString(R.string.interestpoint_name), interestPointName);
////                                                openMap.putExtra(getString(R.string.interest_point_type), interestPointType);
////                                                openMap.putExtra(getString(R.string.location_count), getString(R.string.specific));
////                                                Bundle bundle = new Bundle();
////                                                bundle.putSerializable(getString(R.string.monument_bundle), (Serializable) leafInfo);
////                                                openMap.putExtras(bundle);
////                                                int startX = (int) v.getX();
////                                                int startY = (int) v.getY();
////                                                int width = v.getWidth();
////                                                int height = v.getHeight();
////                                                final ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
////                                                PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
////                                                PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
////                                                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(mapsButton, scalex, scaley);
////                                                //anim.setRepeatCount(1);
////                                                //anim.setRepeatMode(ValueAnimator.REVERSE);
////                                                anim.setDuration(animationScaleTime / 2);
////
////                                                anim.addListener(new AnimatorListenerAdapter() {
////                                                    @Override
////                                                    public void onAnimationEnd(Animator animation) {
////                                                        super.onAnimationEnd(animation);
////
////                                                        if (!mapsButton.hasTransientState()) {
////                                                            startActivity(openMap, options.toBundle());
////                                                        }
////
////                                                    }
////                                                });
////                                                anim.start();
////                                            }
////                                        }
////                                    })
////                                    .start();
////                            return true;
////                    }
////
////
////                    return false;//does not recognise any other touch events
////                }
////            };
////
////            mapsButton.setOnTouchListener(mapTouchListener);
////
////            revealButton.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View v, MotionEvent event) {
////                    switch (event.getAction()) {
////                        case MotionEvent.ACTION_DOWN:
////                            revealButtonDown = true;
////                            //Log.v(LOGTAG,"scale in up is "+galleryButton.getScaleX());
////                            Log.v(LOGTAG, "Reveal DOWN animation " + revealButtonDown);
////
////                            galleryButton.clearAnimation();
////                            mapsButton.clearAnimation();
////                            revealButton.clearAnimation();
////                            revealButton.animate().scaleX(animationdownScale).scaleY(animationdownScale)
////                                    .setDuration(animationScaleTime / 2)
////                                    .setListener(new AnimatorListenerAdapter() {
////                                        @Override
////                                        public void onAnimationCancel(Animator animation) {
////                                            super.onAnimationCancel(animation);
////                                            Log.v(LOGTAG, "Reveal DOWN animation CANCEL");
////                                        }
////
////                                        @Override
////                                        public void onAnimationStart(Animator animation) {
////                                            super.onAnimationStart(animation);
////                                            Log.v(LOGTAG, "Reveal DOWN animation START");
////                                        }
////
////                                        @Override
////                                        public void onAnimationEnd(Animator animation) {
////                                            super.onAnimationEnd(animation);
////                                            Log.v(LOGTAG, "Reveal DOWN animation END " + revealButtonDown);
////                                        }
////                                    })
////                                    .start();
////                            return true;
////                        // break;
////
////                        case MotionEvent.ACTION_UP:
////                            revealButtonDown = false;
////                            //Log.v(LOGTAG,"scale in down is "+galleryButton.getScaleX());
////                            Log.v(LOGTAG, "Reveal UP animation " + revealButtonDown);
////                            revealButton.animate().scaleX(animationUpScale).scaleY(animationUpScale)
////                                    .setDuration(animationScaleTime / 2)
////                                    .setListener(new AnimatorListenerAdapter() {
////                                        @Override
////                                        public void onAnimationCancel(Animator animation) {
////                                            super.onAnimationCancel(animation);
////                                            Log.v(LOGTAG, "Reveal UP animation CANCEL");
////                                        }
////
////                                        @Override
////                                        public void onAnimationStart(Animator animation) {
////                                            super.onAnimationStart(animation);
////                                            Log.v(LOGTAG, "Reveal UP animation START");
////                                        }
////
////                                        @Override
////                                        public void onAnimationEnd(Animator animation) {
////                                            super.onAnimationEnd(animation);
////                                            Log.v(LOGTAG, "Reveal UP animation END " + revealButtonDown);
////                                            if (!revealButtonDown) {
////                                                Log.v(LOGTAG, "Reveal Last Animation " + revealButtonDown);
////                                                PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, animationNormalScale);
////                                                PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, animationNormalScale);
////                                                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(revealButton, scalex, scaley);
////                                                //anim.setRepeatCount(1);
////                                                //anim.setRepeatMode(ValueAnimator.REVERSE);
////                                                anim.setDuration(animationScaleTime / 2);
////                                                anim.start();
////
////                                                float buttonRotation = revealButton.getRotation();
////                                                Log.v(LOGTAG, "revealButton rotation = " + buttonRotation);
////                                                float targetRotation;
////                                                if (buttonRotation == 0) {
////                                                    targetRotation = -90;
////                                                    int margin = (int) getResources().getDimension(R.dimen.activity_std_margin);
////                                                    int width = revealButton.getWidth() + margin;
////                                                    Log.v(LOGTAG, galleryButton.getWidth() + " " + revealButton.getWidth() + " " + margin);
////                                                    galleryButton.setVisibility(View.VISIBLE);
////                                                    galleryButton.setOnTouchListener(null);
////                                                    galleryButton.setAlpha(0.0f);
////                                                    galleryButton.animate().translationX(0 - width).alpha(1.0f).setDuration(animationScaleTime)
////                                                            .setListener(new AnimatorListenerAdapter() {
////                                                                @Override
////                                                                public void onAnimationCancel(Animator animation) {
////                                                                    super.onAnimationCancel(animation);
////                                                                    Log.v(LOGTAG, "GALLERY VISIBLE TRANSITION CANCEL");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationStart(Animator animation) {
////                                                                    super.onAnimationStart(animation);
////                                                                    Log.v(LOGTAG, "GALLERY VISIBLE TRANSITION START");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationEnd(Animator animation) {
////                                                                    super.onAnimationEnd(animation);
////                                                                    galleryButton.setOnTouchListener(galleryTouchListener);
////                                                                    Log.v(LOGTAG, "GALLERY VISIBLE TRANSITION END");
////                                                                }
////                                                            })
////                                                            .start();
////                                                    mapsButton.setVisibility(View.VISIBLE);
////                                                    mapsButton.setAlpha(0.0f);
////                                                    mapsButton.setOnTouchListener(null);
////                                                    mapsButton.animate().translationX(0 - 2 * width).alpha(1.0f).setDuration(animationScaleTime)
////                                                            .setListener(new AnimatorListenerAdapter() {
////                                                                @Override
////                                                                public void onAnimationCancel(Animator animation) {
////                                                                    super.onAnimationCancel(animation);
////                                                                    Log.v(LOGTAG, "MAP VISIBLE TRANSITION CANCEL");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationStart(Animator animation) {
////                                                                    super.onAnimationStart(animation);
////                                                                    Log.v(LOGTAG, "MAP VISIBLE TRANSITION START");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationEnd(Animator animation) {
////                                                                    super.onAnimationEnd(animation);
////                                                                    mapsButton.setOnTouchListener(mapTouchListener);
////                                                                    Log.v(LOGTAG, "MAP VISIBLE TRANSITION END");
////                                                                }
////                                                            })
////                                                            .start();
////
////                                                } else {
////                                                    targetRotation = 0;
////                                                    galleryButton.setAlpha(1.0f);
////                                                    galleryButton.setOnTouchListener(null);
////                                                    galleryButton.animate().translationX(0).alpha(0.0f).setDuration(animationScaleTime)
////                                                            .setListener(new AnimatorListenerAdapter() {
////                                                                @Override
////                                                                public void onAnimationCancel(Animator animation) {
////                                                                    super.onAnimationCancel(animation);
////                                                                    Log.v(LOGTAG, "GALLERY GONE TRANSITION CANCEL");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationStart(Animator animation) {
////                                                                    super.onAnimationStart(animation);
////                                                                    Log.v(LOGTAG, "GALLERY GONE TRANSITION START");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationEnd(Animator animation) {
////                                                                    super.onAnimationEnd(animation);
////                                                                    galleryButton.setVisibility(View.GONE);
////                                                                    Log.v(LOGTAG, "GALLERY GONE TRANSITION END");
////                                                                }
////                                                            })
////                                                            .start();
////                                                    mapsButton.setAlpha(1.0f);
////                                                    mapsButton.setOnTouchListener(null);
////                                                    mapsButton.animate().translationX(0).alpha(0.0f).setDuration(animationScaleTime)
////                                                            .setListener(new AnimatorListenerAdapter() {
////                                                                @Override
////                                                                public void onAnimationCancel(Animator animation) {
////                                                                    super.onAnimationCancel(animation);
////                                                                    Log.v(LOGTAG, "MAPS GONE TRANSITION CANCEL");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationStart(Animator animation) {
////                                                                    super.onAnimationStart(animation);
////                                                                    Log.v(LOGTAG, "MAPS GONE TRANSITION START");
////                                                                }
////
////                                                                @Override
////                                                                public void onAnimationEnd(Animator animation) {
////                                                                    super.onAnimationEnd(animation);
////                                                                    mapsButton.setVisibility(View.GONE);
////                                                                    Log.v(LOGTAG, "MAPS GONE TRANSITION END");
////                                                                }
////                                                            })
////                                                            .start();
////                                                }
////                                                ObjectAnimator rotate = ObjectAnimator.ofFloat(revealButton, View.ROTATION, targetRotation);
////                                                rotate.setDuration(animationScaleTime).start();
////                                            }
////                                        }
////                                    })
////                                    .start();
////                            return true;
////                    }
////
////
////                    return false;//does not recognise any other touch events while processing one
////                }
////            });
////
////
////
////
////
////        } else {
////            Log.v(LOGTAG, "Entered kings");
////            Bitmap setBitmap = leafInfo.getKingTitleImage(packageName_en, interestPointName, ResultDetailActivity.this);
////
////            if (setBitmap == null) {
////                imageView.setImageBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.king)).getBitmap());
////                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
////            } else {
////                imageView.setImageBitmap(setBitmap);
////                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
////            }
////            textview_info.setText(leafInfo.getKing(getString(R.string.king_info)));
////            textview_info.setGravity(Gravity.LEFT);
////            revealButton.setVisibility(View.GONE);
////        }
////
////
////    }
//
//
//    /**
//     * Functioning of Back arrow shown in toolbar
//     *
//     * @return
//     */
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(ResultDetailActivity.this, ResultPrimaryActivity.class);
////        intent.putExtra(getString(R.string.package_name), packageName);
////        intent.putExtra(getString(R.string.package_name_en), packageName_en);
//
//        super.onBackPressed();
//        //startActivity(intent);
//
//    }
//
//    /**
//     * This method checks for the clicked interest point by it's name in the database
//     *
//     * @param packageName_en       Name of the Heritage site that usr chooses initially
//     * @param interestPointName Clicked interest point name
//     * @return clicked LeafInfo object
//     */
//    public LeafInfo LoadInterestPoint(String packageName_en, String interestPointName) {
//
//        interestPointName = interestPointName.toLowerCase();
//
//        PackageReader reader;
//        packageName_en = packageName_en.toLowerCase().replace("\\s", "");
//        reader = new PackageReader(packageName_en, ResultDetailActivity.this, language);
//        ArrayList<LeafInfo> interestPointsList;
//
//
//        interestPointsList = reader.getMonumentsList();
//
//        Log.v(LOGTAG, "clicked point is " + interestPointName);
//        Log.v(LOGTAG, "interestPointsList size is " + interestPointsList.size());
//
//
//        LeafInfo leafInfo;
//        for (int i = 0; i < interestPointsList.size(); i++) {
//            leafInfo = interestPointsList.get(i);
//            //Log.v(LOGTAG, "Available titles are " + leafInfo.getMonument(getString(R.string.interest_point_title)).toLowerCase());
//            if (leafInfo.getMonument(getString(R.string.interest_point_title)).toLowerCase().equals(interestPointName)) {
//                return leafInfo;
//            }
//        }
//
//        //ArrayList<LeafInfo> interestPointsList = new PackageContentActivity().giveMonumentList();
//
//
//        return null;
//    }
//
//
//    private void checkAllPermissions() {
//        //Setting Location permissions
//        if (checkLocationPermission()) {
//            locationRequested = true;
//            Log.v(LOGTAG, "ResultDetailActivity has Location permission");
//        } else {
//            Log.v(LOGTAG, "ResultDetailActivity Requesting Location permission");
//            requestLocationPermission();
//        }
//        //Setting Storage permissions
//        if (checkStoragePermission()) {
//            storageRequested = true;
//            Log.v(LOGTAG, "ResultDetailActivity has storage permission");
//            setViews();
////            setListeners();
////            setShowCaseViews();
//        } else {
//            Log.v(LOGTAG, "ResultDetailActivity Requesting storage permission");
//            requestStoragePermission();
//        }
//    }
//
//    /**
//     * Checking if read/write permissions are set or not
//     *
//     * @return
//     */
//    protected boolean checkStoragePermission() {
//        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
//        if (result == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    protected boolean checkLocationPermission() {
//        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
//        if (result == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    protected void requestStoragePermission() {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            //Toast.makeText(this, getString(R.string.storage_permission_request), Toast.LENGTH_LONG).show();
//
//            Log.v(LOGTAG, "requestStoragePermission if");
//            ActivityCompat.requestPermissions(ResultDetailActivity.this,
//                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//
//
//        } else {
//            Log.v(LOGTAG, "requestStoragePermission else");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions(ResultDetailActivity.this,
//                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//            }
//        }
//    }
//
//    protected void requestLocationPermission() {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//            //toast to be shown while requesting permissions
//            //Toast.makeText(this, getString(R.string.gps_permission_request), Toast.LENGTH_LONG).show();
//            Log.v(LOGTAG, "requestLocationPermission if");
//            ActivityCompat.requestPermissions(ResultDetailActivity.this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//
//        } else {
//            Log.v(LOGTAG, "requestLocationPermission else");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions(ResultDetailActivity.this,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            }
//        }
//    }
//
//    /**
//     * if read/write permissions are not set, then request for them.
//     */
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.v(LOGTAG, "requestCode = " + requestCode);
//
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
//                locationRequested = true;
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    Log.v(LOGTAG, "ResultDetailActivity has FINE GPS permission");
//                    totalPermissions = totalPermissions + 1;
//                } else {
//                    Log.v(LOGTAG, "ResultDetailActivity does not have FINE GPS permission");
//                    //Log.v(LOGTAG,"1");
//                    totalPermissions = totalPermissions - 1;
//                }
//                break;
//
//            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
//                locationRequested = true;
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    Log.v(LOGTAG, "ResultDetailActivity has COARSE GPS permission");
//                    totalPermissions = totalPermissions + 1;
//                } else {
//                    Log.v(LOGTAG, "ResultDetailActivity does not have COARSE GPS permission");
//                    totalPermissions = totalPermissions - 1;
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(ResultDetailActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        //Toast to be shown while re-directing to settings
//                        //Log.v(LOGTAG,"2 if");
//                        //openApplicationPermissions();
//                    } else {
//                        //Log.v(LOGTAG,"2 else");
//                        //openApplicationPermissions();
//                    }
//                }
//                break;
//
//
//            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
//                storageRequested = true;
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.v(LOGTAG, "ResultDetailActivity has READ storage permissions");
//                    totalPermissions = totalPermissions + 1;
//                    setViews();
////                    setListeners();
////                    setShowCaseViews();
//
//                } else {
//                    //openApplicationPermissions();
//                    Log.v(LOGTAG, "ResultDetailActivity does not have READ storage permissions");
//                    //Log.v(LOGTAG,"3");
//                    totalPermissions = totalPermissions - 1;
//
//                }
//                break;
//
//            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
//                storageRequested = true;
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.v(LOGTAG, "ResultDetailActivity has WRITE storage permissions");
//                    totalPermissions = totalPermissions + 1;
//                } else {
//                    Log.v(LOGTAG, "ResultDetailActivity does not have WRITE storage permissions");
//                    totalPermissions = totalPermissions - 1;
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(ResultDetailActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                        //Log.v(LOGTAG,"4 if");
//                        //openApplicationPermissions();
//                    } else {
//                        //Log.v(LOGTAG,"4 else");
//                        //openApplicationPermissions();
//                    }
//                }
//                break;
//
//        }
//
//        Log.v(LOGTAG, "totalPermissions = " + totalPermissions + " storageRequested = " + storageRequested + " locationRequested = " + locationRequested);
//        if (totalPermissions <= 0 & storageRequested & locationRequested) {
//            //Log.v(LOGTAG, "5");
//            Log.v(LOGTAG, "openApplicationPermissions");
//            openApplicationPermissions();
//        }
//
//    }
//
//    private void openApplicationPermissions() {
//        Toast.makeText(this, getString(R.string.all_permissions_open_settings), Toast.LENGTH_LONG).show();
//        final Intent intent_permissions = new Intent();
//        intent_permissions.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent_permissions.addCategory(Intent.CATEGORY_DEFAULT);
//        intent_permissions.setData(Uri.parse("package:" + ResultDetailActivity.this.getPackageName()));
//
//        //Disabling the following flag solved the premature calling of onActivityResult(http://stackoverflow.com/a/30882399/4983204)
//        //if it doesnot work check here http://stackoverflow.com/a/22811103/4983204
//        //intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//
//        ResultDetailActivity.this.startActivityForResult(intent_permissions, 100);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.v(LOGTAG, "returned back from other activity " + requestCode + " " + resultCode);
//        checkAllPermissions();
//    }



}
