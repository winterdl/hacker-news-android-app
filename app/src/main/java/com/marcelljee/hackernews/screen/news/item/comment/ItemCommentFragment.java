package com.marcelljee.hackernews.screen.news.item.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcelljee.hackernews.databinding.FragmentItemCommentBinding;
import com.marcelljee.hackernews.factory.SnackbarFactory;
import com.marcelljee.hackernews.fragment.ToolbarFragment;
import com.marcelljee.hackernews.loader.HackerNewsResponse;
import com.marcelljee.hackernews.loader.ItemListLoader;
import com.marcelljee.hackernews.model.Item;
import com.marcelljee.hackernews.utils.CollectionUtils;

import org.parceler.Parcels;

import java.util.List;

public class ItemCommentFragment extends ToolbarFragment
        implements LoaderManager.LoaderCallbacks<HackerNewsResponse<List<Item>>> {

    private static final String ARG_ITEM = "com.marcelljee.hackernews.screen.news.item.arg.ITEM";
    private static final String ARG_ITEM_PARENT_NAME = "com.marcelljee.hackernews.screen.news.item.arg.ITEM_PARENT_NAME";
    private static final String ARG_ITEM_POSTER_NAME = "com.marcelljee.hackernews.screen.news.item.arg.ITEM_POSTER_NAME";

    private static final String STATE_COMMENT_DATA = "com.marcelljee.hackernews.screen.news.item.state.COMMENT_DATA";
    private static final String STATE_CURRENT_PAGE = "com.marcelljee.hackernews.screen.news.item.state.CURRENT_PAGE";

    private static final int LOADER_ID_COMMENT_ITEM = 400;

    private static final int ITEM_COUNT = 10;

    private Item mItem;
    private String mItemParentName;
    private String mItemPosterName;

    private FragmentItemCommentBinding mBinding;
    private CommentAdapter mAdapter;

    private int mCurrentPage = 1;

    public static ItemCommentFragment newInstance(Item item, String itemParentName, String itemPosterName) {
        ItemCommentFragment fragment = new ItemCommentFragment();

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
        mBinding = FragmentItemCommentBinding.inflate(inflater, container, false);
        mBinding.setTotal(mItem.getKids().size());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        if (TextUtils.isEmpty(mItemParentName) && TextUtils.isEmpty(mItemPosterName)) {
            mAdapter = new CommentAdapter(getToolbarActivity(), null, mItem.getBy());
        } else {
            mAdapter = new CommentAdapter(getToolbarActivity(), mItem.getBy(), mItemPosterName);
        }

        mBinding.rvCommentList.setLayoutManager(layoutManager);
        mBinding.rvCommentList.setAdapter(mAdapter);
        mBinding.rvCommentList.showDivider();
        mBinding.rvCommentList.setOnLoadMoreListener((page, totalItemsCount) -> nextPageComments());

        if (savedInstanceState == null) {
            refresh();
        } else {
            onRestoreInstanceState(savedInstanceState);
        }

        return mBinding.getRoot();
    }

    private void onRestoreInstanceState(Bundle inState) {
        mAdapter.swapItems(Parcels.unwrap(inState.getParcelable(STATE_COMMENT_DATA)));
        mCurrentPage = inState.getInt(STATE_CURRENT_PAGE);

        if (mAdapter.getItemCount() <= 0) refresh();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_COMMENT_DATA, Parcels.wrap(mAdapter.getItems()));
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<HackerNewsResponse<List<Item>>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_COMMENT_ITEM:
                List<Long> kids = mItem.getKids();

                List<Long> list = CollectionUtils.subList(kids,
                        (mCurrentPage - 1) * ITEM_COUNT,
                        mCurrentPage * ITEM_COUNT);

                return new ItemListLoader(getContext(), list);
            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<HackerNewsResponse<List<Item>>> loader,
                               HackerNewsResponse<List<Item>> response) {
        if (response.isSuccessful()) {
            switch (loader.getId()) {
                case LOADER_ID_COMMENT_ITEM:
                    mAdapter.addItems(response.getData());
                    mBinding.rvCommentList.hideProgressBar();
                    break;
                default:
            }
        } else {
            SnackbarFactory.createRetrieveErrorSnackbar(mBinding.getRoot(), v -> retrieveComments()).show();
            mBinding.rvCommentList.hideProgressBar();
        }

    }

    @Override
    public void onLoaderReset(Loader<HackerNewsResponse<List<Item>>> loader) {

    }

    public void refresh() {
        mAdapter.clearItems();
        mCurrentPage = 1;
        mBinding.rvCommentList.showProgressBar();
        retrieveComments();
    }

    private void nextPageComments() {
        mCurrentPage++;
        retrieveComments();
    }

    private void retrieveComments() {
        if (mItem.getKids() == null) return;
        getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID_COMMENT_ITEM);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID_COMMENT_ITEM, null, this);
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