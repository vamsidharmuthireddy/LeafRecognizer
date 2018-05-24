package www.cvit.leafrecognizer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

/**
 * Created by HOME on 16-03-2017.
 */

public class FullScreenImageActivity extends AppCompatActivity {


    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen_image);

        Intent intent = getIntent();
        String imageURL = intent.getStringExtra("imageURL");


        final TouchImageView imgDisplay;
        Button btnClose;

        imgDisplay = (TouchImageView) findViewById(R.id.imgDisplay);

        Glide.with(this).load(imageURL).asBitmap()
                .placeholder(R.drawable.leaf).into(imgDisplay);

//        Glide.with(this).load(imageURL).asBitmap().placeholder(R.drawable.leaf)
//                .into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource,
//                                        GlideAnimation<? super Bitmap> glideAnimation) {
//                imgDisplay.setImageBitmap(resource);
//                }
//            });

    }


}