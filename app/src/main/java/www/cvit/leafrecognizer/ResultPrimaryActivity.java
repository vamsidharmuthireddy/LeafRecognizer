package www.cvit.leafrecognizer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by vamsidhar on 15/2/18.
 */

public class ResultPrimaryActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private static final String LOGTAG = "ResultPrimaryActivity";

    private String[] resultString = new String[10];
    private String[] resultName = new String[10];

    private ImageView queryImageView;
    private Toolbar toolbar;

    private ArrayList<LeafInfo> leafInfo;


    private File imgFile;

    private Boolean runOffline ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_primary);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Results");
        toolbar.setBackgroundColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgFile = new  File(getIntent().getStringExtra("query_loc"));

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            queryImageView = (ImageView) findViewById(R.id.query_image);
            queryImageView.setImageBitmap(myBitmap);
//            Glide.with(this).load(myBitmap).into(queryImageView);

            queryImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openImage = new Intent(ResultPrimaryActivity.this,
                            FullScreenImageActivity.class);
                    openImage.putExtra("imageURL", getIntent().getStringExtra("query_loc"));
                    openImage.putExtra("query", "true");
                    openImage.putExtra("drawableLoc",R.drawable.leaf);
                    startActivity(openImage);
                }
            });

        }


        runOffline =  getIntent().getExtras().getBoolean("runOffline");

        if(getIntent().hasExtra("resultString")) {
            String result = getIntent().getStringExtra("resultString");


            Log.v(LOGTAG,"result = "+result);
            if (result==null){
                for (int i=0;i<resultString.length;i++){
                    resultString[i] = "1";
                }
            }else {
                resultString = result.split("\t");
            }


        }

        loadleaf();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_result_primary);
        recyclerView.setHasFixedSize(true);
        //recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewLayoutManager = new PreLoadingLinearLayoutManager(ResultPrimaryActivity.this);
        new PreLoadingLinearLayoutManager(ResultPrimaryActivity.this).setPages(1);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new ResultPrimaryAdapter(ResultPrimaryActivity.this,
                                leafInfo, resultString, runOffline);

//        recyclerViewAdapter = new ResultPrimaryAdapter(ResultPrimaryActivity.this);
        recyclerViewAdapter.setHasStableIds(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public static class PreLoadingLinearLayoutManager extends LinearLayoutManager {
        private int mPages = 1;
        private OrientationHelper mOrientationHelper;

        public PreLoadingLinearLayoutManager(Context context) {
            super(context);
        }

        public PreLoadingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public PreLoadingLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void setOrientation(final int orientation) {
            super.setOrientation(orientation);
            mOrientationHelper = null;
        }

        /**
         * Set the number of pages of layout that will be preloaded off-screen,
         * a page being a pixel measure equivalent to the on-screen size of the
         * recycler view.
         *
         * @param pages the number of pages; can be {@code 0} to disable preloading
         */
        public void setPages(final int pages) {
            this.mPages = pages;
        }

        @Override
        protected int getExtraLayoutSpace(final RecyclerView.State state) {
            if (mOrientationHelper == null) {
                mOrientationHelper = OrientationHelper.createOrientationHelper(this, getOrientation());
            }
            return mOrientationHelper.getTotalSpace() * mPages;
        }
    }


    private void loadleaf(){
        PackageReader reader;

        reader = new PackageReader(ResultPrimaryActivity.this);

        leafInfo = reader.getLeafList();

        Log.v(LOGTAG, "interestPointsList size is " + leafInfo.size());

        LeafInfo leaf;
        for (int i = 0; i < resultString.length; i++) {
            leaf = leafInfo.get(Integer.parseInt(resultString[i]) - 1);
            resultName[i] = leaf.getLeaf(getString(R.string.scientific_name_tag));

            Log.v(LOGTAG, resultString[i]+" "+Integer.parseInt(resultString[i])+" Id " + leaf.getLeaf(getString(R.string.id_tag))+" " +resultName[i]);

        }

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
        imgFile.delete();
//        Intent intent = new Intent(ResultPrimaryActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }


}
