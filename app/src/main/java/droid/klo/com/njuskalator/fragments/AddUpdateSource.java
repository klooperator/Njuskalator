package droid.klo.com.njuskalator.fragments;

import android.app.Fragment;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import droid.klo.com.njuskalator.MainActivity;
import droid.klo.com.njuskalator.R;
import droid.klo.com.njuskalator.database.DaoCP;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 4/18/17.
 */

public class AddUpdateSource extends Fragment {

    //region variables
    private static final String TAG = "AddUpdateSource";
    private  boolean isExisted;
    private  String name;
    private  String link;
    private  int top_val;
    private  int bot_val;
    private  int vauvau;
    private  String hexColor;
    private Source s;

    //endregion

    //region Overrides
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if(getArguments() != null){
            isExisted=true;
            s=new DaoCP(getActivity()).getSource(getArguments().getLong("source_id"));
        }else{
            isExisted=false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.f_add_update_source,container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).enableHamburgerAsBack(true);

        final Button bttn_save = (Button) getActivity().findViewById(R.id.faus_save);
        final TextView txt_name = (TextView) getActivity().findViewById(R.id.faus_name);
        final TextView txt_link = (TextView) getActivity().findViewById(R.id.faus_link);
        final TextView txt_top_value = (TextView) getActivity().findViewById(R.id.faus_top);
        final TextView txt_bottom_value = (TextView) getActivity().findViewById(R.id.faus_bot);
        final ToggleButton vau_toggle = (ToggleButton) getActivity().findViewById((R.id.faus_vau));
        final RadioGroup rgColor = (RadioGroup) getActivity().findViewById(R.id.faus_color_group);
        final Button bttn_delete = (Button)getActivity().findViewById(R.id.faus_delete) ;
        final FloatingActionButton floater = (FloatingActionButton)getActivity().findViewById(R.id.faus_floater);

        floater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JustWebView jwv = new JustWebView();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, jwv, "EmptyWebView").addToBackStack("EmptyWebView").commit();
            }
        });

        if(isExisted) {

            txt_name.setText(s.getName());
            txt_link.setText(s.getLink());
            txt_bottom_value.setText(""+s.getBottom_value());
            txt_top_value.setText(""+s.getTop_value());
            if (s.getVauvau() == 1) vau_toggle.setChecked(true);

            bttn_delete.setVisibility(View.VISIBLE);
            bttn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DaoCP(getActivity()).deleteSource(s.getId());
                    //((MainActivity)getActivity()).updateService();
                    getActivity().onBackPressed();
                }
            });
        }else{
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
            try{
                String clipString = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                if(clipString.contains("njuskalo.hr"))txt_link.setText(clipString);
            }catch (Exception e){
                Log.e(TAG, e.getLocalizedMessage());
                txt_link.setText("");
            }

        }
        bttn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(vau_toggle.isChecked())vauvau=1;
                else vauvau=0;

                if((txt_name.getText().toString()).matches("")){
                    txt_name.setBackgroundResource(R.color.redError);
                    name=null;
                }else name=txt_name.getText().toString();

                if((txt_link.getText().toString()).matches("")){
                    txt_link.setBackgroundResource(R.color.redError);
                    link=null;
                }else link=txt_link.getText().toString();

                if((txt_top_value.getText().toString()).matches("")){
                    top_val=-1;
                }else top_val=Integer.parseInt(txt_top_value.getText().toString());

                if((txt_bottom_value.getText().toString()).matches("")){
                    bot_val=-1;
                }else bot_val=Integer.parseInt(txt_bottom_value.getText().toString());

                int color;
                RadioButton selectedRadioBttn = (RadioButton) rgColor.findViewById(rgColor.getCheckedRadioButtonId());
                if(selectedRadioBttn != null){
                    color = Color.parseColor("#FFFFFF");
                    Drawable background = selectedRadioBttn.getBackground();
                    if (background instanceof ColorDrawable)color = ((ColorDrawable) background).getColor();
                }else{
                    color = Color.parseColor("#FFFFFF");
                }
                hexColor = String.format("#%06X", (0xFFFFFF & color));

                if(name==null || link==null)return;
                else{
                    s = new Source(name,link,top_val,bot_val,vauvau, hexColor);
                    new DaoCP(getActivity()).insertSource(s);
                    getActivity().onBackPressed();

                }


            }
        });
    }

    //endregion
}
