package com.example.imagestopdf.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imagestopdf.activities.ImageViewActivity;
import com.example.imagestopdf.R;
import com.example.imagestopdf.models.ModeLIamge;

import java.util.ArrayList;

public class AdapterImage extends RecyclerView.Adapter<AdapterImage.HolderImage> {

    private Context context;
    private ArrayList<ModeLIamge> imageArrayList;

    public AdapterImage(Context context, ArrayList<ModeLIamge> imageArrayList) {
        this.context = context;
        this.imageArrayList = imageArrayList;
    }

    @NonNull
    @Override
    public HolderImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(context).inflate(R.layout.row_image,parent,false);
        return new HolderImage(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderImage holder, int position) {

        ModeLIamge modeLIamge = imageArrayList.get(position);

        Uri imageUri = modeLIamge.getImageUri();

        Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.imageTv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("imageUri", ""+imageUri);
                context.startActivity(intent);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                modeLIamge.setChecked(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }


    class HolderImage extends RecyclerView.ViewHolder{


        ImageView imageTv;
        CheckBox checkBox;

        public HolderImage(@NonNull View itemView) {
            super(itemView);


            imageTv = itemView.findViewById(R.id.imageTv);
            checkBox = itemView.findViewById(R.id.checkBox);

        }
    }

}
