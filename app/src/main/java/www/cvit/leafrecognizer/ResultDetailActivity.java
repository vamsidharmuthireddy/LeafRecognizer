package www.cvit.leafrecognizer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
    private TextView text_scientific_name;
    private CardView card_scientific_name;
    private TextView text_common_name;
    private CardView card_common_name;
    private TextView text_description;
    private CardView card_description;
    private TextView text_utility;
    private CardView card_utility;
    private LeafInfo leafInfo;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String language;
    private String leafName;
    private String imageUrl;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result_details);


        //we are getting tha name of the interest point that was clicked
        Intent intent = getIntent();
        leafName = intent.getStringExtra(getString(R.string.leaf_name));
        imageUrl = intent.getStringExtra(getString(R.string.image_url));

        //loading the relevant interest point
        leafInfo = LoadInterestPoint(leafName);
        Log.v(LOGTAG, "clicked leaf is " + leafName);

        checkAllPermissions();

    }

    private void setViews() {

        toolbar = (Toolbar) findViewById(R.id.coordinatorlayout_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setting up the interest point name as title on action bar in co-ordinator layout
        toolbar.setTitle(leafName.toUpperCase());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.coordinatorlayout_colltoolbar);
        collapsingToolbarLayout.setTitle(leafName.toUpperCase());
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarStyle);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarStyle);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorBlack));

        //setting up the interest point image as image in image view in co-ordinator layout
        imageView = (ImageView) findViewById(R.id.coordinatorlayout_imageview);

        card_scientific_name = (CardView) findViewById(R.id.result_scientific_name);
        text_scientific_name = (TextView) card_scientific_name.findViewById(R.id.cardview_text);

        card_common_name = (CardView) findViewById(R.id.result_common_name);
        text_common_name = (TextView) card_common_name.findViewById(R.id.cardview_text);

        card_description = (CardView) findViewById(R.id.result_description);
        text_description = (TextView) card_description.findViewById(R.id.cardview_text);

        card_utility = (CardView) findViewById(R.id.result_utility);
        text_utility = (TextView) card_utility.findViewById(R.id.cardview_text);

    }


    private void setListeners() {

        Log.v(LOGTAG, "Entered Monuments");

//        imageView.setImageBitmap(setBitmap);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(imageUrl).asBitmap()
                .placeholder(R.drawable.leaf).into(imageView);

        text_scientific_name.setText(leafInfo.getLeaf(getString(R.string.scientific_name_tag)));
        text_scientific_name.setGravity(Gravity.LEFT);

        text_common_name.setText(leafInfo.getLeaf(getString(R.string.common_name_tag)));
        text_common_name.setGravity(Gravity.LEFT);

        text_description.setText(leafInfo.getLeaf(getString(R.string.description_tag)));
        text_description.setGravity(Gravity.LEFT);

        text_utility.setText(leafInfo.getLeaf(getString(R.string.utility_tag)));
        text_utility.setGravity(Gravity.LEFT);

    }


    /**
     * Functioning of Back arrow shown in toolbar
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResultDetailActivity.this, ResultPrimaryActivity.class);
//        intent.putExtra(getString(R.string.package_name), packageName);
//        intent.putExtra(getString(R.string.package_name_en), packageName_en);

        super.onBackPressed();
        //startActivity(intent);

    }

    /**
     * This method checks for the clicked interest point by it's name in the database
     *
     * @param interestPointName Clicked interest point name
     * @return clicked LeafInfo object
     */
    public LeafInfo LoadInterestPoint(String interestPointName) {

        interestPointName = interestPointName;

        PackageReader reader;
        reader = new PackageReader(ResultDetailActivity.this);
        ArrayList<LeafInfo> leavesList;

        leavesList = reader.getLeafList();

        Log.v(LOGTAG, "clicked point is " + interestPointName);
        Log.v(LOGTAG, "leavesList size is " + leavesList.size());

        LeafInfo leafInfo;
        for (int i = 0; i < leavesList.size(); i++) {
            leafInfo = leavesList.get(i);
            //Log.v(LOGTAG, "Available titles are " + leafInfo.getMonument(getString(R.string.interest_point_title)).toLowerCase());
            if (leafInfo.getLeaf(getString(R.string.scientific_name_tag)).equals(interestPointName)) {
                return leafInfo;
            }
        }

        return null;
    }


    private void checkAllPermissions() {
        //Setting Location permissions
        if (checkLocationPermission()) {
            locationRequested = true;
            Log.v(LOGTAG, "ResultDetailActivity has Location permission");
        } else {
            Log.v(LOGTAG, "ResultDetailActivity Requesting Location permission");
            requestLocationPermission();
        }
        //Setting Storage permissions
        if (checkStoragePermission()) {
            storageRequested = true;
            Log.v(LOGTAG, "ResultDetailActivity has storage permission");
            setViews();
            setListeners();
//            setShowCaseViews();
        } else {
            Log.v(LOGTAG, "ResultDetailActivity Requesting storage permission");
            requestStoragePermission();
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

    protected boolean checkLocationPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //Toast.makeText(this, getString(R.string.storage_permission_request), Toast.LENGTH_LONG).show();

            Log.v(LOGTAG, "requestStoragePermission if");
            ActivityCompat.requestPermissions(ResultDetailActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


        } else {
            Log.v(LOGTAG, "requestStoragePermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(ResultDetailActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
    }

    protected void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            //toast to be shown while requesting permissions
            //Toast.makeText(this, getString(R.string.gps_permission_request), Toast.LENGTH_LONG).show();
            Log.v(LOGTAG, "requestLocationPermission if");
            ActivityCompat.requestPermissions(ResultDetailActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            Log.v(LOGTAG, "requestLocationPermission else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(ResultDetailActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    /**
     * if read/write permissions are not set, then request for them.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(LOGTAG, "requestCode = " + requestCode);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                locationRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(LOGTAG, "ResultDetailActivity has FINE GPS permission");
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "ResultDetailActivity does not have FINE GPS permission");
                    //Log.v(LOGTAG,"1");
                    totalPermissions = totalPermissions - 1;
                }
                break;

            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                locationRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(LOGTAG, "ResultDetailActivity has COARSE GPS permission");
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "ResultDetailActivity does not have COARSE GPS permission");
                    totalPermissions = totalPermissions - 1;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ResultDetailActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Toast to be shown while re-directing to settings
                        //Log.v(LOGTAG,"2 if");
                        //openApplicationPermissions();
                    } else {
                        //Log.v(LOGTAG,"2 else");
                        //openApplicationPermissions();
                    }
                }
                break;


            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                storageRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "ResultDetailActivity has READ storage permissions");
                    totalPermissions = totalPermissions + 1;
                    setViews();
                    setListeners();
