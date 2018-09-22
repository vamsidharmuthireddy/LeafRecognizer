package www.cvit.leafrecognizer;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vamsidhar on 15/2/18.
 */


public class ResultPrimaryAdapter extends RecyclerView.Adapter<ResultPrimaryAdapter.DataObjectHolder> {

    /**
     * This class is called from InterestPointsFragment after we get all the interest points
     * This class sets the picture and text(Title) on the InterestPointsFragment's recycler view
     */

    private static final String LOGTAG = "ResultPrimaryAdapter";

    private Context context;
    private ArrayList<LeafInfo> leafInfo;
    private String[] resultString = new String[10];
    private String[] resultName = new String[10];
    private String[] imageURL;
    private int[] drawableLoc;
    private String baseURL =
            "http://preon.iiit.ac.in/~vamsidhar_muthireddy/leaf_recognizer_router/title_images/";
    private String extension = ".jpg";

    private Boolean runOffline ;


    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public DataObjectHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.result_image);
            this.textView = (TextView) view.findViewById(R.id.result_text);
        }
    }

//    public ResultPrimaryAdapter(Context _context) {
//        context = _context;
//        notifyDataSetChanged();
//    }

    public ResultPrimaryAdapter(Context _context, ArrayList<LeafInfo> leafInfo,
                                String[] resultString, Boolean runOffline) {
        context = _context;
        this.leafInfo = leafInfo;
        this.resultString = resultString;
        this.runOffline = runOffline;
        notifyDataSetChanged();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_result, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        setViews(holder, position);
        setListeners(holder, position);


    }

    private void setViews(DataObjectHolder holder, int position) {

        ImageView imageView = holder.imageView;
        TextView textView = holder.textView;


        LeafInfo leaf;
        leaf = leafInfo.get(Integer.parseInt(resultString[position]) - 1);
        resultName[position] = leaf.getLeaf(context.getString(R.string.scientific_name_tag));
        textView.setText(resultName[position]);


//        String imageURL;
//        int drawableLoc = R.drawable.leaf;
        if (runOffline){
            imageURL[position]= "";
            drawableLoc[position] = context.getResources()
                            .getIdentifier("title_"+resultString[position],
                            "drawable",context.getPackageName());
        }else{
            imageURL[position]= baseURL+resultString[position]+extension;
            drawableLoc[position] = R.drawable.leaf;
        }

        Log.d(LOGTAG, "position = "+position+"imagePath = " + imageURL[position]);

        Glide.with(context).load(imageURL[position]).asBitmap()
                .placeholder(drawableLoc[position]).into(imageView);

//        if (imagePath == null) {
//            Glide.with(context)
//                    .load(R.drawable.monument)
//                    .fitCenter()
//                    .into(imageView);
//            //    Log.v(LOGTAG,"imagepath is null");
//
//        } else {
//
//
////            imagePath = interestPoints.get(0).getMonumentTitleImagePath(packageName_en, holder.textView.getText().toString(), context);
////            Log.v(LOGTAG, "changed imagePath = "+imagePath);
//            File file = new File(imagePath);
//            Uri uri = Uri.fromFile(file);
//            Glide.with(context)
//                    .load(uri)
//                    .asBitmap()
//                    .placeholder(R.drawable.monument)
//                    .centerCrop()
//                    .into(imageView);
//
//
//        }
    }


    private void setListeners(DataObjectHolder _holder, int _position) {

        final DataObjectHolder holder = _holder;
        final int position = _position;

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String leafName = holder.textView.getText().toString().toLowerCase();
//                String imageURL = baseURL+resultString[position]+extension;

                Log.d(LOGTAG, v.getId() + " is clicked" + " position= " + position
                        + " leafName = " + leafName);

                Intent openImage = new Intent(context,
                        FullScreenImageActivity.class);
                openImage.putExtra("imageURL", imageURL[position]);
                openImage.putExtra("drawableLoc", drawableLoc[position]);
                openImage.putExtra("query", "false");
                context.startActivity(openImage);
            }
        });


        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String leafName = holder.textView.getText().toString().toLowerCase();
//                String imageURL = baseURL+resultString[position]+extension;

                Log.d(LOGTAG, v.getId() + " is clicked" + " position= " + position
                        + " leafName = " + leafName);

                Intent openLeafDetail = new Intent(context,ResultDetailActivity.class);
                openLeafDetail.putExtra(context.getString(R.string.leaf_name),resultName[position]);
                openLeafDetail.putExtra(context.getString(R.string.image_url), imageURL[position]);
                openLeafDetail.putExtra("drawableLoc", drawableLoc[position]);
                context.startActivity(openLeafDetail);
            }
        });

    }


//    @Override
//    public int getItemCount() {
//        return 0;
//    }

    @Override
    public int getItemCount() {
//        Log.v(LOGTAG,leafInfo.size()+"");
        return resultString.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}

