package com.example.xyzreader.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

/**
 * Created by António "Curtes Malteser" Bastião on 15/05/2018.
 */

public class ArticleBodyAdapter extends RecyclerView.Adapter<ArticleBodyAdapter.ArticleBodyViewHolder> {

    private String[] mBodyText;

    public ArticleBodyAdapter(String[] text) {
        this.mBodyText = text;
    }

    @NonNull
    @Override
    public ArticleBodyAdapter.ArticleBodyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.article_row, viewGroup, false);
        return new ArticleBodyAdapter.ArticleBodyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleBodyAdapter.ArticleBodyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mBodyText.length;
    }

    public class ArticleBodyViewHolder extends RecyclerView.ViewHolder {

        TextView articleParagraph;

        public ArticleBodyViewHolder(View itemView) {
            super(itemView);

            articleParagraph = itemView.findViewById(R.id.articleParagraph);
        }

        void bind(int listIndex) {
            articleParagraph.setText(Html.fromHtml(mBodyText[listIndex]));
        }
    }
}
