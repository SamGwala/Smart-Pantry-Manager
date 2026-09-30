package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Ingredient;

import java.util.List;

/**
 Binds the list of pantry Ingredients to a RecyclerView.
 Uses a simple click listener interface so the hosting Activity
 decides what happens when a row is tapped (open Add/Edit screen).
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    private List<Ingredient> ingredientList;
    private OnIngredientClickListener listener;

    public PantryAdapter(List<Ingredient> ingredientList, OnIngredientClickListener listener) {
        this.ingredientList = ingredientList;
        this.listener = listener;
    }

    public void updateData(List<Ingredient> newList) {
        this.ingredientList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);

        holder.textName.setText(ingredient.getName());

        String quantityText = ingredient.getQuantity() + " " + ingredient.getUnit();
        holder.textQuantity.setText(quantityText);

        if (ingredient.getExpiryDate() == null || ingredient.getExpiryDate().trim().isEmpty()) {
            holder.textExpiry.setText("No expiry date set");
        } else {
            holder.textExpiry.setText("Expires: " + ingredient.getExpiryDate());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (ingredientList == null) {
            return 0;
        }
        return ingredientList.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textQuantity;
        TextView textExpiry;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textIngredientName);
            textQuantity = itemView.findViewById(R.id.textIngredientQuantity);
            textExpiry = itemView.findViewById(R.id.textIngredientExpiry);
        }
    }
}
