package dev.dect.thumbs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.dect.thumbs.R;
import dev.dect.thumbs.model.ListButtonSubTextModel;
import dev.dect.thumbs.utils.Utils;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class ListButtonSubTextAdapter extends RecyclerView.Adapter<ListButtonSubTextAdapter.MyViewHolder> {
    private final ArrayList<ListButtonSubTextModel> LIST_BUTTONS;

    public ListButtonSubTextAdapter(ArrayList<ListButtonSubTextModel> listButtonSubTextModels) {
        this.LIST_BUTTONS = listButtonSubTextModels;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout EL_CONTAINER;

        private final TextView EL_TITLE,
                               EL_SUB_TITLE;

        public MyViewHolder(View view) {
            super(view);

            this.EL_CONTAINER = view.findViewById(R.id.listContainer);

            this.EL_TITLE = view.findViewById(R.id.itemTitle);
            this.EL_SUB_TITLE = view.findViewById(R.id.itemSubTitle);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_button_sub_text, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ListButtonSubTextModel listButtonSubTextModel = LIST_BUTTONS.get(position);

        final Context ctx = holder.EL_CONTAINER.getContext();

        holder.EL_TITLE.setText(ctx.getString(listButtonSubTextModel.getIdTitle()));

        if(listButtonSubTextModel.getValue() == null) {
            holder.EL_SUB_TITLE.setVisibility(View.GONE);
        } else {
            holder.EL_SUB_TITLE.setVisibility(View.VISIBLE);

            holder.EL_SUB_TITLE.setText(listButtonSubTextModel.getValue());
        }

        if(listButtonSubTextModel.isLastItemFromGroup()) {
            holder.EL_CONTAINER.setBackground(null);
        } else {
            holder.EL_CONTAINER.setBackgroundResource(R.drawable.list_item_divisor_horizontal_bottom);
        }

        if(listButtonSubTextModel.isIsMessageText()) {
            holder.EL_CONTAINER.setOnClickListener((v) -> listButtonSubTextModel.getListener().onButtonClicked(listButtonSubTextModel, holder.getAbsoluteAdapterPosition()));
            holder.EL_SUB_TITLE.setTextColor(ctx.getColor(R.color.list_item_sub_title));
            holder.EL_SUB_TITLE.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else {
            holder.EL_SUB_TITLE.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            if(listButtonSubTextModel.getListener() == null || !listButtonSubTextModel.isEnabled()) {
                holder.EL_SUB_TITLE.setTextColor(ctx.getColor(R.color.list_item_sub_title));

                if(listButtonSubTextModel.getDisabledMessage() != null) {
                    holder.EL_CONTAINER.setOnClickListener((v) -> Toast.makeText(ctx, listButtonSubTextModel.getDisabledMessage(), Toast.LENGTH_SHORT).show());
                }

                holder.EL_CONTAINER.setOnLongClickListener((v) -> {
                    Utils.copyToClipboard(ctx, listButtonSubTextModel.getValue());

                    return true;
                });
            } else {
                holder.EL_SUB_TITLE.setTextColor(ctx.getColor(R.color.list_item_sub_title_active));
                holder.EL_CONTAINER.setOnClickListener((v) -> listButtonSubTextModel.getListener().onButtonClicked(listButtonSubTextModel, holder.getAbsoluteAdapterPosition()));
            }
        }

        if(listButtonSubTextModel.isVisible()) {
            holder.EL_CONTAINER.setVisibility(View.VISIBLE);
            holder.EL_CONTAINER.setLayoutParams(new ViewGroup.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        } else {
            holder.EL_CONTAINER.setVisibility(View.GONE);
            holder.EL_CONTAINER.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return LIST_BUTTONS.size();
    }
}
