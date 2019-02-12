package com.itcs.aihome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GroupSettingItemsAdapter extends RecyclerView.Adapter<GroupSettingItemsAdapter.ViewHolder> {

    private List<GroupSettingItems> groupSettingItemsList;
    private Context context;
    private AlertDialog.Builder builder;

    public GroupSettingItemsAdapter(Context context, List<GroupSettingItems> groupSettingItems) {
        this.context = context;
        this.groupSettingItemsList = groupSettingItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.device_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final GroupSettingItems groupSettingItems = groupSettingItemsList.get(i);
        viewHolder.itemName.setText(groupSettingItems.getName());
        viewHolder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(groupSettingItems.getType(), i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupSettingItemsList.size();
    }

    private void deleteItem(final String type, final int position) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setIcon(R.drawable.exclamation_triangle);
        builder.setMessage("Are you sure want to delete this " + type + "?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(type.equals("user")) {
                    groupSettingItemsList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "This " + type + " removed", Toast.LENGTH_LONG).show();
                } else {
                    groupSettingItemsList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "This " + type + " removed", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemName;
        public SolidIconTextView removeItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            removeItem = itemView.findViewById(R.id.remove_item);
        }
    }
}
