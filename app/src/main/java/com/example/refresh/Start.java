package com.example.refresh;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.*;
import android.view.View;
import android.content.Intent;

public class Start extends AppCompatActivity {

    LinearLayout mainLayout;
    TextView brandName;
    ImageView logo;
    Button continueBTN;
    TextView cdtView;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        mainLayout = findViewById(R.id.main);
        brandName = findViewById(R.id.brandName);
        logo = findViewById(R.id.logo);
        continueBTN = findViewById(R.id.continueBTN);

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Start.this, Login.class);
                startActivity(intent);
                timer.cancel();
            }

        });

        //Count Down Timer

        cdtView = findViewById(R.id.cdt);

        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String stTimerText = millisUntilFinished/1000 + "";
                cdtView.setText("Continuing in " + stTimerText + " Seconds");
            }

            @Override
            public void onFinish() {
                cdtView.setText("Continuing...");
                Intent intent = new Intent(Start.this, Login.class);
                startActivity(intent);
            }
        }.start();

        //Temporarily Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item1) {
            Intent intent = new Intent(Start.this, Login.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.item2) {
            Intent intent = new Intent(Start.this, SignUp.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.item3) {
            Intent intent = new Intent(Start.this, Home.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}