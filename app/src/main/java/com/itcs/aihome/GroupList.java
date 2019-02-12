package com.itcs.aihome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GroupList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private List<GroupItem> groupItem;
    private FloatingActionButton addgroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group);
        init();
        main();
    }

    private void init() {
        addgroup = findViewById(R.id.add_group);
        recyclerView = findViewById(R.id.group_items);
    }

    private void main() {
        loadData();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new GroupAdapter(groupItem, R.layout.group_item, getApplicationContext());
        recyclerView.setAdapter(adapter);
        addgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), addgroup.class);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        groupItem = new ArrayList<>();
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
        groupItem.add(new GroupItem("Group 1", "Helipin, Albus, Igro, Aurelius, Leta", "1"));
    }
}
