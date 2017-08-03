package co.marcelje.hackernews.screen.news.item.head;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.marcelje.hackernews.databinding.FragmentCommentBinding;
import co.marcelje.hackernews.handlers.ItemUserClickHandlers;
import co.marcelje.hackernews.loader.ItemListLoader;
import co.marcelje.hackernews.loader.HackerNewsResponse;
import co.marcelje.hackernews.model.Item;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

public class CommentFragment extends ItemHeadFragment
        implements LoaderManager.LoaderCallbacks<HackerNewsResponse<List<Item>>> {

    private static final String ARG_ITEM = "co.marcelje.hackernews.screen.news.item.head.arg.ITEM";
    private static final String ARG_ITEM_PARENT_NAME = "co.marcelje.hackernews.screen.news.item.head.arg.ITEM_PARENT_NAME";
    private static final String ARG_ITEM_POSTER_NAME = "co.marcelje.hackernews.screen.news.item.head.arg.ITEM_POSTER_NAME";

    private static final int LOADER_ID_COMMENT_HEAD = 100;

    private FragmentCommentBinding mBinding;

    private Item mItem;
    private String mItemParentName;
    private String mItemPosterName;

    public static CommentFragment newInstance(Item item, String itemParentName, String itemPosterName) {
        CommentFragment fragment = new CommentFragment();

        Bundle args = createArguments(item, itemParentName, itemPosterName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentCommentBinding.inflate(inflater, container, false);
        mBinding.setItem(mItem);
        mBinding.setActivity(getToolbarActivity());
        mBinding.setItemParentName(mItemParentName);
        mBinding.setItemPosterName(mItemPosterName);
        mBinding.setItemUserClickHandlers(new ItemUserClickHandlers(getToolbarActivity()));

        mBinding.tvCommentInfo.setMovementMethod(LinkMovementMethod.getInstance());

        // TODO: find a better way to remove maxLines
        mBinding.commentHead.tvText.setMaxLines(Integer.MAX_VALUE);
        mBinding.commentHead.tvText.setMovementMethod(LinkMovementMethod.getInstance());

        return mBinding.getRoot();
    }

    @Override
    public void refresh() {
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID_COMMENT_HEAD, null, this);
    }

    @Override
    public Loader<HackerNewsResponse<List<Item>>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_COMMENT_HEAD:
                return new ItemListLoader(getActivity(), Collections.singletonList(mItem.getId()));
            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<HackerNewsResponse<List<Item>>> loader,
                               HackerNewsResponse<List<Item>> response) {
        if (response.isSuccessful()) {
            switch (loader.getId()) {
                case LOADER_ID_COMMENT_HEAD:
                    mItem = response.getData().get(0);
                    mBinding.setItem(mItem);
                    break;
                default:
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<HackerNewsResponse<List<Item>>> loader) {

    }

    private static Bundle createArguments(Item item, String itemParentName, String itemPosterName) {
        Bundle args = new Bundle();
        if (item != null) args.putParcelable(ARG_ITEM, Parcels.wrap(item));
        if (!TextUtils.isEmpty(itemParentName)) args.putString(ARG_ITEM_PARENT_NAME, itemParentName);
        if (!TextUtils.isEmpty(itemPosterName)) args.putString(ARG_ITEM_POSTER_NAME, itemPosterName);

        return args;
    }

    private void extractArguments() {
        Bundle args = getArguments();

        if (args.containsKey(ARG_ITEM)) {
            mItem = Parcels.unwrap(args.getParcelable(ARG_ITEM));
        }

        if (args.containsKey(ARG_ITEM_PARENT_NAME)) {
            mItemParentName = args.getString(ARG_ITEM_PARENT_NAME);
        }

        if (args.containsKey(ARG_ITEM_POSTER_NAME)) {
            mItemPosterName = args.getString(ARG_ITEM_POSTER_NAME);
        }
    }
}