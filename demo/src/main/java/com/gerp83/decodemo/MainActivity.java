package com.gerp83.decodemo;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gerp83.deco.utils.FileUtils;

/**
 * Created by gerp83
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TestAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        FileUtils.get().deleteAll(this);

        recyclerView = findViewById(R.id.recyclerView);

        adapter = new TestAdapter();

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration());
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 15;

        }
    }

}
