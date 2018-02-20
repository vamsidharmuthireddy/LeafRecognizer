package www.cvit.leafrecognizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vamsidhar on 20/2/18.
 */


public class LeafInfo implements Serializable {
    private int _id;
    private HashMap<String, String> leafDetails;

    private static final String dataLocation = "completePackages/extracted/";

    private static final String imageType = ".jpg";
    private static final String latitudeTag = "lat";
    private static final String longitudeTag = "long";
    private static final String imageTag = "image";
    private static final String imagesTag = "images";

    private static final String imagesNameSplitter = ",";


    private static final String LOGTAG = "InterestPoint";

    public LeafInfo() {
        //Don't take context in constructor. This class will not be serializable
        leafDetails = new HashMap<String, String>();

    }


    /**
     * This method sets the interest point(monument) details
     *
     * @param key   It's the tag name of the particular xml field
     * @param value It's the value in the relevant tag of the xml file
     */
    public void setLeaf(String key, String value) {
        leafDetails.put(key, value);
    }

    /**
     * This method gives back the interest point(monument) details
     * @param key It's the tag name of the particular xml field
     * @return Value of the selected xml field
     */
    public String getLeaf(String key) {
        return leafDetails.get(key);
    }




    /**
     * This class is used to get the image related to a particular interest point
     *
     * @return Image of Interest point in Bitmap data type
     */
    public Bitmap getLeafTitleImage(String packageName_en, String interestPointName, Context context) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        String imageName = leafDetails.get(imageTag);
        //Log.v(LOGTAG, "interestPointName is " + interestPointName);

        String image_path = dataLocation + packageName_en + File.separator + imageName + imageType;

        File imageFile = new File(context.getFilesDir(), image_path);
        //Log.v(LOGTAG, imageFile.getAbsolutePath());
        if(imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            //Log.v(LOGTAG, imageName + imageType);

            return bitmap;
        }

        return null;

    }


    /**
     * This class is used to get the image path related to a particular interest point
     *
     * @return Image path of Interest point in String data type
     */
    public String getLeafTitleImagePath(String packageName_en, String interestPointName, Context context) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        String imageName = leafDetails.get(imageTag);
        //Log.v(LOGTAG, "interestPointName is " + interestPointName);

        String image_path = dataLocation + packageName_en + File.separator + imageName + imageType;

        File imageFile = new File(context.getFilesDir(), image_path);
        //Log.v(LOGTAG, imageFile.getAbsolutePath());
        if (imageFile.exists()) {
            return imageFile.getAbsolutePath();
        }

        return null;

    }




    /**
     * This class is called from ImagePagerFragmentActivity when Image button is clicked
     * This class is used to get all the images related to a particular interest point.
     * This class is not hard coded.
     *
     * @return Images of Interest point in Bitmap Array data type
     */
    public ArrayList<Bitmap> getLeafImages(Context context, String packageName_en, String interestPointName) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

//        String[] image_names = {"a1", "a2", "a3", "a4", "a5"};

        String allImages = leafDetails.get(imagesTag);

        //Log.v("getImages",interestPointName);

//        Log.v("getImages",allImages);
        List<String> imagesList = Arrays.asList(allImages.split(imagesNameSplitter));

        ArrayList<Bitmap> image_bitmaps = new ArrayList<Bitmap>();


        for (int i = 0; i < imagesList.size(); i++) {
            String imageName = imagesList.get(i);
//            Log.v("getImages",imageName);
            String image_path = dataLocation + packageName_en + "/" + imageName + imageType;

            File imageFile = new File(context.getFilesDir(), image_path);
            if (imageFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
//                bitmap = bitmap.createScaledBitmap(bitmap, 627, 353, false);
                image_bitmaps.add(bitmap);
            }
        }

        return image_bitmaps;

    }


    public ArrayList<String> getLeafImagePaths(Context context, String packageName_en, String interestPointName) {

        packageName_en = packageName_en.toLowerCase().replace("\\s", "");

        String allImages = leafDetails.get(imagesTag);

        //Log.v("getImages",interestPointName);

//        Log.v("getImages",allImages);
        ArrayList<String> imagesList = new ArrayList<String>();
        imagesList.addAll(Arrays.asList(allImages.split(imagesNameSplitter)));

        for (int i = 0; i < imagesList.size(); i++) {

            String image_path = context.getFilesDir() + File.separator
                    + dataLocation + packageName_en + File.separator + imagesList.get(i) + imageType;

            imagesList.set(i, image_path);

            File imageFile = new File(image_path);
            if (!imageFile.exists()) {
                imagesList.remove(i);
            }

        }

        return imagesList;

    }


}
