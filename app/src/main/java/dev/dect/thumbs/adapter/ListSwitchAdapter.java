package dev.dect.thumbs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.dect.thumbs.R;
import dev.dect.thumbs.model.ListSwitchModel;

@SuppressLint({"UseSwitchCompatOrMaterialCode", "ApplySharedPref"})
public class ListSwitchAdapter extends RecyclerView.Adapter<ListSwitchAdapter.MyViewHolder> {
    private final ArrayList<ListSwitchModel> LIST_SWITCHES;

    public ListSwitchAdapter(ArrayList<ListSwitchModel> listSwitches) {
        this.LIST_SWITCHES = listSwitches;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout EL_CONTAINER;

        private final Switch EL_SWITCH;

        private final TextView EL_TITLE,
                               EL_SUB_TITLE;

        public MyViewHolder(View view) {
            super(view);

            this.EL_CONTAINER = view.findViewById(R.id.switchContainer);

            this.EL_SWITCH = view.findViewById(R.id.switchBtn);

            this.EL_TITLE = view.findViewById(R.id.switchTitle);
            this.EL_SUB_TITLE = view.findViewById(R.id.switchSubTitle);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_switch, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ListSwitchModel listSwitch = LIST_SWITCHES.get(position);

        final Context ctx = holder.EL_SWITCH.getContext();

        holder.EL_TITLE.setText(ctx.getString(listSwitch.getIdTitle()));

        if(listSwitch.hasSubTitle()) {
            holder.EL_SUB_TITLE.setVisibility(View.VISIBLE);
            holder.EL_SUB_TITLE.setText(ctx.getString(listSwitch.getIdSubTitle()));
        } else {
            holder.EL_SUB_TITLE.setVisibility(View.GONE);
        }

        if(!listSwitch.isLastItemFromGroup()) {
            holder.EL_CONTAINER.setBackgroundResource(R.drawable.list_item_divisor_horizontal_bottom);
        }

        holder.EL_SWITCH.setChecked(listSwitch.isEnabled());

        holder.EL_CONTAINER.setOnClickListener((l) -> holder.EL_SWITCH.toggle());

        holder.EL_SWITCH.setOnCheckedChangeListener((v, b) -> {
            listSwitch.setEnabled(b);

            new Handler(Looper.getMainLooper()).postDelayed(() -> listSwitch.getListener().onChange(listSwitch, b, holder.getAbsoluteAdapterPosition()), 250);
        });

        if(listSwitch.isVisible()) {
            holder.EL_CONTAINER.setVisibility(View.VISIBLE);
            holder.EL_CONTAINER.setLayoutParams(new ViewGroup.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        } else {
            holder.EL_CONTAINER.setVisibility(View.GONE);
            holder.EL_CONTAINER.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return LIST_SWITCHES.size();
    }
}
