package dev.dect.thumbs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.dect.thumbs.R;
import dev.dect.thumbs.model.FontModel;

public class PickerFontAdapter extends RecyclerView.Adapter<PickerFontAdapter.MyViewHolder> {
    private final ArrayList<FontModel> FONTS;

    private FontModel FONT_ACTIVE;

    private int INDEX_ACTIVE = 0;

    private RecyclerView RECYCLER_VIEW;

    public PickerFontAdapter(ArrayList<FontModel> fonts, FontModel fontActive) {
        this.FONTS = fonts;
        this.FONT_ACTIVE = fontActive;

        if(fontActive != null) {
            for(int i = 0; i < fonts.size(); i++) {
                if(fonts.get(i).getId() == fontActive.getId()) {
                    INDEX_ACTIVE = i + 1;

                    break;
                }
            }
        }
    }

    public FontModel getFontSelected() {
        return FONT_ACTIVE;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout EL_CONTAINER;

        private final ImageView EL_RADIO;

        private final TextView EL_NAME;

        public MyViewHolder(View view) {
            super(view);

            this.EL_CONTAINER = view.findViewById(R.id.radioContainer);

            this.EL_NAME = view.findViewById(R.id.radioName);

            this.EL_RADIO = view.findViewById(R.id.selectIcon);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_picker_item, parent, false)
        );
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.RECYCLER_VIEW = recyclerView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final FontModel font = FONTS.get(position);

        holder.EL_NAME.setText(font.getName() + " [01:23:45]");

        final Context ctx = holder.EL_CONTAINER.getContext();

        holder.EL_NAME.setTypeface(Typeface.createFromAsset(ctx.getAssets(), font.getPath()));

        if(FONT_ACTIVE == null) {
            holder.EL_RADIO.setImageResource(R.drawable.radio_off);
        } else {
            holder.EL_RADIO.setImageResource(FONT_ACTIVE.getId() == font.getId() ? R.drawable.radio_on : R.drawable.radio_off);
        }

        holder.EL_CONTAINER.setOnClickListener((l) -> {
            holder.EL_RADIO.setImageResource(R.drawable.radio_on);

            final int i = INDEX_ACTIVE;

            INDEX_ACTIVE = holder.getAbsoluteAdapterPosition();

            FONT_ACTIVE = font;

            notifyItemChanged(i);

            RECYCLER_VIEW.callOnClick();
        });
    }

    @Override
    public int getItemCount() {
        return FONTS.size();
    }
}