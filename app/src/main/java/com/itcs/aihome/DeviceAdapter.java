package com.itcs.aihome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private List<DataDevice> dataDevices;
    private Context context;
    private int count = 0;

    public DeviceAdapter(List<DataDevice> dataDevices, Context context) {
        this.dataDevices = dataDevices;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final DataDevice dataDevice = dataDevices.get(i);
        viewHolder.device_name.setText(dataDevice.getName());
        viewHolder.adddevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataDevice.getFlag() == 0) {
                    dataDevice.setFlag(1);
                    count += 1;
                    Toast.makeText(context, String.valueOf(count) + " devices selected", Toast.LENGTH_LONG).show();
                    viewHolder.adddevice.setText(R.string.minus_circle);
                    Log.e("DeviceAdapter", "Device selected : true");
                } else {
                    dataDevice.setFlag(0);
                    count -= 1;
                    Toast.makeText(context, "This device removed", Toast.LENGTH_LONG).show();
                    viewHolder.adddevice.setText(R.string.plus);
                    Log.e("DeviceAdapter", "Device selected : false");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView device_name;
        public SolidIconTextView adddevice;
        public ViewHolder(View view) {
            super(view);
            device_name = view.findViewById(R.id.device_name);
            adddevice = view.findViewById(R.id.add_device);
        }
    }
}
