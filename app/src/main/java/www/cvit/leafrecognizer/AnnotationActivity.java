package www.cvit.leafrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

public class AnnotationActivity extends AppCompatActivity {
    private String LOGTAG = "AnnotationActivity";

    private String[] resultString = new String[5];
    private ImageView queryImageView;
    private CardView result_1;
    private CardView result_2;
    private CardView result_3;
    private ImageView resultImageView_1;
    private ImageView resultImageView_2;
    private ImageView resultImageView_3;
    private TextView resultTextView_1;
    private TextView resultTextView_2;
    private TextView resultTextView_3;

    private String baseURL =
            "http://preon.iiit.ac.in/~vamsidhar_muthireddy/leaf_recognizer_router/title_images/";
    private String extension = "_0001.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        Log.v(LOGTAG,"here in annotation");

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
        String[] url = new String[5];
        url[0] = baseURL+resultString[0]+extension;
        url[1] = baseURL+resultString[1]+extension;
        url[2] = baseURL+resultString[2]+extension;

        Log.v(LOGTAG,url[0]);
        Log.v(LOGTAG,url[1]);
        Log.v(LOGTAG,url[2]);


        result_1 = (CardView)findViewById(R.id.result_1);
        result_2 = (CardView)findViewById(R.id.result_2);
        result_3 = (CardView)findViewById(R.id.result_3);

        resultTextView_1 = (TextView)result_1.findViewById(R.id.result_text);
        resultTextView_2 = (TextView)result_2.findViewById(R.id.result_text);
        resultTextView_3 = (TextView)result_3.findViewById(R.id.result_text);

        resultTextView_1.setText("This is type "+ resultString[0]);
        resultTextView_2.setText("This is type "+resultString[1]);
        resultTextView_3.setText("This is type "+resultString[2]);

        resultImageView_1 = (ImageView)result_1.findViewById(R.id.result_image);
        resultImageView_2 = (ImageView)result_2.findViewById(R.id.result_image);
        resultImageView_3 = (ImageView)result_3.findViewById(R.id.result_image);


        Glide.with(this).load(url[0]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_1);
        Glide.with(this).load(url[1]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_2);
        Glide.with(this).load(url[2]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_3);

    }



}
