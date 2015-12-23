package com.abearablecode.todo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditItemActivity extends AppCompatActivity {

    private EditText etItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String item = getIntent().getStringExtra("item");
        int pos = getIntent().getIntExtra("position",0);

        setContentView(R.layout.content_edit_item);
        etItem = (EditText) findViewById(R.id.editItemText);
        //etItem.setText("Dishes");
        etItem.setText(item);


    }


}
