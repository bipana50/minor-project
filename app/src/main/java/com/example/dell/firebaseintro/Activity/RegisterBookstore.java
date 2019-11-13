package com.example.dell.firebaseintro.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dell.firebaseintro.R;

public class RegisterBookstore extends AppCompatActivity {

    private Toolbar toolbar;
    private static final String TITLE = "Register Bookstore";
    private EditText bookStoreName;
    private EditText bookStorePhone;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bookstore);

        toolbar = (Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        bookStoreName = (EditText) findViewById(R.id.enterBookstoreName);
        bookStorePhone = (EditText) findViewById(R.id.enterBookstorePhone);
        nextButton = (Button) findViewById(R.id.nextButton);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = bookStoreName.getText().toString().trim();
                String phone = bookStorePhone.getText().toString().trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone))
                {
                   Intent intent = new Intent(RegisterBookstore.this, RegisterStoreMap.class);
                   intent.putExtra("bName",name);
                   intent.putExtra("bPhone",phone);
                   startActivity(intent);

                } else {

                    Snackbar.make(v,"Enter all fields",Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                RegisterBookstore.this.onBackPressed();
                break;

        }

       return super.onOptionsItemSelected(item);
    }
}
