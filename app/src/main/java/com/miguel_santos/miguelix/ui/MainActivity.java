package com.miguel_santos.miguelix.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_santos.miguelix.R;
import com.miguel_santos.miguelix.model.Category;
import com.miguel_santos.miguelix.util.CategoryTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryTask.CategoryLoader {

    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_category_list);

        List<Category> categoryList = new ArrayList<>();
        CategoryTask categoryTask = new CategoryTask(this);
        categoryTask.setCategoryLoader(this);
        categoryTask.execute("https://tiagoaguiar.co/api/netflix/home");

        categoryAdapter = new CategoryAdapter(this, categoryList, R.layout.category_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(categoryAdapter);
    }

    @Override
    public void onResult(List<Category> categoryList) {
        categoryAdapter.setCategories(categoryList);
        categoryAdapter.notifyDataSetChanged();
    }

}