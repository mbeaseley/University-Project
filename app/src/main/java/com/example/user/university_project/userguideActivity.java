package com.example.user.university_project;
/*
* Created By Michael Beaseley
* Brunel Dissertation Project
* 2016/2017
*/
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class userguideActivity extends AppCompatActivity {
    ImageView btn_back;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);

        btn_back = (ImageView) findViewById(R.id.link_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(userguideActivity.this, mapview.class);
                startActivity(in);
            }
        });

        mLayout = (LinearLayout) findViewById(R.id.linear_layout);

        for (int i = 1; i<=1; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_content,null);
            TextView txtView = (TextView) view.findViewById(R.id.content_text);

            txtView.setText(R.string.Linear_textView + i);
            mLayout.addView(view);
        }

    }
}
