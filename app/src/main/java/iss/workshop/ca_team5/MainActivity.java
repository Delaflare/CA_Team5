package iss.workshop.ca_team5;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hahah

        String test = "Lyra is here";
        String test1 = "Ian is here";

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
