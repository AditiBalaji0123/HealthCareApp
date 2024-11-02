package com.adam.miniproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class NutritionVideosActivity extends AppCompatActivity {
    private WebView webView1, webView2, webView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_videos);

        webView1 = findViewById(R.id.webView1);
        webView2 = findViewById(R.id.webView2);
        webView3 = findViewById(R.id.webView3);

        // Enable JavaScript
        WebSettings webSettings1 = webView1.getSettings();
        webSettings1.setJavaScriptEnabled(true);

        WebSettings webSettings2 = webView2.getSettings();
        webSettings2.setJavaScriptEnabled(true);

        WebSettings webSettings3 = webView3.getSettings();
        webSettings3.setJavaScriptEnabled(true);

        // Load YouTube videos in WebViews
        webView1.loadData(getVideoHtml("https://www.youtube.com/embed/xyQY8a-ng6g"), "text/html", "utf-8");
        webView2.loadData(getVideoHtml("https://www.youtube.com/embed/inEPlZZ_SfA"), "text/html", "utf-8");
        webView3.loadData(getVideoHtml("https://www.youtube.com/embed/dnS1rrMPcQ0"), "text/html", "utf-8");
    }

    private String getVideoHtml(String videoUrl) {
        return "<html><body style='margin:0;padding:0;'><iframe width='100%' height='315' src='" + videoUrl + "' frameborder='0' allowfullscreen></iframe></body></html>";
    }
}
