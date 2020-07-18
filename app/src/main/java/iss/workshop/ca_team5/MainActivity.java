package iss.workshop.ca_team5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private GridView gridView;
    private GridViewAdapter gridAdapter;

    ProgressBar ProBar;
    TextView textView;

    public static int DOWNLOAD_COMPLETED = 2;
    public static int count = 0;
    public int completed = 0;
    private boolean startCanDownload = true;
    public static String prev_url = "";
    private ArrayList<GridItem> imgItems = new ArrayList<>();

    //for getting urls
    private String mUrl = "";
    private WebView mWebView;
    private static final String EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(jpg|png))$)";
    static List<String> workingImages = new ArrayList<String>();
    public ArrayList<GridItem> selectedImage = new ArrayList<GridItem>();

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
        //set tag buttons
        Button btn_f = findViewById(R.id.tag_food);
        btn_f.setOnClickListener(this);
        Button btn_l = findViewById(R.id.tag_love);
        btn_l.setOnClickListener(this);
        Button btn_b = findViewById(R.id.tag_biz);
        btn_b.setOnClickListener(this);
        Button btn_p = findViewById(R.id.tag_ppl);
        btn_p.setOnClickListener(this);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(!imgItems.isEmpty()){
                    if (selectedImage.contains(imgItems.get(position))) {
                        view.setBackground(null);
                        selectedImage.remove(imgItems.get(position));
                    } else {
                        selectedImage.add(imgItems.get(position));
                        view.setBackground(getDrawable((R.drawable.img_select_border)));
                        if (selectedImage.size() == 6) {
                            Intent intent = new Intent(MainActivity.this, GameActivity.class);
                            saveSelectedImages();
                            startActivity(intent);
                        }
                    }
                }
            }
        });


        // add background music
        serviceIntent = new Intent(getApplicationContext(), MyService.class);
        startService(new Intent(getApplicationContext(), MyService.class));

    }

    //get selected images
    public List<String> getSelectedImages(View v) {

        return null;
    }

    //get the list of default images
    public ArrayList<GridItem> getList() {
        //.clear();
        ArrayList<GridItem> imgList = new ArrayList<>();
        //created transparent icon
        Bitmap icon = Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8);
        for (int i = 0; i < 20; i++) {
            imgList.add(new GridItem(icon));
        }
        return imgList;
    }


    @Override
    public void onClick(View v) {
        EditText url = findViewById(R.id.url);
        switch (v.getId()) {
            case R.id.fetch:
                if(prev_url.equals(url.toString())) {
                    Button btn = findViewById(R.id.fetch); btn.setEnabled(false); }

                if (!selectedImage.isEmpty()) {
                    gridAdapter.clear();
                    gridView = findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(MainActivity.this, R.layout.grid_item, getList());
                    gridView.setAdapter(gridAdapter);
                }
                    //Get URl
                    mUrl = url.getText().toString();

                    //Get Images from Website
                    selectedImage.clear();
                    mWebView = findViewById(R.id.web_view);
                    WebSettings webSettings = mWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    //stop current download

                    if (!((completed == 0 || prev_url.isEmpty()) || completed == 20 || prev_url.equals(url))) // Start//finish//same link
                    {
                        //for view
                        gridAdapter.updateImageList(getList());
                        startCanDownload = false;
                        completed = 0;

                    }
                    mWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished (WebView view, String url) {
                            /* This call inject JavaScript into the page which just finished loading. */
                            if (!prev_url.equals(url)) {
                                imgItems.clear();
                                gridAdapter.updateImageList(getList());
                                String l_url = "javascript:window.HTMLOUT.processHTML('" + url + "','<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');";
                                mWebView.loadUrl(l_url);
                                completed = 0;
                                startCanDownload = true;
//                                //set probar visible after download start
                                textView = findViewById(R.id.status);
                                ProBar = findViewById(R.id.ProBar);
                                textView.setVisibility(View.VISIBLE);
                                ProBar.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
                    mWebView.loadUrl(mUrl);

                    TextView textView1 = findViewById(R.id.info);
                    textView1.setVisibility(View.VISIBLE);
                break;

            case R.id.tag_food:
                url.setText("https://stocksnap.io/search/food");
                break;
            case R.id.tag_love:
                url.setText("https://stocksnap.io/search/love");
                break;
            case R.id.tag_biz:
                url.setText("https://stocksnap.io/search/business");
                break;
            case R.id.tag_ppl:
                url.setText("https://stocksnap.io/search/people");
                break;
            default:break;
        }
    }//end of onClick


    @SuppressLint("HandlerLeak")
    Handler mainHdl = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == DOWNLOAD_COMPLETED) {

                imgItems.add(new GridItem((Bitmap) msg.obj));
                gridAdapter.updateImageList(imgItems);

            }
            if (completed == workingImages.size()) {
                startCanDownload = false;
                count = 0;
            }
        }
    };

    //Inteface to get URLs
    public class MyJavaScriptInterface   //Wai Testing
    {
        public MyJavaScriptInterface() {
            list = new ArrayList<String>() {  };
        }

        private List<String> list;

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String url, String html) {
            prev_url = url;
            int count = 0;
            // process the html as needed by the app
            Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
            Matcher m = p.matcher(html);
            while (m.find()) {
                String srcResult = m.group(1);
                Pattern p2 = Pattern.compile(EXTENSION_PATTERN);
                Matcher img = p2.matcher(srcResult);
                if (img.find()) {
                    list.add(srcResult);
                    count++;
                }
                if (count == 20) break;
            }


            workingImages.clear();
            workingImages.addAll(list);

            startCanDownload = true;
            startRepeatingDownload();


        }
    }



    protected void downloadImage(String target) throws IOException {


        Bitmap bitmap = null;
        try {
            URL url = new URL(target);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream in = url.openStream();
            bitmap = BitmapFactory.decodeStream(in);
            //compress bitmap
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            Bitmap cmp_bitMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (startCanDownload != false) {
                updateImage(cmp_bitMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateImage(Bitmap bitmap) {
        Message msg = new Message();
        msg.what = DOWNLOAD_COMPLETED;
        msg.obj = bitmap;
        mainHdl.sendMessage(msg);
    }

    protected void saveSelectedImages() {
        for (int i = 0; i < 6; i++) {
            String name = "image" + i;
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = getApplicationContext().openFileOutput(name, Context.MODE_PRIVATE);
                selectedImage.get(i).getImage().compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    Handler handler = new Handler();
    public  Runnable download=new Runnable() {
        @Override
        public void run() {
            ProBar = findViewById(R.id.ProBar);// ian code
            textView = findViewById(R.id.status);
            completed = 0;// ian code
            try {
                startCanDownload = true;
                for (String i : workingImages) {
                    if (!startCanDownload) {
                        break;
                    }

                    if (startCanDownload) {
                        downloadImage(i);
                        completed++;// ian code
                        textView.setText(completed + "/20 has been downloaded");// ian
                        ProBar.setProgress(completed);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            downloadedNo = 0;
        }

    };
    void stopRepeatingDownload()
    {
        handler.removeCallbacks(download);
    }
    void startRepeatingDownload()
    {
        download.run();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}//end of all
