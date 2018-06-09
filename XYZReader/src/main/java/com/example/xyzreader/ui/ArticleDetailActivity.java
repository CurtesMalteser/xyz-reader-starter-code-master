package com.example.xyzreader.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.views.CurtesMalteserImageView;

import java.util.HashMap;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    //private View mUpButtonContainer;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View mUpButton;
    private CurtesMalteserImageView photo;
    private FloatingActionButton mFabShare;
    private HashMap<String, Integer> mColorsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        getSupportLoaderManager().initLoader(0, null, this);

        mToolbar = findViewById(R.id.toolbar);

        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);

        photo = findViewById(R.id.photo);

        mFabShare = findViewById(R.id.fab_share);
        mFabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        mColorsMap = new HashMap<>();

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == 0 && positionOffset == 0.0) setActivityUI(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mUpButton.animate()
                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
                        .setDuration(300);
                photo.animate()
                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
                        .setDuration(300);
                mFabShare.animate()
                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
                        .setDuration(300);
            }

            @Override
            public void onPageSelected(final int position) {
                setActivityUI(position);
            }
        });

        mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });


        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
            }
        }
    }

    private void setActivityUI(int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);

            collapsingToolbarLayout.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));
            ImageLoaderHelper.getInstance(ArticleDetailActivity.this).getImageLoader()
                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.from(bitmap).generate();

                                Palette.Swatch statusBarColor = p.getDarkVibrantSwatch();

                                // Check that the Vibrant swatch is available
                                if (statusBarColor != null) {
                                    if (Build.VERSION.SDK_INT >= 21)
                                        getWindow().setStatusBarColor(statusBarColor.getRgb());
                                } else {
                                    if (Build.VERSION.SDK_INT >= 21)
                                        getWindow().setStatusBarColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                                R.color.theme_primary_dark));
                                }

                                Palette.Swatch colorSwatch = p.getVibrantSwatch();

                                if (colorSwatch != null) {
                                    collapsingToolbarLayout.setBackgroundColor(colorSwatch.getRgb());
                                    collapsingToolbarLayout.setStatusBarScrimColor(colorSwatch.getRgb());
                                    collapsingToolbarLayout.setContentScrimColor(colorSwatch.getRgb());

                                    mColorsMap.put(getResources().getString(R.string.toolbar_color), colorSwatch.getRgb());
                                    mColorsMap.put(getResources().getString(R.string.title_text_color), colorSwatch.getTitleTextColor());
                                    mColorsMap.put(getResources().getString(R.string.body_text_color), colorSwatch.getBodyTextColor());

                                } else {
                                    collapsingToolbarLayout.setBackgroundColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                            R.color.theme_primary));
                                    collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                            R.color.theme_primary));
                                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                            R.color.theme_primary));

                                    mColorsMap.put(getResources().getString(R.string.toolbar_color), ContextCompat.getColor(ArticleDetailActivity.this,
                                            R.color.theme_primary));
                                    mColorsMap.put(getResources().getString(R.string.title_text_color), android.R.color.black);
                                    mColorsMap.put(getResources().getString(R.string.body_text_color), android.R.color.black);
                                }

                                collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                        android.R.color.transparent));

                                photo.setImageBitmap(imageContainer.getBitmap());

                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
