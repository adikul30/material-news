package kulkarni.aditya.materialnews.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.model.Sources;

/**
 * Created by adicool on 13/7/17.
 */

public class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.ViewHolder> {

    private String TAG = this.getClass().getSimpleName();

    private List<Sources> sourceList, sourceListCopy;
    private List<Sources> selectedList = new ArrayList<>();
    private Context mContext;

    public SourcesAdapter(List<Sources> sourceList, Context mContext) {
        this.sourceList = sourceList;
        sourceListCopy = new ArrayList<>();
        sourceListCopy.addAll(sourceList);
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_item, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int pos) {
        holder.sourceText.setText(sourceList.get(pos).getSource());
        holder.checkBox.setChecked(sourceList.get(pos).isSelected());
        holder.checkBox.setTag(sourceList.get(pos));
    }

    @Override
    public int getItemCount() {
        return sourceList == null ? 0 : sourceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sourceText;
        CheckBox checkBox;
        LinearLayout checkboxLayout;

        ViewHolder(View itemView) {
            super(itemView);
            sourceText = itemView.findViewById(R.id.source);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkboxLayout = itemView.findViewById(R.id.checkboxLayout);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    CheckBox cB = (CheckBox) v;
                    Sources item = (Sources) cB.getTag();
                    item.setSelected(cB.isChecked());
                    sourceList.get(pos).setSelected(cB.isChecked());
                    if(item.isSelected()) {
                        selectedList.add(item);
                    }
                    else {
                        selectedList.remove(item);
                    }
                }
            });
        }

    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public List<Sources> getSelectedList() {
        return selectedList;
    }

    public int getSelectedSize() {
        return selectedList.size();
    }

    public void filter(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        sourceList.clear();
        if (searchText.length() == 0) {
            sourceList.addAll(sourceListCopy);
        }
        else {
            for (int i = 0; i < sourceListCopy.size(); i++) {
                if (sourceListCopy.get(i).getSource().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    sourceList.add(sourceListCopy.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }

}
