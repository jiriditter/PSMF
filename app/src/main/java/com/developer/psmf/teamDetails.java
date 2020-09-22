package com.developer.psmf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

//pridat do Favorites
//pridat zapasy do kalendare

public class teamDetails extends AppCompatActivity implements HTTP_getRozpis.gotRozpis, HTTP_tabulkaTymu.hotovo, HTTP_populateHriste.HristePopulated {
    private String result_json, tym, www;
    public int teamID, group, liga, year, season, competition;
    private static Context ctx;
    private String sharedPrefsString;
    private String rozpis_JSON;
    private List<skupina> vysledek;
    private List<skupina> shPref;
    List<TabulkaRozpis> _tabRozpis = new ArrayList<>();
    List<TabulkaRozpis> _tabRozpisToShow = new ArrayList<>();
    private AlertDialog.Builder alertDialogBuilder;
    private Boolean _showRozpis;
    List<_hriste> hriste = new ArrayList<>();
    private _config cfg;
    Button b_tabulka, b_rozpis, b_nastaveni;
    int b_buttonpressed;

    private static String URL;
    private static String dotaz_teamdetails;
    //private String dotaz_rozpis = "https://api.psmf.zlutazimnice.cz/api/v1/team-details?token=&competition=";
    private static String dotaz_TeamStandings;
    private static String dotaz_hriste;

    private static int LIMIT_NUMBER_OF_FAVORITES = 5;

    //class TabulkaRozpis implements Comparable {
    class TabulkaRozpis implements Comparator<TabulkaRozpis> {
        private String hometeam, awayteam;
        private String hriste, cas;
        private int kolo, goalsAway, goalsHome;

        public void setKolo(int kolo) {
            this.kolo = kolo;
        }

        public void setGoalsAway(int goalsAway) {
            this.goalsAway = goalsAway;
        }

        public void setGoalsHome(int goalsHome) {
            this.goalsHome = goalsHome;
        }

        public void setHometeam(String hometeam) {
            this.hometeam = hometeam;
        }

        public void setAwayteam(String awayteam) {
            this.awayteam = awayteam;
        }

        public void setHriste(String hriste) {
            this.hriste = hriste;
        }

        public void setCas(String cas) {
            this.cas = cas;
        }

        public String getHometeam() {
            return hometeam;
        }

        public String getAwayteam() {
            return awayteam;
        }

        public String getHriste() {
            return hriste;
        }

        public String getCas() {
            return cas;
        }

        public int getKolo() {
            return kolo;
        }

        public int getGoalsAway() {
            return goalsAway;
        }

        public int getGoalsHome() {
            return goalsHome;
        }

        @Override
        public int compare(TabulkaRozpis o1, TabulkaRozpis o2) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Calendar cal = Calendar.getInstance();
            int yer = cal.get(Calendar.YEAR);
            String d1 = o1.getCas();
            String d2 = o2.getCas();
            String[] _tmp = new String[5];
            String[] _tmp2 = new String[5];
            _tmp = d1.split("\\.");
            _tmp2 = d2.split("\\.");
            String datum1 = _tmp[0] + "." + _tmp[1] + "." + yer;
            String datum2 = _tmp2[0] + "." + _tmp2[1] + "." + yer;
            Log.d("comparator", "datum1="+datum1+" ~ datum2="+datum2);
            try {
                Date date1 = sdf.parse(datum1);
                Date date2 = sdf.parse(datum2);
                if (date1.compareTo(date2) > 0) {
                    Log.d("comparator", "d1 > d2");
                } else if (date1.compareTo(date2) < 0) {
                    Log.d("comparator", "d1 < d2");
                } else if (date1.compareTo(date2) == 0) {
                    Log.d("comparator", "d1 = d2");
                }
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }

