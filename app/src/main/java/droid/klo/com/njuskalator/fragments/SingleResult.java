package droid.klo.com.njuskalator.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import droid.klo.com.njuskalator.MainActivity;
import droid.klo.com.njuskalator.R;
import droid.klo.com.njuskalator.database.DaoCP;
import droid.klo.com.njuskalator.database.ExcludeUsers;
import droid.klo.com.njuskalator.database.Result;

/**
 * Created by prpa on 4/22/17.
 */

public class SingleResult extends Fragment {

    //region variable
    private static final String TAG = "SingleResult";
    private Result r;
    //endregion

    //region Overrides
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            r=new DaoCP(getActivity()).getResult(getArguments().getLong("result_id"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_signle_result,container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).enableHamburgerAsBack(true);


        TextView tvTitle = (TextView)getActivity().findViewById(R.id.sr_title);
        tvTitle.setText(this.r.getTitle());

        TextView tvContent = (TextView)getActivity().findViewById(R.id.sr_content);
        tvContent.setText(this.r.getContent());

        TextView tvPrice = (TextView)getActivity().findViewById(R.id.sr_price);
        tvPrice.setText(""+this.r.getPrice());

        TextView tvLink = (TextView)getActivity().findViewById(R.id.sr_link);
        tvLink.setText(this.r.getLink());
        tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((TextView)v).getText().toString()));
                startActivity(browserIntent);*/
                Bundle b = new Bundle();
                b.putString("link", r.getLink());


                //TODO start single result
                JustWebView jwv = new JustWebView();
                jwv.setArguments(b);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, jwv, "JustWebView_"+r.getLink()).addToBackStack("JustWebView_"+r.getLink()).commit();

            }
        });

        Button bPhone = (Button)getActivity().findViewById(R.id.sr_phone);
        bPhone.setText(this.r.getPhone_number());
        bPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + r.getPhone_number().replaceAll("\\D+", "")));
                startActivity(callIntent);
            }
        });

        ToggleButton tbSeller = (ToggleButton)getActivity().findViewById(R.id.sr_seller);
        tbSeller.setText(this.r.getSeller());
        tbSeller.setTextOn(this.r.getSeller());
        tbSeller.setTextOff(this.r.getSeller());
        tbSeller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DaoCP dao = new DaoCP(getActivity());
                if(isChecked){
                    String sellerToExclude = buttonView.getText().toString();
                    dao.insertExcludedUser(new ExcludeUsers(sellerToExclude));
                }else{
                    String sellerToExclude = buttonView.getText().toString();
                    dao.deleteExcludedUser(sellerToExclude);
                }
            }
        });

        WebView wvTable = (WebView) getActivity().findViewById(R.id.sr_table);
        wvTable.loadDataWithBaseURL(null, this.r.getTable(), "text/html", "utf-8", null);
    }
}
