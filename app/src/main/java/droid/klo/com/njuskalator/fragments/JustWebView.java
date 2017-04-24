package droid.klo.com.njuskalator.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

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

        WebView vw = (WebView) getActivity().findViewById(R.id.fwv_wv);
        vw.getSettings().setJavaScriptEnabled(true);
        vw.loadUrl(this.link);
    }

    //endregion
}
