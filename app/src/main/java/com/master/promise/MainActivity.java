package com.master.promise;

import android.os.Bundle;
import android.view.View;

import com.master.exo290.PlayerActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View playView = findViewById(R.id.play_view);
        playView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerActivity.start(view.getContext());
            }
        });

    }
}
