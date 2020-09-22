package com.developer.psmf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

//limituj favorites na 5? tymu
public class async_showFavories {
    private String sharedPrefsString;
    private Context ctx;

    //sharedprefs example: |Efficenza AC^1083^E^4^1|Arabská^42^A^6^1

    public async_showFavories(Context c) {
        this.ctx = c;
        LoadSharedPrefs();
    }

    public void RemoveFavorites(String tym) {
        LoadSharedPrefs();
        String newprefstr = "";
        String[] str = new String[99];
        //|Efficenza AC^1083^E^4^1|Arabská^42^A^6^1
        if (sharedPrefsString.length() > 5) {
            str = sharedPrefsString.split("\\|");
            Log.d("PSMF", "String co budu parsovat :: " + sharedPrefsString);
            for (int i = 0; i < str.length; i++) {
                if (str[i].length() > 0) {
                    String[] sub = new String[199];
                    for (int f = 0; f < sub.length; f++) sub[f] = "";
                    Log.d("PSMF", "substring :: " + str[i]);
                    sub = str[i].split("\\^");

                    if(!tym.equals(sub[0])) {
                        newprefstr += "|" + str[i];
                    } else Toast.makeText(ctx, ctx.getResources().getString(R.string.tymsmazan), Toast.LENGTH_SHORT).show(); //else Log.d("PSMF", "SPLIT team jsem nasel, huraaaa!!! :: " + sub[0]);
                }
            }
            sharedPrefsString = newprefstr;
            SaveSharedPrefs(sharedPrefsString);
            //og.d("PSMF", "Nove nahrazenej sharedprefsstr :: " + newprefstr);
        }
    }

    private void LoadSharedPrefs() {
        SharedPreferences shp = ctx.getSharedPreferences("Favorites", MODE_PRIVATE);
        Map<String,?> entries = shp.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            if(key.equals("listtymu")) {
                sharedPrefsString = entries.get(key).toString();
            }
            Log.d("PSMF", "SharedPrefs found :: " + key + " = " + entries.get(key).toString());
        }
    }

    public int KolikZaznamuSharedPrefs() {
        int z =0;
        String[] str = new String[99];
        LoadSharedPrefs();
        if((sharedPrefsString != null) && (sharedPrefsString.length() >5)) {
            str = sharedPrefsString.split("\\|");
            Log.d("PSMF", "String co budu parsovat :: " + sharedPrefsString);
            return str.length;
        }
        return z;
    }

    public Boolean isTheTeamThere(String watta) {
        if(sharedPrefsString != null) {
            if (sharedPrefsString.contains(watta)) return true;
        }
        return false;
    }

    public void SaveSharedPrefs(String watta) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("Favorites", MODE_PRIVATE).edit();
        Log.d("PSMF", "SharedPrefs :: Sejvuju :: " + watta);
        editor.putString("listtymu", watta);
        editor.commit();
    }

    public List<skupina> VratMiSharedPrefJakoList() {
        String[] str = new String[99];
        List<skupina> lokal = new ArrayList<>();

        if((sharedPrefsString != null) && (sharedPrefsString.length() >5)) {
            str = sharedPrefsString.split("\\|");
            Log.d("PSMF", "String co budu parsovat :: " + sharedPrefsString);
            for (int i = 0; i < str.length; i++) {
                if (str[i].length() > 0) {
                    String[] sub = new String[199];
                    for(int f=0;f<sub.length;f++) sub[f] = "";
                    Log.d("PSMF", "substring :: " + str[i]);
                    sub = str[i].split("\\^");
                    skupina sk = new skupina();
                    //"|^" + this.tym + "^" + this.teamID + "^" + this.group + "^" + this.liga + "^" + this.competition;
                    Log.d("PSMF", "Sub[0] = " + sub[0]);
                    Log.d("PSMF", "Sub[1] = " + Integer.parseInt(sub[1]));
                    Log.d("PSMF", "Sub[2] = " + sub[2].charAt(0));
                    Log.d("PSMF", "Sub[3] = " + Integer.parseInt(sub[3]));
                    Log.d("PSMF", "Sub[4] = " + Integer.parseInt(sub[4]));

                    sk.setNazev(sub[0]);
                    sk.setTeam_id(Integer.parseInt(sub[1]));
                    sk.setSkupina(sub[2].charAt(0));
                    sk.setLiga(Integer.parseInt(sub[3]));
                    sk.setCompetition(Integer.parseInt(sub[4]));
                    lokal.add(sk);
                }
            }
        }
        return lokal;
    }
}
