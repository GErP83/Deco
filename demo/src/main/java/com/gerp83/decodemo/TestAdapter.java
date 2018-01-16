package com.gerp83.decodemo;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gerp83.deco.DecoView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gerp83
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

    private ArrayList<String> itemList;
    private Random random;

    TestAdapter() {

        random = new Random();
        itemList = new ArrayList<>();
        for(int i = 0; i < 1000; i ++) {
            itemList.add("https://loremflickr.com/400/200?random=" + i);
        }
    }

    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_item, parent, false);
        return new TestHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TestHolder holder, int position) {

        String url = itemList.get(holder.getAdapterPosition());

        if(holder.getAdapterPosition() % 2 == 0) {
            holder.decoView.setCropToCircle(true);
        } else {
            holder.decoView.setCropToCircle(false);
            holder.decoView.setCorners(random.nextInt(50), random.nextInt(50), random.nextInt(50), random.nextInt(50));

        }
        holder.decoView.setStroke(random.nextInt(30), Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));

        holder.decoView.get(url, R.drawable.decoplaceholder);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class TestHolder extends RecyclerView.ViewHolder {

        DecoView decoView;

        TestHolder(View view) {
            super(view);
            decoView = view.findViewById(R.id.decoView);

        }
    }

}
