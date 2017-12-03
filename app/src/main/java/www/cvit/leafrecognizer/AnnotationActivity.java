package www.cvit.leafrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class AnnotationActivity extends AppCompatActivity {
    private String LOGTAG = "AnnotationActivity";

    private String[] resultString = new String[5];
    private ImageView queryImageView;
    private ImageView resultImageView_1;
    private ImageView resultImageView_2;
    private ImageView resultImageView_3;

    private String baseURL =
            "http://preon.iiit.ac.in/~vamsidhar_muthireddy/leaf_recognizer_router/title_images/";
    private String extension = "_0001.jpg";

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
        File imgFile = new  File(getIntent().getStringExtra("query_loc"));

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            queryImageView = (ImageView) findViewById(R.id.query_image);
            queryImageView.setImageBitmap(myBitmap);
//            Glide.with(this).load(myBitmap).into(queryImageView);

        }


        if(getIntent().hasExtra("resultString")) {
            String result = getIntent().getStringExtra("resultString");
            resultString = result.split("\t");
        }
        loadImages();
    }

//    @GlideModule
//    public final class MyAppGlideModule extends AppGlideModule {}

    private void loadImages(){
        resultImageView_1 = (ImageView)findViewById(R.id.result_image_1);
        resultImageView_2 = (ImageView)findViewById(R.id.result_image_2);
        resultImageView_3 = (ImageView)findViewById(R.id.result_image_3);

        String[] url = new String[5];
        url[0] = baseURL+resultString[0]+extension;
        url[1] = baseURL+resultString[1]+extension;
        url[2] = baseURL+resultString[2]+extension;

        Log.v(LOGTAG,url[0]);
        Log.v(LOGTAG,url[1]);
        Log.v(LOGTAG,url[2]);


        Glide.with(this).load(url[0]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_1);
        Glide.with(this).load(url[1]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_2);
        Glide.with(this).load(url[2]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_3);

    }



}
