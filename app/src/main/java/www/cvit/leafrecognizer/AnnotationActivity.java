package www.cvit.leafrecognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AnnotationActivity extends AppCompatActivity {
    private String LOGTAG = "AnnotationActivity";

    private String[] resultString = new String[5];
    private String[] resultName = new String[5];
    private String[] result_url = new String[5];
    private ImageView queryImageView;
    private CardView result_0;
    private CardView result_1;
    private CardView result_2;
    private ImageView resultImageView_0;
    private ImageView resultImageView_1;
    private ImageView resultImageView_2;
    private TextView resultTextView_0;
    private TextView resultTextView_1;
    private TextView resultTextView_2;

    private LeafInfo leafInfo;
    private Toolbar toolbar;

    private String baseURL =
            "http://preon.iiit.ac.in/~vamsidhar_muthireddy/leaf_recognizer_router/title_images/";
    private String extension = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        Log.v(LOGTAG,"here in annotation");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Results");
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        super.onBackPressed();
        Intent intent = new Intent(AnnotationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadImages(){
        result_url = new String[5];
        result_url[0] = baseURL+resultString[0]+extension;
        result_url[1] = baseURL+resultString[1]+extension;
        result_url[2] = baseURL+resultString[2]+extension;

        Log.v(LOGTAG,result_url[0]);
        Log.v(LOGTAG,result_url[1]);
        Log.v(LOGTAG,result_url[2]);

        result_0 = (CardView)findViewById(R.id.result_1);
        result_1 = (CardView)findViewById(R.id.result_2);
        result_2 = (CardView)findViewById(R.id.result_3);

        resultImageView_0 = (ImageView) result_0.findViewById(R.id.result_image);
        resultImageView_1 = (ImageView) result_1.findViewById(R.id.result_image);
        resultImageView_2 = (ImageView) result_2.findViewById(R.id.result_image);

        Glide.with(this).load(result_url[0]).asBitmap()
                .placeholder(R.drawable.leaf).into(resultImageView_0);
        Glide.with(this).load(result_url[1]).asBitmap()
                .placeholder(R.drawable.leaf).into(resultImageView_1);
        Glide.with(this).load(result_url[2]).asBitmap()
                .placeholder(R.drawable.leaf).into(resultImageView_2);

        loadText();
    }

    private void loadText(){
        result_0 = (CardView)findViewById(R.id.result_1);
        result_1 = (CardView)findViewById(R.id.result_2);
        result_2 = (CardView)findViewById(R.id.result_3);

        resultTextView_0 = (TextView) result_0.findViewById(R.id.result_text);
        resultTextView_1 = (TextView) result_1.findViewById(R.id.result_text);
        resultTextView_2 = (TextView) result_2.findViewById(R.id.result_text);

//        String[] resultName = new String[5];
        loadleaf();

        resultTextView_0.setText(resultName[0]);
        resultTextView_1.setText(resultName[1]);
        resultTextView_2.setText(resultName[2]);

        setListeners();
    }

    private void setListeners(){
        result_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnnotationActivity.this,ResultDetailActivity.class);
                intent.putExtra(getString(R.string.leaf_name),resultName[0]);
                intent.putExtra(getString(R.string.image_url), result_url[0]);
                startActivity(intent);
            }
        });

        result_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnnotationActivity.this,ResultDetailActivity.class);
                intent.putExtra(getString(R.string.leaf_name),resultName[1]);
                intent.putExtra(getString(R.string.image_url), result_url[1]);
                startActivity(intent);
            }
        });

        result_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnnotationActivity.this,ResultDetailActivity.class);
                intent.putExtra(getString(R.string.leaf_name),resultName[2]);
                intent.putExtra(getString(R.string.image_url), result_url[2]);
                startActivity(intent);
            }
        });
    }

    private void loadleaf(){
        resultName = new String[5];
        PackageReader reader;

        reader = new PackageReader(AnnotationActivity.this);
        ArrayList<LeafInfo> leafList;


        leafList = reader.getLeafList();

        Log.v(LOGTAG, "interestPointsList size is " + leafList.size());

        LeafInfo leafInfo;
        for (int i = 0; i < leafList.size(); i++) {
            leafInfo = leafList.get(i);
            Log.v(LOGTAG, "Id " + leafInfo.getLeaf(getString(R.string.id_tag)));
            if (leafInfo.getLeaf(getString(R.string.id_tag)).equals(resultString[0])) {
                resultName[0] = leafInfo.getLeaf(getString(R.string.scientific_name_tag));
                Log.v("result1 ",resultName[0]);
            }
            if (leafInfo.getLeaf(getString(R.string.id_tag)).equals(resultString[1])) {
                resultName[1] = leafInfo.getLeaf(getString(R.string.scientific_name_tag));
                Log.v("result2 ",resultName[1]);
            }
            if (leafInfo.getLeaf(getString(R.string.id_tag)).equals(resultString[2])) {
                resultName[2] = leafInfo.getLeaf(getString(R.string.scientific_name_tag));
                Log.v("result2 ",resultName[2]);
            }
        }

        //ArrayList<InterestPoint> interestPointsList = new PackageContentActivity().giveMonumentList();

    }

}
