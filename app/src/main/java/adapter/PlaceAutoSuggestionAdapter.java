package adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import com.bawp.coachme.model.PlaceApi;

import java.util.ArrayList;

public class PlaceAutoSuggestionAdapter extends ArrayAdapter implements Filterable {
ArrayList<String>results = new ArrayList<>();
    int resource;
    Context context;
    PlaceApi placeApi=new PlaceApi();
    public PlaceAutoSuggestionAdapter(Context context, int resId) {
        super(context, resId);
        this.context=context;
        this.resource=resId;
    }
    @Override
    public int getCount() {
        return results.size();
    }
    @Override
    public String getItem(int pos) {
        return results.get(pos);
    }
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    results = placeApi.autoComplete(constraint.toString());
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results!= null && results.count>0){
                    notifyDataSetChanged();
                }
                else{
                    notifyDataSetChanged();
                }
            }
        };

        return filter;
    }
    }
