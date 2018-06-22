package com.example.user.university_project;
/*
* Created By Michael Beaseley
* Brunel Dissertation Project
* 2016/2017
*
* Disclaimer: Code used from University Year 2 Group Project
*/
import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btn_log;
    TextView text_forgot, text_sign;
    private final int CODE_PERMISSIONS = 3;
    private boolean valid = true;
    private String UserN, PassW;
    private EditText USERN, PASSW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        USERN = (EditText) findViewById(R.id.edit_user);
        PASSW = (EditText) findViewById(R.id.edit_pass);

        btn_log = (Button) findViewById(R.id.link_log);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_log.setBackgroundColor(Color.rgb(51, 153, 255));
                login();
            }
        });

        text_forgot = (TextView) findViewById(R.id.link_forgot);
        text_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                text_forgot.setTextColor(Color.rgb(255, 255, 255));
                Intent in = new Intent(MainActivity.this, forgotActivity.class);
                startActivity(in);
            }
        });

        text_sign = (TextView) findViewById(R.id.link_sign);
        text_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                text_sign.setTextColor(Color.rgb(255, 255, 255));
                Intent in = new Intent(MainActivity.this, signActivity.class);
                startActivity(in);
            }
        });

        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, neededPermissions, CODE_PERMISSIONS);
    }

    public void onRequestPermissionsResults(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
        } else {
            onLoginSuccess();
        }
    }

    public boolean validate() {
        UserN = USERN.getText().toString();
        PassW = PASSW.getText().toString();

        if(UserN.equalsIgnoreCase("admin")) {
            if(PassW.equals("0")) {
                valid = true;
            }
            else {
                valid = false;
            }
        } else {
            valid = false;
        }
        return valid;
    }

    public void onLoginSuccess () {
        Intent in = new Intent(MainActivity.this, mapview.class);
        startActivity(in);
    }

    public void onLoginFailed() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView txt = (TextView) layout.findViewById(R.id.toast_text);
        txt.setText(R.string.login_error);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 150);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        btn_log.setBackgroundColor(Color.rgb(63,81,181));
    }
}
