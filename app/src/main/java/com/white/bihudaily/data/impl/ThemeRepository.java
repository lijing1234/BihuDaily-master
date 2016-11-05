package com.white.bihudaily.data.impl;

import android.content.Context;

import com.white.bihudaily.app.BihuDailyApplication;
import com.white.bihudaily.base.BaseRepository;
import com.white.bihudaily.bean.Story;
import com.white.bihudaily.bean.Theme;
import com.white.bihudaily.data.ThemeSource;
import com.white.bihudaily.db.ReaderDao;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Author White
 * Date 2016/8/13
 * Time 16:16
 */
public class ThemeRepository extends BaseRepository implements ThemeSource {

    Action1<Theme> mAction1 = new Action1<Theme>() {
        @Override
        public void call(Theme theme) {
            // 标记已读
            List<Integer> reader = new ReaderDao(BihuDailyApplication.getAppContext())
                    .getReaderList();
            List<Story> themeStories = theme.getStories();
            for (Story story : themeStories) {
                story.setRead(reader.contains(story.getId()));
                // 标记无图
                if (story.getImages() == null || story.getImages().size() == 0) {
                    story.setShowType(Story.TYPE_NO_IMG_STORY);
                }
            }
        }
    };

    @Override
    public void saveReader(Context context, int id) {
        ReaderDao readerDao = new ReaderDao(context);
        readerDao.save(id);
    }

    @Override
    public List<Integer> getReader(Context context) {
        ReaderDao readerDao = new ReaderDao(context);
        return readerDao.getReaderList();
    }


    @Override
    public Observable<Theme> loadTheme(int id) {
        return mBihuApi.getTheme(id).doOnNext(mAction1);
    }


    @Override
    public Observable<Theme> loadBeforeTheme(int themeId, int storyId) {
        return mBihuApi.getBeforeTheme(themeId, storyId).doOnNext(mAction1);
    }
}
