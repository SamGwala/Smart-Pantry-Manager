package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;

import java.util.List;

/**
 Binds a list of Recipes (already filtered by RecipeMatcher) to a RecyclerView.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;
    private boolean isAlmostList;

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this(recipeList, listener, false);
    }

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener, boolean isAlmostList) {
        this.recipeList = recipeList;
        this.listener = listener;
        this.isAlmostList = isAlmostList;
    }

    public void updateData(List<Recipe> newList) {
        this.recipeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.textName.setText(recipe.getName());

        int ingredientCount = recipe.getRequiredIngredients().size();
        holder.textSubtitle.setText(ingredientCount + " ingredients needed");

        if (isAlmostList) {
            holder.frameStampBadge.setBackgroundResource(R.drawable.stamp_almost_badge);
            holder.textStampMark.setText("~");
            holder.textStampMark.setTextColor(
                    holder.itemView.getResources().getColor(R.color.colorAlmost));
        } else {
            holder.frameStampBadge.setBackgroundResource(R.drawable.stamp_match_badge);
            holder.textStampMark.setText("✓");
            holder.textStampMark.setTextColor(
                    holder.itemView.getResources().getColor(R.color.colorMatch));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (recipeList == null) {
            return 0;
        }
        return recipeList.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textSubtitle;
        FrameLayout frameStampBadge;
        TextView textStampMark;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textSubtitle = itemView.findViewById(R.id.textRecipeSubtitle);
            frameStampBadge = itemView.findViewById(R.id.frameStampBadge);
            textStampMark = itemView.findViewById(R.id.textStampMark);
        }
    }
}
