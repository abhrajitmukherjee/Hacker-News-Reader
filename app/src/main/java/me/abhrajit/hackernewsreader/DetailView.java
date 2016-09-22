package me.abhrajit.hackernewsreader;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import me.abhrajit.hackernewsreader.webcalls.ConnectivityCheck;

public class DetailView extends AppCompatActivity {
    final String URL_KEY="INTENT_URL";
    final String IMG_URL_KEY="IMAGE_URL";
    final String TITLE="URL_TITLE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ConnectivityCheck cCheck=new ConnectivityCheck(this);
        if (!cCheck.isConnected()){

            String text = getString(R.string.noInternet);
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();

        }

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_view);
        final ProgressBar Pbar=(ProgressBar) findViewById(R.id.pB1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        CollapsingToolbarLayout colToolbar = (CollapsingToolbarLayout) findViewById(R.id.detail_collapsing);
        Intent intent=getIntent();
        final String url=intent.getStringExtra(URL_KEY);
        String imageUrl=intent.getStringExtra(IMG_URL_KEY);
        String urlTitle=intent.getStringExtra(TITLE);
        Log.v("IMAGE","-----IMAGE URL:---------"+imageUrl);
        ImageView iv=(ImageView)findViewById(R.id.photo);
        colToolbar.setTitle(urlTitle);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        iv.setColorFilter(filter);
        if (!url.equals("invalid")){

            Picasso.with(this).load(imageUrl).into(iv);

        }else
        {
            Picasso.with(this).load(R.drawable.filler_image).into(iv);
        }

        WebView webView=(WebView)findViewById(R.id.web_view);



        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if(urlTitle.indexOf("[pdf]")>0){
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
         //   sendIntent.putExtra(Intent.EXTRA_TEXT, url);
         //   sendIntent.setType("application/pdf");
            startActivity(sendIntent);
            finish();

        }
        else{
            webView.loadUrl(url);
        }



        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                }

                Pbar.setProgress(progress);
                if(progress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });


    }

}
