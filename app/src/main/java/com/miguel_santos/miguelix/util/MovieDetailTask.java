package com.miguel_santos.miguelix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.miguel_santos.miguelix.model.Movie;
import com.miguel_santos.miguelix.model.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MovieDetailTask extends AsyncTask<String, Void, MovieDetail> {

    private final WeakReference<Context> contextWeakReference;
    private ProgressDialog dialog;
    private MovieDetailLoader movieDetailLoader;

    public MovieDetailTask(Context context) {
        this.contextWeakReference = new WeakReference<>(context);
    }

    public void setMovieDetailLoader(MovieDetailLoader movieDetailLoader) {
        this.movieDetailLoader = movieDetailLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = contextWeakReference.get();
        dialog = ProgressDialog.show(context, "Carregando", "", true);
    }

    @Override
    protected MovieDetail doInBackground(String... strings) {
        String url = strings[0];
        try {
            URL requestURL = new URL(url);

            HttpsURLConnection urlConnection = (HttpsURLConnection) requestURL.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode > 400) {
                throw new IOException("Erro de comunicação com o servidor.");
            }

            InputStream inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            String jsonAsString = toString(bufferedInputStream);
            MovieDetail movieDetail = getMovieDetail(new JSONObject(jsonAsString));

            inputStream.close();
            urlConnection.disconnect();

            return movieDetail;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Conversão dos dados em bytes para String.
    private String toString(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream byteArrayoutputStream = new ByteArrayOutputStream();
        int readedBytes;

        while ((readedBytes = inputStream.read(bytes)) > 0) {
            byteArrayoutputStream.write(bytes, 0, readedBytes);
        }

        return byteArrayoutputStream.toString();
    }

    //Criar objeto do MovieDetail e passar os dados de uma lista de movies e o FIlme escolhido.
    // Para pegar os dados de um json, deve ser inserida o nome das keys corretamente.
    private MovieDetail getMovieDetail(JSONObject json) throws JSONException {
        int id = json.getInt("id");
        String cover_url = json.getString("cover_url");
        String title = json.getString("title");
        String synopsis = json.getString("desc");
        String cast = json.getString("cast");

        List<Movie> similarMovieList = new ArrayList<>();
        JSONArray similarMovieArray = json.getJSONArray("movie");
        for (int i = 0; i < similarMovieArray.length(); i++) {
            JSONObject similar_movie = similarMovieArray.getJSONObject(i);

            int similar_id = similar_movie.getInt("id");
            String similar_coverUrl = similar_movie.getString("cover_url");
            Movie similar_movieObject = new Movie();
            similar_movieObject.setId(similar_id);
            similar_movieObject.setCoverUrl(similar_coverUrl);

            similarMovieList.add(similar_movieObject);
        }

        Movie movie = new Movie();
        movie.setId(id);
        movie.setCoverUrl(cover_url);
        movie.setTitle(title);
        movie.setSynopsis(synopsis);
        movie.setCast(cast);

        return new MovieDetail(movie, similarMovieList);
    }

    @Override
    protected void onPostExecute(MovieDetail movieDetail) {
        super.onPostExecute(movieDetail);
        dialog.dismiss();
        if (movieDetailLoader != null) movieDetailLoader.onResult(movieDetail);
    }

    public interface MovieDetailLoader {
        void onResult(MovieDetail movieDetail);
    }

}
