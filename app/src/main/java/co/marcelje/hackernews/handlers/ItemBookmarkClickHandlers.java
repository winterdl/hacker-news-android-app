package co.marcelje.hackernews.handlers;

import android.view.View;
import android.widget.ImageButton;

import org.greenrobot.eventbus.EventBus;

import co.marcelje.hackernews.R;
import co.marcelje.hackernews.activity.ToolbarActivity;
import co.marcelje.hackernews.database.HackerNewsDao;
import co.marcelje.hackernews.event.BookmarkEvent;
import co.marcelje.hackernews.factory.SnackbarFactory;
import co.marcelje.hackernews.model.Item;
import co.marcelje.hackernews.screen.news.NewsActivity;

public class ItemBookmarkClickHandlers {

    private final ToolbarActivity mActivity;

    public ItemBookmarkClickHandlers(ToolbarActivity activity) {
        mActivity = activity;
    }

    public void onClick(View view, Item data) {
        if (view instanceof ImageButton) {
            ImageButton bookmarkButton = (ImageButton) view;

            if (HackerNewsDao.isItemAvailable(mActivity, data.getId())) {
                HackerNewsDao.deleteItem(mActivity, data.getId());
                SnackbarFactory.createUnbookmarkedSuccessSnackBar(view).show();
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_border);
            } else {
                HackerNewsDao.insertItem(mActivity, data);
                SnackbarFactory.createBookmarkedSuccessSnackBar(view).show();
                bookmarkButton.setImageResource(R.drawable.ic_bookmark);
            }

            if (!NewsActivity.class.getName().equals(mActivity.getClass().getName())) {
                EventBus.getDefault().post(new BookmarkEvent());
            }
        }
    }
}