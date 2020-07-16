package iss.workshop.ca_team5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private GridView gridView;
    private GridViewAdapter gridAdapter;

    ProgressBar ProBar;
    ProgressBar ProBar2;

    public static int PROGRESS_UPDATE = 1;
    public static int DOWNLOAD_COMPLETED = 2;
    public static int count = 0;
    private boolean startCanDownload = false;
    public static boolean loadedFlag = false;
    public static String prev_url = "";
    private ArrayList<GridItem> imgItems = new ArrayList<>();

    //for getting urls
    private String mUrl = "https://via.placeholder.com/500";
    private WebView mWebView;
    private static final String EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(jpg|png))$)";
    static List<String> workingImages = new ArrayList<String>();
    static ArrayList<String> selectedImage = new ArrayList<String>();

    //for music
    private Intent serviceIntent;

    public static int downloadedNo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //for view
        gridView = findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item, getList());
        gridView.setAdapter(gridAdapter);
        //fetch
        Button btn = findViewById(R.id.fetch);
        btn.setOnClickListener(this);
        //Temp go to game view
        Button btn1 = findViewById(R.id.temp);
        btn1.setOnClickListener(this);

        try {
            this.getSupportActionBar().hide();   //Remove the action bar
        } catch (NullPointerException e) {
        }
        ;

        // add background music
        serviceIntent = new Intent(getApplicationContext(), MyService.class);
        startService(new Intent(getApplicationContext(), MyService.class));

    }

    //get the list of images
    public ArrayList<GridItem> getList() {
        ArrayList<GridItem> imgList = new ArrayList<>();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.not_found);

        for (int i = 0; i < 20; i++) {
            imgList.add(new GridItem(icon));
        }


        return imgList;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fetch) {
            //Get URL
            EditText url = findViewById(R.id.url);
            if (url != null) {
                mUrl = url.getText().toString();
                //Get Images from Website
                mWebView = findViewById(R.id.web_view);
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        /* This call inject JavaScript into the page which just finished loading. */
                        if (!prev_url.equals(url)) {
                            imgItems.clear();
                            String l_url = "javascript:window.HTMLOUT.processHTML('" + url + "','<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";
                            mWebView.loadUrl(l_url);
                        }
                    }
                });
                mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
                mWebView.loadUrl(mUrl);
            }
        } else if (v.getId() == R.id.image_view) {
            String img = workingImages.get(v.getId());
            if (selectedImage.contains(img)) {
                ((ImageView) v).setBackground(null);
                selectedImage.remove(img);
            } else {
                selectedImage.add(img);
                ((ImageView) v).setBackground(getResources().getDrawable(R.drawable.img_select_border));
                if (selectedImage.size() == 6) {
                    //Write For Second Activity
                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("selected", selectedImage);
                    startActivityForResult(intent, 0);
                    finish();
                    Toast.makeText(this, "Next Activity", Toast.LENGTH_SHORT).show();
                }
            }

        }else if(v.getId() == R.id.temp){
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }
    }

    //end of onClick


    @SuppressLint("HandlerLeak")
    Handler mainHdl = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == DOWNLOAD_COMPLETED) {
                count++;
                //ArrayList<GridItem> imgItems = new ArrayList<>();
                imgItems.add(new GridItem((Bitmap) msg.obj));
                gridAdapter.updateImageList(imgItems);
            }
            if(count==workingImages.size()){
                //gridAdapter.updateImageList(imgItems);
                startCanDownload=false;
                workingImages.clear();
                count=0;
            }
        }
    };

    //Inteface to get URLs
    public class MyJavaScriptInterface   //Wai Testing
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
        public void processHTML(String url,String html) {
            prev_url=url;
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
            startCanDownload=true;

            //getting 20 clean URLs and start downloading
            new Thread(new Runnable(){
                @Override
                public void run() {
                    if (startCanDownload) {
                        for (String i : workingImages) {
                            try {
                                downloadImage(i);
                                System.out.print("downloading");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
            downloadedNo = 0;
        }}

    protected void downloadImage(String target) throws IOException {
        int imageLen = 0;
        int totalSoFar = 0;
        int readLen = 0;
        Bitmap bitmap = null;
        int lastPercent = 0;
        byte[] imgBytes;

        ProBar = (ProgressBar) findViewById(R.id.ProBar1);

        try {
            URL url = new URL(target);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            imageLen = conn.getContentLength();
            imgBytes = new byte[imageLen];

            InputStream in = url.openStream();
            BufferedInputStream bufIn = new BufferedInputStream(in, 1024);

            byte[] data = new byte[1024];
            while ((readLen = bufIn.read(data)) != -1) {
                System.arraycopy(data, 0, imgBytes, totalSoFar, readLen);
                totalSoFar += readLen;

                int percent = Math.round(totalSoFar * 100 / imageLen);
                if (percent - lastPercent >= 10) {
                    // updateProgress(percent);
                    lastPercent = percent;
                    ProBar.setProgress(lastPercent);
                }
            }

            ProBar.setProgress(100);

            //updateProgress(100);
            bitmap = BitmapFactory.decodeByteArray(imgBytes,0,imageLen);
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
}//end of all
