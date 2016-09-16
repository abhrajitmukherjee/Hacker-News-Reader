package me.abhrajit.hackernewsreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.squareup.picasso.Picasso;

public class DetailView extends AppCompatActivity {
    final String URL_KEY="INTENT_URL";
    final String IMG_URL_KEY="IMAGE_URL";
    final String TITLE="URL_TITLE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_view);
        final ProgressBar Pbar=(ProgressBar) findViewById(R.id.pB1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout colToolbar = (CollapsingToolbarLayout) findViewById(R.id.detail_collapsing);
        Intent intent=getIntent();
        String url=intent.getStringExtra(URL_KEY);
        String imageUrl=intent.getStringExtra(IMG_URL_KEY);
        String urlTitle=intent.getStringExtra(TITLE);
        Log.v("IMAGE","-----IMAGE URL:---------"+imageUrl);
        ImageView iv=(ImageView)findViewById(R.id.photo);
        colToolbar.setTitle(urlTitle);
        if (!url.equals("invalid")){
            Picasso.with(this).load(imageUrl).into(iv);

        }else
        {
            iv.setImageBitmap(null);
        }

        WebView webView=(WebView)findViewById(R.id.web_view);



        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if(urlTitle.indexOf("[pdf]")>0){
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
         //   sendIntent.putExtra(Intent.EXTRA_TEXT, url);
         //   sendIntent.setType("application/pdf");
            startActivity(sendIntent);
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
