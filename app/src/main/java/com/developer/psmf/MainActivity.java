package com.developer.psmf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements getLeagueList.diag_completed, HTTP_HledejTym.taskCompleted {
    private Button bHansp, bVet, bSuperVet, bUltraVet;
    private ImageView _vyhledjtym, _settings;
    private TextView zobrazujuvysledky;
    private AutoCompleteTextView tv_lookupteam;
    private getLeagueList ligovy_list, ligovy_list_1, ligovy_list_2, ligovy_list_3, ligovy_list_4;
    private List<String> arr = new ArrayList<>();
    private int precache_1, precache_2, precache_3, precache_4;
    private int user_league_selection;

    private List<skupina> HANSPseznamLig;
    private List<Integer> HANSPprehledLig = new ArrayList<Integer>();
    private List<skupina> VETPseznamLig;
    private List<Integer> VETPprehledLig = new ArrayList<Integer>();
    private List<skupina> SUPPseznamLig;
    private List<Integer> SUPPrehledLig = new ArrayList<Integer>();
    private List<skupina> ULTRAPseznamLig;
    private List<Integer> ULTRAPprehledLig = new ArrayList<Integer>();
    //private List<skupina> tmp_seznamLig;
    //private List<Integer> tmp_prehledLig = new ArrayList<Integer>();

    private Context ctx;
    private int rok, mesic;
    private int wantedLiga;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog precacheDialog;
    private ExpandableListView expandableListView;
    List<String[]> secondLevel = new ArrayList<>();
    List<LinkedHashMap<String, String[]>> data = new ArrayList<>();
    private LinearLayout vysledky;
    private String getLeagueListString;
    private ArrayAdapter<String> hledejTymy;
    private TextView precache_hansp, precache_vet, precache_supervet, precache_ultravet;
    private String _supportedVersion = "Jelly Bean - 4.1+";
    private int _minSDKVersion = 16;
    private _config cfg;

    private static String URL_hosting;
    private static String URL_hledejtym;
    private static String URL_hanspaulka;
    private static String URL_veterani;
    private static String URL_superveterani;
    private static String URL_ultraveterani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT < _minSDKVersion){
            OlderAppFound();
        } else {
            setContentView(R.layout.main_screen);
            _Init();
        }
    }

    private void _Init() {
        ctx = this;
        precache_1 =0;
        precache_2 =0;
        precache_3 =0;
        precache_4 =0;

        cfg = new _config();
        URL_hosting = cfg.getURL_hosting();
        URL_hledejtym = cfg.getURL_hledejtym();
        URL_hanspaulka = cfg.getURL_hanspaulka();
        URL_veterani = cfg.getURL_veterani();
        URL_superveterani = cfg.getURL_superveterani();
        URL_ultraveterani = cfg.getURL_ultraveterani();

        this.rok = Calendar.getInstance().get(Calendar.YEAR);
        this.mesic = Calendar.getInstance().get(Calendar.MONTH);
        bHansp = (Button) findViewById(R.id.ibtn_Hansp);
        bVet = (Button) findViewById(R.id.ibtn_VET);
        bSuperVet = (Button) findViewById(R.id.ibtn_SupVet);
        bUltraVet = (Button) findViewById(R.id.ibtn_UltraVet);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        _vyhledjtym = (ImageView) findViewById(R.id.btn_vyhledjtym);
        _settings = (ImageView) findViewById(R.id.btn_settings);
        zobrazujuvysledky = (TextView) findViewById(R.id.zobrazujuvysledky);
        zobrazujuvysledky.setVisibility(View.GONE);
        tv_lookupteam = (AutoCompleteTextView) findViewById(R.id.tv_lookupteam);
        final int calyear = Calendar.getInstance().get(Calendar.YEAR);
        final int calmth = Calendar.getInstance().get(Calendar.MONTH);
        vysledky = (LinearLayout) findViewById(R.id.vysledkovac);
        expandableListView = (ExpandableListView) findViewById(R.id.lvExp);
        wantedLiga =1; //Hanspaulka by default

        //prepare lists for cache all teams in the league
        getLeagueListString = null;
        HANSPseznamLig = new ArrayList<skupina>();
        HANSPprehledLig = new ArrayList<Integer>();
        VETPseznamLig = new ArrayList<skupina>();
        VETPprehledLig = new ArrayList<Integer>();
        SUPPseznamLig = new ArrayList<skupina>();
        SUPPrehledLig = new ArrayList<Integer>();
        ULTRAPseznamLig = new ArrayList<skupina>();
        ULTRAPprehledLig = new ArrayList<Integer>();

        int syzn =0;
        if (calmth < 8) syzn =1; else syzn =2;
        SuperFetch();
        //nacachuj HANSP
        //ligovy_list = new getLeagueList(MainActivity.this, MainActivity.this);
        //ligovy_list.execute(URL_hanspaulka + syzn + "&year=" + calyear);

        //kliknu na HANSPAULKU
        bHansp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int syzn =0;
                if (calmth < 8) syzn =1; else syzn =2;
                wantedLiga =1;
                //pokud dotaz jeste nebyl spusten, nacachuj vysledek
                if((getLeagueListString != null) && (HANSPseznamLig.size() >0)) {
                        _parseResultIntoAdapter(getLeagueListString, 1, 1);
                } else {
                    ligovy_list = new getLeagueList(MainActivity.this, MainActivity.this, 1);
                    ligovy_list.execute(URL_hosting + URL_hanspaulka + syzn + "&year=" + calyear);
                }
                //PocetLig(seznamLig);
                zobrazujuvysledky.setText(getResources().getString(R.string.hanspaulka));
                zobrazujuvysledky.setVisibility(View.VISIBLE);
                user_league_selection = 1;
                ButtonGraphicsRefresh();
            }
        });

        //kliknu na VETERANY
        bVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int syzn =0;
                if (calmth < 8) syzn =1; else syzn =2;
                wantedLiga =2;
                Log.d("PSMF", "getLeagueListString = " + getLeagueListString);
                Log.d("PSMF", "VETPprehledLig.size = " + VETPprehledLig.size());
                if((getLeagueListString != null) && (VETPprehledLig.size() >0)) {
                    Log.d("PSMF", "vse je nacachovano, nepotrebuju HTTP");
                    _parseResultIntoAdapter(getLeagueListString, 1, 2);
                } else {
                    Log.d("PSMF", "volam HTTP protoze list je prazdny");
                    ligovy_list = new getLeagueList(MainActivity.this, MainActivity.this, 2);
                    ligovy_list.execute(URL_hosting + URL_veterani + syzn + "&year=" + calyear);
                }
                zobrazujuvysledky.setText(getResources().getString(R.string.veterani));
                zobrazujuvysledky.setVisibility(View.VISIBLE);
                user_league_selection = 2;
                ButtonGraphicsRefresh();
            }
        });

        bSuperVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int syzn =0;
                if (calmth < 8) syzn =1; else syzn =2;
                wantedLiga =3;
                Log.d("PSMF", "getLeagueListString = " + getLeagueListString);
                Log.d("PSMF", "SUPprehledLig.size = " + SUPPseznamLig.size());
                if((getLeagueListString != null) && (SUPPseznamLig.size() >0)) {
                    Log.d("PSMF", "vse je nacachovano, nepotrebuju HTTP");
                    _parseResultIntoAdapter(getLeagueListString, 1, 3);
                } else {
                    Log.d("PSMF", "volam HTTP protoze list je prazdny");
                    ligovy_list = new getLeagueList(MainActivity.this, MainActivity.this, 3);
                    ligovy_list.execute(URL_hosting + URL_superveterani + syzn + "&year=" + calyear);
                }
                zobrazujuvysledky.setText(getResources().getString(R.string.superveterani));
                zobrazujuvysledky.setVisibility(View.VISIBLE);
                user_league_selection = 3;
                ButtonGraphicsRefresh();
            }
        });

        bUltraVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int syzn =0;
                if (calmth < 8) syzn =1; else syzn =2;
                wantedLiga =4;
                Log.d("PSMF", "getLeagueListString = " + getLeagueListString);
                Log.d("PSMF", "ULTRAPseznamLig.size = " + ULTRAPseznamLig.size());
                if((getLeagueListString != null) && (ULTRAPseznamLig.size() >0)) {
                    Log.d("PSMF", "vse je nacachovano, nepotrebuju HTTP");
                    _parseResultIntoAdapter(getLeagueListString, 1, 4);
                } else {
                    Log.d("PSMF", "volam HTTP protoze list je prazdny");
                    ligovy_list = new getLeagueList(MainActivity.this, MainActivity.this, 4);
                    ligovy_list.execute(URL_hosting + URL_ultraveterani + syzn + "&year=" + calyear);
                }
                zobrazujuvysledky.setText(getResources().getString(R.string.ultraveterani));
                zobrazujuvysledky.setVisibility(View.VISIBLE);
                user_league_selection = 4;
                ButtonGraphicsRefresh();
            }
        });

        //HLEDAM TYM - SEARCH pressed
        _vyhledjtym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strLookupResult = tv_lookupteam.getText().toString();
                if (strLookupResult.trim().length() > 0) {
                    HTTP_HledejTym asyncTask = new HTTP_HledejTym(ctx, MainActivity.this);
                    String link = "";
                    link = URL_hosting + URL_hledejtym + tv_lookupteam.getText() + "&year="+rok+"&token";
                    /*
                    if(mesic <=2) {
                        link = URL_hledejtym + tv_lookupteam.getText() + "&year="+(rok-1)+"&token";
                    } else link = URL_hledejtym + tv_lookupteam.getText() + "&year="+rok+"&token";
                    */
                    Log.d("PSMF", "Volam halo halo :: " + link);
                    asyncTask.execute(link);
                }
            }
        });

        _settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                async_showFavories sFav = new async_showFavories(ctx);
                final List<skupina> _tmp = sFav.VratMiSharedPrefJakoList();
                alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage("Nastavení");
                View view = getLayoutInflater().inflate(R.layout.alertdialog_options, null);
                alertDialogBuilder.setView(view);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView tV = view.findViewById(R.id.showfavorites);
                Button b1 = view.findViewById(R.id.pridejzapasydokalendare);
                Button b2 = view.findViewById(R.id.pridejdooblibenych);
                Button b_PSMF = (Button) view.findViewById(R.id.btn_otevriPSMFweb);
                LinearLayout tejbl = (LinearLayout) view.findViewById(R.id.vypis_favorites);

                b1.setVisibility(View.GONE);
                b2.setVisibility(View.GONE);
                tV.setText(getResources().getString(R.string.oblibenetymy));
                if(_tmp.size() >0) {
                    tV.setVisibility(View.VISIBLE);
                    for (int o = 0; o < _tmp.size(); o++) {
                        Button b_tym = new Button(ctx);
                        b_tym.setLayoutParams(lparams);
                        b_tym.setText(_tmp.get(o).getNazev() + ", " + _tmp.get(o).getLiga() + "." + _tmp.get(o).getSkupina());
                        Log.d("PSMF", "AlertDialog :: " + _tmp.get(o).getNazev() + ", " + _tmp.get(o).getLiga() + "." + _tmp.get(o).getSkupina());
                        tejbl.addView(b_tym);
                        final skupina st = _tmp.get(o);

                        b_tym.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HTTP_teamDetails wwwTD = new HTTP_teamDetails(ctx);
                                wwwTD.execute(String.valueOf(st.getNazev()));
                                alertDialog.dismiss();
                            }
                        });
                    }
                } else tV.setVisibility(View.GONE);

                b_PSMF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "http://www.psmf.cz/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
        });
    }

    private void OlderAppFound() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Aplikace podporuje verzi Android " + _supportedVersion + ". Vaše zařízení má naistalovanou verzi " + android.os.Build.VERSION.RELEASE);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finishAffinity();
                        System.exit(0);
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void SuperFetch() {
        final int calyear = Calendar.getInstance().get(Calendar.YEAR);
        final int calmth = Calendar.getInstance().get(Calendar.MONTH);
        int syzn =0;
        if (calmth < 8) syzn =1; else syzn =2;
        AlertDialog.Builder precacheDiagBuilder = new AlertDialog.Builder(MainActivity.this);
        precacheDiagBuilder.setMessage("Precache");
        View view = getLayoutInflater().inflate(R.layout.precache_alert, null);
        precacheDiagBuilder.setView(view);
        precacheDialog = precacheDiagBuilder.create();
        precache_hansp = (TextView) view.findViewById(R.id.precache_hansp);
        precache_vet = (TextView) view.findViewById(R.id.precache_vet);
        precache_supervet = (TextView) view.findViewById(R.id.precache_supervet);
        precache_ultravet = (TextView) view.findViewById(R.id.precache_ultra);
        precacheDialog.show();

        Log.d("MainActivity", "running prefetch ligovy_list_1: " + URL_hosting + URL_hanspaulka + syzn + "&year=" + calyear);
        ligovy_list_1 = new getLeagueList(MainActivity.this, MainActivity.this, 1);
        ligovy_list_1.execute(URL_hosting + URL_hanspaulka + syzn + "&year=" + calyear);

        Log.d("MainActivity", "running prefetch ligovy_list_2: " + URL_hosting + URL_veterani + syzn + "&year=" + calyear);
        ligovy_list_2 = new getLeagueList(MainActivity.this, MainActivity.this, 2);
        ligovy_list_2.execute(URL_hosting + URL_veterani + syzn + "&year=" + calyear);

        Log.d("MainActivity", "running prefetch ligovy_list_3: " + URL_hosting + URL_superveterani + syzn + "&year=" + calyear);
        ligovy_list_3 = new getLeagueList(MainActivity.this, MainActivity.this, 3);
        ligovy_list_3.execute(URL_hosting + URL_superveterani + syzn + "&year=" + calyear);

        Log.d("MainActivity", "running prefetch ligovy_list_4: " + URL_hosting + URL_ultraveterani + syzn + "&year=" + calyear);
        ligovy_list_4 = new getLeagueList(MainActivity.this, MainActivity.this, 4);
        ligovy_list_4.execute(URL_hosting + URL_ultraveterani + syzn + "&year=" + calyear);
    }

    private Boolean doesItExist(String[] str, String s) {
        for(String ss : str) {
            if(ss.equals(s)) return true;
        }
        return false;
    }

    private void RefreshEXListAdapterFull(int comp) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        secondLevel = new ArrayList<>();
        data = new ArrayList<>();
        List<Integer> ligy = new ArrayList<>();
        List<skupina> docasnaliga = new ArrayList<>();
        int pocetlig = 0;

        Log.d("PSMF", "RefreshEXListAdapterFull; comp=" + comp);

        switch(comp) {
            case 1: ligy = PocetLig(HANSPseznamLig);
                    docasnaliga = HANSPseznamLig;
                    pocetlig = ligy.size();
                break;
            case 2: ligy = PocetLig(VETPseznamLig);
                    docasnaliga = VETPseznamLig;
                    pocetlig = ligy.size();
                break;
            case 3: ligy = PocetLig(SUPPseznamLig);
                    docasnaliga = SUPPseznamLig;
                    pocetlig = ligy.size();
                break;
            case 4: ligy = PocetLig(ULTRAPseznamLig);
                    docasnaliga = ULTRAPseznamLig;
                    pocetlig = ligy.size();
                break;
        }

        List<String> a_ligy = new ArrayList<>(); //[ligy.size()];
        Collections.sort(ligy);

        for(int f=0; f<ligy.size(); f++) {
            if(ligy.get(f) != 0) {
                a_ligy.add(String.valueOf(ligy.get(f)) + ". Liga");
            }
        }
        LinkedHashMap<String, String[]> L3 = new LinkedHashMap<>();

        List<String[]> secondLevel = new ArrayList<>();
        String[] hansp = new String[199];
        for (int i = 0; i < 199; i++) {
            hansp[i] = new String();
        }
        //Log.d("PSMF", "pocet lig nalezen = " + pocetlig);

        int p=0, liga=0;
        for (int pl=0; pl<pocetlig; pl++) {
            //loop skrze vsechny zaznamy
            liga = ligy.get(pl);
            List<skupina> sk = new ArrayList<>();
            sk = VratMiLigu(liga, docasnaliga); //vrati vsechny skupiny a tymy pozadovane ligy
            List<String> skupinky = new ArrayList<>();

            //ziskej info o jednotlivych ligach
            for(skupina s : sk) {
                //Log.d("PSMF", "Iz daar " + String.valueOf(s.getSkupina()) + "? ");
                if(!skupinky.contains(String.valueOf(s.getSkupina())))
                {
                    skupinky.add(String.valueOf(s.getSkupina()));
                    //Log.d("PSMF", "NOPE, iznt. pridavam do skupinek :: " + String.valueOf(s.getSkupina()));
                }

            }

            //konvertuj List na pole
            String[] skupina = new String[skupinky.size()];
            Log.d("PSMF", "Welikost skupinky: " + skupinky.size());
            for (int i = 0; i < skupinky.size(); i++) {
                skupina[i] = "Skupina " + skupinky.get(i);
                //Log.d("PSMF", ">> Liga = " + liga + " :: skupina = " + skupina[i]);
            }
            Arrays.sort(skupina);
            String sb = "";
            for(String g : skupina) {
                sb += g + ", ";
            }
            //Log.d("PSMF", ">> Liga = " + liga + " :: secondLevel.add(skupina) = " + sb);
            secondLevel.add(skupina);

            //iterujeme pres vsechny skupiny v jednotlivych ligach
            L3 = new LinkedHashMap<>();
            for(int i=0; i<skupinky.size(); i++) {
                String[] tymecky = vratMiTymy(sk, liga, skupina[i]);
                /* test purposes */
                sb = "";
                for(String g : tymecky) {
                    sb += g + ", ";
                }
                //Log.d("PSMF", "looper:: liga=" + liga + " :: " + skupina[i] + " :: tymy=" + sb);
                /* test purposes */
                //Log.d("PSMF", "adding:: " + skupina[i] + " :: " + sb);
                L3.put(skupina[i], tymecky);
            }
            data.add(L3);
        }

        //List<LinkedHashMap<String, String[]>> data = new ArrayList<>();
        for(Map<String, String[]> map : data){
            for(String key : map.keySet()){
                String[] sub = map.get(key);
                String t = "";
                for(String z : sub) {
                    t += z + ", ";
                }
                //Log.d("PSMF", "key: " + key + " :: value = " + t);
            }
        }

        //napln adapter
        vysledky.removeAllViews();
        vysledky.addView(zobrazujuvysledky);
        vysledky.addView(expandableListView);
        String[] mojepolevole = a_ligy.toArray(new String[a_ligy.size()]);
        ThreeLevelListAdapter threeLevelListAdapter = new ThreeLevelListAdapter(this, mojepolevole, secondLevel, data);
        expandableListView.setAdapter(threeLevelListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousgroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousgroup) expandableListView.collapseGroup(previousgroup);
                previousgroup = groupPosition;
            }
        });
        threeLevelListAdapter.notifyDataSetChanged();
        ButtonGraphicsRefresh();
    }

    private char GroupToChar(int c) {
        return (char) (c+64);
    }

    private Boolean existuje(List<skupina> lst, skupina sk) {
        for(skupina s : lst) {
            if(s.getNazev().equals(sk.getNazev())) return true;
        }
        return false;
    }

    /* INTERFACES */
    private void _parseResultIntoAdapter(String res, int prepinac, int competition) {
        vysledky.removeAllViews();
        zobrazujuvysledky.setVisibility(View.VISIBLE);
        vysledky.addView(zobrazujuvysledky);

        JSONObject jO = null;
        try {
            jO = new JSONObject(res);
            JSONArray jAsan = null;
            jAsan = jO.getJSONArray("teams");
            Log.d("PSMF", "_parseResultIntoAdapter, prepinac="+prepinac+", competition="+competition+", res="+res);

            for (int i = 0; i < jAsan.length(); i++) {
                JSONObject c = jAsan.getJSONObject(i);
                skupina sk = new skupina();
                String teamName="", www="";
                int group=0, teamscount=0, league=1, teamID=0, barva=0, compet=0;

                switch(prepinac) {
                    case 1:
                        //https://api.psmf.zlutazimnice.cz/api/v1/teams?token=&year=2017&competition=1&season=1
                        group = c.getInt("group");
                        barva = c.getInt("jerseyColor");
                        league = c.getInt("league");
                        teamID = c.getInt("teamId");
                        compet = c.getInt("competition");
                        teamName = c.getString("teamName");
                        www = c.getString("www");
                        break;
                    case 2:
                        group = c.getInt("group");
                        teamName = c.getString("teamName");
                        league = c.getInt("league");
                        compet = c.getInt("competition");
                        break;
                }
                sk.setLiga(league);
                sk.setColor(barva);
                sk.setTeam_id(teamID);
                sk.setSkupina(GroupToChar(group));
                sk.setWww(www);
                sk.setNazev(teamName);
                sk.setTeamscount(teamscount);
                //Log.d("PSMF", "parsing... " + league + "." + GroupToChar(group) + ": " + teamName);
                if(prepinac ==2) {
                    switch (competition) {
                        case 1: if (!existuje(HANSPseznamLig, sk)) HANSPseznamLig.add(sk);
                                Log.d("PSMF", "+++ADAPTER >> HANSPseznamLig adding " + teamName + ", competition=" + compet);
                            break;
                        case 2: if (!existuje(VETPseznamLig, sk)) VETPseznamLig.add(sk);
                            break;
                        case 3: if (!existuje(SUPPseznamLig, sk)) SUPPseznamLig.add(sk);
                            break;
                        case 4: if (!existuje(VETPseznamLig, sk)) VETPseznamLig.add(sk);
                            break;
                    }
                } else {
                    switch (competition) {
                        case 1: if(compet == competition) HANSPseznamLig.add(sk);
                            break;
                        case 2: if(compet == competition) VETPseznamLig.add(sk);
                            break;
                        case 3: if(compet == competition) SUPPseznamLig.add(sk);
                            break;
                        case 4: if(compet == competition) VETPseznamLig.add(sk);
                            break;
                    }
                }
                switch (competition) {
                    case 1: HANSPprehledLig.add(league);
                        break;
                    case 2: VETPprehledLig.add(league);
                        break;
                    case 3: SUPPrehledLig.add(league);
                        break;
                    case 4: ULTRAPprehledLig.add(league);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            RefreshEXListAdapterFull(competition);
        }
    }

    private void AddCustomSearchResult(String res) {
        vysledky.removeAllViews();
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        zobrazujuvysledky.setText(getResources().getString(R.string.vysledkyhledani));
        zobrazujuvysledky.setVisibility(View.VISIBLE);
        vysledky.addView(zobrazujuvysledky);
        LayoutInflater inf = this.getLayoutInflater();
        View view = inf.inflate(R.layout.searchteamresults, null, false);
        view.setBackgroundResource(R.drawable.bordel);
        view.setPadding(20,20,20,20);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15, 10, 15, 10);
        view.setLayoutParams(layoutParams);
        TextView tvSearch = (TextView) view.findViewById(R.id.Teamname);
        tvSearch.setText(res);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kliknul jsem na vysledek hledani, otevri detail tymu
                TextView tmName = (TextView) v.findViewById(R.id.Teamname);
                String tymnejm = tmName.getText().toString().substring(0, tmName.getText().toString().length()-5);
                Log.d("PSMF", "teamdetails s tmName = " + tymnejm);
                HTTP_teamDetails wwwTD = new HTTP_teamDetails(ctx);
                wwwTD.execute(String.valueOf(tymnejm));
            }
        });
        wrapper.addView(view);
        vysledky.addView(wrapper);
    }

    private void _parseResultIntoAdapterFindTeam(String res) {
        JSONObject jO = null;
        JSONArray jAsan = null;
        vysledky.removeAllViews();
        ScrollView skrol = new ScrollView(this);
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        zobrazujuvysledky.setText(getResources().getString(R.string.vysledkyhledani));
        zobrazujuvysledky.setVisibility(View.VISIBLE);
        vysledky.addView(zobrazujuvysledky);

        LayoutInflater inf = this.getLayoutInflater();
        try {
            jO = new JSONObject(res);
            jAsan = jO.getJSONArray("teams");
            for (int i = 0; i < jAsan.length(); i++) {
                View view = inf.inflate(R.layout.searchteamresults, null, false);
                view.setBackgroundResource(R.drawable.bordel);
                view.setPadding(20,20,20,20);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(15, 10, 15, 10);
                view.setLayoutParams(layoutParams);
                TextView tvSearch = (TextView) view.findViewById(R.id.Teamname);

                JSONObject c = jAsan.getJSONObject(i);
                skupina sk = new skupina();
                String teamName = "";
                int group = 0, league = 1;
                group = c.getInt("group");
                teamName = c.getString("teamName");
                league = c.getInt("league");
                tvSearch.setText(teamName + ", " + league + "." + GroupToChar(group));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //kliknul jsem na vysledek hledani, otevri detail tymu
                        TextView tmName = (TextView) v.findViewById(R.id.Teamname);
                        String tymnejm = tmName.getText().toString().substring(0, tmName.getText().toString().length()-5);
                        Log.d("PSMF", "teamdetails s tmName = " + tymnejm);
                        HTTP_teamDetails wwwTD = new HTTP_teamDetails(ctx);
                        wwwTD.execute(String.valueOf(tymnejm));
                    }
                });
                wrapper.addView(view);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            skrol.addView(wrapper);
            vysledky.addView(skrol);
        }
    }

    //nacachuje vsechny tymy do pole
    private void CacheAllTeams(String res) {
        Log.d("CacheAllTeams", "string = " + res);
        JSONObject jO = null;
        //List<String> arr = new ArrayList<>();
        try {
            jO = new JSONObject(res);
            JSONArray jAsan = null;
            jAsan = jO.getJSONArray("teams");

            for (int i = 0; i < jAsan.length(); i++) {
                JSONObject c = jAsan.getJSONObject(i);
                skupina sk = new skupina();
                String teamName="", www="";
                int group=0, teamscount=0, league=1, teamID=0, barva=0;
                int competition=0;

                //https://api.psmf.zlutazimnice.cz/api/v1/teams?token=&year=2017&competition=1&season=1
                group = c.getInt("group");
                barva = c.getInt("jerseyColor");
                league = c.getInt("league");
                teamID = c.getInt("teamId");
                competition = c.getInt("competition");
                teamName = c.getString("teamName");
                www = c.getString("www");

                sk.setLiga(league);
                sk.setColor(barva);
                sk.setTeam_id(teamID);
                sk.setSkupina(GroupToChar(group));
                sk.setWww(www);
                sk.setNazev(teamName);
                sk.setTeamscount(teamscount);
                //arr.add(teamName + ", " + league + "." + GroupToChar(group) + "[" + competition + "]");
                arr.add(teamName + ", " + league + "." + GroupToChar(group));
                //Log.d("PSMF", "ADAPTER ++ " + teamName + ", " + league + "." + GroupToChar(group));
                switch(competition) {
                    case 1:
                        HANSPseznamLig.add(sk);
                        HANSPprehledLig.add(league);
                        break;
                    case 2:
                        VETPseznamLig.add(sk);
                        VETPprehledLig.add(league);
                        break;
                    case 3:
                        SUPPseznamLig.add(sk);
                        SUPPrehledLig.add(league);
                        break;
                    case 4:
                        ULTRAPseznamLig.add(sk);
                        ULTRAPprehledLig.add(league);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            hledejTymy = new ArrayAdapter<String>(ctx, R.layout.naseptavac, R.id.item, arr);
            hledejTymy.notifyDataSetChanged();
            tv_lookupteam.setAdapter(hledejTymy);
            tv_lookupteam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String klik = parent.getAdapter().getItem(position).toString();
                    AddCustomSearchResult(klik);
                    tv_lookupteam.setText("");
                }
            });
        }
    }

    //interface z getLeagueList
    //nacte celou Hanspalku
    @Override
    public void diag_completed(final Object o, int res, String vysledek) {
        //Log.d("diag_completed", "objekt = " + o.toString());
        //Log.d("diag_completed", "res = " + res);
        //Log.d("diag_completed", "vysledek = " + vysledek);
        String jA = (String) o;
        if(vysledek.equalsIgnoreCase("200")) {
            if ((!((String) o).isEmpty()) || ((String)o != null)) {
                switch (res) {
                    case 1:
                        precache_hansp.setText("OK");
                        getLeagueListString = jA;
                        Log.d("diag_completed", "Calling CacheAllTeams (1)");
                        CacheAllTeams(jA);
                        break;
                    case 2:
                        precache_vet.setText("OK");
                        getLeagueListString = jA;
                        Log.d("diag_completed", "Calling CacheAllTeams (2)");
                        CacheAllTeams(jA);
                        break;
                    case 3:
                        precache_supervet.setText("OK");
                        getLeagueListString = jA;
                        Log.d("diag_completed", "Calling CacheAllTeams (3)");
                        CacheAllTeams(jA);
                        break;
                    case 4:
                        precache_ultravet.setText("OK");
                        getLeagueListString = jA;
                        Log.d("diag_completed", "Calling CacheAllTeams (4)");
                        CacheAllTeams(jA);
                        break;
                }

                if ((precache_hansp.getText().equals("OK")) & (precache_vet.getText().equals("OK"))
                        & (precache_supervet.getText().equals("OK")) & (precache_ultravet.getText().equals("OK"))) {
                    precacheDialog.dismiss();
                } else if (res == 4) PrecacheFailed(vysledek);
            } else Log.d("diag_completed", "objekt = null");
        } else {
            Log.d("diag_completed", "vysledek <> 200");
            PrecacheFailed(vysledek);
        }
    }

    private void PrecacheFailed(String v) {
        StringBuilder sB = new StringBuilder();
        sB.append("Nepodařilo se načíst všechna data.");
        if (!precache_hansp.getText().equals("OK")) sB.append("\n [Hanspaulka] ");
        if (!precache_vet.getText().equals("OK")) sB.append("\n [Veteráni] ");
        if (!precache_supervet.getText().equals("OK")) sB.append("\n [Superveteráni] ");
        if (!precache_ultravet.getText().equals("OK")) sB.append("\n [Ultraveteráni] ");
        sB.append("\nNašeptávač nebude fungovat správně.");
        Toast.makeText(ctx, sB.toString(), Toast.LENGTH_LONG).show();
        Log.d("MainActivity", "diag_completed returned " + v);
        if(precacheDialog.isShowing()) precacheDialog.dismiss();
    }

    //interface z HTTP_HledejTym hotovo
    @Override
    public void taskCompleted(Object o) {
        if(o != null) {
            String jA = (String) o;
            //stara metoda zobrazeni vysledku v treeview
            //_parseResultIntoAdapter(jA, 2);
            _parseResultIntoAdapterFindTeam(jA);
        } else {
            HTTP_HledejTym asyncTask = new HTTP_HledejTym(ctx, MainActivity.this);
            String link = URL_hosting + URL_hledejtym + tv_lookupteam.getText() + "&year="+(rok-1)+"&token";
            Log.d("PSMF", "Volam halo halo #2:: " + link);
            asyncTask.execute(link);
        }
    }

    //
    // handlujeme se skupinou
    //
    public List<skupina> VratMiLigu(int liga, List<skupina> lst) {
        List<skupina> _tmp = new ArrayList<>();
        for (int i=0; i<lst.size(); i++) {
            skupina sk = new skupina();
            sk = lst.get(i);
            //Log.d("PSMF", sk.getLiga() + "." + sk.getSkupina() + " = [" + liga + "] ... " + sk.getNazev() + " ANO/NE?");
            if(sk.getLiga() == liga) {
                _tmp.add(sk);
                //Log.d("PSMF", " ... ANO");
            }
        }
        return _tmp;
    }

    public List<Integer> PocetLig(List<skupina> lst) {
        List<Integer> _tmp = new ArrayList<>();
        for (int i=0; i<lst.size(); i++) {
            skupina sk = new skupina();
            sk = lst.get(i);
            if(!_tmp.contains(sk.getLiga())) _tmp.add(sk.getLiga());
        }
        Log.d("PSMF", "PocetLig: " + _tmp.toString());
        return _tmp;
    }

    public String[] vratMiTymy(List<skupina> s, Integer liga, String skupina) {
        List<String> tymy = new ArrayList<>();
        String[] parsniskupinu = skupina.split(" "); //format: Skupina A, Skupina B....
        String skup = parsniskupinu[1];
        for(skupina sk : s) {
            if((sk.getLiga() == liga) & (String.valueOf(sk.getSkupina()).equals(skup))) {
                tymy.add(sk.getNazev());
            }
        }
        String[] tym = new String[tymy.size()];
        for (int i = 0; i < tymy.size(); i++) {
            tym[i] = tymy.get(i);
        }
        return tym;
    }

    private void ButtonGraphicsRefresh() {
        switch (user_league_selection) {
            case 1: bHansp.setBackgroundResource(R.drawable.tlacidlo_pressed);
                bVet.setBackgroundResource(R.drawable.tlacidlo_release);
                bSuperVet.setBackgroundResource(R.drawable.tlacidlo_release);
                bUltraVet.setBackgroundResource(R.drawable.tlacidlo_release);
                break;
            case 2: bVet.setBackgroundResource(R.drawable.tlacidlo_pressed);
                bHansp.setBackgroundResource(R.drawable.tlacidlo_release);
                bSuperVet.setBackgroundResource(R.drawable.tlacidlo_release);
                bUltraVet.setBackgroundResource(R.drawable.tlacidlo_release);
                break;
            case 3: bSuperVet.setBackgroundResource(R.drawable.tlacidlo_pressed);
                bHansp.setBackgroundResource(R.drawable.tlacidlo_release);
                bVet.setBackgroundResource(R.drawable.tlacidlo_release);
                bUltraVet.setBackgroundResource(R.drawable.tlacidlo_release);
                break;
            case 4: bUltraVet.setBackgroundResource(R.drawable.tlacidlo_pressed);
                bHansp.setBackgroundResource(R.drawable.tlacidlo_release);
                bSuperVet.setBackgroundResource(R.drawable.tlacidlo_release);
                bVet.setBackgroundResource(R.drawable.tlacidlo_release);
                break;
        }
    }
}
