package www.cvit.leafrecognizer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by vamsidhar on 15/2/18.
 */

public class ResultPrimaryActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private static final String LOGTAG = "MonumentAllFragment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_primary);

        //interestPoints = ((PackageContentActivity) this.getActivity()).giveMonumentList();
        //interestPoints = new MonumentActivity().monumentList;


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_result_primary);
        recyclerView.setHasFixedSize(true);
        //recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewLayoutManager = new PreLoadingLinearLayoutManager(ResultPrimaryActivity.this);
        new PreLoadingLinearLayoutManager(ResultPrimaryActivity.this).setPages(1);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        //setting the view of the PLACES tab
        recyclerViewAdapter = new ResultPrimaryAdapter(ResultPrimaryActivity.this);
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


}
