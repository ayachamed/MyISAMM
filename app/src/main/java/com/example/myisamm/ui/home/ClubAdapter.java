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

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    private int[] clubImages;
    private Context context;

    // Array of URLs for each club
    private String[] clubUrls = {
            "https://www.facebook.com/profile.php?id=61567247850707",
            "https://www.facebook.com/robotiqueisamm/",
            "https://www.facebook.com/OrendaJE",
            "https://www.facebook.com/IsammMicrosoftClub",
            "https://www.facebook.com/profile.php?id=100087931522321",
            "https://www.facebook.com/LOGISAMM",
            "https://www.facebook.com/music.club.isamm",
            "https://www.facebook.com/ClubJ2I",
            "https://www.facebook.com/profile.php?id=61551015728123"

    };

    public ClubAdapter(int[] clubImages, Context context) {
        this.clubImages = clubImages;
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

        holder.clubImage.setImageResource(clubImages[position]);

        // Set click listener for each image
        holder.clubImage.setOnClickListener(v -> {
            String clubName = "Club " + (position + 1);
            String url = clubUrls[position];

            if (url != null) {
                // Open the link in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } else {
                // Fallback Toast if URL is null
                Toast.makeText(context, "No link available for " + clubName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubImages.length;
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        ImageView clubImage;

        public ClubViewHolder(View itemView) {
            super(itemView);
            clubImage = itemView.findViewById(R.id.club_image);
        }
    }
}
