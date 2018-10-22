/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package www.cvit.leafrecognizer;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.tensorflow.lite.Interpreter;

/** Classifies images with Tensorflow Lite. */
public class ImageClassifier {
//public class ImageClassifier implements Parcelable {
//public class ImageClassifier implements Serializable{

    /** Tag for the {@link Log}. */
    private static final String LOGTAG = "ImageClassifier";

    /** Name of the model file stored in Assets. */
    private static final String MODEL_PATH = "graph.lite";

    /** Name of the label file stored in Assets. */
    private static final String LABEL_PATH = "labels.txt";

    /** Number of results to show in the UI. */
    private static final int RESULTS_TO_SHOW = 10;

    /** Dimensions of inputs. */
    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 3;

    static final int DIM_IMG_SIZE_X = 224;
    static final int DIM_IMG_SIZE_Y = 224;

//    private static final int IMAGE_MEAN = 128;
//    private static final float IMAGE_STD = 128.0f;


    private static final int[] IMAGE_MEAN = {124,116,104};
    private static final float[] IMAGE_STD = {58.395f,57.12f,57.375f};


    /* Preallocated buffers for storing image data in. */
    private int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    private Interpreter tflite;

    /** Labels corresponding to the output of the vision model. */
    private List<String> labelList;

    /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs. */
    private ByteBuffer imgData = null;

    /** An array to hold inference results, to be feed into Tensorflow Lite as outputs. */
    private float[][] labelProbArray = null;
    /** multi-stage low pass filter **/
    private float[][] filterLabelProbArray = null;
    private static final int FILTER_STAGES = 3;
    private static final float FILTER_FACTOR = 0.4f;

    private PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
//                            return (o1.getValue()).compareTo(o2.getValue());  //ascending order
                            return (o2.getValue()).compareTo(o1.getValue());    //descending order
                        }
                    });

    /** Initializes an {@code ImageClassifier}. */
    ImageClassifier(Activity activity) throws IOException {
        tflite = new Interpreter(loadModelFile(activity));
        labelList = loadLabelList(activity);
        imgData =
                ByteBuffer.allocateDirect(
                        4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        Log.d(LOGTAG, "Created a Tensorflow Lite Image Classifier.");
    }

    /** Classifies a frame from the preview stream. */
    String classifyFrame(Bitmap bitmap) {
        labelProbArray = new float[1][labelList.size()];
        filterLabelProbArray = new float[FILTER_STAGES][labelList.size()];
        if (tflite == null) {
            Log.e(LOGTAG, "Image classifier has not been initialized; Skipped.");
            return "Uninitialized Classifier.";
        }
        convertBitmapToByteBuffer(bitmap);
        // Here's where the magic happens!!!
        long startTime = SystemClock.uptimeMillis();
        tflite.run(imgData, labelProbArray);
        long endTime = SystemClock.uptimeMillis();
        Log.d(LOGTAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));

        // smooth the results
        applyFilter();

        // print the results
        String textToShow = printTopKLabels();
//        textToShow = Long.toString(endTime - startTime) + "ms" + textToShow;

        sortedLabels.clear();

        return textToShow;
    }

    void applyFilter(){
        int num_labels =  labelList.size();
//    Log.d(LOGTAG,"p0:"+Float.toString(filterLabelProbArray[0][0])
//              +"p1:"+Float.toString(filterLabelProbArray[1][0])
//              +"p2:"+Float.toString(filterLabelProbArray[2][0]));

        // Low pass filter `labelProbArray` into the first stage of the filter.
        for(int j=0; j<num_labels; ++j){
            filterLabelProbArray[0][j] += FILTER_FACTOR*(labelProbArray[0][j] -
                    filterLabelProbArray[0][j]);
        }
        // Low pass filter each stage into the next.
        for (int i=1; i<FILTER_STAGES; ++i){
            for(int j=0; j<num_labels; ++j){
                filterLabelProbArray[i][j] += FILTER_FACTOR*(
                        filterLabelProbArray[i-1][j] -
                                filterLabelProbArray[i][j]);

            }
        }

        // Copy the last stage filter output back to `labelProbArray`.
        for(int j=0; j<num_labels; ++j){
            labelProbArray[0][j] = filterLabelProbArray[FILTER_STAGES-1][j];
        }
//    Log.d(LOGTAG,"a0:"+Float.toString(filterLabelProbArray[0][0])
//            +"a1:"+Float.toString(filterLabelProbArray[1][0])
//            +"a2:"+Float.toString(filterLabelProbArray[2][0]));


    }

    /** Closes tflite to release resources. */
    public void close() {
        tflite.close();
        tflite = null;
    }

    /** Reads label list from Assets. */
    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);

//            String leafId = line.split("_")[0];
//            labelList.add(leafId);

        }
        reader.close();
        return labelList;
    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /** Writes Image data into a {@code ByteBuffer}. */
    private void convertBitmapToByteBuffer(Bitmap queryBitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        queryBitmap = cropNResizeBitmap(queryBitmap);
        queryBitmap.getPixels(intValues, 0, queryBitmap.getWidth(), 0, 0, queryBitmap.getWidth(), queryBitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
//        Log.d(LOGTAG,"val:"+val);
                //val is ARGB and is 32 bit and has range 0-255
                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN[0])/IMAGE_STD[0]);  //Red
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN[1])/IMAGE_STD[1]);   //Green
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN[2])/IMAGE_STD[2]);        //Blue
            }
        }
        long endTime = SystemClock.uptimeMillis();
        Log.d(LOGTAG, "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime));
    }


    private Bitmap cropNResizeBitmap(Bitmap queryBitmap){
        int width = queryBitmap.getWidth();
        int height = queryBitmap.getHeight();
        Log.d(LOGTAG,"bitmapWidth: "+width+" bitmapHeight: "+height);

        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(queryBitmap, cropW, cropH, newWidth, newHeight);
        cropImg = Bitmap.createScaledBitmap(cropImg,DIM_IMG_SIZE_X,DIM_IMG_SIZE_Y,true);

        return cropImg;
    }

    /** Prints top-K labels, to be shown in UI as the results. */
    private String printTopKLabels() {
        for (int i = 0; i < labelList.size(); ++i) {
            String tempLabel = labelList.get(i).split("_")[0];
            Float tempLabelProb = labelProbArray[0][i];

            sortedLabels.add(new AbstractMap.SimpleEntry<>(tempLabel, tempLabelProb));

        }


//        Log.d(LOGTAG,labelList.size()+" : "+sortedLabels.size());
        String textToShow;
        final int size = sortedLabels.size();


        String[] label = new String[RESULTS_TO_SHOW];
        int trimCount = 0;
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> labelNprob = sortedLabels.poll();
//            Log.d(LOGTAG,"OUT: "+labelNprob.getKey()+" : "+labelNprob.getValue());
            if ( trimCount < RESULTS_TO_SHOW) {
                if (!Arrays.asList(label).contains(labelNprob.getKey())) {
//                    Log.d(LOGTAG,"IN: "+labelNprob.getKey()+" : "+labelNprob.getValue()+" trimCount:"+trimCount);
                    label[trimCount] = labelNprob.getKey();
                    trimCount++;
                }
            }else{
                Log.d(LOGTAG,"Done with 10 labels. Label size: "+label.length);
                break;
            }

        }


        textToShow = TextUtils.join("\t",label);
        return textToShow;
    }



}

