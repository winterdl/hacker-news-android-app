package co.marcelje.hackernews.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import co.marcelje.hackernews.BR;
import co.marcelje.hackernews.database.DatabaseDao;
import co.marcelje.hackernews.model.Item;
import co.marcelje.hackernews.screen.news.item.BaseItemActivity;

import java.util.ArrayList;
import java.util.List;

import co.marcelje.hackernews.activity.ToolbarActivity;

public abstract class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    protected static final int VIEW_TYPE_NEWS = 1;
    protected static final int VIEW_TYPE_COMMENT = 2;

    private static final String ITEM_TYPE_COMMENT = "comment";
    private static final String ITEM_TYPE_STORY = "story";
    private static final String ITEM_TYPE_POLL = "poll";
    private static final String ITEM_TYPE_JOB = "job";

    private final ToolbarActivity mActivity;
    private final List<Item> mItems;
    private final String mItemParentName;
    private final String mItemPosterName;

    protected ItemAdapter(ToolbarActivity activity, String itemParentName, String itemPosterName) {
        mActivity = activity;
        mItems = new ArrayList<>();
        mItemParentName = itemParentName;
        mItemPosterName = itemPosterName;
    }

    protected ItemAdapter(ToolbarActivity activity) {
        mActivity = activity;
        mItems = new ArrayList<>();
        mItemParentName = null;
        mItemPosterName = null;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = getItem(position);
        holder.binding.setVariable(BR.item, item);
    }

    @Override
    public int getItemViewType(int position) {
        switch (getItem(position).getType()) {
            case ITEM_TYPE_COMMENT:
                return VIEW_TYPE_COMMENT;
            case ITEM_TYPE_STORY:
            case ITEM_TYPE_JOB:
            case ITEM_TYPE_POLL:
            default:
                return VIEW_TYPE_NEWS;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    protected ToolbarActivity getActivity() {
        return mActivity;
    }

    public Item getItem(int position) {
        return mItems.get(position);
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void swapItems(List<Item> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<Item> items) {
        mItems.addAll(items);
        notifyItemRangeInserted(mItems.size() - items.size(), items.size());
    }

    public class ItemViewHolder<T extends ViewDataBinding>
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final T binding;
        private final boolean mClickable;

        public ItemViewHolder(T binding, boolean clickable) {
            super(binding.getRoot());
            this.binding = binding;
            mClickable = clickable;

            if (mClickable) {
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            Item data = mItems.get(getAdapterPosition());
            BaseItemActivity.startActivity(mActivity, data, mItemParentName, mItemPosterName);

            switch (data.getType()) {
                case ITEM_TYPE_STORY:
                case ITEM_TYPE_POLL:
                case ITEM_TYPE_JOB:
                    DatabaseDao.insertHistoryItem(mActivity, data);
                    DatabaseDao.insertReadItem(mActivity, data);
                    //TODO: find a better way to update the UI
                    notifyItemChanged(getAdapterPosition());
                    break;
                default:
                    //do nothing
            }
        }
    }
}
