package kulkarni.aditya.materialnews.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import kulkarni.aditya.materialnews.Model.Sources;
import kulkarni.aditya.materialnews.R;

/**
 * Created by adicool on 13/7/17.
 */

public class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.ViewHolder> {

    private ArrayList<Sources> sourceList,sourceListCopy;
    ArrayList<Sources> selectedList = new ArrayList<Sources>();
    Context mContext;

    public SourcesAdapter(ArrayList<Sources> sourceList, Context mContext) {
        this.sourceList = sourceList;
        sourceListCopy = new ArrayList<>();
        sourceListCopy.addAll(sourceList);
        Log.v("list", String.valueOf(sourceList));
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_item,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int pos = position;
        holder.sourceText.setText(sourceList.get(pos).getSource());
        holder.checkBox.setChecked(sourceList.get(pos).isSelected());
        holder.checkBox.setTag(sourceList.get(pos));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cB = (CheckBox) v;
                Sources item = (Sources) cB.getTag();
                item.setSelected(cB.isChecked());
                sourceList.get(pos).setSelected(cB.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        if(sourceList == null){
            return 0;
        } else {
            return sourceList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sourceText;
        CheckBox checkBox;
        LinearLayout checkboxLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            sourceText = (TextView)itemView.findViewById(R.id.source);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
            checkboxLayout = (LinearLayout)itemView.findViewById(R.id.checkboxLayout);
        }

    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public ArrayList<Sources> getSelectedList() {
        for(int i=0;i<sourceList.size();i++){
            if(sourceList.get(i).isSelected()){
                selectedList.add(sourceList.get(i));
            }
        }
        return selectedList;
    }

    public int getSelectedSize(){
        return selectedList.size();
    }

    public void filter(String searchText)
    {
        searchText = searchText.toLowerCase(Locale.getDefault());

        sourceList.clear();
        if (searchText.length() == 0)
        {
            sourceList.addAll(sourceListCopy);
        }
        else
        {

            for (int i = 0; i < sourceListCopy.size(); i++)
            {

                if (sourceListCopy.get(i).getSource().toLowerCase(Locale.getDefault()).contains(searchText))
                {
                    sourceList.add(sourceListCopy.get(i));
                }
            }
        }

        notifyDataSetChanged();
    }

}
