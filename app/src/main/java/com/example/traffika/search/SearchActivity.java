package com.example.traffika.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.traffika.R;
import com.example.traffika.db.Worker;

public class SearchActivity extends AppCompatActivity {

    EditText search_bar;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tableLayout = findViewById(R.id.tableLayout);

        search_bar=(EditText) findViewById(R.id.etSearch);

    }
    public void onSearch(View view){
        TableRow row = findViewById(R.id.tableRow1);
        String type="search";
        String keyword=search_bar.getText().toString();
        Worker worker=new Worker(this, tableLayout, row);
        worker.execute(type, keyword);
    }
}