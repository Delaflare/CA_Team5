package iss.workshop.ca_team5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;

    //Wai Testing
    GridView gridView;
    static List<String> workingImages =new ArrayList<String>();
    private String mUrl= "https://via.placeholder.com/500";
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private static final String EXTENSION_PATTERN ="([^\\s]+(\\.(?i)(jpg|png))$)";

    //


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
        final GridViewAdapter adapter=new GridViewAdapter(this,R.layout.first_grid);
        final GridView listView=findViewById(R.id.gridview);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    System.out.println("Running...");
            }
        }).start();

        ///////////////////end  music feature///////////
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


        }
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
