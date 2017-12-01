package www.cvit.leafrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class AnnotationActivity extends AppCompatActivity {
    private String LOGTAG = "AnnotationActivity";

    private String[] results = new String[5];
    private ImageView queryImageView;
    private ImageView resultImageView_1;
    private ImageView resultImageView_2;
    private ImageView resultImageView_3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        Log.v(LOGTAG,"here in annotation");
//        if(getIntent().hasExtra("outImage")) {
//            Log.v(LOGTAG,"There is data");
//            Bitmap result_bitmap = getIntent().getExtras().getParcelable("outImage");
//            if(result_bitmap != null){
//                Log.v(LOGTAG,"data received is not null");
//            }else{
//                Log.v(LOGTAG,"data received is null");
//            }
//            resultImageView = (ImageView) findViewById(R.id.result_image);
//            resultImageView.setImageBitmap(result_bitmap);
//        }


//        if(getIntent().hasExtra("outImage")) {
//            resultImageView_1 = (ImageView)findViewById(R.id.result_image);
//            Bitmap result_bitmap = BitmapFactory.decodeByteArray(
//                    getIntent().getByteArrayExtra("outImage"),
//                    0,getIntent().getByteArrayExtra("outImage").length);
//            resultImageView_1.setImageBitmap(result_bitmap);
//        }
//
//        String resLocation = getIntent().getStringExtra("res_loc");
//
//        File imgFile = new  File(getIntent().getStringExtra("query_loc"));
//
//        if(imgFile.exists()){
//
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            queryImageView = (ImageView) findViewById(R.id.query_image);
//            queryImageView.setImageBitmap(myBitmap);
//
//        }


        if(getIntent().hasExtra("resultLocation")) {
            results = getIntent().getStringArrayExtra("resultLocation");
        }
        loadImages();
    }

    private void loadImages(){
        resultImageView_1 = (ImageView)findViewById(R.id.result_image);
        resultImageView_2 = (ImageView)findViewById(R.id.result_image_2);
        resultImageView_3 = (ImageView)findViewById(R.id.result_image_3);

        Glide.with(this).asBitmap().load(results[0]).into(resultImageView_1);

        Glide.with(this).asBitmap().load(results[1]).into(resultImageView_2);

        Glide.with(this).asBitmap().load(results[2]).into(resultImageView_3);

    }



}
