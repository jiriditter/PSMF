package com.developer.psmf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HTTP_getTeamsInGroup extends AsyncTask<String, String, String> {
    private SkupinaNactena hotovo;
    private ProgressDialog pracuju;
    private Activity akt;
    private _config cfg;
    private String URLrok, URLcompetition, URLseason, URLLeague, URLGroup;
    private String myURL;
    private String lookupteam = "teams?token=&year=";

    public HTTP_getTeamsInGroup(SkupinaNactena sk) {
        this.hotovo = sk;
    }

    public interface SkupinaNactena {
        void SkupnaNactena(Object o);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        cfg = new _config();
        myURL = cfg.getURL_hosting() + lookupteam;
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
        try {
            URLrok = strings[0];
            URLcompetition = strings[1];
            URLseason = strings[2];
            URLGroup = strings[3];
            URLLeague = strings[4];
            String link = myURL + URLrok + "&competition=" + URLcompetition + "&season=" + URLseason + "&league=" + URLLeague + "&group=" + URLGroup;
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
        Log.d("PSMF", "Result = " + s);
        //pracuju.dismiss();
        if(s != null) {
            JSONObject JArda = null;
            try {
                JArda = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {hotovo.SkupnaNactena(JArda);}
        }
    }
}
