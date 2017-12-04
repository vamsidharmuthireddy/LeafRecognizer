package www.cvit.leafrecognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by vamsidhar on 4/12/17.
 */

public class CameraActivityInbuilt extends AppCompatActivity {
    private Bitmap queryImage;
    private String LOGTAG = "CameraActivityInbuilt";
    public static Menu menu;
    public static MenuItem menuItem;
    private CropImageView cropImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_inbuilt);

//        if(getIntent().hasExtra("outImage")) {
//            Log.v(LOGTAG,"There is data");
//            Bitmap result_bitmap = getIntent().getExtras().getParcelable("outImage");
//            if(result_bitmap != null){
//                Log.v(LOGTAG,"data received is not null");
//            }else{
//                Log.v(LOGTAG,"data received is null");
//            }
//            queryImage = result_bitmap;
//        }else{
//            Log.v(LOGTAG,"There is no data");
//        }

        String saveName = Environment.getExternalStorageDirectory().toString()
                + File.separator + getString(R.string.save_name);

        getBitmap(saveName);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
//        cropImageView.setGuidelines(1);
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
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
//        menuItem.setVisible(false);
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

            saveImage(croppedImage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getBitmap(String path) {
        try {
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

    private void saveImage(Bitmap cropped){
        File croppedFile = new File(Environment.getExternalStorageDirectory(),
                getString(R.string.save_name));
        if (croppedFile == null) {
            Log.v(LOGTAG, "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(croppedFile);
            cropped.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            Log.v(LOGTAG,"croppedFile: "+croppedFile.toString());
            fos.close();
            sendToServer(croppedFile);
        } catch (FileNotFoundException e) {
            Log.v(LOGTAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.v(LOGTAG, "Error accessing file: " + e.getMessage());
        }
    }

    private void sendToServer(File croppedFile){
        ContactServer contactServer = new ContactServer(this, this);
        contactServer.execute(croppedFile.toString());
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    /** Start pick image activity with chooser. */
//    public void onSelectImageClick(View view) {
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setActivityTitle("My Crop")
//                .setCropShape(CropImageView.CropShape.OVAL)
//                .setCropMenuCropButtonTitle("Done")
//                .setRequestedSize(400, 400)
//                .setCropMenuCropButtonIcon(R.drawable.ic_leaf)
//                .start(this);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        // handle result of CropImageActivity
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
//                Toast.makeText(
//                        this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
//                        .show();
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}