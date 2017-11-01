package www.cvit.leafrecognizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vamsidhar on 30/10/17.
 */

public class Contact extends AppCompatActivity{
    private static final String TAG = "Contact";
    TextView mResultView;
    private Context mContext = this;
    private final String SERVERURL = "";
    private static final String save_name = "/pic.jpg";
    private boolean mCameraReadyFlag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File img_file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.save_name));

        if(img_file != null) {
            ServerTask task = new ServerTask();
            task.execute(Environment.getExternalStorageDirectory().toString() + "/temp.jpg");
        }

    }


    public class ServerTask extends AsyncTask<String, Integer, String> {
        public byte[] dataToServer;
        private final int UPLOADING_PHOTO_STATE = 0;
        private final int SERVER_PROC_STATE = 1;
        private ProgressDialog dialog;

        HttpURLConnection uploadPhoto(FileInputStream fileInputStream) {
            String serverFileName = "test" + (int)Math.round(Math.random() * 1000.0D) + ".jpg";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String var5 = "*****";

            try {
                URL url = new URL("");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes("--*****\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + serverFileName + "\"" + "\r\n");
                dos.writeBytes("\r\n");
                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                for(int bytesRead = fileInputStream.read(buffer, 0, bufferSize); bytesRead > 0; bytesRead = fileInputStream.read(buffer, 0, bufferSize)) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                }

                dos.writeBytes("\r\n");
                dos.writeBytes("--*****--\r\n");
                this.publishProgress(new Integer[]{Integer.valueOf(1)});
                fileInputStream.close();
                dos.flush();
                return conn;
            } catch (MalformedURLException var14) {
                Log.e("Contact", "error: " + var14.getMessage(), var14);
                return null;
            } catch (IOException var15) {
                Log.e("Contact", "error: " + var15.getMessage(), var15);
                return null;
            }
        }

        String getResultImage(HttpURLConnection conn) {
            try {
                InputStream is = conn.getInputStream();
//                Contact.this.mResultView = BitmapFactory.decodeStream(is);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                String s = "";
                String out = "";
                while ((s = buffer.readLine()) != null){
                    out += s;
                }
                is.close();
                return out;

            } catch (IOException var4) {
                Log.e("Contact", var4.toString());
                var4.printStackTrace();
                return null;
            }

        }

        String processImage(String inputImageFilePath) {
            this.publishProgress(new Integer[]{Integer.valueOf(0)});
            File inputFile = new File(inputImageFilePath);
            String res = "";
            try {
                FileInputStream fileInputStream = new FileInputStream(inputFile);
                HttpURLConnection conn = this.uploadPhoto(fileInputStream);
                if(conn != null) {
                    res = this.getResultImage(conn);
                }

                fileInputStream.close();
                return res;
            } catch (FileNotFoundException var5) {
                Log.e("Contact", var5.toString());
                return null;
            } catch (IOException var6) {
                Log.e("Contact", var6.toString());
                return null;
            }

        }

        public ServerTask() {
            this.dialog = new ProgressDialog(Contact.this.mContext);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Photo captured");
            this.dialog.show();
        }

        protected String doInBackground(String... params) {
            String uploadFilePath = params[0];
            String result = this.processImage(uploadFilePath);

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
            if(progress[0].intValue() == 0) {
                this.dialog.setMessage("Uploading");
                this.dialog.show();
            } else if(progress[0].intValue() == 1) {
                if(this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }

                this.dialog.setMessage("Processing");
                this.dialog.show();
            }

        }

        protected void onPostExecute(String res) {
            if(this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

        }
    }
}
