package com.example.xyzreader.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by António "Curtes Malteser" Bastião on 15/05/2018.
 */

public class ArticleBodyAdapter extends RecyclerView.Adapter<ArticleBodyAdapter.ArticleBodyViewHolder> {

    private static final int TITLE = 0;
    private static final int SUBTITLE = 1;
    private static final int BODY = 2;

    private Context mContext;
    private ArrayList<String> mBodyText;
    private HashMap<String, Integer> mColorsMap;

    public ArticleBodyAdapter(Context context, ArrayList<String> text, HashMap<String, Integer> colorsMap) {
        this.mContext = context;
        this.mBodyText = text;
        this.mColorsMap = colorsMap;
    }

    @NonNull
    @Override
    public ArticleBodyAdapter.ArticleBodyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == TITLE) {
            view = inflater.inflate(R.layout.title_layout, viewGroup, false);
            view.setBackgroundColor(mColorsMap.get(mContext.getResources().getString(R.string.toolbar_color)));
        } else if (viewType == SUBTITLE){
            view = inflater.inflate(R.layout.subtitle_layout, viewGroup, false);
            view.setBackgroundColor(mColorsMap.get(mContext.getResources().getString(R.string.toolbar_color)));
        }else {
            view = inflater.inflate(R.layout.article_row, viewGroup, false);
        }

        return new ArticleBodyAdapter.ArticleBodyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleBodyAdapter.ArticleBodyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE;
        } else if (position == 1) {
            return SUBTITLE;
        } else {
            return BODY;
        }
    }

    @Override
    public int getItemCount() {
        return mBodyText.size();
    }

    public class ArticleBodyViewHolder extends RecyclerView.ViewHolder {

        TextView articleParagraph;
        TextView articleTitle;
        TextView articleByLine;

        public ArticleBodyViewHolder(View itemView) {
            super(itemView);

            articleTitle = itemView.findViewById(R.id.article_title);
            articleByLine = itemView.findViewById(R.id.article_byline);
            articleParagraph = itemView.findViewById(R.id.articleParagraph);
        }

        void bind(int listIndex) {
            if (listIndex == 0) {
                articleTitle.setTextColor(mColorsMap.get(mContext.getResources().getString(R.string.title_text_color)));
                articleTitle.setText(Html.fromHtml(mBodyText.get(listIndex)));
            } else if (listIndex == 1) {
                articleByLine.setTextColor(mColorsMap.get(mContext.getResources().getString(R.string.body_text_color)));
                articleByLine.setText(Html.fromHtml(mBodyText.get(listIndex)));
            } else {
                articleParagraph.setMovementMethod(new LinkMovementMethod());
                articleParagraph.setText(mBodyText.get(listIndex));
            }
        }
    }
}
