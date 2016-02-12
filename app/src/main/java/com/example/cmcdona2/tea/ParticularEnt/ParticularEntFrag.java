package com.example.cmcdona2.tea.ParticularEnt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cmcdona2.tea.Other.Constants;
import com.example.cmcdona2.tea.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ParticularEntFrag extends Fragment {

    public Bitmap bm;
    public ProgressDialog loading;
    public String signal;
    public String EventDescription;
    public String EventName;
    public String Location;
    public String StartDate;
    public String EndDate;
    public int StartYear;
    public int StartMonth;
    public int StartDay;
    public int StartHour;
    public int StartMinute;
    public int EndYear;
    public int EndMonth;
    public int EndDay;
    public int EndHour;
    public int EndMinute;
    public String societyName;
    public View view;

    private static TextView eventName;

    private static TextView eventDescription;

    private static ImageView imgview;

    EventDescriptionListener activityCommander;

    public interface EventDescriptionListener {

        void GMAP();

        void Calendar();
    }

    @Override

    public void onAttach(Activity activity) {

        super.onAttach(activity);

        try {

            activityCommander = (EventDescriptionListener) activity;

        } catch (ClassCastException e)

        {

            throw new ClassCastException(activity.toString());

        }

    }

    @Nullable

    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,

                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.particular_ent_activity_fragment, container, false);

        establishConnection(new ParticularEntFrag.VolleyCallback() {
            @Override
            public void handleData(String response) {

                JSONObject particularEntData;
                byte[] data;
                Bitmap bitmap;

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray(Constants.JSON_ARRAY);
                    String imageTemp;

                    particularEntData = result.getJSONObject(0);
                    imageTemp = particularEntData.getString(Constants.KEY_LOWRES);
                    EventName = particularEntData.getString(Constants.KEY_EVENTNAME);
                    EventDescription = particularEntData.getString(Constants.KEY_EVENTDESCRIPTION);
                    StartDate = particularEntData.getString(Constants.KEY_STARTDATE);
                    EndDate = particularEntData.getString(Constants.KEY_ENDDATE);
                    Location = particularEntData.getString(Constants.KEY_LOCATION);
                    societyName = particularEntData.getString(Constants.KEY_SOCIETYNAME);
                    data = Base64.decode(imageTemp, Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bm = bitmap;
                    StartDay = Integer.parseInt(StartDate.substring(8, 10));
                    StartMonth = Integer.parseInt(StartDate.substring(5, 7));
                    StartYear = Integer.parseInt(StartDate.substring(0, 4));
                    StartHour = Integer.parseInt(StartDate.substring(11, 13));
                    StartMinute = Integer.parseInt(StartDate.substring(14, 16));
                    EndDay = Integer.parseInt(EndDate.substring(8, 10));
                    EndMonth = Integer.parseInt(EndDate.substring(5, 7));
                    EndYear = Integer.parseInt(EndDate.substring(0, 4));
                    EndHour = Integer.parseInt(EndDate.substring(11, 13));
                    EndMinute = Integer.parseInt(EndDate.substring(14, 16));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ImageView imgview = (ImageView) view.findViewById(R.id.myImgView);
                imgview.setImageBitmap(bm);
                TextView eventDescription = (TextView) view.findViewById(R.id.eventDescription);
                eventDescription.setText(EventDescription);
                TextView eventName = (TextView) view.findViewById(R.id.eventName);
                eventName.setText(EventName);

            }
        });


        final ImageButton getDirections = (ImageButton) view.findViewById(R.id.getDirections);

        final ImageButton going = (ImageButton) view.findViewById(R.id.going);
        going.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        goingCLicked(v);
                    }
                }
        );
        getDirections.setOnClickListener(

                new View.OnClickListener() {

                    public void onClick(View v)

                    {

                        buttonCLicked(v);

                    }

                }

        );

        return view;

    }

    public void buttonCLicked(View view)

    {

        activityCommander.GMAP();

    }

    public void goingCLicked(View view) {
        activityCommander.Calendar();
    }

    private String establishConnection(final VolleyCallback callback) {

        SharedPreferences appPrefs = ParticularEntFrag.this.getContext().getSharedPreferences("appPrefs", 0);
        //SharedPreferences.Editor appPrefsEditor = appPrefs.edit();

        loading = ProgressDialog.show(this.getContext(), "Please wait...", "Fetching data...", false, false);

        int eventID = appPrefs.getInt("ID",0);
        String urlExtension = Integer.toString(eventID);
        String url = Constants.HIRES_URL + urlExtension;

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
                        //Toast.makeText(ParticularEntFrag, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(stringRequest);

        return signal;
    }

    public interface VolleyCallback {
        void handleData(String response);
    }

}