        /*
        @Override
        public int compareTo(Object o) {
            TabulkaRozpis t = (TabulkaRozpis) o;
            return this.kolo - ((TabulkaRozpis) o).getKolo();
        }
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_details);

        cfg = new _config();
        URL = cfg.getURL_hosting();
        dotaz_teamdetails = cfg.getURL_teamdetails();
        dotaz_TeamStandings = cfg.getDotaz_TeamStandings();
        dotaz_hriste = cfg.getDotaz_hriste();

        vysledek = new ArrayList<>();
        Intent intent = getIntent();
        result_json = intent.getStringExtra("RESULT");
        Log.d("teamDetails", "Dostal jsem Intent RESULT = [" + result_json + "]");

        if(result_json != null) {
            if(_jsonRecords() >0) {
                Log.d("PSMF", "onCreate [teamDetails.java] :: " + result_json);
                PARSE_RESULT(result_json, 0);
                _Init();

                //vyhledej nadchazejici 3 zapasy
                _showRozpis = false;
                HTTP_getRozpis rozp = new HTTP_getRozpis(ctx, teamDetails.this);
                String param = URL + dotaz_teamdetails + year + "&competition=" + competition + "&season=" + season + "&id=" + teamID;
                Log.d("PSMF", "teamDetails, param = " + param);
                rozp.execute(param);
            }
        } else Toast.makeText(ctx, ctx.getResources().getString(R.string.error_nacitanidat), Toast.LENGTH_SHORT).show();
    }

    private int _jsonRecords() {
        JSONObject jO = null;
        int pocet = 0;
        try {
            jO = new JSONObject(result_json);
            JSONArray jAsan = null;
            jAsan = jO.getJSONArray("teams");
            pocet = jAsan.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("_jsonRecords", "pole ma velikost = [" + pocet + "]");
        return pocet;
    }

    private void refresh_buttons() {
        Log.d("b_buttonpressed", "hodnota = " + b_buttonpressed);
        switch(b_buttonpressed) {
            case 0: b_tabulka.setBackgroundResource(R.drawable.tlacidlo_release);
                b_rozpis.setBackgroundResource(R.drawable.tlacidlo_release);
                b_nastaveni.setBackgroundResource(R.drawable.tlacidlo_release);
                break;
            case 1: b_tabulka.setBackgroundResource(R.drawable.tlacidlo_pressed);
                    b_rozpis.setBackgroundResource(R.drawable.tlacidlo_release);
                    b_nastaveni.setBackgroundResource(R.drawable.tlacidlo_release);
                break;
            case 2: b_tabulka.setBackgroundResource(R.drawable.tlacidlo_release);
                b_rozpis.setBackgroundResource(R.drawable.tlacidlo_pressed);
                b_nastaveni.setBackgroundResource(R.drawable.tlacidlo_release);
                break;
            case 3: b_tabulka.setBackgroundResource(R.drawable.tlacidlo_release);
                b_rozpis.setBackgroundResource(R.drawable.tlacidlo_release);
                b_nastaveni.setBackgroundResource(R.drawable.tlacidlo_pressed);
                break;
        }
    }

    private void _Init() {
        ctx = this;
        sharedPrefsString = "";
        _showRozpis = false;
        //SaveSharedPrefs();
        b_tabulka = findViewById(R.id.Tabulka);
        b_rozpis = findViewById(R.id.Rozpis);
        b_nastaveni = findViewById(R.id.nastaveni);
        TextView tVpristizapas = (TextView) findViewById(R.id.tV_status);
        tVpristizapas.setVisibility(View.GONE);
        HTTP_populateHriste Hriste = new HTTP_populateHriste(ctx, teamDetails.this);
        Hriste.execute(URL + dotaz_hriste);

        b_rozpis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showRozpis =true;
                b_buttonpressed = 2;
                HTTP_getRozpis rozp = new HTTP_getRozpis(ctx, teamDetails.this);
                rozp.execute(URL + dotaz_teamdetails + year + "&competition=" + competition + "&season=" + season + "&id=" + teamID);
                refresh_buttons();
            }
        });

        b_tabulka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = URL + dotaz_TeamStandings + year + "&competition="+competition+"&season="+season+"&league="+liga+"&group="+group+"&type=final";
                b_buttonpressed = 1;
                Log.d("PSMF", "link = " + link);
                HTTP_tabulkaTymu hTT = new HTTP_tabulkaTymu(ctx, teamDetails.this);
                hTT.execute(link);
                refresh_buttons();
            }
        });

        b_nastaveni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_buttonpressed = 3;
                refresh_buttons();
                alertDialogBuilder = new AlertDialog.Builder(teamDetails.this);
                alertDialogBuilder.setMessage("Nastavení");
                View view = getLayoutInflater().inflate(R.layout.alertdialog_options, null);
                alertDialogBuilder.setView(view);
                //LinearLayout skroller = (LinearLayout) view.findViewById(R.id.vypisfavorites);

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                LoadSharedPrefs();

                TextView tv_favorites = (TextView) view.findViewById(R.id.showfavorites);
                tv_favorites.setVisibility(View.GONE);
                LinearLayout tejbl = (LinearLayout) view.findViewById(R.id.vypis_favorites);

                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                async_showFavories sFav = new async_showFavories(ctx);
                final List<skupina> _tmp = sFav.VratMiSharedPrefJakoList();
                if(_tmp.size() >0) { tv_favorites.setVisibility(View.VISIBLE); tv_favorites.setText(getResources().getString(R.string.odebratoblibenetymy)); }
                for(int o=0; o<_tmp.size(); o++) {
                    Button b_tym = new Button(ctx);
                    b_tym.setLayoutParams(lparams);
                    Drawable img = ctx.getResources().getDrawable( R.drawable.remove);
                    img.setBounds( 0, 0, 60, 60 );
                    b_tym.setCompoundDrawables( img, null, null, null );
                    b_tym.setText(_tmp.get(o).getNazev() + ", " + _tmp.get(o).getLiga() + "." + _tmp.get(o).getSkupina());
                    Log.d("PSMF", "AlertDialog :: " + _tmp.get(o).getNazev() + ", " + _tmp.get(o).getLiga() + "." + _tmp.get(o).getSkupina());
                    tejbl.addView(b_tym);
                    final skupina st = _tmp.get(o);
                    b_tym.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            async_showFavories asSF = new async_showFavories(ctx);
                            asSF.RemoveFavorites(st.getNazev());
                            b_buttonpressed = 0;
                            refresh_buttons();
                            alertDialog.dismiss();
                        }
                    });
                }

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        b_buttonpressed = 0;
                        refresh_buttons();
                        alertDialog.dismiss();
                    }
                });

                Button b_pridejOblibene = (Button) view.findViewById(R.id.pridejdooblibenych);
                Button b_pridejDoKalendare = (Button) view.findViewById(R.id.pridejzapasydokalendare);
                Button b_PSMF = (Button) view.findViewById(R.id.btn_otevriPSMFweb);
                Button b_TydenikHansp = (Button) view.findViewById(R.id.btn_TydenikHanspaulka);

                b_PSMF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://www.psmf.cz/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

                b_TydenikHansp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://tydenik.psmf.cz/hl/"+liga+"/"+ Character.toLowerCase(GroupToChar(group))+"/";
                        Log.d("PSMF","Wolam Tydenik URL = " + url);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

                b_pridejOblibene.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //LoadSharedPrefs();
                        async_showFavories asSF = new async_showFavories(ctx);
                        if(asSF.KolikZaznamuSharedPrefs() <=LIMIT_NUMBER_OF_FAVORITES){
                            String sb = "|" + tym + "^" + teamID + "^" +GroupToChar(group) + "^" + liga + "^" + competition;
                            if(!asSF.isTheTeamThere(tym)) {
                                asSF.SaveSharedPrefs(sharedPrefsString + sb);
                                Toast.makeText(ctx, ctx.getResources().getString(R.string.pridano), Toast.LENGTH_SHORT).show();
                            } else Toast.makeText(ctx, ctx.getResources().getString(R.string.favorites_exists), Toast.LENGTH_SHORT).show();
                        } else Toast.makeText(ctx, ctx.getResources().getString(R.string.moc_favorites), Toast.LENGTH_SHORT).show();
                        b_buttonpressed = 0;
                        refresh_buttons();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private char GroupToChar(int c) {
        return (char) (c+64);
    }
    private int CharToGroup(char c) {
        //Log.d("PSMF", "CharToGroup :: c=" + c + " >>> " + Character.getNumericValue(c));
        return Character.getNumericValue(c)-9;
    }

    private void PARSE_RESULT(String s, int prepinac) {
        if(s != null) {
            try {
                rozpis_JSON = s;
                JSONObject jO = new JSONObject(s);
                Log.d("PSMF", "PARSE_RESULT = " + s);
                JSONArray jAsan = null;
                jAsan = jO.getJSONArray("teams");
                for (int i = 0; i < jAsan.length(); i++) {
                    skupina sk = new skupina();
                    int group=0, barva=0, league=0, competition=0, season=0, rok=0;
                    String steamName="", www="";

                    JSONObject c = jAsan.getJSONObject(i);
                    group = c.getInt("group");
                    barva = c.optInt("jerseyColor", 0);
                    league = c.getInt("league");
                    competition = c.getInt("competition");
                    season = c.getInt("season");
                    rok = c.getInt("year");
                    teamID = c.getInt("teamId");
                    steamName = c.getString("teamName");
                    www = c.getString("www");

                    sk.setNazev(steamName);
                    sk.setWww(www);
                    sk.setTeam_id(teamID);
                    sk.setColor(barva);
                    sk.setLiga(league);
                    sk.setSkupina(GroupToChar(group));
                    sk.setCompetition(competition);
                    sk.setSeason(season);
                    sk.setYear(rok);
                    vysledek.add(sk);
                    //String datum = c.getString("date");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                ZobrazVysledek();
            }
        }
    }

    private void NadchazejiciZapasy() {
        //vysledek;
        Collections.sort(_tabRozpis, new TabulkaRozpis());
        //Collections.sort(_tabRozpis);
        Calendar cal = Calendar.getInstance();
        int dizManf = cal.get(Calendar.MONTH) +1;
        int dizDej = cal.get(Calendar.DAY_OF_MONTH);
        TableLayout table = (TableLayout)findViewById(R.id.tabule);
        table.removeAllViews();
        TextView tVpristizapas = (TextView) findViewById(R.id.tV_status);
        tVpristizapas.setVisibility(View.VISIBLE);
        tVpristizapas.setText(getResources().getString(R.string.nadchazejicizapasy));
        Boolean ukaz_zaznam = false;

        for(int c=0; c<_tabRozpis.size(); c++) {
            ukaz_zaznam = false;
            String datum = _tabRozpis.get(c).cas;
            String tym_doma = _tabRozpis.get(c).hometeam;
            String tym_venku = _tabRozpis.get(c).awayteam;
            String hriste = _tabRozpis.get(c).hriste;
            int kolo = _tabRozpis.get(c).kolo;
            String[] _tmp = new String[5];
            _tmp = datum.split("\\.");
            int preparovanyDej = Integer.parseInt(_tmp[0]);
            int preparovanyManf = Integer.parseInt(_tmp[1]);
            Log.d("NadchazejiciZapasy", "[datum]:" + datum + ", [tym_doma]:" + tym_doma + ",[tym_venku]:" + tym_venku + ", [kolo]:" + kolo + " >> prepDay="+preparovanyDej+", prepManf="+preparovanyManf);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

                if (dizManf < preparovanyManf) {
                    ukaz_zaznam = true;
                    Log.d("NadchazejiciZapasy", "dizManf <= preparovanyManf :: " + dizManf + "<=" + preparovanyManf + "?? " + " => YES");
                } else if (dizManf == preparovanyManf) {
                    Log.d("NadchazejiciZapasy", "dizManf <= preparovanyManf :: " + dizManf + "<=" + preparovanyManf + "?? " + " => NO");
                    if (dizDej <= preparovanyDej) {
                        Log.d("NadchazejiciZapasy", "dizDej <= preparovanyDej :: " + dizDej + "<=" + preparovanyDej + "?? " + " => YES");
                        ukaz_zaznam = true;
                    } else {
                        ukaz_zaznam = false;
                        Log.d("NadchazejiciZapasy", "dizDej <= preparovanyDej :: " + dizDej + "<=" + preparovanyDej + "?? " + " => NO");
                    }
                }

                //pridej zapasy do temp tabulky
                if (ukaz_zaznam) {
                    TableRow row = new TableRow(ctx);

                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(5, 5, 5, 5);
                    row.setLayoutParams(lp);
                    row.setPadding(5, 10, 5, 10);

                    TextView t1 = new TextView(ctx);
                    TextView t2 = new TextView(ctx);
                    TextView t3 = new TextView(ctx);
                    t1.setText(datum);
                    if (!tym_doma.equals(tym)) t2.setText(" - " + tym_doma);
                    else t2.setText(" - " + tym_venku);

                    //zjisti link na hriste
                    String ihrisko = null;
                    ihrisko = Hriste_VratMiURL(hriste);
                    if (ihrisko != null) {
                        String URLBuildMe = "<a href='" + ihrisko + "'>" + hriste + "</a>";
                        t3.setText(Html.fromHtml(URLBuildMe));
                        t3.setClickable(true);
                        t3.setMovementMethod(LinkMovementMethod.getInstance());
                    } else t3.setText(hriste);

                    row.addView(t1);
                    row.addView(t2);
                    row.addView(t3);
                    table.addView(row);
                }
        }
    }

    private String Hriste_VratMiURL(String zkratka) {
        String ret = "https://maps.google.com/?q=";
        for(int f=0;f<hriste.size();f++) {
            //Log.d("Hriste_VratMiURL", zkratka + " ?= " + hriste.get(f).getZkratka());
            if(zkratka.equals(hriste.get(f).getZkratka())) {
                if((!hriste.get(f).getLAT().equals("null")) && (!hriste.get(f).getLONG().equals("null"))) {
                    ret += hriste.get(f).getLAT() + "," + hriste.get(f).getLONG();
                } else return null;
                break;
            }
        }
        return ret;
    }

    private void ZobrazVysledek() {
        TextView teamName = findViewById(R.id.teamName);
        TextView teamWWW = findViewById(R.id.teamWWW);
        //TextView teamLiga = findViewById(R.id.teamLiga);

        //Log.d("PSMF", "WELIKOST POLE WOLE = " + vysledek.size());
        if(vysledek.size() > 0) {
            skupina sk = vysledek.get(vysledek.size() - 1);
            this.competition = sk.getCompetition();
            this.season = sk.getSeason();
            this.teamID = sk.getTeam_id();
            this.group = CharToGroup(sk.getSkupina());
            this.liga = sk.getLiga();
            this.tym = sk.getNazev();
            this.www = sk.getWww();
            this.year = sk.getYear();

            String soutez = "";
            switch (competition) {
                case 1:
                    soutez = "(Hanspaulka)";
                    break;
                case 2:
                    soutez = "(Veteráni)";
                    break;
                case 3:
                    soutez = "(Superveteráni)";
                    break;
                case 4:
                    soutez = "(Ultraveteráni)";
                    break;
            }

            teamName.setText(this.tym + ", " + getResources().getString(R.string.liga) + ": " + this.liga + ". " + GroupToChar(this.group) + " " + soutez);
            Log.d("PSMF", "WWW mi vraci " + www);
            if (www != null && !www.isEmpty() && !www.equals("null")) {
                teamWWW.setClickable(true);
                teamWWW.setMovementMethod(LinkMovementMethod.getInstance());
                teamWWW.setText(Html.fromHtml("<a href='" + www + "'>" + www + "</a>"));
            } else teamWWW.setText("");
        } else Toast.makeText(ctx, ctx.getResources().getString(R.string.error_nacitanidat), Toast.LENGTH_SHORT).show();
    }

    //interface z HTTP_getRozpis
    @Override
    public void taskCompleted(Object o) {
        String jA = (String) o;
        _tabRozpis = new ArrayList<>();
        //Log.d("PSMF", "RESULT :: " + jA);
        try {
            JSONObject jO = new JSONObject(jA);
            JSONArray jAsan = null;
            jAsan = jO.getJSONArray("games");
            //Log.d("PSMF", "Welikost pole je wole " + jAsan.length());
            for (int i = 0; i < jAsan.length(); i++) {
                JSONObject c = jAsan.getJSONObject(i);
                int kolo = c.getInt("order");
                String teamHome = c.getString("teamHome");
                String teamAway = c.getString("teamAway");
                int goalsHome = 0;
                if (!c.isNull("goalsHome")) goalsHome = c.getInt("goalsHome"); else goalsHome = 0;
                int goalsAway = 0;
                if (!c.isNull("goalsAway")) goalsAway = c.getInt("goalsAway"); else goalsAway = 0;
                String date = c.getString("date");
                String hriste = c.getString("location");
                //Log.d("PSMF", "parsed ... " + teamHome + " " + goalsHome + " : " + goalsAway + " " + teamAway);

                TabulkaRozpis tmp = new TabulkaRozpis();
                tmp.setAwayteam(teamAway);
                tmp.setHometeam(teamHome);
                tmp.setCas(VratMiDatum(date));
                tmp.setHriste(hriste);
                tmp.setGoalsAway(goalsAway);
                tmp.setGoalsHome(goalsHome);
                tmp.setKolo(kolo);

                _tabRozpis.add(tmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(_showRozpis) UpdateTable(_tabRozpis);
            else NadchazejiciZapasy();
        }
    }

    private void UpdateTable(List<TabulkaRozpis> lst) {
        //Collections.sort(lst);
        Collections.sort(lst, new TabulkaRozpis());

        TableLayout table = (TableLayout)findViewById(R.id.tabule);
        table.removeAllViews();
        Resources resource = ctx.getResources();
        for(int i=0; i<lst.size(); i++) {
            TabulkaRozpis tR = lst.get(i);

            //1. radek
            final TextView tv = new TextView(this);
            final TextView tvHriste = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.LEFT);
            tv.setPadding(5, 15, 0, 15);
            tvHriste.setPadding(5,15,0,15);

            String hriste = tR.getHriste();
            String ihrisko = Hriste_VratMiURL(hriste);
            if(ihrisko != null) {
                String URLBuildMe = "<a href='" + ihrisko + "'>" + hriste + "</a>";
                Log.d("PSMF", "hriste = " + hriste + " :: " + URLBuildMe);
                tvHriste.setText(Html.fromHtml(URLBuildMe));
                tvHriste.setClickable(true);
                tvHriste.setMovementMethod(LinkMovementMethod.getInstance());
            } else tvHriste.setText(hriste);

            SpannableString ss = new SpannableString("    " + tR.getCas() + " - " + tR.getKolo() + ". kolo");
            Drawable d = ContextCompat.getDrawable(this, R.drawable.calendar);
            Drawable d2 = ContextCompat.getDrawable(this, R.drawable.clock);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            d2.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ImageSpan span2 = new ImageSpan(d2, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            ss.setSpan(span2, 11, 12, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv.setText(ss);

            //2. radek
            TableRow.LayoutParams paramsTymname = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            //paramsTymname.setMargins(2,2,2,2);
            paramsTymname.weight = 5;
            paramsTymname.width = 0;
            TableRow.LayoutParams paramsSmall = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            //paramsSmall.setMargins(2,2,2,2);
            paramsSmall.weight = 1;
            paramsSmall.width = 0;

            TableRow tr = new TableRow(ctx);
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            trParams.span =13;
            //tr.setWeightSum(13);
            TableRow tr2 = new TableRow(ctx);
            TableRow.LayoutParams trParams2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr2.setLayoutParams(trParams2);
            tr2.setWeightSum(13);


            //Domaci tym
            final TextView tv3 = new TextView(this);
            tv3.setLayoutParams(paramsTymname);
            tv3.setGravity(Gravity.RIGHT);
            tv3.setGravity(Gravity.CENTER_VERTICAL);
            tv3.setPadding(5, 5, 5, 5);
            tv3.setBackgroundColor(Color.rgb(255, 255, 255));
            tv3.setText(tR.getHometeam());

            //hoste tym
            final TextView tv8 = new TextView(this);
            tv8.setLayoutParams(paramsTymname);
            tv8.setBackgroundColor(Color.rgb(255, 255, 255));
            tv8.setGravity(Gravity.LEFT);
            tv8.setGravity(Gravity.CENTER_VERTICAL);
            tv8.setPadding(5, 5, 0, 5);
            tv8.setText(tR.getAwayteam());


            //goly domaci
            final TextView tv4 = new TextView(this);
            tv4.setLayoutParams(paramsSmall);
            tv4.setBackgroundColor(Color.rgb(255, 255, 255));
            tv4.setGravity(Gravity.RIGHT);
            tv4.setGravity(Gravity.CENTER_VERTICAL);
            tv4.setPadding(5, 5, 5, 5);
            if(tR.getGoalsHome() > tR.getGoalsAway()) {
                tv4.setTypeface(tv4.getTypeface(), Typeface.BOLD);
                tv3.setTypeface(tv3.getTypeface(), Typeface.BOLD);
            }
            tv4.setText(String.valueOf(tR.getGoalsHome()));

            // :
            final TextView tv5 = new TextView(this);
            tv5.setLayoutParams(paramsSmall);
            tv5.setBackgroundColor(Color.rgb(255, 255, 255));
            tv5.setGravity(Gravity.CENTER);
            tv5.setGravity(Gravity.CENTER_VERTICAL);
            tv5.setPadding(5, 5, 0, 5);
            tv5.setText(" : ");

            //goly hoste
            final TextView tv7 = new TextView(this);
            //tv7.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tv7.setLayoutParams(paramsSmall);
            tv7.setGravity(Gravity.LEFT);
            tv7.setGravity(Gravity.CENTER_VERTICAL);
            tv7.setBackgroundColor(Color.rgb(255, 255, 255));
            tv7.setPadding(5, 5, 5, 5);
            if(tR.getGoalsHome() < tR.getGoalsAway()) {
                tv7.setTypeface(tv7.getTypeface(), Typeface.BOLD);
                tv8.setTypeface(tv8.getTypeface(), Typeface.BOLD);
            }
            tv7.setText(String.valueOf(tR.getGoalsAway()));

            tr.setBackgroundColor(Color.rgb(209, 211, 214));
            //tr.addView(tv, trParams);
            tr.addView(tv);
            tr.addView(tvHriste);
            table.addView(tr);

            tr2.setBackgroundColor(Color.rgb(209, 211, 214));
            tr2.addView(tv3);
            tr2.addView(tv4);
            tr2.addView(tv5);
            tr2.addView(tv7);
            tr2.addView(tv8);
            table.addView(tr2);
        }
    }

    private String VratMiDatum(String dat) {
        //format 2018-10-15T18:00:00
        String[] d = dat.split("T");
        String[] form = d[0].split("-");
        String cas = d[1].substring(0,d[1].length()-3);
        return form[2] + "." + form[1] + ".,  "+cas;
    }

    //interface z HTTP_tabulkaTymu
    @Override
    public void finished(Object o) {
        TableLayout laya = (TableLayout)findViewById(R.id.tabule);
        laya.removeAllViews();
        if(o != null) {
            String s = (String) o;
            TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tableParams.setMargins(3, 3, 3, 5);
            TableLayout tejbl = new TableLayout(ctx);
            tejbl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            JSONObject tymy = new JSONObject();
            if((s != null) || (!s.isEmpty())) {
                //Log.d("HTTP_getZapasyOstatnich", "dostal jsem se pres NULL");
                try {
                    JSONObject jO = new JSONObject(s);
                    JSONObject jTable  = jO.getJSONObject("table");
                    JSONArray jContent = jTable.getJSONArray("content");
                    Resources resource = ctx.getResources();
                    /* SET HEADER */
                    TableRow row= new TableRow(ctx);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    row.setBackgroundColor(Color.rgb(245, 245, 240));
                    lp.setMargins(5,5,5,5);
                    row.setLayoutParams(lp);
                    row.setPadding(0,10,0,10);
                    TextView tPozice = new TextView(ctx);
                    tPozice.setLayoutParams(rowParams);
                    TextView tTym = new TextView(ctx);
                    tTym.setLayoutParams(rowParams);
                    TextView tVRP = new TextView(ctx);
                    tVRP.setLayoutParams(rowParams);
                    TextView tGF = new TextView(ctx);
                    tGF.setLayoutParams(rowParams);
                    TextView tGA = new TextView(ctx);
                    tGA.setLayoutParams(rowParams);
                    TextView tOdehrano = new TextView(ctx);
                    tOdehrano.setLayoutParams(rowParams);
                    TextView tPTS = new TextView(ctx);
                    tPTS.setLayoutParams(rowParams);
                    tPozice.setText("#");
                    tTym.setText(" | " + getResources().getString(R.string.tym));
                    tVRP.setText("| " + getResources().getString(R.string.vrp));
                    tGF.setText("| " + getResources().getString(R.string.gf));
                    tGA.setText("| " + getResources().getString(R.string.ga));
                    tOdehrano.setText("| " + getResources().getString(R.string.odeh));
                    tPTS.setText("| " + getResources().getString(R.string.bodu));
                    row.addView(tPozice);
                    row.addView(tTym);
                    row.addView(tVRP);
                    row.addView(tGF);
                    row.addView(tGA);
                    row.addView(tOdehrano);
                    row.addView(tPTS);
                    tejbl.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    /* END HEADER */
                    for(int u=0; u<jContent.length(); u++) {
                        JSONObject ooo = jContent.getJSONObject(u);
                        String pozice = ooo.getString("position");
                        String tym = ooo.getString("teamName");
                        String vitezstvi = ooo.getString("wins");
                        String remiz = ooo.getString("draws");
                        String proher = ooo.getString("loses");
                        String gF = ooo.getString("goalsFor");
                        String gA = ooo.getString("goalsAgainst");
                        String odehrano = ooo.getString("numOfGames");
                        String ptz = ooo.getString("points");

                        row= new TableRow(ctx);
                        lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                        if(mod(u,2) ==0) row.setBackgroundColor(Color.rgb(245, 245, 240));
                        else row.setBackgroundColor(Color.rgb(255,255,255));
                        lp.setMargins(5,5,5,5);
                        row.setLayoutParams(lp);
                        //row.setPadding(0,10,0,10);

                        //TextView tPozice = ((Activity) akt).findViewById(R.id.Pozice);
                        tPozice = new TextView(ctx);
                        tPozice.setLayoutParams(rowParams);
                        //TextView tTym = ((Activity) akt).findViewById(R.id.Tym);
                        tTym = new TextView(ctx);
                        tTym.setLayoutParams(rowParams);
                        //TextView tVRP = ((Activity) akt).findViewById(R.id.VRP);
                        tVRP = new TextView(ctx);
                        tVRP.setLayoutParams(rowParams);
                        //TextView tGF = ((Activity) akt).findViewById(R.id.GF);
                        tGF = new TextView(ctx);
                        tGF.setLayoutParams(rowParams);
                        //TextView tGA = ((Activity) akt).findViewById(R.id.GA);
                        tGA = new TextView(ctx);
                        tGA.setLayoutParams(rowParams);
                        //TextView tOdehrano = ((Activity) akt).findViewById(R.id.Odeh);
                        tOdehrano = new TextView(ctx);
                        tOdehrano.setLayoutParams(rowParams);
                        //TextView tPTS = ((Activity) akt).findViewById(R.id.PTS);
                        tPTS = new TextView(ctx);
                        tPTS.setLayoutParams(rowParams);

                        if((vitezstvi.equals("")) || (vitezstvi.isEmpty())) vitezstvi = "0";
                        if((proher.equals("")) || (proher.isEmpty())) proher = "0";
                        if((remiz.equals("")) || (remiz.isEmpty())) remiz = "0";

                        tPozice.setText(pozice);
                        tTym.setText(" | " + tym);
                        tVRP.setText(" | "+ vitezstvi+"/"+remiz+"/"+proher);
                        tGF.setText(" | " + gF);
                        tGA.setText(" | "+ gA);
                        tOdehrano.setText(" | " + odehrano);
                        tPTS.setText(" | "+ ptz);
                        //Log.d("HTTP_teamStandings", "Pozice #" + pozice + ", Tym=" + tym + ", VRP=" + vitezstvi+"/"+remiz+"/"+proher);

                        row.addView(tPozice);
                        row.addView(tTym);
                        row.addView(tVRP);
                        row.addView(tGF);
                        row.addView(tGA);
                        row.addView(tOdehrano);
                        row.addView(tPTS);
                        tejbl.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                laya.addView(tejbl);
            }
        }
    }

    private int mod(int x, int y)
    {
        int result = x % y;
        return result < 0? result + y : result;
    }

    private void SaveSharedPrefs() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("Favorites", MODE_PRIVATE).edit();
        LoadSharedPrefs();
        Log.d("PSMF", "SharedPrefs :: Loaded :: " + sharedPrefsString);
        String sb = "|" + this.tym + "^" + this.teamID + "^" + GroupToChar(this.group) + "^" + this.liga + "^" + this.competition;
        sharedPrefsString += sb;
        editor.putString("listtymu", sharedPrefsString);
        editor.commit();
        /*
        editor.putString("teamName", this.tym);
        editor.putString("www", this.www);
        editor.putInt("teamID", this.teamID);
        editor.putInt("group", this.group);
        editor.putInt("liga", this.liga);
        editor.putInt("year", this.year);
        editor.putInt("season", this.season);
        editor.putInt("competition", this.competition);
        if(editor.commit() ==true) Toast.makeText(this, "Uloženo", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Došlo k chybě při ukládání. Má aplikace povoleno ukládat na úložiště?", Toast.LENGTH_SHORT).show();
        */
