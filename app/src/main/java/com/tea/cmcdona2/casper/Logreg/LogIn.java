package com.tea.cmcdona2.casper.Logreg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tea.cmcdona2.casper.Ents.EntsActivity;
import com.tea.cmcdona2.casper.R;
import com.tea.cmcdona2.casper.Other.Constants;


import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity implements View.OnClickListener {

    Button press;
    //Button reglink;
    public EditText etEmail, etPassword;
    TextView reglink;
    boolean userExists;
    String email, password;

    //DatabaseHelper databaseHelper;
    //LocalUserHelpeRr localUserHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        press = (Button) findViewById(R.id.thing);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        reglink = (TextView) findViewById(R.id.tvRegisterLink);

        press.setOnClickListener(this);
        reglink.setOnClickListener(this);

        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        //databaseHelper = new DatabaseHelper(this);
        //localUserHelper = new LocalUserHelper(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thing:

                //String email = etEmail.getText().toString();
                //String password = etPassword.getText().toString();

                if (email.equals("")) {
                    Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
                if (password.equals("")) {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                }
                if (!email.equals("") && !password.equals("")) {

                    userLogin();



                }


            case R.id.tvRegisterLink:
                Intent registerIntent = new Intent(this, Register.class);
                startActivity(registerIntent);

                break;
        }

    }

    private void userLogin() {
        //email = etEmail.getText().toString().trim();
        //password = etPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){

                            Intent hIntent = new Intent(LogIn.this, EntsActivity.class);
                            startActivity(hIntent);
                        }else{
                            Toast.makeText(LogIn.this,response,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LogIn.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put(Constants.KEY_EMAIL,email);
                map.put(Constants.KEY_PASSWORD, password);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
