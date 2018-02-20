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
import java.util.ArrayList;

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

    private LeafInfo leafInfo;

    private String baseURL =
            "http://preon.iiit.ac.in/~vamsidhar_muthireddy/leaf_recognizer_router/title_images/";
    private String extension = ".jpg";

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
            if (result==null){
                resultString[0] = "1";
                resultString[1] = "1";
                resultString[2] = "1";
            }else {
                resultString = result.split("\t");
            }

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

        resultImageView_1 = (ImageView)result_1.findViewById(R.id.result_image);
        resultImageView_2 = (ImageView)result_2.findViewById(R.id.result_image);
        resultImageView_3 = (ImageView)result_3.findViewById(R.id.result_image);

        Glide.with(this).load(url[0]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_1);
        Glide.with(this).load(url[1]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_2);
        Glide.with(this).load(url[2]).asBitmap()
                .placeholder(R.drawable.ic_leaf).into(resultImageView_3);

        loadText();
    }

    private void loadText(){
        result_1 = (CardView)findViewById(R.id.result_1);
        result_2 = (CardView)findViewById(R.id.result_2);
        result_3 = (CardView)findViewById(R.id.result_3);

        resultTextView_1 = (TextView)result_1.findViewById(R.id.result_text);
        resultTextView_2 = (TextView)result_2.findViewById(R.id.result_text);
        resultTextView_3 = (TextView)result_3.findViewById(R.id.result_text);

        String[] resultName = new String[5];
        resultName = loadleaf();

        resultTextView_1.setText(resultName[0]);
        resultTextView_2.setText(resultName[1]);
        resultTextView_3.setText(resultName[2]);
    }

    private String[] loadleaf(){
        String[] resultName = new String[5];
        PackageReader reader;

        reader = new PackageReader(AnnotationActivity.this);
        ArrayList<LeafInfo> leafList;


        leafList = reader.getLeafList();

        Log.v(LOGTAG, "interestPointsList size is " + leafList.size());

        LeafInfo leafInfo;
        for (int i = 0; i < leafList.size(); i++) {
            leafInfo = leafList.get(i);
            Log.v(LOGTAG, "Id " + leafInfo.getLeaf(getString(R.string.id)));
            if (leafInfo.getLeaf(getString(R.string.id)).equals(resultString[0])) {
                resultName[0] = leafInfo.getLeaf(getString(R.string.scientific_name));
                Log.v("result1 ",resultName[0]);
            }
            if (leafInfo.getLeaf(getString(R.string.id)).equals(resultString[1])) {
                resultName[1] = leafInfo.getLeaf(getString(R.string.scientific_name));
                Log.v("result2 ",resultName[1]);
            }
            if (leafInfo.getLeaf(getString(R.string.id)).equals(resultString[2])) {
                resultName[2] = leafInfo.getLeaf(getString(R.string.scientific_name));
                Log.v("result2 ",resultName[2]);
            }
        }

        //ArrayList<InterestPoint> interestPointsList = new PackageContentActivity().giveMonumentList();


        return resultName;
    }

}
