package droid.klo.com.njuskalator.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import droid.klo.com.njuskalator.MainActivity;
import droid.klo.com.njuskalator.R;
import droid.klo.com.njuskalator.database.DaoCP;
import droid.klo.com.njuskalator.database.Source;

/**
 * Created by prpa on 4/18/17.
 */

public class ListSearches extends Fragment {

    //region Variables
    private static final String TAG = "ListSearches";
    List<Source> sources;
    //endregion

    //region Overrides
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView");
        //desc: ovo mora biti tu inace se back button nece pretvorit nazad u hamburger
        ((MainActivity)getActivity()).enableHamburgerAsBack(false);
        View view = inflater.inflate(R.layout.f_list_searches,container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Log.d(TAG, "onViewCreated");
        //region Populate listview
        sources = new DaoCP(getActivity()).getSources();
        ListView mListView = (ListView)getActivity().findViewById(R.id.fls_listview);
        if(mListView!=null){
            mListView.setAdapter(new lvAdapter());

            //region list item click methods
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle b = new Bundle();
                    b.putLong("source_id", id);

                    ListResults lr = new ListResults();
                    lr.setArguments(b);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, lr, "ListResults_"+id).addToBackStack("ListResults_"+id).commit();
                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle b = new Bundle();
                    b.putLong("source_id", id);

                    AddUpdateSource aus = new AddUpdateSource();
                    aus.setArguments(b);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, aus, "UpdateSource").addToBackStack("UpdateSource").commit();
                    return true;
                }
            });
            //endregion
        }
        //endregion

        //region set floating button
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fls_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, new AddUpdateSource(), "AddNewSource").addToBackStack("AddNewSource").commit();
            }
        });
        //endregion
    }
    //endregion

    //region ListView base adapter
    private class lvAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return sources.size();
        }

        @Override
        public Source getItem(int position) {
            return sources.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long)sources.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = ((LayoutInflater) getActivity().getLayoutInflater()).inflate(R.layout.single_fls, parent, false);

                viewHolder.name = (TextView) convertView.findViewById(R.id.sfls_name);
                viewHolder.info = (TextView) convertView.findViewById(R.id.sfls_info);
                viewHolder.count = (TextView) convertView.findViewById(R.id.sfls_count);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String info = "";
            //region info string construct
            if(getItem(position).getVauvau()==1)info = "VauVau ON, ";
            if(getItem(position).getBottom_value()!=-1)info +="od " + getItem(position).getBottom_value() + " ";
            if(getItem(position).getTop_value()!=-1)info +="do " + getItem(position).getTop_value();
            //endregion
            viewHolder.name.setText(getItem(position).getName());
            viewHolder.info.setText(info);
            viewHolder.count.setText(""+new DaoCP(getActivity()).getNotViewedResults(getItemId(position)));
            convertView.setBackgroundColor(Color.parseColor(getItem(position).getColor()));

            return convertView;
        }

    }
    private static class ViewHolder {

        TextView name;
        TextView info;
        TextView count;

    }
    //endregion
}



