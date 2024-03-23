package com.example.finflow.bottom_fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finflow.CustomAdapter;
import com.example.finflow.DetailsFragment;
import com.example.finflow.Models.NewsApiResponse;
import com.example.finflow.Models.NewsHeadlines;
import com.example.finflow.OnFetchDataListener;
import com.example.finflow.R;
import com.example.finflow.RequestManager;
import com.example.finflow.SelectListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment implements SelectListener, View.OnClickListener {
    RecyclerView recyclerView;
    CustomAdapter adapter;
    ProgressDialog dialog;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        searchView = view.findViewById(R.id.search_view);
        Button btnBusiness = view.findViewById(R.id.btn_1);
        Button btnEntertainment = view.findViewById(R.id.btn_2);
        Button btnGeneral = view.findViewById(R.id.btn_3);
        Button btnHealth = view.findViewById(R.id.btn_4);
        Button btnScience = view.findViewById(R.id.btn_5);
        Button btnSports = view.findViewById(R.id.btn_6);
        Button btnTechnology = view.findViewById(R.id.btn_7);

        btnBusiness.setOnClickListener(this);
        btnEntertainment.setOnClickListener(this);
        btnGeneral.setOnClickListener(this);
        btnHealth.setOnClickListener(this);
        btnScience.setOnClickListener(this);
        btnSports.setOnClickListener(this);
        btnTechnology.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ProgressDialog(requireContext());
        dialog.setTitle("Fetching news articles..");
        dialog.show();

        recyclerView = view.findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        loadNewsArticles("general");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.setTitle("Fetching news articles of " + query);
                dialog.show();
                RequestManager manager = new RequestManager(requireContext());
                manager.getNewsHeadlines(listener, "general", query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        String category = "";
        if (v.getId() == R.id.btn_1) {
            category = "business";
        } else if (v.getId() == R.id.btn_2) {
            category = "entertainment";
        } else if (v.getId() == R.id.btn_3) {
            category = "general";
        } else if (v.getId() == R.id.btn_4) {
            category = "health";
        } else if (v.getId() == R.id.btn_5) {
            category = "science";
        } else if (v.getId() == R.id.btn_6) {
            category = "sports";
        } else if (v.getId() == R.id.btn_7) {
            category = "technology";
        }
        if (!category.isEmpty()) {
            dialog.setTitle("Fetching news articles of " + category);
            dialog.show();
            RequestManager manager = new RequestManager(requireContext());
            manager.getNewsHeadlines(listener, category, null);
        }
    }

    private void loadNewsArticles(String category) {
        RequestManager manager = new RequestManager(requireContext());
        manager.getNewsHeadlines(listener, category, null);
    }

    private final OnFetchDataListener<NewsApiResponse> listener = new OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            if (list.isEmpty()) {
                Toast.makeText(requireContext(), "No data found!!!", Toast.LENGTH_SHORT).show();
            } else {
                showNews(list);
            }
            dialog.dismiss();
        }

        @Override
        public void onError(String message) {
            Toast.makeText(requireContext(), "An Error Occurred!!!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    };

    private void showNews(List<NewsHeadlines> list) {
        List<NewsHeadlines> filteredList = new ArrayList<>();
        for (NewsHeadlines headline : list) {
            if (!headline.isRemoved) {
                filteredList.add(headline);
            }
        }
        adapter = new CustomAdapter(requireContext(), filteredList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", headlines);
        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.bottomFragment, detailsFragment)
                .addToBackStack(null)
                .commit();
    }
}
