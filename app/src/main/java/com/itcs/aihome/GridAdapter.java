package com.itcs.aihome;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    LayoutInflater inflater;
    private Context context;
    private List<Model> models;
    private Socket socket;
    private String TAG = "GridAdapter";

    public GridAdapter(Context context, List<Model> models, Socket socket) {
        this.context = context;
        this.models = models;
        if(socket != null) {
            this.socket = socket;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.light_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Model model = models.get(i);
        int image = 0;
        String status = "";
        if(model.getStatus() == 0) {
            if(model.getTag().equals("light")) {
                image = R.drawable.bulb_white;
            } else {
                image = R.drawable.ac_white;
            }
            status = "ON";
            viewHolder.cardView.setBackgroundResource(R.drawable.bg_gradient);
            viewHolder.status.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.device_name.setTextColor(Color.parseColor("#ffffff"));
            viewHolder.cog.setBackgroundResource(R.drawable.cog_white);
        } else {
            if(model.getTag().equals("light")) {
                image = R.drawable.bulb;
            } else {
                image = R.drawable.ac;
            }
            status = "OFF";
            viewHolder.cardView.setBackgroundResource(R.drawable.bgwhite_corner);
            viewHolder.status.setTextColor(Color.parseColor("#414141"));
            viewHolder.device_name.setTextColor(Color.parseColor("#414141"));
            viewHolder.cog.setBackgroundResource(R.drawable.cog);
        }
        viewHolder.imageView.setImageResource(image);
        viewHolder.status.setText(status);
        viewHolder.device_name.setText(model.getDevices());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action;
                if(model.getFlag() == 0) {
                    model.setFlag(1);
                    action = 1;
                } else {
                    model.setFlag(0);
                    action = 0;
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("idaccess", model.getIdaccess());
                    jsonObject.put("iddevice", model.getIddevice());
                    jsonObject.put("idcontroller", model.getIdcontroller());
                    jsonObject.put("action", action);
                    socket.emit("trigger", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView device_name, status;
        private ImageButton cog;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            device_name = itemView.findViewById(R.id.devices);
            status = itemView.findViewById(R.id.status);
            cog = itemView.findViewById(R.id.btncog);
            cardView = itemView.findViewById(R.id.light_cards);
        }
    }
}
