package com.example.dell.firebaseintro.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.dell.firebaseintro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddBookFromStore extends AppCompatActivity {

    private Toolbar toolbar;
    private String TITLE = "ADD BOOK ";

    private ArrayAdapter<String> adapterGenre;
    private Spinner spinnerGenre;
    private EditText bName;
    private EditText bAuthor;
    private EditText bPiece;
    private EditText bCost;
    private Button addButton;
    private String mGenre ;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private ProgressDialog progressBar;
    private String storeID;
    private String storeName;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_from_store);

        toolbar = (Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        bName = (EditText) findViewById(R.id.bs_add_book);
        bAuthor = (EditText) findViewById(R.id.bs_add_author);
        bPiece = (EditText) findViewById(R.id.bs_add_number_piece);
        bCost = (EditText) findViewById(R.id.bs_add_price);
        addButton = (Button) findViewById(R.id.bs_add_button);
        spinnerGenre = (Spinner) findViewById(R.id.bs_spinner_genre);


        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("StoreBook");
        mUser = mAuth.getCurrentUser();

        progressBar = new ProgressDialog(this);



        adapterGenre = new ArrayAdapter<>(AddBookFromStore.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner_genre));
        adapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGenre.setAdapter(adapterGenre);
        spinnerGenre.setPrompt("Select Genre");


        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGenre = spinnerGenre.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = spinnerGenre.getItemAtPosition(0).toString();

            }
        });


        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {

            storeID = bundle.getString("storeID");
            storeName = bundle.getString("storeName");
            latitude = bundle.getString("latitude");
            longitude = bundle.getString("longitude");
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToDatabase(v);
            }
        });

    }

    private void addToDatabase(final View view)
    {

        final String name = bName.getText().toString().trim();
        final String author = bAuthor.getText().toString().trim();
        final String pieces = bPiece.getText().toString().trim();
        final String cost = bCost.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(author)
                && !TextUtils.isEmpty(pieces) && !TextUtils.isEmpty(cost) )
        {
            progressBar.setMessage("Uploading to database...");
            progressBar.show();


            String key = mDatabaseReference.push().getKey();

            Map<String , String> dataTosave = new HashMap<>();

            dataTosave.put("name",name);
            dataTosave.put("author",author);
            dataTosave.put("cost",cost);
            dataTosave.put("storeID",storeID);
            dataTosave.put("id",key);
            dataTosave.put("genre",mGenre);
            dataTosave.put("pieces",pieces);
            dataTosave.put("latitude",latitude);
            dataTosave.put("longitude",longitude);

            mDatabaseReference.child(key).setValue(dataTosave).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(view,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            });


            progressBar.dismiss();

            Intent intent = new Intent(AddBookFromStore.this,BookStoreDetail.class);
            intent.putExtra("storeID", storeID);
            intent.putExtra("storeName", storeName);
            intent.putExtra("latitude",latitude);
            intent.putExtra("longitude",longitude);
            startActivity(intent);

            finish();
        }
        else
        {
            Snackbar.make(view,"Enter all the fields with asterisk", Snackbar.LENGTH_LONG).show();
        }


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {

            case android.R.id.home:

                Intent intent = new Intent(AddBookFromStore.this,BookStoreDetail.class);
                intent.putExtra("storeID", storeID);
                intent.putExtra("storeName", storeName);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);

                finish();
                break;

        }
        return true;
    }
}
