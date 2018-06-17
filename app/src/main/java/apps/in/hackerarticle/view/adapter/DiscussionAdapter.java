package apps.in.hackerarticle.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.in.hackerarticle.R;
import apps.in.hackerarticle.data.AppUtils;
import apps.in.hackerarticle.data.entity.Discussion;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {

    private List<Discussion> entities;
    private Context context;

    public DiscussionAdapter(List<Discussion> entities, Context context) {
        this.entities = entities;
        this.context = context;
    }

    public void addProduct(Discussion entity) {
        entities.add(entity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout, initialize the View Holder
        View v =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.discussion_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Discussion entity = entities.get(position);
            holder.view.setTag(position + "");
            String time = AppUtils.timeAgo(entity.getTime() + "", context);
            holder.tv_time_by.setText(time + " - " + entity.getBy());
            holder.tv_text.setText(entity.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        @BindView(R.id.tv_text)
        TextView tv_text;

        @BindView(R.id.tv_time_by)
        TextView tv_time_by;

        public ViewHolder(View itemView) {
            super(itemView);
            try {
                ButterKnife.bind(this, itemView);
                this.view = itemView;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
