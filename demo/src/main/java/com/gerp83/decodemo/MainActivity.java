package com.gerp83.decodemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gerp83.deco.DecoView;

/**
 * Created by gerp83
 */

public class MainActivity extends AppCompatActivity {

    private DecoView decoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        decoView = findViewById(R.id.decoView);

        //decoView.setStroke(20, Color.parseColor("#ff0000"));
        //decoView.setCorners(0, 40, 80, 120);

        decoView.get("http://farm3.staticflickr.com/2674/3714839394_273ec19445_b.jpg");
        //decoView.get("https://media.giphy.com/media/l3q2yQ5lJqg6RMWFq/giphy.gif");

        /*decoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });*/

    }

}
