package com.example.vulcan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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