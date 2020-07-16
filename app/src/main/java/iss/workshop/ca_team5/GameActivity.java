package iss.workshop.ca_team5;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.app.Activity;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    GridView gridView;

    //TODO : to be replace with 6 selected images
    int[] images = {R.drawable.hug, R.drawable.laugh, R.drawable.peep, R.drawable.snore,
            R.drawable.stop, R.drawable.tired,R.drawable.hug, R.drawable.laugh, R.drawable.peep, R.drawable.snore,
            R.drawable.stop, R.drawable.tired};
    //
    int[] hidden = {R.drawable.hidden, R.drawable.hidden, R.drawable.hidden, R.drawable.hidden,
            R.drawable.hidden, R.drawable.hidden,R.drawable.hidden, R.drawable.hidden, R.drawable.hidden,R.drawable.hidden,
            R.drawable.hidden, R.drawable.hidden};
    int[] position ={0,1,2,3,4,5,0,1,2,3,4,5};

    int[] shuffledImages;
    int[] shuffledPos;

    boolean[] isFlipped ={false,false,false,false,false,false,false,false,false,false,false,false};
    int click = 0;
    int currentPos = -1;
    int prevPos = -1;
    int count = 0;

    //timer test-code
    private int timerSec = 0;
    private boolean running;
    private boolean wasRunning;

    private int mGridViewBGColor = Color.parseColor("#FF86A38B");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (savedInstanceState != null) {
            timerSec = savedInstanceState
                    .getInt("seconds");
            running = savedInstanceState
                    .getBoolean("running");
            wasRunning = savedInstanceState
                    .getBoolean("wasRunning");
        }
        runTimer();

        shuffledPos = shuffle(position);
        shuffledImages = shuffleImages(images, shuffledPos);

        gridView = findViewById(R.id.grid_view);
        GridAdapter adapter = new GridAdapter(this, hidden);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GridAdapter adapter = (GridAdapter)gridView.getAdapter();
                running = true;
                if(!isFlipped[i] && click == 0) {
                    isFlipped[i] = true;
                    click++;
                    prevPos = i;
                    adapter.flipImage(i, shuffledImages[i]);
                }
                if(!isFlipped[i] && click == 1){

                    if(!isMatched(prevPos,i)){
                        click++;
                        currentPos = i;
                        adapter.flipImage(i, shuffledImages[i]);
                        //adapter.flipBack(currentPos, prevPos);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GridAdapter adapter = (GridAdapter) gridView.getAdapter();
                                adapter.flipBack(currentPos, prevPos);
                                prevPos = -1;
                                currentPos = -1;
                                click = 0;

                            }
                        }, 1000);
                        isFlipped[i] = false;
                        isFlipped[prevPos] = false;
                        //add sound
                        Toast.makeText(getApplicationContext(), "Not Match", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        adapter.flipImage(i, shuffledImages[i]);
                        isFlipped[i] =true;
                        // add sound
                        Toast.makeText(getApplicationContext(), "Match", Toast.LENGTH_SHORT).show();
                        count++;
                        System.out.println(count);
                        prevPos = -1;
                        currentPos = -1;
                        click = 0;
                        if (count == 6) {running = false;}

                        final TextView countView
                        = (TextView)findViewById(
                        R.id.countMatches);
                        if (count > 0) {countView.setText(count + "/6 matches");}
                    }
                }
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.
    @Override
    protected void onResume()
    {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }
    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState
                .putInt("seconds", timerSec);
        savedInstanceState
                .putBoolean("running", running);
        savedInstanceState
                .putBoolean("wasRunning", wasRunning);
    }

    //shuffle position
    public int[] shuffle(int[] position){
        Random random = new Random();
        int[] shuffledPos = new int[position.length];
        int n = position.length;
        for (int i=0 ;i < n ;i++){
            int r = random.nextInt(n-i);
            int temp = position[r];
            position[r] = position[i];
            position[i] = temp;
        }
        for(int i=0; i < n ;i++){
            shuffledPos[i] = position[i];
        }
        return shuffledPos;
    }

    //shuffle images based on shuffled position
    public int[] shuffleImages(int[] images, int[] shuffledPos){
        int n = images.length;
        int[] shuffledImages = new int[n];
        for(int i = 0; i<n;i++){
            shuffledImages[i]=images[shuffledPos[i]];
        }
        return shuffledImages;
    }

    //checked if images matched
    public boolean isMatched(int prevPos, int currentPos){
        return (shuffledPos[prevPos] == shuffledPos[currentPos]);
    }

    public void setBgColor(View view){
        // Initialize a new color drawable array
        ColorDrawable[] colors = {
                new ColorDrawable(Color.RED), // Animation starting color
                new ColorDrawable(mGridViewBGColor) // Animation ending color
        };
        // Initialize a new transition drawable instance
        TransitionDrawable transitionDrawable = new TransitionDrawable(colors);
        // Set the clicked item background
        view.setBackground(transitionDrawable);
        // Finally, Run the item background color animation
        // This is the grid view item click effect
        transitionDrawable.startTransition(200); // 600 Milliseconds
    }
    private void runTimer()
    {
        final TextView timeView
            = (TextView)findViewById(
            R.id.timer);

        final Handler handler
                = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = timerSec / 3600;
                int minutes = (timerSec % 3600) / 60;
                int secs = timerSec % 60;

                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);

                timeView.setText(time);

                if (running) {
                    timerSec++;
                }

                handler.postDelayed(this, 1000);
            }
        });
    }
}