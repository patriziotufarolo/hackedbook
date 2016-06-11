package eu.tufarolo.patrizio.hackedbook;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, Handler.Callback {

    private WebView webView;
    private LinearLayout progressLayout;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private boolean doubleBackPressedOnce = false;
    private final Handler handler = new Handler(this);

    private final static String FacebookHome = "https://touch.facebook.com";
    private final static String UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity MyActivity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Hacked Book");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setLogo(R.drawable.ic_logo);
        setActionBar(toolbar);



        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        cookieManager.setAcceptCookie(true);

        webView = (WebView) findViewById(R.id.webView);
        webView.setOnTouchListener(this);




        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressSpinner);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessageDelayed(1, 100);
                try {
                    URI uri = new URI(url);
                    String domain = uri.getHost();
                    String[] domainSplitted = domain.split(".");
                    String domain_start = new String();
                    String domain_name = new String();
                    if (domainSplitted.length > 0) domain_start = domain.split(".")[0];
                    if (domainSplitted.length > 1) domain_name = domain.split(".")[1];
                    if (!domain_start.equalsIgnoreCase("facebook") && !domain_name.equalsIgnoreCase("facebook")) {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    }
                    else {
                        view.loadUrl(url);
                        return true;
                    }
                }
                catch (URISyntaxException exception) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressLayout.getVisibility() == View.VISIBLE) {
                    progressLayout.setVisibility(View.GONE);
                    progressBar.clearAnimation();
                    progressBar.setProgress(0);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressLayout.setVisibility(View.VISIBLE);



                ObjectAnimator animation = ObjectAnimator.ofInt (progressBar, "progress", 0, 500);
                animation.setDuration (5000);
                animation.setInterpolator (new DecelerateInterpolator());
                animation.start ();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                MyActivity.setProgress(newProgress * 100);
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(UserAgent);
        webView.getSettings().setDomStorageEnabled(true);

        if (savedInstanceState != null) webView.restoreState(savedInstanceState);
        else webView.loadUrl(FacebookHome);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        handler.sendEmptyMessageDelayed(1, 100);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }

        if (doubleBackPressedOnce) {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, R.string.press_it_twice, Toast.LENGTH_SHORT).show();

        this.doubleBackPressedOnce = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackPressedOnce = false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        this.doubleBackPressedOnce = false;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_home:
                webView.loadUrl(FacebookHome);
                break;
            case R.id.action_refresh:
                webView.reload();
                break;
            case R.id.action_about:
                Toast toast = Toast.makeText(this, R.string.developed_by, Toast.LENGTH_SHORT);
                TextView toast_text = (TextView) toast.getView().findViewById(android.R.id.message);
                toast_text.setGravity(Gravity.CENTER);
                toast.show();
                break;
            default:
                break;
        }
        return true;
    }
}
