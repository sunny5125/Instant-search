package sanna.z;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.metrics.MetricsManager;

import static sanna.z.R.id.webview;


public class MainActivity extends AppCompatActivity
                            implements NavigationView.OnNavigationItemSelectedListener,
                                        AppBarLayout.OnOffsetChangedListener,
                                        View.OnClickListener {
    FloatingActionButton fab;
    EditText search;
    CheckBox all,music,video,compressed,book;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    Boolean floating = true,doubleBackToExitPressedOnce = false;;
    String urlFinal,extension;
    AppBarLayout appBarLayout;
    public WebView webView;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        search = (EditText)findViewById(R.id.editText);
        search.setSelected(false);
        all = (CheckBox)findViewById(R.id.checkBox);
        music = (CheckBox)findViewById(R.id.checkBox3);
        video = (CheckBox)findViewById(R.id.checkBox2);
        book = (CheckBox)findViewById(R.id.checkBox4);
        compressed = (CheckBox)findViewById(R.id.checkBox5);
        webView = (WebView)findViewById(webview);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);


        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        //webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().supportZoom();
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setDownloadListener(new DownloadListener() {
                                        @Override
                                        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                            request.setMimeType(mimeType);
                                            //------------------------COOKIE!!------------------------
                                            String cookies = CookieManager.getInstance().getCookie(url);
                                            request.addRequestHeader("cookie", cookies);
                                            //------------------------COOKIE!!------------------------
                                            request.addRequestHeader("User-Agent", userAgent);
                                            request.setDescription("Downloading file...");
                                            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                                            request.allowScanningByMediaScanner();
                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                            dm.enqueue(request);
                                            Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                                        }
                                    });
        //Hockyapp
        checkForUpdates();
        checkForCrashes();
        FeedbackManager.register(this);
        MetricsManager.register(this, getApplication());
    }
    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Tracking.startUsage(this);
        // Further statements
        // ...
    }
    @Override
    public void onPause() {
        Tracking.stopUsage(this);
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(webView.canGoBack()) {
            webView.goBack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_helpus) {
            // Handle the camera action
            FeedbackManager.showFeedbackActivity(MainActivity.this);
        } else if (id == R.id.nav_rate) {
            Toast.makeText(this, "Google play store link not available", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_like) {
            Toast.makeText(this, "facebook link not available", Toast.LENGTH_SHORT).show();

        }  else if (id == R.id.nav_share) {
            ShareCompat.IntentBuilder
                    .from(this)
                    .setText("check out this app!!")
                    .setType("text/plain")
                    .setChooserTitle("Instant Search")
                    .startChooser();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        FrameLayout currentFragment = (FrameLayout) findViewById(R.id.main_framelayout_title);
        if (verticalOffset == -collapsingToolbarLayout.getHeight() + toolbar.getHeight()) {
            floating = false;
            currentFragment.setVisibility(View.GONE);
        }else if (verticalOffset == collapsingToolbarLayout.getVerticalFadingEdgeLength()) {
                floating = true;
                currentFragment.setVisibility(View.VISIBLE);
            }
        }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.fab:
                if (floating) {
                    if (search.getText().length() == 0) {
                        search.setError(Html.fromHtml("<font color='red'>can't be blank !</font>"));
                    } else {
                        try {
                            String searchString = search.getText().toString();
                            String query = searchString.split("\\.")[0];
                            Log.v("query",query);
                            if (searchString.split("\\.").length == 1){
                                if (video.isChecked()){
                                    //Log.v("query","check");
                                    extension = "mkv|mp4|avi|webm|flv|mov|mpg|mpeg";
                                }else if (music.isChecked()){
                                    extension = "mp3|wav|m4a|ogg|wma|flac";
                                }else if (book.isChecked()){
                                    extension = "epub|pdf";
                                }else if(compressed.isChecked()){
                                    extension = "zip|rar|gz|7z|tar|Z";
                                }else if(all.isChecked()) {
                                    extension = "undefined";
                                }
                            }else{
                                extension = searchString.split("\\.")[1];
                            }
                            Log.v("query",extension);
                            urlFinal = SearchingURL(query,extension);
                            webView.loadUrl(urlFinal);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }else {
                    appBarLayout.setExpanded(true);

                }

                break;
        }

    }

    private String SearchingURL(String query,String extension){
       String search_url = "http://www.google.com/search?q="+query+"+-inurl:(htm|html|php|pls|txt) intitle:index.of \\\"last modified\\\" ("+extension+")\")";
        return search_url;
    }



}
