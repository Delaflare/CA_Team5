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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

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
    TextView textView;

    public static int PROGRESS_UPDATE = 1;
    public static int DOWNLOAD_COMPLETED = 2;
    public static int count = 0;
    private boolean startCanDownload = false;
    public static boolean loadedFlag = false;
    public static String prev_url = "";
    private ArrayList<GridItem> imgItems = new ArrayList<>();
    private ArrayList<String> imgStringList = new ArrayList<>();
    //for getting urls
    private String mUrl = "https://via.placeholder.com/500";
    private WebView mWebView;
    private static final String EXTENSION_PATTERN = "([^\\s]+(\\.(?i)(jpg|png))$)";
    static List<String> workingImages = new ArrayList<String>();
    public ArrayList<String> selectedImage = new ArrayList<String>();

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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), "Click"+(i+1), Toast.LENGTH_SHORT).show();
                System.out.println(imgStringList);
                if(!imgStringList.isEmpty()){
                    if(selectedImage.contains(imgStringList.get(i))){
                        view.setBackground(null);
                        selectedImage.remove(imgStringList.get(i));
                    }
                    else{
                        selectedImage.add(imgStringList.get(i));
                        System.out.println("Selected images are in the below list");
                        System.out.println(selectedImage);
                        view.setBackground(getDrawable((R.drawable.img_select_border)));
                        if(selectedImage.size() == 6){
                            Intent intent = new Intent(MainActivity.this, GameActivity.class);
                            intent.putExtra("selected", selectedImage);
                            //startActivityForResult(intent, 0);
                            startActivity(intent);
                            //finish();
                        }
                    }
                }
            }
        });


        try {
            this.getSupportActionBar().hide();   //Remove the action bar
        } catch (NullPointerException e) {
        }
        ;

        // add background music
        serviceIntent = new Intent(getApplicationContext(), MyService.class);
        startService(new Intent(getApplicationContext(), MyService.class));

    }

    //get selected images
    public List<String> getSelectedImages(View v){

        return null;
    }
    //get the list of images
    public ArrayList<GridItem> getList() {
        //.clear();
        ArrayList<GridItem> imgList = new ArrayList<>();
        //created transparent icon
        Bitmap icon = Bitmap.createBitmap(10,10, Bitmap.Config.ALPHA_8);
        for (int i = 0; i < 20; i++) {
            imgList.add(new GridItem(icon));
        }
        return imgList;
    }

    @Override
    public void onClick(View v) {
        EditText url = findViewById(R.id.url);
        switch(v.getId()) {
            case R.id.fetch:
                //Get URL
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

                                //set probar visible after download start
                                textView = findViewById(R.id.status);
                                ProBar = findViewById(R.id.ProBar);
                                textView.setVisibility(View.VISIBLE);
                                ProBar.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
                    mWebView.loadUrl(mUrl);

                    //set probar visible
                    textView = findViewById(R.id.status);
                    ProBar = findViewById(R.id.ProBar);
                    textView.setVisibility(View.VISIBLE);
                    ProBar.setVisibility(View.VISIBLE);
                    TextView textView1 = findViewById(R.id.info);
                    textView1.setVisibility(View.VISIBLE);
                }
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
        }
    }//end of onClick


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
                    ProBar = findViewById(R.id.ProBar);// ian code
                    textView = findViewById(R.id.status);
                    int c = 0;// ian code
                    if (startCanDownload) {
                        for (String i : workingImages) {
                            try {
                                downloadImage(i);
                                c = c +1;// ian code
                                textView.setText( c + "/20 has been downloaded");// ian
                                ProBar.setProgress(c);
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
            }

            bitmap = BitmapFactory.decodeByteArray(imgBytes,0,imageLen);
            updateImage(bitmap);
            imgStringList.add(bitmap.toString());
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
