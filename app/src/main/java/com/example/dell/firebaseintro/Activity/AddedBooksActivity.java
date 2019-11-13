package com.example.dell.firebaseintro.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebaseintro.Adapter.RecyclerViewAdapter;
import com.example.dell.firebaseintro.Model.Book;
import com.example.dell.firebaseintro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class AddedBooksActivity extends PostListActivity {

    private List<Book> myBookList;
    private ValueEventListener mvalueEventListener;
    private DatabaseReference mDatabaseReference;
    private RecyclerViewAdapter recyclerViewAdapter;
    private int hasBookstore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!

        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.content_added_books, null, false);
        drawerLayout.addView(contentView, 0);

        myBookList = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        String key = mUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(key);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String hasStore = dataSnapshot.child("hasBookstore").getValue(String.class);

                hasBookstore = Integer.parseInt(hasStore);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(AddedBooksActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Book");
        mDatabaseReference.keepSynced(true);

        getSupportActionBar().setTitle("Added Books");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        navigationView.setCheckedItem(R.id.added_books);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                Book book = ds.getValue(Book.class);

                if (book.getId().equals(mUser.getUid()))
                    myBookList.add(book);

            }

            if(!myBookList.isEmpty()) {
                recyclerViewAdapter = new RecyclerViewAdapter(AddedBooksActivity.this, myBookList);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }

            else {

                Toast.makeText(AddedBooksActivity.this, "No items!", Toast.LENGTH_LONG).show();

                startActivity(new Intent(AddedBooksActivity.this,PostListActivity.class));
                finish();

            }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        mDatabaseReference.addValueEventListener(valueEventListener);
        mvalueEventListener = valueEventListener;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.home_book :

                if (mUser != null && mAuth != null) {

                    item.setChecked(true);
                    startActivity(new Intent(AddedBooksActivity.this, PostListActivity.class));
                    finish();
                    break;
                }


            case R.id.signOut:

                if (mUser != null && mAuth != null) {
                    mAuth.signOut();
                    startActivity(new Intent(AddedBooksActivity.this, MainActivity.class));
                    finish();
                }
                break;

            case R.id.add_book:

                if (mUser != null && mAuth != null) {
                    startActivity(new Intent(AddedBooksActivity.this, AddBookActivity.class));

                }
                break;

            case R.id.added_books :

                if((mUser != null) && mAuth != null)
                {
                    item.setChecked(true);
                }
                break;



            case R.id.search:

                item.setChecked(true);
                startActivity(new Intent(AddedBooksActivity.this,SearchActivityClass.class));
               finish();
                break;


            case R.id.register_bookstore:


                if(hasBookstore == 0) {
                    item.setChecked(true);
                    startActivity(new Intent(AddedBooksActivity.this, RegisterBookstore.class));
                }
                else
                {


                    AlertDialog.Builder alertDialogBuilder;
                    final AlertDialog dialog;
                    LayoutInflater inflater;

                    alertDialogBuilder = new AlertDialog.Builder(AddedBooksActivity.this);

                    inflater = LayoutInflater.from(AddedBooksActivity.this);
                    View view = inflater.inflate(R.layout.ok_dialog, null);

                    TextView okText = (TextView) view.findViewById(R.id.okText);
                    Button okButton = (Button) view.findViewById(R.id.okButton);

                    okText.setText("You already have a Bookstore!!");

                    alertDialogBuilder.setView(view);
                    dialog = alertDialogBuilder.create();
                    dialog.show();


                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            navigationView.setCheckedItem(R.id.added_books);
                        }
                    });


                }
                break;

            case R.id.my_bookstore:

                if(hasBookstore == 1){
                    item.setChecked(true);
                    startActivity(new Intent(AddedBooksActivity.this,MyBookStoreActivity.class));

                }
                else
                {

                    AlertDialog.Builder alertDialogBuilder;
                    final AlertDialog dialog;
                    LayoutInflater inflater;

                    alertDialogBuilder = new AlertDialog.Builder(AddedBooksActivity.this);

                    inflater = LayoutInflater.from(AddedBooksActivity.this);
                    View view = inflater.inflate(R.layout.ok_dialog, null);

                    TextView okText = (TextView) view.findViewById(R.id.okText);
                    Button okButton = (Button) view.findViewById(R.id.okButton);

                    okText.setText("Bookstore not registered!!");
                    alertDialogBuilder.setView(view);
                    dialog = alertDialogBuilder.create();
                    dialog.show();


                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            navigationView.setCheckedItem(R.id.added_books);
                        }
                    });
                }
                break;

        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_1);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();


        if(mvalueEventListener!=null)
            mDatabaseReference.removeEventListener(mvalueEventListener);

        if(!myBookList.isEmpty())
        recyclerViewAdapter.clearData();

        myBookList.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
