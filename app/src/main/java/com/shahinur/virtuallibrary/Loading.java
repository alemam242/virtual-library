package com.shahinur.virtuallibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Loading extends AppCompatActivity {

    ImageView iconLibrary;
    TextView iconTitle;
    SharedPreferences sharedPreferences;


    public static boolean isNightModeActive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);
        iconLibrary = findViewById(R.id.iconLibrary);
        iconTitle = findViewById(R.id.iconTitle);

        sharedPreferences = getSharedPreferences(""+R.string.app_name,MODE_PRIVATE);

        Animation LeftToRight = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_to_right);
        iconLibrary.startAnimation(LeftToRight);

        Animation RightToLeft = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_to_left);
        iconTitle.startAnimation(RightToLeft);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if(networkInfo!=null && networkInfo.isConnected()) {
                    String username = sharedPreferences.getString("username", "guest");
                    if (username.equals("guest")) {
                        Intent intent = new Intent(getApplicationContext(),Login2.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    Intent intent = new Intent(getApplicationContext(),NoInternet.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}