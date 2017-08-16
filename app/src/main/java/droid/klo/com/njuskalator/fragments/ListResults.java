package droid.klo.com.njuskalator.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import droid.klo.com.njuskalator.database.Result;

/**
 * Created by prpa on 4/18/17.
 */

public class ListResults extends Fragment {

    //region varriables
    private static final String TAG = "ListResults";
    private List<Result> resultList;
    //endregion

    //region Overrides

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            long what= getArguments().getLong("source_id");

            if(what == -1)resultList=new DaoCP(getActivity()).getFavorites();
            else resultList=new DaoCP(getActivity()).getResults(getArguments().getLong("source_id"), 0, 50);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_list_results,container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).enableHamburgerAsBack(true);

        ListView mListView = (ListView)getActivity().findViewById(R.id.flr_list);
        if(mListView!=null){
            mListView.setAdapter(new lvAdapter());

            //region list item click methods
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle b = new Bundle();
                    b.putLong("result_id", id);


                    //TODO start single result
                    SingleResult sr = new SingleResult();
                    sr.setArguments(b);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, sr, "SingleResult_"+id).addToBackStack("SingleResult_"+id).commit();
                }
            });
            //endregion
        }
    }

    //endregion

    //region list adapter
    private class lvAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return resultList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = ((LayoutInflater) getActivity().getLayoutInflater()).inflate(R.layout.single_flr, parent, false);

                viewHolder.title = (TextView) convertView.findViewById(R.id.sflr_title);
                viewHolder.seller = (TextView) convertView.findViewById(R.id.sflr_seller);
                viewHolder.price = (TextView) convertView.findViewById(R.id.sflr_price);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String info = "";
            viewHolder.title.setText(((Result)getItem(position)).getTitle());
            viewHolder.seller.setText(((Result)getItem(position)).getSeller());
            viewHolder.price.setText(""+((Result)getItem(position)).getPrice());
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView title;
        TextView seller;
        TextView price;
    }
    //endregion
}
