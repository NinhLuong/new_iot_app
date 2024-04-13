package com.example.iot_app.webview_page;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.iot_app.device.FanFragment;
import com.example.iot_app.R;
import com.example.iot_app.device.AirFragment;
import com.example.iot_app.device.LampFragment;

public class WebFragment extends Fragment {

    private WebView webView, myWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);

        webView = (WebView) rootView.findViewById(R.id.map_webView);
        myWebView = (WebView) rootView.findViewById(R.id.map_webView);
        myWebView.loadUrl("https://app.ohmnilabs.com/");

        // Enable Javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setDomStorageEnabled(true);

        // Enable cookies
        CookieManager.getInstance().setAcceptCookie(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
        }

        // Force links and redirects to open in the WebView instead of in a browser
        myWebView.setWebViewClient(new WebViewClient());

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                    }
                });
            }
        });

        return rootView;
    }

   /* @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map_webView = view.findViewById(R.id.map_webView);
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        map_webView.setWebViewClient(customWebViewClient);

        map_webView.getSettings().setJavaScriptEnabled(true);
        map_webView.getSettings().setDomStorageEnabled(true);
        map_webView.loadUrl("https://www.messenger.com/");
    }*/

    /*private class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri url = request.getUrl();
            // Check if the URL matches the Google Maps URL pattern
            if (url.toString().startsWith("https://www.google.com/maps/")) {
                // If it is a Google Maps URL, open the Maps app using an Intent
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
                intent.setPackage("com.google.android.apps.maps"); // Specify the Maps app package
                startActivity(intent);
                return true;  // Cancel the current WebView navigation
            }
            // Allow the WebView to load the URL by default
            return super.shouldOverrideUrlLoading(view, request);
        }
    }*/
}