package iss.workshop.ca_team5;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {

    MediaPlayer player;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate()
    {
        player = MediaPlayer.create(this, R.raw.cateam5_bgm);
        player.start();
        player.setLooping(true);

    }

    public void onForeGroundService(Intent intent, int startId)
    {
    }
    public void onDestory()
    {
        player.stop();
    }




}
