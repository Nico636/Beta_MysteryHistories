package com.mysteryinc.beta_mysteryhistories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LottieAnimationView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (LottieAnimationView) findViewById(R.id.img_der);
        img.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_der:
                Intent act = new Intent(getApplicationContext(), Misterios_Act.class);
                startActivity(act);
                Animatoo.animateSplit(this);
                break;
        }
    }
}