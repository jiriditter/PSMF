package com.developer.psmf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GUI_GroupDetails extends AppCompatActivity implements HTTP_getTeamsInGroup.SkupinaNactena {
    private Activity akt;
    private String rok, skupina, liga, competition, season;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupdetails);
        akt = this;

        Intent mA = getIntent();
        if (mA != null) {
            liga = mA.getStringExtra("LIGA");
            skupina = mA.getStringExtra("SKUPINA");
            rok = mA.getStringExtra("ROK");
            season = mA.getStringExtra("SEZONA");
            competition = mA.getStringExtra("COMPETITION");
            HTTP_getTeamsInGroup HTP = new HTTP_getTeamsInGroup(GUI_GroupDetails.this);
            HTP.execute(rok, competition, season, liga, skupina);
            TextView id_text = (TextView) findViewById(R.id.textskupiny);

            int skup = Character.getNumericValue(skupina.charAt(0)) +64; //prevod ASCII, 65=A
            char skp = (char) skup;
            id_text.setText(R.string.prehledskupiny + liga + skp);
        } else {
            Log.d("PSMF", "Intent je NULL");
            return;
        }
    }

    @Override
    public void SkupnaNactena(Object o) {
        if(o != null) {
            try {
                JSONObject jO = (JSONObject) o;
                JSONArray jAsan = jO.getJSONArray("teams");
                int count = jAsan.length();
                for (int p = 0; p < count; p++) {
                    View udajetymu = View.inflate(akt, R.layout.tym_grouplist, null);
                    LinearLayout lltym = udajetymu.findViewById(R.id.tymlayout);
                    lltym.setBackgroundResource(R.drawable.tym_grouplist);

                    LinearLayout ll = (LinearLayout) findViewById(R.id.teamlister);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(5, 10, 5, 10);
                    udajetymu.setLayoutParams(layoutParams);

                    JSONObject Jojako = jAsan.getJSONObject(p);
                    String tym = Jojako.getString("teamName");
                    String webovky = Jojako.getString("www");
                    TextView tymname = (TextView) udajetymu.findViewById(R.id.tymname);
                    TextView webpage = (TextView) udajetymu.findViewById(R.id.webstranky);
                    tymname.setText(tym);
                    webpage.setText(webovky);
                    ll.addView(udajetymu);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
