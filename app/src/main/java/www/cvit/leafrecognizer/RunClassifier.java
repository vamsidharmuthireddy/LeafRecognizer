package www.cvit.leafrecognizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class RunClassifier extends AsyncTask<Bitmap,Integer,String> {
    private ProgressDialog progressDialog;
    private Bitmap croppedImage;
    private Context context;
    private ImageClassifier classifier;
    public AsyncResponse delegate = null;


    public interface AsyncResponse {
        void processFinish(String output);
    }



    public RunClassifier(Context _context, ImageClassifier _classifier,AsyncResponse delegate) {
        this.context = _context;
        this.classifier = _classifier;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.setMessage("Querying");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.processFinish(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.dismiss();
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        croppedImage = bitmaps[0];
//        String resultString = "";
        String resultString = classifier.classifyFrame(croppedImage);
        return resultString;
    }
}
