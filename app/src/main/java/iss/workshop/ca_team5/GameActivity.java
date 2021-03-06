package iss.workshop.ca_team5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<GridItem> gameImage = new ArrayList<>();

    MediaPlayer player1;
    MediaPlayer player2;

    int[] position = {0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5};
    Bitmap[] shuffledImages;
    int[] shuffledPos;
    boolean[] isFlipped = {false, false, false, false, false, false, false, false, false, false, false, false};
    int click = 0;
    int currentPos = -1;
    int prevPos = -1;
    int count = 0;

    //timer
    private int timerSec = -3;
    private int countdown = 3;
    private int endTime = 0;
    private boolean running;
    private boolean wasRunning;
    private boolean gameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final Toast wrong = Toast.makeText(getApplicationContext(), "Not Match", Toast.LENGTH_SHORT);


        final Toast correct = Toast.makeText(getApplicationContext(), "Match", Toast.LENGTH_SHORT);
        wrong.setGravity(Gravity.CENTER, 0, 0);
        correct.setGravity(Gravity.CENTER, 0, 0);


        loadGameImage(); //to retrieve selected image from file
        getGameImages(); // duplicate game image
        shuffledPos = shuffle(position); //shuffle image position
        shuffledImages = shuffleImages(); //shuffle images based on position

        if (savedInstanceState != null) {
            timerSec = savedInstanceState
                    .getInt("seconds");
            running = savedInstanceState
                    .getBoolean("running");
            wasRunning = savedInstanceState
                    .getBoolean("wasRunning");
        }
        runTimer();
        running = true;


        player1 = MediaPlayer.create(this, R.raw.wrong);
        player2 = MediaPlayer.create(this, R.raw.correct);


        gridView = findViewById(R.id.grid_view);
        Bitmap hidden1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.placeholder);
        Bitmap[] hidden = {hidden1, hidden1, hidden1, hidden1, hidden1,
                hidden1, hidden1, hidden1, hidden1, hidden1, hidden1, hidden1};
        GridAdapter adapter = new GridAdapter(this, hidden);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GridAdapter adapter = (GridAdapter) gridView.getAdapter();
                running = true;
                if (!isFlipped[i] && click == 0 && gameStarted) {
                    isFlipped[i] = true;
                    click++;
                    prevPos = i;
                    adapter.flipImage(i, shuffledImages[i]);
                }
                if (!isFlipped[i] && click == 1 && gameStarted) {
                    if (!isMatched(prevPos, i)) {
                        click++;
                        currentPos = i;
                        adapter.flipImage(i, shuffledImages[i]);
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
                        }, 500);
                        isFlipped[i] = false;
                        isFlipped[prevPos] = false;
                        //add sound

                        player1.start();
                        wrong.setGravity(Gravity.CENTER, 0, 0);
                        wrong.show();

                    } else {
                        adapter.flipImage(i, shuffledImages[i]);
                        isFlipped[i] = true;
                        // add sound
                        player2.start();
                        correct.setGravity(Gravity.CENTER, 0, 0);

                        correct.show();
                        count++;
                        System.out.println(count);
                        prevPos = -1;
                        currentPos = -1;
                        click = 0;
                        //
                        //THE GAME ENDS HERE!
                        if (count == 6) {
                            running = false;
                            endTime = timerSec;
                            wrong.cancel();
                            correct.cancel();
                            showEndDialog(endTime);
                        }

                        final TextView countView
                                = (TextView) findViewById(
                                R.id.countMatches);
                        if (count > 0) {
                            countView.setText(count + " out of 6 matched");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.
    @Override
    protected void onResume() {
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
    public int[] shuffle(int[] position) {
        Random random = new Random();
        int[] shuffledPos = new int[position.length];
        int n = position.length;
        for (int i = 0; i < n; i++) {
            int r = random.nextInt(n - i);
            int temp = position[r];
            position[r] = position[i];
            position[i] = temp;
        }
        for (int i = 0; i < n; i++) {
            shuffledPos[i] = position[i];
        }
        return shuffledPos;
    }

    //shuffle images based on shuffled position
    public Bitmap[] shuffleImages() {
        int n = gameImage.size();
        Bitmap[] shuffledImages = new Bitmap[n];
        for (int i = 0; i < n; i++) {
            shuffledImages[i] = gameImage.get(shuffledPos[i]).getImage();
        }
        return shuffledImages;
    }

    //checked if images matched
    public boolean isMatched(int prevPos, int currentPos) {
        return (shuffledPos[prevPos] == shuffledPos[currentPos]);
    }


    private void runTimer() {
        final TextView timeView
                = (TextView) findViewById(
                R.id.timer);


        final Handler handler
                = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run() {
                int hours = timerSec / 3600;
                int minutes = (timerSec % 3600) / 60;
                int secs = timerSec % 60;

                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d",
                                minutes, secs);

                timeView.setText(time);
//                timeStartDialog.setText(time);

                if (running) {
                    timerSec++;

                }
                if (timerSec == 1) {
                    gameStarted = true;
                }


                if (timerSec<2) {
                    showStartDialog(countdown);
                    countdown--;
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void showEndDialog(int endTime) {



        int minutes = (endTime % 3600) / 60;
        int secs = endTime % 60;
        String time
                = String
                .format(Locale.getDefault(),
                        "%d:%02d",
                        minutes, secs);

        //TestWPP
        Intent intent = new Intent(GameActivity.this, ShowSuccessActivity.class);
        if (minutes != 0) {
            intent.putExtra("showMsg","You took " + minutes + " minutes and " + secs + " seconds!");
        } else {
            intent.putExtra("showMsg","Amazing! You only took " + secs + " seconds!");
        }
        startActivityForResult(intent,THIRD_ACTIVITY_REQUEST );

        //
    }

    private void showStartDialog(int countdown) {


        //countdown--;
        String shownText = Integer.toString(countdown);
        if (countdown == 0) {
            shownText = "Good Luck!";
        }
        AlertDialog.Builder builder2 = new AlertDialog.Builder(GameActivity.this);

        LayoutInflater inflater2 = getLayoutInflater();
        View dialoglayout2 = inflater2.inflate(R.layout.game_start_dialog, null);

        TextView messageView2 = (TextView) dialoglayout2.findViewById(R.id.timetostart);
        messageView2.setText(shownText);

        builder2.setView(dialoglayout2);
        final AlertDialog ad = builder2.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                    if (ad.isShowing()) {
                        ad.dismiss();
                    }
            }
        };

        ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 1000);
    }

    public void loadGameImage() {
        for (int i = 0; i < 6; i++) {
            String name = "image" + i;
            FileInputStream fileInputStream;
            Bitmap bitmap = null;
            try {
                fileInputStream = getApplicationContext().openFileInput(name);
                bitmap = BitmapFactory.decodeStream(fileInputStream);
                gameImage.add(new GridItem(bitmap));
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void getGameImages() {
        for (int i = 0; i < 6; i++) {
            gameImage.add(gameImage.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    static final int THIRD_ACTIVITY_REQUEST = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == THIRD_ACTIVITY_REQUEST) {

            startActivity(new Intent(GameActivity.this, MainActivity.class));
            finish();
            }
        }
    }