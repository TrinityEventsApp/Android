package com.tea.cmcdona2.casper.Socs;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tea.cmcdona2.casper.Other.Constants;
import com.tea.cmcdona2.casper.Ents.EntsActivity;
import com.tea.cmcdona2.casper.R;
import com.tea.cmcdona2.casper.Splash.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocsActivity extends ActionBarActivity {

    public int len;
    public boolean[] idsActive;
    public String signal;
    public String[] str;
    public Bitmap[] bm;
    public static Activity SocsActivity;
    public GridView gridView;
    public SocsAdapter gridAdapter;
    public ProgressDialog loading;
    public Boolean previouslyLaunched;
    public ArrayList<Integer> activeGridPositions = new ArrayList<Integer>();
    public ArrayList<String> socNames_active = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //caching for previosulyLaunched and fromEntsActivity

        SharedPreferences appPrefs = SocsActivity.this.getSharedPreferences("appPrefs", 0);
        final SharedPreferences.Editor appPrefsEditor = appPrefs.edit();

        final Boolean fromEntsActivity = appPrefs.getBoolean("fromEntsActivity", false);

        setContentView(R.layout.socs_activity);
        SocsActivity = this; //remember this activity to be finished in next activity

        // actionBar.setIcon(R.drawable.app_icon);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        //actionBar.setIcon(R.drawable.app_icon);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        previouslyLaunched = appPrefs.getBoolean("previouslyLaunched", false);

        if (fromEntsActivity || !previouslyLaunched) {

            if (!fromEntsActivity) {

                Intent intent = new Intent(SocsActivity.this, SplashActivity.class);
                appPrefsEditor.putBoolean("allSocsFlag", false).commit();
                startActivity(intent);
            }

            //else fromEntsActivity so no need for splash, then run activity


            //after establishing a connection and receiving data, do the following

            establishConnection(new VolleyCallback() {
                @Override
                public void handleData(String response) {

                    JSONObject socData;
                    byte[] data;
                    Bitmap bitmap;

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray result = jsonObject.getJSONArray(Constants.JSON_ARRAY);
                        len = result.length();

                        String[] name = new String[len];
                        String[] imageTemp = new String[len];
                        bm = new Bitmap[len];
                        str = new String[len];

                        appPrefsEditor.putInt("idsActive_size", len).commit();

                        if(previouslyLaunched)    //User has previously launched the app and navigated to EntsActivity - hence, they have selected at least one society
                            socNames_active = loadArrayList("socNames_active", SocsActivity.this); //Get the names of the societies that the user has previously selected

                        for (int i = 0; i < len; i++) {

                            socData = result.getJSONObject(i);
                            name[i] = socData.getString(Constants.KEY_NAME);
                            imageTemp[i] = socData.getString(Constants.KEY_IMAGE);
                            data = Base64.decode(imageTemp[i], Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            bm[i] = bitmap;
                            str[i] = name[i];

                            if(previouslyLaunched)  //User has some active societies
                            {
                                //Determine if the grid position corresponding to this society should be active
                                SharedPreferences appPrefs = SocsActivity.this.getSharedPreferences("appPrefs", 0);
                                int socNames_active_size = appPrefs.getInt("socNames_active_size", 0);
                                for(int j = 0; j < socNames_active_size; j++) {
                                    if(str[i] == socNames_active.get(j))   //The society names for grid position i is one of the user's saved society names
                                        activeGridPositions.add(i);         //Add grid position i to the ArrayList of active positions
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    idsActive = new boolean[len];

                    if (!fromEntsActivity) {
                        for (int i = 0; i < len; i++) {
                            idsActive[i] = false;
                        }
                    }

                    else{
                        idsActive = loadArray("idsActive", SocsActivity.this);
                    }

                    int num_active_socs = 0;
                    for(int i = 0; i < len; i++)
                    {
                        if(idsActive[i])
                            num_active_socs++;
                    }
                    appPrefsEditor.putInt("socNames_active_size", num_active_socs).commit();


                    gridView = (GridView) findViewById(R.id.gridView);
                    gridAdapter = new SocsAdapter(SocsActivity.this, R.layout.soc_item, getSocItems());
                    gridView.setAdapter(gridAdapter);

                    gridView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                            ArrayList<SocItem> socItems = new ArrayList<SocItem>();
                            socItems = getSocItems();

                            SocItem item = socItems.get(position);
                            String socName = item.getTitle();

                            Integer i = (int) (long) id;

                            if (v.isActivated()) {
                                v.setActivated(false);
                                idsActive[i] = false;
                                socNames_active.remove(socName);
                                activeGridPositions.remove(new Integer(position));
                                Toast.makeText(SocsActivity.this, "Removed " + socName, Toast.LENGTH_SHORT).show();
                            } else {
                                v.setActivated(true);
                                idsActive[i] = true;
                                socNames_active.add(socName);
                                activeGridPositions.add(position);
                                Toast.makeText(SocsActivity.this, "Added " + socName, Toast.LENGTH_SHORT).show();
                            }

                            storeArray(idsActive, "idsActive", SocsActivity.this);
                            storeArrayList(activeGridPositions, "activeGridPositions", SocsActivity.this);
                            storeArrayList_String(socNames_active, "socNames_active", SocsActivity.this);
                        }
                    });

                }
            });

            appPrefsEditor.putBoolean("fromEntsActivity", false);
            appPrefsEditor.commit();
        } else {
            //splash and bipass
            Intent intent = new Intent(SocsActivity.this, EntsActivity.class);
            intent.putExtra("biPassedSocsActivity", true);
            appPrefsEditor.putBoolean("allSocsFlag", false).commit();
            startActivity(intent);
        }

    }

    private String establishConnection(final VolleyCallback callback) {

        loading = ProgressDialog.show(this, "Please wait...", "Fetching data...", false, false);

        String url = Constants.DATA_URL;

        StringRequest stringRequest;
        stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                callback.handleData(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(SocsActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(SocsActivity.this, "Please turn on wifi", Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        return signal;
    }

    public interface VolleyCallback {
        void handleData(String response);
    }

    //used for creating the checkbox

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkbox, menu);
        return true;
    }

    public void tickOnClick(MenuItem item) {

        Boolean atLeastOneHasBeenSelected = false;

        for(int i = 0; i <len; i++){
            if(idsActive[i]){
                atLeastOneHasBeenSelected = true;
                break;
            }
        }

        if(atLeastOneHasBeenSelected){
            tickOnClickCallback();
        }
        else{
            Toast.makeText(SocsActivity.this, "Please select a society", Toast.LENGTH_LONG).show();
        }
    }

    public void tickOnClickCallback() {

        Intent intent = new Intent(SocsActivity.this, EntsActivity.class);

        SharedPreferences appPrefs = SocsActivity.this.getSharedPreferences("appPrefs", 0);
        SharedPreferences.Editor appPrefsEditor = appPrefs.edit();
        appPrefsEditor.putBoolean("previouslyLaunched", true);


        appPrefsEditor.putInt("numSocs", len);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            sb.append(str[i]).append(",");
        }

        appPrefsEditor.putString("societyName", sb.toString());
        appPrefsEditor.commit();

        startActivity(intent);
        finish();
    }

    //adding SocItems, which are an image (bitmap) and a string, to an array

    private ArrayList<SocItem> getSocItems() {

        final ArrayList<SocItem> imageItems = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            imageItems.add(new SocItem(bm[i], str[i]));
        }

        return imageItems;
    }

    //functions for storing and loading a boolean array

    public boolean storeArray(boolean[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("appPrefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++)
            editor.putBoolean(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    //test comment

    public boolean[] loadArray(String arrayName, Context mContext) {

        SharedPreferences appPrefs = mContext.getSharedPreferences("appPrefs", 0);
        int size = appPrefs.getInt(arrayName + "_size", 0);
        boolean array[] = new boolean[size];
        for (int i = 0; i < size; i++)
            array[i] = appPrefs.getBoolean(arrayName + "_" + i, false);
        return array;
    }

    //Function for loading
    public boolean storeArrayList(ArrayList<Integer> arrayList, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("appPrefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", arrayList.size());
        for (int i = 0; i < arrayList.size(); i++)  //Iterate through the ArrayList
        {
            editor.putInt(arrayName + "_" + i, arrayList.get(i));  //Store all of the strings from the ArrayList
        }
        return editor.commit();
    }

    public boolean storeArrayList_String(ArrayList<String> arrayList, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("appPrefs", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", arrayList.size());
        for (int i = 0; i < arrayList.size(); i++)  //Iterate through the ArrayList
        {
            editor.putString(arrayName + "_" + i, arrayList.get(i));  //Store all of the strings from the ArrayList
        }
        return editor.commit();
    }

    public ArrayList<String> loadArrayList(String arrayName, Context mContext) {
        SharedPreferences appPrefs = mContext.getSharedPreferences("appPrefs", 0);
        int size = appPrefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < size; i++)
            array.add(appPrefs.getString(arrayName + "_" + i, "BLANK"));  //Load all of the strings into the ArrayList
        return array;
    }

}

