package dev.dect.thumbs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.dect.thumbs.R;
import dev.dect.thumbs.model.ListButtonSwitchModel;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class ListButtonSwitchAdapter extends RecyclerView.Adapter<ListButtonSwitchAdapter.MyViewHolder> {
    private final ArrayList<ListButtonSwitchModel> LIST_BUTTONS;

    public ListButtonSwitchAdapter(ArrayList<ListButtonSwitchModel> btns) {
        this.LIST_BUTTONS = btns;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout EL_CONTAINER,
                                       EL_BTN;

        private final TextView EL_TITLE,
                               EL_SUB_TITLE;

        private final Switch EL_SWITCH;

        public MyViewHolder(View view) {
            super(view);

            this.EL_CONTAINER = view.findViewById(R.id.listContainer);

            this.EL_BTN = view.findViewById(R.id.btn);

            this.EL_TITLE = view.findViewById(R.id.itemTitle);
            this.EL_SUB_TITLE = view.findViewById(R.id.itemSubTitle);

            this.EL_SWITCH = view.findViewById(R.id.switchBtn);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_button_switch, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ListButtonSwitchModel listButtonSwitch = LIST_BUTTONS.get(position);

        final Context ctx = holder.EL_CONTAINER.getContext();

        holder.EL_TITLE.setText(ctx.getString(listButtonSwitch.getIdTitle()));

        holder.EL_SUB_TITLE.setText(listButtonSwitch.getValue());

        if(!listButtonSwitch.isLastItemFromGroup()) {
            holder.EL_CONTAINER.setBackgroundResource(R.drawable.list_item_divisor_horizontal_bottom);
        }

        holder.EL_BTN.setOnClickListener((l) -> listButtonSwitch.getListener().onButtonClicked(listButtonSwitch, holder.getAbsoluteAdapterPosition()));

        holder.EL_SWITCH.setChecked(listButtonSwitch.isEnabled());

        holder.EL_SWITCH.setOnCheckedChangeListener((v, b) -> {
            listButtonSwitch.setEnabled(b);

            listButtonSwitch.getListener().onChange(b);
        });
    }

    @Override
    public int getItemCount() {
        return LIST_BUTTONS.size();
    }
}
