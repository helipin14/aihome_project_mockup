package com.itcs.aihome;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

public class ColorItemAdapter extends RecyclerView.Adapter<ColorItemAdapter.ViewHolder> {

    private List<ColorItem> colors;
    private Context context;
    private int selected_color = 0;

    public ColorItemAdapter(List<ColorItem> colors, Context context) {
        this.context = context;
        this.colors = colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.color_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final ColorItem color = colors.get(i);
        viewHolder.colorbtn.setTextColor(color.getColor());
        viewHolder.colorbtn.setTag(new Integer(i));
        viewHolder.colorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_color != 0) {
                    if(color.getFlag() == 0) {
                        color.setFlag(1);
                        selected_color = i;
                        viewHolder.colorbtn.setText(R.string.check_circle);
                        Toast.makeText(context, "Flag : " + String.valueOf(color.getFlag()), Toast.LENGTH_SHORT).show();
                    } else {
                        color.setFlag(0);
                        selected_color = 0;
                        viewHolder.colorbtn.setText(R.string.circle);
                        Toast.makeText(context, "Flag : " + String.valueOf(color.getFlag()), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public SolidIconTextView colorbtn;
        public LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorbtn = itemView.findViewById(R.id.color_button);
            container = itemView.findViewById(R.id.container_colorbutton);
        }
    }
}
