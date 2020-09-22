package com.developer.psmf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class getLeagueList extends AsyncTask<String, String, String> {
    private diag_completed hotovo;
    private ProgressDialog pracuju;
    private int prep;
    private Activity akt;
    private String rezalt;

    public getLeagueList(diag_completed diagco, Activity ak, int p) {
        this.hotovo = diagco;
        this.akt =ak;
        this.prep = p;
    }

    public interface diag_completed {
        void diag_completed(Object o, int prepinac, String resultCode);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        rezalt = "";
        /*
        pracuju = new ProgressDialog(akt);
        pracuju.setIndeterminate(false);
        pracuju.setCancelable(false);
        pracuju.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pracuju.setMessage("Přemýšlím...");
        pracuju.show();
        */
    }

    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        BufferedReader bufferedReader;
        if(strings[0] != null) {
            try {
                String link = strings[0];
                Log.d("PSMF", link);

                HttpsURLConnection connection = (HttpsURLConnection) new URL(link).openConnection();
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
                Log.d("getLeagueList", "response code = " + responseCode);
                rezalt = Integer.toString(responseCode);
                return String.valueOf(result);
            } catch (MalformedURLException e) {
                rezalt = e.toString();
            } catch (IOException e) {
                rezalt = e.toString();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //pracuju.dismiss();
        hotovo.diag_completed(s, prep, rezalt);
    }
}
