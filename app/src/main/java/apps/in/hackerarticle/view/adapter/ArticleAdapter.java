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
import apps.in.hackerarticle.data.entity.Story;
import apps.in.hackerarticle.view.listener.Itemlistener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Story> entities;
    private Context context;
    private Itemlistener listener;

    public ArticleAdapter(List<Story> entities, Context context, Itemlistener listener) {
        this.entities = entities;
        this.context = context;
        this.listener = listener;
    }

    public void addProduct(Story entity) {
        entities.add(entity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Story entity = entities.get(position);
            holder.view.setTag(position + "");
            holder.tv_vote.setText(entity.getScore() + "");

            holder.tv_title.setText(entity.getTitle());
            holder.tv_site.setText(entity.getUrl());
            String time = AppUtils.timeAgo(entity.getTime() + "", context);
            holder.tv_time_by.setText(time + " - " + entity.getBy());
            holder.tv_count.setText(entity.getDescendants() + "");

            holder.view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Story story = entities.get(position);
                                listener.ItemClick(story);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
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

        @BindView(R.id.tv_count)
        TextView tv_count;

        @BindView(R.id.tv_time_by)
        TextView tv_time_by;

        @BindView(R.id.tv_site)
        TextView tv_site;

        @BindView(R.id.tv_title)
        TextView tv_title;

        @BindView(R.id.tv_vote)
        TextView tv_vote;

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