/*
        editor.clear();
        editor.commit();
*/
    }

    private void LoadSharedPrefs() {
        SharedPreferences shp = this.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        Map<String,?> entries = shp.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            if(key.equals("listtymu")) {
                sharedPrefsString = entries.get(key).toString();
            }
            Log.d("PSMF", "SharedPrefs found :: " + key + " = " + entries.get(key).toString());
        }
    }

    //Interface z HTTP_populateHriste
    @Override
    public void HristePopulated(Object o) {
        String j = (String) o;
        hriste = new ArrayList<>();
        if(j != null) {
            JSONObject jO = null;
            try {
                jO = new JSONObject(j);
                JSONArray Arr = jO.getJSONArray("fields");
                for (int i = 0; i < Arr.length(); i++) {
                    _hriste hh = new _hriste();
                    JSONObject c = Arr.getJSONObject(i);
                    //hh.setActive(c.getInt("active"));
                    hh.setAdresa(c.getString("fieldAddress"));
                    hh.setJmeno(c.getString("fieldName"));
                    hh.setZkratka(c.getString("fieldShortcut"));
                    hh.setPopis(c.getString("fieldDescription"));
                    hh.setLAT(c.getString("fieldLatitude"));
                    hh.setLONG(c.getString("fieldLongitude"));
                    hriste.add(hh);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
