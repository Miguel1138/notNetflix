package com.miguel_santos.miguelix.ui.movie;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_santos.miguelix.R;
import com.miguel_santos.miguelix.model.Movie;
import com.miguel_santos.miguelix.ui.onItemCLickListener;
import com.miguel_santos.miguelix.util.ImageDownloaderTask;

import java.util.List;

public class MovieListAdapter
        extends RecyclerView.Adapter<MovieListAdapter.MovieHolder>
        implements onItemCLickListener {

    private final int layoutResource;
    private final List<Movie> movieList;
    private final Context context;

    public MovieListAdapter(Context context, List<Movie> movieList, int resource) {
        this.context = context;
        this.movieList = movieList;
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new MovieHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie movie = movieList.get(position);
        new ImageDownloaderTask(holder.cover).execute(movie.getCoverUrl());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setMovies(List<Movie> movieList) {
        this.movieList.clear();
        this.movieList.addAll(movieList);
    }

    @Override
    public void onCLick(int position) {
        // Pegar cover, e inserir numa intent e passar para a próxima Activity.
        if (movieList.get(position).getId() <= 3) {
            Intent intent = new Intent(context, MovieActivity.class);
            intent.putExtra("id", movieList.get(position).getId());
            context.startActivity(intent);
        }
    }

    protected static class MovieHolder extends RecyclerView.ViewHolder {

        private final ImageView cover;

        public MovieHolder(@NonNull View itemView, final onItemCLickListener onItemCLickListener) {
            super(itemView);
            cover = itemView.findViewById(R.id.img_cover);
            itemView.setOnClickListener(v -> onItemCLickListener.onCLick(getAdapterPosition()));
        }

    }

}
