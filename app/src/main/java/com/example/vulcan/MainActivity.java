package com.example.vulcan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity {
    EditText etUsername,etPassword;
    Button btButton;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btButton = (Button) findViewById(R.id.bt_button);

        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String token = preferences.getString("token", "");

//        StrictMode.ThreadPolicy policy = new
//                StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        try {
//            // Construct data
//            String apiKey = "apikey=" + "XnBgXl0ij4Q-I3SbavNtbucS82g8hU2Ld0UFstRewl";
//            String message = "&message=" + "Hi There";
//            String sender = "&sender=" + "TXTLCL";
//            String numbers = "&numbers=" + "917899924211";
//
//            // Send data
//            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
//            String data = apiKey + numbers + message + sender;
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
//            conn.getOutputStream().write(data.getBytes("UTF-8"));
//            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            final StringBuffer stringBuffer = new StringBuffer();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//            rd.close();
//
//            showToast(stringBuffer.toString());
//            Log.d("errorFail", stringBuffer.toString());
//        } catch (Exception e) {
//            System.out.println("Error SMS "+e);
//            showToast("Authentication Failed " +e);
//            //Log.d("ErrorFail", e.getMessage());
//        }

        if( token != "")
        {
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }

        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String Username = etUsername.getText().toString();
                 String Password = etPassword.getText().toString();

                new LoginUser().execute(Username, Password);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public class LoginUser extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... string) {
            String Email = string[0];
            String Password = string[1];

            OkHttpClient okHttpClient = new OkHttpClient();


            String json = "{'userName':'"+Email+"','password':'"+Password+"'}";
            Log.d("s",json);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), json);

            Request request = new Request.Builder()
                    .url(Constant.LOGIN_URL)
                    .post(body)
                    .build();

            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                //Log.d("check",response.body().string());
                if(response.code() == 200) {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("user");
                    String jwtToken = jsonObject.getString("jwtToken");
                    String userName = jsonObject.getString("userName");


                    if(response.code() == 200) {
                        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                        preferences.edit().putString("token", jwtToken).commit();
                        preferences.edit().putString("username", userName).commit();
                        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        showToast("Authentication Failed");
                    }
                } else {
                    showToast("Authentication Failed");
                }

            } catch (Exception e) {
              e.printStackTrace();
            }
            return null;

        }

    }

    public void showToast(final String text){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast t =  Toast.makeText(getApplicationContext(),
                        text, Toast.LENGTH_LONG);
                t.show();
            }
        });
    }

}