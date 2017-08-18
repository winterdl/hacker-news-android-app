package com.marcelljee.hackernews.screen.news.item;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcelljee.hackernews.R;
import com.marcelljee.hackernews.event.ItemUpdateEvent;
import com.marcelljee.hackernews.fragment.ToolbarFragment;
import com.marcelljee.hackernews.model.Item;
import com.marcelljee.hackernews.screen.news.item.comment.ItemCommentFragment;
import com.marcelljee.hackernews.screen.news.item.head.CommentFragment;
import com.marcelljee.hackernews.screen.news.item.head.ItemHeadFragment;
import com.marcelljee.hackernews.screen.news.item.head.StoryFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

public class ItemFragment extends ToolbarFragment {

    private static final String ARG_ITEM = "com.marcelljee.hackernews.screen.news.item.arg.ITEM";
    private static final String ARG_POSTER_ITEM = "com.marcelljee.hackernews.screen.news.item.arg.POSTER_ITEM";
    private static final String ARG_ITEM_LOADER_OFFSET = "com.marcelljee.hackernews.screen.news.item.arg.ITEM_LOADER_OFFSET";

    private static final String TAG_HEAD_FRAGMENT = "com.marcelljee.hackernews.screen.news.item.tag.HEAD_FRAGMENT";
    private static final String TAG_COMMENT_FRAGMENT = "com.marcelljee.hackernews.screen.news.item.tag.COMMENT_FRAGMENT";

    private Item mItem;
    private Item mPosterItem;
    private int mLoaderOffset;

    public static ItemFragment newInstance(Item item, Item posterItem, int loaderOffset) {
        ItemFragment fragment = new ItemFragment();

        Bundle args = createArguments(item, posterItem, loaderOffset);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        extractArguments();

        if (savedInstanceState == null) {
            switch (mItem.getType()) {
                case ItemActivity.ITEM_TYPE_COMMENT:
                    loadFragment(CommentFragment.newInstance(mItem));
                    break;
                case ItemActivity.ITEM_TYPE_STORY:
                case ItemActivity.ITEM_TYPE_POLL:
                case ItemActivity.ITEM_TYPE_JOB:
                default:
                    loadFragment(StoryFragment.newInstance(mItem));
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe()
    @SuppressWarnings({"unused"})
    public void onItemUpdateEvent(ItemUpdateEvent event) {
        mItem.update(event.getItem());
    }

    private static Bundle createArguments(Item item, Item posterItem, int loaderOffset) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, Parcels.wrap(item));
        args.putParcelable(ARG_POSTER_ITEM, Parcels.wrap(posterItem));
        args.putInt(ARG_ITEM_LOADER_OFFSET, loaderOffset);

        return args;
    }

    private void extractArguments() {
        Bundle args = getArguments();

        if (args.containsKey(ARG_ITEM)) {
            mItem = Parcels.unwrap(args.getParcelable(ARG_ITEM));
        }

        if (args.containsKey(ARG_POSTER_ITEM)) {
            mPosterItem = Parcels.unwrap(args.getParcelable(ARG_POSTER_ITEM));
        }

        if (args.containsKey(ARG_ITEM_LOADER_OFFSET)) {
            mLoaderOffset = args.getInt(ARG_ITEM_LOADER_OFFSET);
        }
    }

    private void loadFragment(ItemHeadFragment headFragment) {
        ItemCommentFragment commentFragment = ItemCommentFragment.newInstance(mItem, mPosterItem, mLoaderOffset);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.item_head_container, headFragment, TAG_HEAD_FRAGMENT)
                .replace(R.id.item_comment_container, commentFragment, TAG_COMMENT_FRAGMENT)
                .commit();
    }

    private ItemHeadFragment getHeadFragment() {
        return (ItemHeadFragment) getChildFragmentManager().findFragmentByTag(TAG_HEAD_FRAGMENT);
    }

    public void refresh() {
        getHeadFragment().refresh();
    }
}
