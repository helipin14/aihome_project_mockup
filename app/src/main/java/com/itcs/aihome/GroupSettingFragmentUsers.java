package com.itcs.aihome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GroupSettingFragmentUsers extends Fragment {

    private RecyclerView recyclerView;
    private List<GroupSettingItems> groupSettingItems;
    private GroupSettingItemsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.group_settings_item, container, false);
        init(view);
        loadData();
        main();
        return view;
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.group_settings_items);
    }

    private void main() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupSettingItemsAdapter(getContext(), groupSettingItems);
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        groupSettingItems = new ArrayList<>();
        groupSettingItems.add(new GroupSettingItems("User 1", "1", "user"));
        groupSettingItems.add(new GroupSettingItems("User 2", "1", "user"));
        groupSettingItems.add(new GroupSettingItems("User 3", "1", "user"));
        groupSettingItems.add(new GroupSettingItems("User 4", "1", "user"));
        groupSettingItems.add(new GroupSettingItems("User 5", "1", "user"));
        groupSettingItems.add(new GroupSettingItems("User 6", "1", "user"));
        groupSettingItems.add(new GroupSettingItems("User 7", "1", "user"));
    }
}
