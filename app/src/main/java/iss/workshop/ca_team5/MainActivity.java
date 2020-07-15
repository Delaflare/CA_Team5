package iss.workshop.ca_team5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener{

    MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hahah

        String test = "Lyra is here";
        String test1 = "shashank is here";

//////////////////////
        ////////////// Start play music feature////////////////////
        player = MediaPlayer.create(this, R.raw.over_the_rainbow);
        player.start();
        player.setLooping(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    System.out.println("Running...");
            }
        }).start();
        ///////////////////end  music feature///////////

        //button for start game
        Button btnStart = findViewById(R.id.start);
        if(btnStart != null)
            btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }










    //////////////////////////////
    // for background music
        @Override
        protected void onPause () {
            super.onPause();
            player.pause();
        }

        @Override
        protected void onResume () {
            super.onResume();
            player.start();
        }

        @Override
        protected void onStop () {
            super.onStop();
            player.seekTo(0);
        }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            player.release();
        }


    }
//////////////////////////////
