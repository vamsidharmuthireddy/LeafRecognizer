package www.cvit.leafrecognizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vamsidhar on 31/10/17.
 */

public class ContactServer extends AsyncTask<String,Void,Void> {

    private static final String LOGTAG = "ContactServer";
    public Context context;
    public Activity activity;
    private String num;
    private String inputImageFilePath;
    private Bitmap resultBitmap;
    private String resultString;
    private String resLocation;

    private File mFile;
    private final String serverURL =
            "http://preon.iiit.ac.in/~vamsidhar_muthireddy/leaf_recognizer_router/preon2node.php";

    private ProgressDialog progressDialog;


    public ContactServer(Context _context, Activity _activity) {
        context = _context;
        activity = _activity;
    }


    private HttpURLConnection uploadPhoto(String inputImageFileUri) {
        final String fileName = inputImageFileUri;
//        final String fileName = "test"+ (int) Math.round(Math.random()*1000) + ".jpg";

        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "*****";

        File inputFile = new File(inputImageFileUri);
        Log.v(LOGTAG,inputFile.toString());

        if (!inputFile.exists()){
            Log.v(LOGTAG,"File doesn't exists");
            return null;
        }
        else{

            try {

                FileInputStream fileInputStream = new FileInputStream(inputFile);
                Log.v(LOGTAG,"serverURL: "+serverURL);

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                // Use a post method.
                Log.v(LOGTAG,"Setting up request properties");
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                Log.v(LOGTAG,"Setting up DataOutputStream");
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data;" +
                        " name=\"uploaded_file\";" +
                        "fileName=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024*1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                Log.v(LOGTAG,"Uploading Data using DataOutputStream");
                while (bytesRead > 0) {
//                    Log.v(LOGTAG,"bytesRead: "+bytesRead);
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                final int server_response_code = conn.getResponseCode();
                Log.v(LOGTAG,"Server response code: "+server_response_code);
                if(server_response_code >= 200 &&  server_response_code < 400){
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context,"Connection Successfull: "+server_response_code,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context,"Connection Not Successfull: "+server_response_code,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                fileInputStream.close();
                dos.flush();
                return conn;
            } catch (MalformedURLException var14) {
                Log.e(LOGTAG, "error: " + var14.getMessage(), var14);
                return null;
            } catch (IOException var15) {
                Log.e(LOGTAG, "error: " + var15.getMessage(), var15);
                return null;
            }
        }
    }

    private void saveResultImage(){

        FileOutputStream output = null;
        resLocation = Environment.getExternalStorageDirectory().toString()
                +File.separator+BuildConfig.APPLICATION_ID+File.separator+"res.jpeg";


        Log.v(LOGTAG,resLocation);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        File resFile = new File(resLocation);
        try {
            output = new FileOutputStream(resFile);
            BufferedOutputStream bos = new BufferedOutputStream(output);
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            Log.v(LOGTAG,"Received image is empty");
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                    Log.v(LOGTAG, "Image is saved");
                } catch (IOException e) {
                    Log.v(LOGTAG,"Received image is null");
                    e.printStackTrace();
                }
            }
        }
    }

    private void getResultAnnotation(HttpURLConnection conn) {
        try {
            InputStream is = conn.getInputStream();
            Log.v(LOGTAG,is.toString());
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String s = "";
            String out = "";
            while ((s = buffer.readLine()) != null){
                out += s;
            }
            is.close();
            String parts[] = out.split("<br/>");
            Log.v(LOGTAG,parts[parts.length-1]);
            resultString = parts[parts.length-1];
//            get result image from server
//            Bitmap outBitmap = BitmapFactory.decodeStream(is);
//            resultBitmap = outBitmap;
//            is.close();
//            saveResultImage();
//            return out;

        } catch (IOException var4) {
            Log.e(LOGTAG, var4.toString());
            var4.printStackTrace();
//            return null;
        }

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.setMessage("Uploading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Log.v(LOGTAG, "Progress is " + progressDialog.getProgress());
    }

    @Override
    protected Void doInBackground(String... params) {
        inputImageFilePath = params[0];

        Bitmap result;
        HttpURLConnection conn = uploadPhoto(inputImageFilePath);

            if(conn != null) {
                Log.v(LOGTAG, conn.toString());
                getResultAnnotation(conn);
            }else{
                Log.v(LOGTAG,"null connection");
            }

//        return result;
        return null;
    }

    @Override
    protected void onPostExecute(Void out) {
        progressDialog.dismiss();

//        Intent callAnnotation = new Intent(context, AnnotationActivity.class);
        Intent callAnnotation = new Intent(context, ResultPrimaryActivity.class);

        callAnnotation.putExtra("queryLocation", inputImageFilePath);
        callAnnotation.putExtra("runOffline",false);

//        ByteArrayOutputStream bs = new ByteArrayOutputStream();
//        resultBitmap.compress(Bitmap.CompressFormat.JPEG,50,bs);
//        callAnnotation.putExtra("outImage", bs.toByteArray());
//        callAnnotation.putExtra("res_loc", resLocation);

        callAnnotation.putExtra("resultString", resultString);

        activity.startActivity(callAnnotation);
        activity.finish();

    }


}

