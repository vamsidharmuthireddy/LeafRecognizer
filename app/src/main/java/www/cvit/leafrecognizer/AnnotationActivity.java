package www.cvit.leafrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class AnnotationActivity extends AppCompatActivity {
    private String LOGTAG = "AnnotationActivity";
    private ImageView resultImageView;
    private ImageView queryImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        if(getIntent().hasExtra("outImage")) {
            resultImageView = (ImageView)findViewById(R.id.result_image);
            Bitmap result_bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("outImage"),
                    0,getIntent().getByteArrayExtra("outImage").length);
            resultImageView.setImageBitmap(result_bitmap);
        }

        String resLocation = getIntent().getStringExtra("res_loc");

        File imgFile = new  File(getIntent().getStringExtra("query_loc"));

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            queryImageView = (ImageView) findViewById(R.id.query_image);
            queryImageView.setImageBitmap(myBitmap);

        }


    }
}
