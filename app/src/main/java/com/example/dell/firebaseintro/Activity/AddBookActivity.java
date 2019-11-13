package com.example.dell.firebaseintro.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dell.firebaseintro.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private EditText bookName;
    private EditText author;
    private EditText price;
    private Button addButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference databaseReference;
    private FirebaseUser mUser;
    private ProgressDialog progressBar;
    private static final int GALLERY_CODE = 1;
    private Uri mImageUri;
    private Spinner spinnerGenre;
    private Spinner spinnerCondition;
    private StorageReference mStorageRef;
    private String mCondition ;
    private String mGenre ;
    private String fname;
    private String phone;
    private ArrayAdapter<String> adapterCondition;
    private ArrayAdapter<String> adapterGenre;
    private Toolbar toolbar;
    private static final String TITLE = "Add Book";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        toolbar = (Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Book");
        mUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = new ProgressDialog(this);


        imageButton = (ImageButton) findViewById(R.id.addImageButton);
        bookName = (EditText) findViewById(R.id.addBookID);
        author = (EditText) findViewById(R.id.addAuthorID);
        price = (EditText) findViewById(R.id.addPriceID);
        addButton = (Button) findViewById(R.id.addBookButtonID);
        spinnerGenre = (Spinner) findViewById(R.id.spinnerGenreID);
        spinnerCondition = (Spinner) findViewById(R.id.spinnerConditionID);


        // for spinner..

        adapterCondition = new ArrayAdapter<>(AddBookActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner_condition));
        adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        adapterGenre = new ArrayAdapter<>(AddBookActivity.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner_genre));
        adapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //.. upto here


        spinnerGenre.setAdapter(adapterGenre);
        spinnerGenre.setPrompt("Select Genre");

        spinnerCondition.setAdapter(adapterCondition);
        spinnerCondition.setPrompt("Select Condition");

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);

            }
        });


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

        spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCondition = spinnerCondition.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCondition = spinnerCondition.getItemAtPosition(0).toString();
            }
        });


        String key = mUser.getUid();

        //TODO : USE SHARED PREFS FOR STORING NAME AND PHONE NUMBER OF SIGNED IN USER AAND USE THAT INSTEAD OF USING LISTENER IN EVERY SINGLE ACTIVITY

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fname = dataSnapshot.child("firstName").getValue(String.class);
                phone = dataSnapshot.child("phoneNumber").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(AddBookActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Adding to database

                addToDB(v);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK)
        {
            mImageUri = data.getData();
            imageButton.setImageURI(mImageUri);
        }
    }

    private void addToDB(final View v)
    {
        final String mname = bookName.getText().toString().trim();
        final String mAuthor = author.getText().toString().trim();
        final String mPrice = price.getText().toString().trim();

        if(!TextUtils.isEmpty(mname) && !TextUtils.isEmpty(mAuthor)
                && !TextUtils.isEmpty(mPrice) && (mImageUri)!=null)
        {
            //start uploading...

            progressBar.setMessage("Uploading to database...");
            progressBar.show();

            StorageReference filepath = mStorageRef.child("Book_images").
                    child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    String key = mDatabaseReference.push().getKey();

                    Map<String , String> dataTosave = new HashMap<>();

                    dataTosave.put("name",mname);
                    dataTosave.put("author",mAuthor);
                    dataTosave.put("price",mPrice);
                    dataTosave.put("image",downloadUrl.toString());
                    dataTosave.put("id",mUser.getUid());
                    dataTosave.put("genre",mGenre);
                    dataTosave.put("condition",mCondition);
                    dataTosave.put("fname",fname);
                    dataTosave.put("phone",phone);
                    dataTosave.put("bookid",key);

                    mDatabaseReference.child(key).setValue(dataTosave).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(v,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                        }
                    });

                    // Old way

                    //mNewPost.child("name").setValue(mname);
                    //mNewPost.child("author").setValue(mAuthor);
                    //mNewPost.child("genre").setValue(mGenre);

                    // and so on......

                    progressBar.dismiss();

                    startActivity(new Intent(AddBookActivity.this,AddedBooksActivity.class));
                    finish();
                }

        });




        } else {

            Snackbar.make(v,"Enter all the fields with asterisk", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {

            case android.R.id.home:
                AddBookActivity.this.onBackPressed();
                break;

        }
        return true;
    }
}
