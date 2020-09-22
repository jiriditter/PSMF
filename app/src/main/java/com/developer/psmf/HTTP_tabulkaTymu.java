package com.developer.psmf;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HTTP_tabulkaTymu extends AsyncTask<String, Void, String> {
    private Context akt;
    LinearLayout laya;
    private hotovo done;

    public interface hotovo {
        void finished(Object o);
    }

    public HTTP_tabulkaTymu(Context a, hotovo done) {
        this.akt = a;
        this.done = done;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String link = strings[0];
            Log.d("HTTP_getZapasyOstatnich", link);

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
            Log.d("HTTP_getZapasyOstatnich", "response code = " + responseCode);
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
        if(s != null) done.finished(s);
    }
}
