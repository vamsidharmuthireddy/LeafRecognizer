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
    private Context context;
//    private ArrayList<InterestPoint> interestPoints;
    private String packageName_en;
    private static final String LOGTAG = "MonumentAllAdapter";

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;

        public DataObjectHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.cardview_monument_image);
            this.textView = (TextView) view.findViewById(R.id.cardview_monument_text);
        }
    }

    public ResultPrimaryAdapter(Context _context) {
        context = _context;
        notifyDataSetChanged();
    }

//    public ResultPrimaryAdapter(ArrayList<InterestPoint> interestPoints, Context _context, String _packageName_en) {
//        context = _context;
//        packageName_en = _packageName_en;
//        this.interestPoints = interestPoints;
//        notifyDataSetChanged();
//    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_result, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {


        setViews(holder, position);
//        setListeners(holder, position);


    }

    private void setViews(DataObjectHolder holder, int position) {

        //SessionManager sessionManager = new SessionManager();
        //final String packageName = sessionManager
        //        .getStringSessionPreferences(
        //                context, context.getString(R.string.package_name_en), context.getString(R.string.default_package_value));

        final String packageName = packageName_en;
        ImageView imageView = holder.imageView;
        TextView textView = holder.textView;

//        textView.setText(interestPoints.get(position).getMonument(context.getString(R.string.interest_point_title)));


//        String imagePath = interestPoints.get(position)
//                .getMonumentTitleImagePath(packageName_en, holder.textView.getText().toString(), context);

//        Log.v(LOGTAG, "imagePath = " + imagePath);

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


//    private void setListeners(DataObjectHolder _holder, int _position) {
//
//        final DataObjectHolder holder = _holder;
//        final int position = _position;
//
//
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String interestPointTitle = holder.textView.getText().toString();
//
//                Log.v(LOGTAG, v.getId() + " is clicked" + " position= " + position + " packageName = " + interestPointTitle);
//
//                Intent openMonument = new Intent(context, InterestPointActivity.class);
//                openMonument.putExtra(context.getString(R.string.interestpoint_name), interestPointTitle);
//                openMonument.putExtra(context.getString(R.string.package_name_en), packageName_en);
//                openMonument.putExtra(context.getString(R.string.interest_point_type), context.getString(R.string.monument));
//                //context.startActivity(openMonument);
//                int startX = (int) v.getX();
//                int startY = (int) v.getY();
//                int width = v.getWidth();
//                int height = v.getHeight();
//                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
//                context.startActivity(openMonument, options.toBundle());
//
//            }
//        });
//
//
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String interestPointTitle = holder.textView.getText().toString().toLowerCase();
//
//                Log.v(LOGTAG, v.getId() + " is clicked" + " position= " + position + " packageName = " + interestPointTitle);
//
//                Intent openMonument = new Intent(context, InterestPointActivity.class);
//                openMonument.putExtra(context.getString(R.string.interestpoint_name), interestPointTitle);
//                openMonument.putExtra(context.getString(R.string.package_name_en), packageName_en);
//                openMonument.putExtra(context.getString(R.string.interest_point_type), context.getString(R.string.monument));
//                //context.startActivity(openMonument);
//                int startX = (int) v.getX();
//                int startY = (int) v.getY();
//                int width = v.getWidth();
//                int height = v.getHeight();
//                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, startX, startY, width, height);
//                context.startActivity(openMonument, options.toBundle());
//
//            }
//        });
//
//    }


    @Override
    public int getItemCount() {
        return 0;
    }
//
//    @Override
//    public int getItemCount() {
//        return interestPoints.size();
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}

