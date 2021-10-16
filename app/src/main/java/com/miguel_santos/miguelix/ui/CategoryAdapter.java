package com.miguel_santos.miguelix.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_santos.miguelix.R;
import com.miguel_santos.miguelix.model.Category;
import com.miguel_santos.miguelix.ui.movie.MovieListAdapter;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private final int layoutResource;
    private final List<Category> categoryList;
    private final Context context;

    public CategoryAdapter(Context context, List<Category> categoryList, int layoutResource) {
        this.context = context;
        this.categoryList = categoryList;
        this.layoutResource = layoutResource;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(layoutResource, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.categoryTitle.setText(category.getName());
        holder.movieList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.movieList.setAdapter(new MovieListAdapter(context, category.getMovies(), R.layout.movie_item));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategories(List<Category> categoryList) {
        this.categoryList.clear();
        this.categoryList.addAll(categoryList);
    }

    protected static class CategoryHolder extends RecyclerView.ViewHolder {

        private final RecyclerView movieList;
        private final TextView categoryTitle;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.txt_category_title);
            movieList = itemView.findViewById(R.id.recycler_movie_list);
        }

    }

}
