package com.example.myisamm.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myisamm.R;
import com.example.myisamm.model.Club;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    private List<Club> clubList;
    private Context context;

    public ClubAdapter(List<Club> clubList, Context context) {
        this.clubList = clubList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_club_image, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = clubList.get(position);

        // Dynamically get drawable resource ID
        int imageResId = context.getResources().getIdentifier(club.getImage(), "drawable", context.getPackageName());
        if (imageResId != 0) {
            holder.clubImage.setImageResource(imageResId);
        }

        // Set click listener for each image
        holder.clubImage.setOnClickListener(v -> {
            String url = club.getUrl();

            if (url != null && !url.isEmpty()) {
                // Open the link in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } else {
                // Fallback Toast if URL is null
                Toast.makeText(context, "No link available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        ImageView clubImage;

        public ClubViewHolder(View itemView) {
            super(itemView);
            clubImage = itemView.findViewById(R.id.club_image);
        }
    }
}
