package com.example.myisamm.ui.courses.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myisamm.R;
import com.example.myisamm.model.MaterialItem;
import java.util.List;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private List<MaterialItem> materialItems;
    private OnMaterialClickListener listener;

    public interface OnMaterialClickListener {
        void onMaterialClick(MaterialItem item);
    }

    public MaterialAdapter(List<MaterialItem> materialItems, OnMaterialClickListener listener) {
        this.materialItems = materialItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        MaterialItem item = materialItems.get(position);
        holder.materialNameTextView.setText(item.getName());

        if (item.getType() != null) {
            if ("LINK".equalsIgnoreCase(item.getType())) {
                holder.materialIconImageView.setImageResource(R.drawable.ic_link_placeholder);
            } else if ("FILE".equalsIgnoreCase(item.getType())) {
                holder.materialIconImageView.setImageResource(R.drawable.ic_file_placeholder);
            } else {
                holder.materialIconImageView.setImageResource(R.drawable.ic_default_material_placeholder);
            }
        } else {
            holder.materialIconImageView.setImageResource(R.drawable.ic_default_material_placeholder); // Fallback
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMaterialClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialItems.size();
    }

    static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView materialNameTextView;
        ImageView materialIconImageView;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            materialNameTextView = itemView.findViewById(R.id.material_name_text_view);
            materialIconImageView = itemView.findViewById(R.id.material_icon_image_view);
        }
    }
}