package com.miguel_santos.miguelix.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.miguel_santos.miguelix.model.Category;
import com.miguel_santos.miguelix.model.Movie;

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

public class CategoryTask extends AsyncTask<String, Void, List<Category>> {

    private final WeakReference<Context> context;
    private ProgressDialog dialog;
    private CategoryLoader categoryLoader;

    public CategoryTask(Context context) {
        this.context = new WeakReference<>(context);
    }

    public void setCategoryLoader(CategoryLoader categoryLoader) {
        this.categoryLoader = categoryLoader;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Context context = this.context.get();
        dialog = ProgressDialog.show(context, "Carregando", "", true);
    }

    @Override
    protected List<Category> doInBackground(String... strings) {
        // Url = link do json object
        String url = strings[0];

        try {
            URL requestUrl = new URL(url);

            HttpsURLConnection urlConnection = (HttpsURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode > 400) {
                throw new IOException("Erro na comunicação do servidor");
            }

            InputStream inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
            String jsonAsString = toString(bufferedInputStream);
            List<Category> categoryList = getCategories(new JSONObject(jsonAsString));

            inputStream.close();
            urlConnection.disconnect();

            return categoryList;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



    private String toString(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int readerBytes;

        while ((readerBytes = inputStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, readerBytes);
        }

        return byteArrayOutputStream.toString();
    }

    private List<Category> getCategories(JSONObject jsonObject) throws JSONException {
        List<Category> categoryList = new ArrayList<>();
        JSONArray categoryArray = jsonObject.getJSONArray("category");

        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject category = categoryArray.getJSONObject(i);
            String title = category.getString("title");

            List<Movie> movieList = new ArrayList<>();
            JSONArray movieArray = category.getJSONArray("movie");

            for (int j = 0; j < movieArray.length(); j++) {
                Movie movieObject = new Movie();
                JSONObject movie = movieArray.getJSONObject(j);
                String coverUrl = movie.getString("cover_url");
                int id = movie.getInt("id");

                movieObject.setCoverUrl(coverUrl);
                movieObject.setId(id);
                movieList.add(movieObject);
            }

            Category categoryObject = new Category();
            categoryObject.setName(title);
            categoryObject.setMovies(movieList);

            categoryList.add(categoryObject);
        }

        return categoryList;
    }

    @Override
    protected void onPostExecute(List<Category> categoryList) {
        super.onPostExecute(categoryList);
        dialog.dismiss();
        if (categoryLoader != null) categoryLoader.onResult(categoryList);
    }

    public interface CategoryLoader {
        void onResult(List<Category> categoryList);
    }

}
