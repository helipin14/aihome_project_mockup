package com.itcs.aihome;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.MultiFormatWriter;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<GroupItem> groupItemList;
    private int Layout;
    private Context context;
    private AlertDialog alertDialog;
    private ImageButton close;
    private generateBarcode generateBarcode;
    private MultiFormatWriter multiFormatWriter;
    private Button generateqrcode;
    private ImageView imageView;
    private LinearLayout linearLayout;

    public GroupAdapter(List<GroupItem> groupItems, int Layout, Context context) {
        this.Layout = Layout;
        this.groupItemList = groupItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(Layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        GroupItem groupItem = groupItemList.get(i);
        viewHolder.group_name.setText(groupItem.getName());
        viewHolder.usershortlist.setText(groupItem.getUsershortlist());
        viewHolder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDelete(i);
            }
        });
        viewHolder.qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        onItemClickListener();
    }

    @Override
    public int getItemCount() {
        return groupItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView group_name, usershortlist;
        private SolidIconTextView qrcode, trash;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qrcode = itemView.findViewById(R.id.showqrcode);
            trash = itemView.findViewById(R.id.trash);
            group_name = itemView.findViewById(R.id.group_name);
            usershortlist = itemView.findViewById(R.id.user_shortlist);
            linearLayout = itemView.findViewById(R.id.parent_groupitem);
        }
    }

    private void showDialog() throws WindowManager.BadTokenException {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_qrcode, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        close = view.findViewById(R.id.dismiss);
        generateqrcode = view.findViewById(R.id.generate);
        imageView = view.findViewById(R.id.qrcode_image);
        generate(imageView);
        generateqrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate(imageView);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        builder.setCancelable(false);
    }

    private void showDialogDelete(final int position) throws WindowManager.BadTokenException {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setIcon(R.drawable.exclamation_triangle);
        builder.setCancelable(false);
        builder.setMessage("Are you sure to delete this group?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                groupItemList.remove(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void generate(ImageView imageView) {
        multiFormatWriter = new MultiFormatWriter();
        String data = "https://www.w3schools.com";
        generateBarcode = new generateBarcode(data, imageView, multiFormatWriter);
        generateBarcode.generate();
    }

    private void onItemClickListener() {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), GroupSettings.class);
                context.startActivity(intent);
            }
        });
    }
}
