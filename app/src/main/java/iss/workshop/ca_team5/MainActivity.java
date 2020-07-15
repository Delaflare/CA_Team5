package iss.workshop.ca_team5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener{

    MediaPlayer player;

    //Wai Testing
    GridView gridView;
    static List<String> workingImages =new ArrayList<String>();
    private String mUrl= "https://via.placeholder.com/500";
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private static final String EXTENSION_PATTERN ="([^\\s]+(\\.(?i)(jpg|png))$)";

    //
    public static int PROGRESS_UPDATE = 1;
    public static int DOWNLOAD_COMPLETED = 2;

    @SuppressLint("HandlerLeak")
    Handler mainHdl = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == PROGRESS_UPDATE) {
                Toast.makeText(MainActivity.this,
                        msg.arg1 + "%", Toast.LENGTH_SHORT).show();
            }
            else if (msg.what == DOWNLOAD_COMPLETED) {
                GridView listView=findViewById(R.id.gridview);
                ImageView imageView = (ImageView) listView.getChildAt(1);
                if (imageView != null)
                    imageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //Wai Testing

        mWebView = findViewById(R.id.web_view);
        mWebView.setVisibility(View.GONE);  //Hidden Web view

        try {
            this.getSupportActionBar().hide();   //Remove the action bar
        }
        catch (NullPointerException e) {}

        //finding list view
        GridViewAdapter adapter=new GridViewAdapter(this,R.layout.first_grid);
        GridView listView=findViewById(R.id.gridview);
        if(listView!=null)
        {
            listView.setAdapter(adapter);

        }
        Button button = findViewById(R.id.ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText newQuote=findViewById(R.id.newQuote);  //Get URL
                if(newQuote!=null) {
                    mUrl = newQuote.getText().toString();
                    //Get Images from Website
                    mWebView = findViewById(R.id.web_view);
                    WebSettings webSettings = mWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    mWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            /* This call inject JavaScript into the page which just finished loading. */
                            mWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                            String urls="";
                            for(String i:workingImages)
                            {
                                 urls+=i.toString() +"\n";

                            }
                            Toast.makeText(MainActivity.this, urls, Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                            String urls="";
                            for(String i:workingImages)
                            {
                                urls+=i.toString() +"\n";

                            }
                            Toast.makeText(MainActivity.this, urls, Toast.LENGTH_LONG).show();
                        }
                    });
                    mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
                    mWebView.loadUrl(mUrl);
                }
            }
        });
        // end Wai//

        String test = "Lyra is here";
        String test1 = "shashank is here";

//////////////////////
        ////////////// Start play music feature////////////////////
        player = MediaPlayer.create(this, R.raw.over_the_rainbow);
        player.start();
        player.setLooping(true);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true)
//                    System.out.println("Running...");
//            }
//        }).start();

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


    public static class MyJavaScriptInterface   //Wai Testing
    {
        public  MyJavaScriptInterface()
        {

        }
        private  String[] list;

        public String[] getList()
        {
            return list;
        }
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            int count = 0;

            // process the html as needed by the app
            Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
            Matcher m = p.matcher(html);
            while (m.find()) {
                String srcResult = m.group(1);
                Pattern p2 = Pattern.compile(EXTENSION_PATTERN);
                Matcher img = p2.matcher(srcResult);
                if (img.find()) {
                    workingImages.add(srcResult);
                    count++;
                }
                if (count == 20) break;
            }
            this.list = new String[workingImages.size()];

            this.list = workingImages.toArray(list);

            new MainActivity().downloadImage(workingImages.get(1));
        }
    }



    protected  void downloadImage(String target) {
        int imageLen = 0;
        int totalSoFar = 0;
        int readLen = 0;
        Bitmap bitmap = null;
        int lastPercent = 0;

        byte[] imgBytes;

        try {
            URL url = new URL(target);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            imageLen = conn.getContentLength();
            imgBytes = new byte[imageLen];

            InputStream in = url.openStream();
            BufferedInputStream bufIn = new BufferedInputStream(in, 2048);

            byte[] data = new byte[1024];
            while ((readLen = bufIn.read(data)) != -1) {
                System.arraycopy(data, 0, imgBytes, totalSoFar, readLen);
                totalSoFar += readLen;

                int percent = Math.round(totalSoFar * 100)/imageLen;
                if (percent - lastPercent >= 10) {
                    //updateProgress(percent);
                    lastPercent = percent;
                }
            }

           // updateProgress(100);
            bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imageLen);
            updateImage(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected  void updateImage(Bitmap bitmap) {
        Message msg = new Message();
        msg.what = DOWNLOAD_COMPLETED;
        msg.obj = bitmap;
        mainHdl.sendMessage(msg);
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
