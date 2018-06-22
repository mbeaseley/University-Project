package com.example.user.university_project;
/*
* Created By Michael Beaseley
* Brunel Dissertation Project
* 2016/2017
*/
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class signActivity extends AppCompatActivity {
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        btn_back = (ImageView) findViewById(R.id.link_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(signActivity.this, MainActivity.class);
                startActivity(in);
            }
        });
    }
}
