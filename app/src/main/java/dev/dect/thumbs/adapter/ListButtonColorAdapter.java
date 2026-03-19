package dev.dect.thumbs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.dect.thumbs.R;
import dev.dect.thumbs.model.ListButtonColorModel;
import dev.dect.thumbs.popup.ColorPickerPopup;
import dev.dect.thumbs.utils.Utils;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class ListButtonColorAdapter extends RecyclerView.Adapter<ListButtonColorAdapter.MyViewHolder> {
    private final ArrayList<ListButtonColorModel> LIST_BUTTONS;

    public ListButtonColorAdapter(ArrayList<ListButtonColorModel> listButtonSubTexts) {
        this.LIST_BUTTONS = listButtonSubTexts;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout EL_CONTAINER;

        private final ImageView EL_COLOR;

        private final TextView EL_TITLE,
                               EL_SUB_TITLE;

        public MyViewHolder(View view) {
            super(view);

            this.EL_CONTAINER = view.findViewById(R.id.listContainer);
            this.EL_COLOR = view.findViewById(R.id.color);

            this.EL_TITLE = view.findViewById(R.id.itemTitle);
            this.EL_SUB_TITLE = view.findViewById(R.id.itemSubTitle);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_button_color, parent, false)
        );
    }

    /** @noinspection CodeBlock2Expr*/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ListButtonColorModel listButtonColor = LIST_BUTTONS.get(position);

        final Context ctx = holder.EL_CONTAINER.getContext();

        holder.EL_TITLE.setText(ctx.getString(listButtonColor.getIdTitle()));

        holder.EL_SUB_TITLE.setText(listButtonColor.getColor().substring(0, listButtonColor.hasAlpha() ? 9 : 7));

        Utils.drawColorSampleOnImageView(holder.EL_COLOR, listButtonColor.getColorInt());

        if(listButtonColor.isLastItemFromGroup()) {
            holder.EL_CONTAINER.setBackground(null);
        } else {
            holder.EL_CONTAINER.setBackgroundResource(R.drawable.list_item_divisor_horizontal_bottom);
        }

        holder.EL_CONTAINER.setOnClickListener((l) -> {
            new ColorPickerPopup(ctx, listButtonColor.getColor(), listButtonColor.hasAlpha(), (color) -> {
                listButtonColor.setColor(color);

                holder.EL_SUB_TITLE.setText(color.substring(0, listButtonColor.hasAlpha() ? 9 : 7));

                Utils.drawColorSampleOnImageView(holder.EL_COLOR, listButtonColor.getColorInt());

                listButtonColor.getListener().onColorPicked(color);
            }).show();
        });
    }

    @Override
    public int getItemCount() {
        return LIST_BUTTONS.size();
    }
}
