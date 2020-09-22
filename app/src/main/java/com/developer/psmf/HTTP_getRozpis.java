package com.developer.psmf;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class HTTP_getRozpis extends AsyncTask<String, Void, String> {
    private gotRozpis hotowo;
    private ProgressDialog dialog;
    private Context ctx;

    public interface gotRozpis {
        void taskCompleted(Object o);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(ctx);
        this.dialog.setMessage("Please wait");
        this.dialog.show();
    }

    public HTTP_getRozpis(Context c, gotRozpis hotowo) {
        this.hotowo = hotowo;
        this.ctx = c;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Log.d("PSMF", "kontaktuju URL = " + strings[0]);
            HttpsURLConnection connection = (HttpsURLConnection) new URL(strings[0]).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String text;
            StringBuilder result = new StringBuilder();
            while ((text = in.readLine()) != null)
                result.append(text);
            in.close();

            int responseCode = connection.getResponseCode();
            Log.d("PSMF", "response code = " + responseCode);
            return String.valueOf(result);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if(s != null) hotowo.taskCompleted(s);
    }
}
