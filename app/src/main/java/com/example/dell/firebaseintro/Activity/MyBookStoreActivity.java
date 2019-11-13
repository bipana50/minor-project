package com.example.dell.firebaseintro.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebaseintro.Adapter.SellerRecyclerAdapter;
import com.example.dell.firebaseintro.Model.Book;
import com.example.dell.firebaseintro.Model.BookStore;
import com.example.dell.firebaseintro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyBookStoreActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private android.support.v7.widget.Toolbar mToolbar;
    private TextView bName;
    private TextView bPhone;
    private TextView bLocation;
    private CardView cardView;
    private ValueEventListener mValueEventListener;
    private String storeID;
    private String storeName;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_store);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Bookstore");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        bName = (TextView) findViewById(R.id.bookstore_name);
        bPhone = (TextView) findViewById(R.id.bookstore_phone);
        bLocation = (TextView) findViewById(R.id.bookstore_location);
        cardView = (CardView) findViewById(R.id.my_bookstore_cardview);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore");

        mDatabaseReference.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {

                    BookStore book = ds.getValue(BookStore.class);

                    if(book.getUserID().equals(mUser.getUid()))
                    {
                        storeID = book.getBookStoreID().trim();
                        storeName = book.getBookStoreName().trim();

                        bName.setText(book.getBookStoreName().trim());
                        bPhone.setText("Phone No. : "+book.getBookStorePhone().trim());

                        latitude = book.getLatitude();
                        longitude = book.getLongitude();
                        //TODO: location nai dekhaune banaune latitude longi matra hoina
                        bLocation.setText("Location : ( " + latitude + " , " + longitude + " )");
                        break;
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MyBookStoreActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };

        mDatabaseReference.addValueEventListener(valueEventListener);

        mValueEventListener = valueEventListener;

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyBookStoreActivity.this,BookStoreDetail.class);
                intent.putExtra("storeID", storeID);
                intent.putExtra("storeName", storeName);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();


        if(mValueEventListener!=null)
            mDatabaseReference.removeEventListener(mValueEventListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {

            case android.R.id.home:
                startActivity(new Intent(MyBookStoreActivity.this,PostListActivity.class));
                finish();
                break;

        }
        return true;
    }

}





