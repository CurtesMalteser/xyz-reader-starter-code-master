package com.example.xyzreader.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
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
import android.view.WindowInsets;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.views.ThreeTwoImageVIew;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    //private View mUpButtonContainer;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View mUpButton;
    private ThreeTwoImageVIew photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 08/05/2018 -> Use CoordinatorLayout to hide the toolbar
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }*/
        setContentView(R.layout.activity_article_detail);

        getSupportLoaderManager().initLoader(0, null, this);

        mToolbar = findViewById(R.id.toolbar);

        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);

        photo = findViewById(R.id.photo);
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
            }

            @Override
            public void onPageSelected(final int position) {
                setActivityUI(position);
            }
        });
        //mUpButtonContainer = findViewById(R.id.up_container);

        mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    view.onApplyWindowInsets(windowInsets);
                    mTopInset = windowInsets.getSystemWindowInsetTop();
                    mUpButtonContainer.setTranslationY(mTopInset);
                    updateUpButtonPosition();
                    return windowInsets;
                }
            });
        }*/

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }
    }

    private void setActivityUI(int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);


            // TODO: 13/05/2018 -> test to load the image from activity
            Log.d("foo", "onPageSelected: " + mCursor.getString(ArticleLoader.Query.TITLE));
            //getSupportActionBar().setTitle(mCursor.getString(ArticleLoader.Query.TITLE));
            collapsingToolbarLayout.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));
            ImageLoaderHelper.getInstance(ArticleDetailActivity.this).getImageLoader()
                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.from(bitmap).generate();

                                Palette.Swatch statusBarColor = p.getDarkVibrantSwatch();

                                // Load default colors
                                int backgroundColor = ContextCompat.getColor(ArticleDetailActivity.this,
                                        R.color.theme_primary);
                                int textColor = ContextCompat.getColor(ArticleDetailActivity.this,
                                        R.color.text_color_primary);

                                // Check that the Vibrant swatch is available
                                if (statusBarColor != null) {
                                    if (Build.VERSION.SDK_INT >= 21)
                                        getWindow().setStatusBarColor(statusBarColor.getRgb());
                                    textColor = statusBarColor.getBodyTextColor();
                                } else {
                                    if (Build.VERSION.SDK_INT >= 21)
                                        getWindow().setStatusBarColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                                R.color.theme_primary_dark));
                                }

                                Palette.Swatch colorSwatch = p.getVibrantSwatch();

                                if (colorSwatch != null) {
                                    //mToolbar.setBackgroundColor(colorSwatch.getRgb());
                                    collapsingToolbarLayout.setBackgroundColor(colorSwatch.getRgb());
                                    collapsingToolbarLayout.setStatusBarScrimColor(colorSwatch.getRgb());
                                    collapsingToolbarLayout.setContentScrimColor(colorSwatch.getRgb());
                                } else {
                                /*mToolbar.setBackgroundColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                        R.color.theme_primary));*/
                                    collapsingToolbarLayout.setBackgroundColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                            R.color.theme_primary));
                                    collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                            R.color.theme_primary));
                                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                            R.color.theme_primary));
                                }

                                collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(ArticleDetailActivity.this,
                                        android.R.color.transparent));

                                //mMutedColor = p.getDarkMutedColor(0xFF333333);
                                photo.setImageBitmap(imageContainer.getBitmap());

                                // TODO: 13/05/2018 -> add the interface here to set the picture on avticity image view
                    /*mRootView.findViewById(R.id.meta_bar)
                            .setBackgroundColor(mMutedColor);
                    updateStatusBar();*/


                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });

            // TODO: 13/05/2018 -> end test to load the image from activity
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

    // TODO: 08/05/2018 -> I don't think I need to move the back button
   /* public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
        if (itemId == mSelectedItemId) {
            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
            updateUpButtonPosition();
        }
    }

    private void updateUpButtonPosition() {
        int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
        mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
    }*/

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
                //updateUpButtonPosition();
            }
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
