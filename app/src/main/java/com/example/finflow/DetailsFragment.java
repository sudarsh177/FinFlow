package com.example.finflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finflow.Models.NewsHeadlines;
import com.squareup.picasso.Picasso;

public class DetailsFragment extends Fragment {
    private NewsHeadlines headlines;

    public DetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            headlines = (NewsHeadlines) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        TextView txt_title = view.findViewById(R.id.text_detail_title);
        TextView txt_author = view.findViewById(R.id.text_detail_author);
        TextView txt_time = view.findViewById(R.id.text_detail_time);
        TextView txt_detail = view.findViewById(R.id.text_detail_detail);
        TextView txt_content = view.findViewById(R.id.text_detail_content);
        ImageView img_news = view.findViewById(R.id.img_detail_news);
        Button openWebsiteButton = view.findViewById(R.id.button_open_website);

        txt_title.setText(headlines.getTitle());
        txt_author.setText(headlines.getAuthor());
        txt_time.setText(headlines.getPublishedAt());
        txt_detail.setText(headlines.getDescription());
        txt_content.setText(headlines.getContent());
        Picasso.get().load(headlines.getUrlToImage()).into(img_news);

        String newsUrl = headlines.getUrl();
        openWebsiteButton.setOnClickListener(v -> {
            if (newsUrl != null && !newsUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl));
                startActivity(browserIntent);
            }
        });

        return view;
    }
}
