package com.food_on.app.CustomerFoodPanel;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.food_on.app.ChefFoodPanel.UpdateDishModel;
import com.food_on.app.Customer;

import com.food_on.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private CustomerHomeAdapter adapter;
    String State, City, Sub;
    DatabaseReference dataaa, databaseReference;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customerhome, null);
        getActivity().setTitle("Food On");
        setHasOptionsMenu(true);
        recyclerView = v.findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.move);
        recyclerView.startAnimation(animation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateDishModelList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.green);


        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                dataaa = FirebaseDatabase.getInstance().getReference("Customer").child(userid);
                dataaa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Customer cust = dataSnapshot.getValue(Customer.class);
                        State = "Kaduna";
                        City = "Kadpoly";
                        Sub = "MainCampus";
                        customermenu();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }


    @Override
    public void onRefresh() {

        try {
            customermenu();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void customermenu() {

        swipeRefreshLayout.setRefreshing(true);
        try {

            databaseReference = FirebaseDatabase.getInstance().getReference("FoodSupplyDetails").child("Kaduna").child("Kadpoly").child("MainCampus");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    updateDishModelList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Log.i("TAG",snapshot1.toString());
                            UpdateDishModel updateDishModel = snapshot1.getValue(UpdateDishModel.class);
                            updateDishModelList.add(updateDishModel);
                        }
                    }

                    try {
                        adapter = new CustomerHomeAdapter(getContext(), updateDishModelList);
                    }catch (Exception e){
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void search(final String searchtext) {

        ArrayList<UpdateDishModel> mylist = new ArrayList<>();
        for (UpdateDishModel object : updateDishModelList) {
            if (object.getDishes().toLowerCase().contains(searchtext.toLowerCase())) {
                mylist.add(object);
            }
        }
        adapter = new CustomerHomeAdapter(getContext(), mylist);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.Searchdish);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Dish");


    }
}
