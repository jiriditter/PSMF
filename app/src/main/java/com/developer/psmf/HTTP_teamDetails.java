package com.developer.psmf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class HTTP_teamDetails extends AsyncTask<String, Void, String> {
    private Context ctx;
    private String tym;
    private int rok;
    private ProgressDialog dialog;
    private _config cfg;
    private static String URL;

    public HTTP_teamDetails(Context c) {
        this.ctx = c;
        rok = Calendar.getInstance().get(Calendar.YEAR);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(ctx);
        this.dialog.setMessage("Přemýšlím...");
        this.dialog.show();
        cfg = new _config();
        URL = cfg.getURL_hosting() + cfg.getURL_hledejtym();
    }

    @Override
    protected String doInBackground(String... strings) {
        this.tym = strings[0];
        Log.d("HTTP_teamDetails", "tym = " + this.tym);
        String link = URL + this.tym + "&year=" + rok + "&token";
        link = link.replaceAll(" ", "%20");
        Log.d("HTTP_teamDetails", "calling URL = " + link);

        try {
            Log.d("HTTP_teamDetails", "calling URL = " + link);

            HttpsURLConnection connection = (HttpsURLConnection) new URL(link).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String text;
            StringBuilder result = new StringBuilder();
            while ((text = in.readLine()) != null) {
                Log.d("doInBackground", text);
                result.append(text);
            }
            in.close();

            int responseCode = connection.getResponseCode();
            Log.d("HTTP_teamDetails", "response code = " + responseCode);
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

        if(s != null) {
            if(!s.equalsIgnoreCase("{  \"teams\": []}")) {
                Log.d("HTTP_teamDetails", "starting intent with param >>> " + s);
                Intent intent = new Intent(ctx, teamDetails.class);
                intent.putExtra("RESULT", s);
                ctx.startActivity(intent);
            } else Toast.makeText(ctx, ctx.getResources().getString(R.string.error_nacitanidat), Toast.LENGTH_SHORT).show();
        }
    }
}
