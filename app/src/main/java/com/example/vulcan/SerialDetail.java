package com.example.vulcan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SerialDetail extends AppCompatActivity {

    Toolbar toolbar;


    private ListView listView;

    String[] key = {"SN", "Model", "Family", "TrackingID", "CustomerID", "OrderID"};
    String[] value = new String[6];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_detail);

        Intent intent = getIntent();
        String sn = intent.getExtras().getString("sn");
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = preferences.getString("token", "");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        new SerailDetail().execute(sn, token);

    }
    public class SerailDetail extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... string) {


            String sn = string[0];
            String token = string[1];
            OkHttpClient okHttpClient = new OkHttpClient();

            String json = "{}";
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), json);


            Request request = new Request.Builder()
                    .url(Constant.SN_DETAIL + sn)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                int status = response.code();
                if (response.code() == 200) {
                    String result = response.body().string();
                    String serial = new JSONObject(result).getString("serialNumber");
                    String tID = new JSONObject(result).getString("trackingID");
                    String cID = new JSONObject(result).getString("customerID");
                    String oID = new JSONObject(result).getString("orderID");
                    String family = new JSONObject(result).getString("oemFamily");
                    String model = new JSONObject(result).getString("oemModel");
                    if (response.code() == 200) {
                        value[0] = serial;
                        value[1] = model;
                        value[2] = family;
                        value[3] = tID;
                        value[4] = cID;
                        value[5] = oID;
                        showList();


                    } else {
                        showToast("Invalid Serial");
                    }
                } else {
                    showToast("Invalid Serial");
                }

            } catch (Exception e) {
                showToast("Invalid Serial");
                e.printStackTrace();
            }

            return null;
        }

    }

    public void showList(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView = (ListView) findViewById(R.id.serialDetailList);

                SerialListAdapter serialListAdapter = new SerialListAdapter(SerialDetail.this, key, value);

                listView.setAdapter(serialListAdapter);

            }
        });
    }

    public void showToast(final String text){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast t =  Toast.makeText(getApplicationContext(),
                        text, Toast.LENGTH_LONG);
                t.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent i = new Intent(getApplicationContext(), ScanSerail.class);
                                startActivity(i);
                                finish();
                            }
                        }, 4000);

            }


        });

    }


}

