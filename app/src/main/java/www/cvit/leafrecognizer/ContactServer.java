package www.cvit.leafrecognizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vamsidhar on 31/10/17.
 */

public class ContactServer extends AsyncTask<String,Void,String> {

    private static final String LOGTAG = "ContactServer";
    public Context context;
    public Activity activity;
    private String num;
    private Bitmap outputImageBitmap;

    private File mFile;
    private final String serverURL =
            "http://preon.iiit.ac.in/~vamsidhar_muthireddy/leaf_recognizer_router/router.php";

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

                URL url = new URL(serverURL);
                // Open a HTTP connection to the URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // Allow Inputs
                conn.setDoInput(true);
                // Allow Outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);

                // Use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";fileName=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                // read file and write it into form...
                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                Log.v(LOGTAG,"Server response code: "+conn.getResponseCode());
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


    private String getResultAnnotation(HttpURLConnection conn) {
        try {
            InputStream is = conn.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String s = "";
            String out = "";
            while ((s = buffer.readLine()) != null){
                out += s;
            }
            is.close();
            return out;

        } catch (IOException var4) {
            Log.e(LOGTAG, var4.toString());
            var4.printStackTrace();
            return null;
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
    protected String doInBackground(String... params) {
        String inputImageFilePath = params[0];

        String result = "";
        HttpURLConnection conn = uploadPhoto(inputImageFilePath);

            if(conn != null) {
                Log.v(LOGTAG, conn.toString());
//                result = getResultAnnotation(conn);
            }else{
                Log.v(LOGTAG,"null connection");
            }

        return result;
    }

    @Override
    protected void onPostExecute(String str) {
        progressDialog.dismiss();


    }


}

