package droid.klo.com.njuskalator.fragments;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;

import droid.klo.com.njuskalator.MainActivity;
import droid.klo.com.njuskalator.R;

/**
 * Created by prpa on 4/22/17.
 */

public class JustWebView extends Fragment {

    //region variables
    private String link;
    //endregion

    //region Overrides

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            this.link = getArguments().getString("link");
        }else {
                this.link = "http://www.njuskalo.hr";
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_web_viewer,container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).enableHamburgerAsBack(true);

        final WebView vw = (WebView) getActivity().findViewById(R.id.fwv_wv);
        vw.getSettings().setJavaScriptEnabled(true);

        vw.setWebViewClient(new WebViewClient(){

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if(url.contains("njuskalo.hr") && !url.contains("ads1"))
                return super.shouldInterceptRequest(view, url);
                else return new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream("".getBytes()));
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if(request.getUrl().toString().contains("njuskalo.hr") && !request.getUrl().toString().contains("ads1"))
                    return super.shouldInterceptRequest(view, request);
                    else return new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream("".getBytes()));
                }return super.shouldInterceptRequest(view, request);
            }
        });

        vw .getSettings().setJavaScriptEnabled(true);
        vw .getSettings().setDomStorageEnabled(true);

        vw.loadUrl(this.link);

        FloatingActionButton floater = (FloatingActionButton)getActivity().findViewById(R.id.jwv_floater);
        floater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("link", vw.getUrl().toString());
                clipboard.setPrimaryClip(clip);
                getActivity().onBackPressed();
            }
        });

    }

    //endregion
}
