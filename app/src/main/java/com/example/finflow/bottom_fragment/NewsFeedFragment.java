package com.example.finflow.bottom_fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.finflow.Login;
import com.example.finflow.Models.NewsApiResponse;
import com.example.finflow.Models.NewsHeadlines;
import com.example.finflow.OnFetchDataListener;
import com.example.finflow.R;
import com.example.finflow.RequestManager;
import com.example.finflow.SelectListener;
import com.example.finflow.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment implements SelectListener, View.OnClickListener {
    RecyclerView recyclerView;
    CustomAdapter adapter;
    ProgressDialog dialog;
    SearchView searchView;
    FirebaseAuth mAuth;


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
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks here
        int id = item.getItemId();
        if (id == R.id.user_profile) {
            // Handle user profile action
            Intent intent = new Intent(getActivity(), UserProfile.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.logout) {
            // Handle logout action
            // Implement logout functionality here

          logout();
          return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        mAuth.signOut();
        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();


    }
}
