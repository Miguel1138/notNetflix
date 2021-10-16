package com.miguel_santos.miguelix.ui.movie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_santos.miguelix.R;
import com.miguel_santos.miguelix.model.Movie;
import com.miguel_santos.miguelix.model.MovieDetail;
import com.miguel_santos.miguelix.ui.MainActivity;
import com.miguel_santos.miguelix.util.ImageDownloaderTask;
import com.miguel_santos.miguelix.util.MovieDetailTask;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {

    private ImageView cover;
    private Toolbar movieToolbar;
    private TextView txtTitle;
    private TextView txtSynopsis;
    private TextView txtCast;
    private RecyclerView recyclerSimilarMovies;
    private MovieListAdapter movieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        bindViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoader(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
        }

        List<Movie> movieList = new ArrayList<>();

        movieListAdapter = new MovieListAdapter(MovieActivity.this, movieList, R.layout.similar_movie_item);
        recyclerSimilarMovies.setAdapter(movieListAdapter);
        recyclerSimilarMovies.setLayoutManager(new GridLayoutManager(this, 3));

        // Toolbar setup
        setSupportActionBar(movieToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle(null);
        }
    }

    private void bindViews() {
        movieToolbar = findViewById(R.id.movie_toolbar);
        cover = findViewById(R.id.movie_cover_description);
        txtTitle = findViewById(R.id.txt_movie_title);
        txtSynopsis = findViewById(R.id.txt_movie_synopsis);
        txtCast = findViewById(R.id.txt_movie_cast);
        recyclerSimilarMovies = findViewById(R.id.recycler_similar_movies);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MovieActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(MovieDetail movieDetail) {
        ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask(cover);
        imageDownloaderTask.setShadowEnabled(true);
        imageDownloaderTask.execute(movieDetail.getMovie().getCoverUrl());

        movieListAdapter.setMovies(movieDetail.getSimilarMoviesList());
        txtTitle.setText(movieDetail.getMovie().getTitle());
        txtCast.setText(movieDetail.getMovie().getCast());
        txtSynopsis.setText(movieDetail.getMovie().getSynopsis());
        movieListAdapter.notifyDataSetChanged();
    }

}