//                    setShowCaseViews();

                } else {
                    //openApplicationPermissions();
                    Log.v(LOGTAG, "ResultDetailActivity does not have READ storage permissions");
                    //Log.v(LOGTAG,"3");
                    totalPermissions = totalPermissions - 1;

                }
                break;

            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                storageRequested = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "ResultDetailActivity has WRITE storage permissions");
                    totalPermissions = totalPermissions + 1;
                } else {
                    Log.v(LOGTAG, "ResultDetailActivity does not have WRITE storage permissions");
                    totalPermissions = totalPermissions - 1;
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ResultDetailActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //Log.v(LOGTAG,"4 if");
                        //openApplicationPermissions();
                    } else {
                        //Log.v(LOGTAG,"4 else");
                        //openApplicationPermissions();
                    }
                }
                break;

        }

        Log.v(LOGTAG, "totalPermissions = " + totalPermissions + " storageRequested = " + storageRequested + " locationRequested = " + locationRequested);
        if (totalPermissions <= 0 & storageRequested & locationRequested) {
            //Log.v(LOGTAG, "5");
            Log.v(LOGTAG, "openApplicationPermissions");
            openApplicationPermissions();
        }

    }

    private void openApplicationPermissions() {
        Toast.makeText(this, getString(R.string.all_permissions_open_settings), Toast.LENGTH_LONG).show();
        final Intent intent_permissions = new Intent();
        intent_permissions.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent_permissions.addCategory(Intent.CATEGORY_DEFAULT);
        intent_permissions.setData(Uri.parse("package:" + ResultDetailActivity.this.getPackageName()));

        //Disabling the following flag solved the premature calling of onActivityResult(http://stackoverflow.com/a/30882399/4983204)
        //if it doesnot work check here http://stackoverflow.com/a/22811103/4983204
        //intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent_permissions.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        ResultDetailActivity.this.startActivityForResult(intent_permissions, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(LOGTAG, "returned back from other activity " + requestCode + " " + resultCode);
        checkAllPermissions();
    }



}
