package com.example.finflow.bottom_fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;

import com.example.finflow.Login;
import com.example.finflow.MainActivity1;
import com.example.finflow.R;
import com.example.finflow.ReminderCode.AddReminderFragment;
import com.example.finflow.UserProfile;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class BillPaymentRemainderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_payment_remainder, container, false);
        setHasOptionsMenu(true);
        MaterialButton addItemReminderButton = view.findViewById(R.id.button_add_reminder);
        addItemReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddReminderFragment.class);
                startActivity(intent);
            }
        });
        return view;
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
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();


    }

}