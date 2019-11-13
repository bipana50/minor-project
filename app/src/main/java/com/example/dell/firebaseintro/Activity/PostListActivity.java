package com.example.dell.firebaseintro.Activity;

import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebaseintro.Adapter.SellerRecyclerAdapter;
import com.example.dell.firebaseintro.Model.Book;
import com.example.dell.firebaseintro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    protected FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    protected FirebaseUser mUser;
    protected SellerRecyclerAdapter recyclerViewAdapter;
    private List<Book> bookList;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle mToggle;
    protected NavigationView navigationView;
    protected Toolbar mToolbar;
    protected TextView userEmail;
    protected RecyclerView recyclerView;
    protected TextView userName;
    protected TextView userPhone;
    protected LinearLayoutManager mManager;
    private Query query;
    private ValueEventListener mValueEventListener;
    private int hasBookstore ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_1);
        mToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close);
        mToolbar = (Toolbar) findViewById(R.id.nav_bar);
        navigationView = (NavigationView) findViewById(R.id.nav_view_1);


        View header = navigationView.getHeaderView(0);
        userEmail = (TextView) header.findViewById(R.id.user_email_id);
        userName = (TextView) header.findViewById(R.id.user_full_name);
        userPhone = (TextView) header.findViewById(R.id.phone_number);


        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();



        // FOR USERs INFO

        //TODO : display pp of user

        userEmail.setText(mUser.getEmail());
        String key = mUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(key);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String fname = dataSnapshot.child("firstName").getValue(String.class);
                String lname = dataSnapshot.child("lastName").getValue(String.class);
                String phone = dataSnapshot.child("phoneNumber").getValue(String.class);

                String hasStore = dataSnapshot.child("hasBookstore").getValue(String.class);

                hasBookstore = Integer.parseInt(hasStore);

                userName.setText(fname + " " + lname);
                userPhone.setText(phone);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(PostListActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        //TODO : add shared prefs boolean value such that the books are loaded only once when the app starts; otherwise load from saved data
        //TODO : Do this (sync )only if first data in SQLite is not same as first in firebase
        //TODO : otherwise unnecessary downloading every time this activity is created and remove listener every time


        bookList = new ArrayList<>();


        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewID);

        mManager = new LinearLayoutManager(PostListActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mManager);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Book");

        mDatabaseReference.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        navigationView.setCheckedItem(R.id.home_book);



        //TODO : ADD INFINITE SCROLLER

        query = mDatabaseReference.limitToFirst(10);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {

                    Book book = ds.getValue(Book.class);

                    if(!book.getId().equals(mUser.getUid()))
                        bookList.add(book);


                }

                if(!bookList.isEmpty()) {
                    recyclerViewAdapter = new SellerRecyclerAdapter(PostListActivity.this, bookList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.notifyDataSetChanged();

                } else{

                    Toast.makeText(PostListActivity.this,"No items!",Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(PostListActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };

        query.addValueEventListener(valueEventListener);

        mValueEventListener = valueEventListener;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

       // recyclerViewAdapter.clearData();

    }

    @Override
    protected void onPause() {
        super.onPause();


        if(mValueEventListener!=null)
            query.removeEventListener(mValueEventListener);

        if(!bookList.isEmpty())
        recyclerViewAdapter.clearData();

        bookList.clear();

        }

    @Override
    protected void onStop() {
        super.onStop();



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()) {
            case android.R.id.home:

                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.home_book :

                if((mUser != null) && mAuth != null)
                {
                    item.setChecked(true);
                }
                break;

            case R.id.signOut:

                if (mUser != null && mAuth != null) {
                    mAuth.signOut();
                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();
                }
                break;

            case R.id.add_book:

                if (mUser != null && mAuth != null) {

                    startActivity(new Intent(PostListActivity.this, AddBookActivity.class));

                }
                break;

            case R.id.added_books:

                if (mUser != null && mAuth != null) {

                    item.setChecked(true);
                    startActivity(new Intent(PostListActivity.this,AddedBooksActivity.class));
                    finish();
                    break;
                }

            case R.id.search:

                item.setChecked(true);
                startActivity(new Intent(PostListActivity.this,SearchActivityClass.class));

                finish();
                break;

            case R.id.register_bookstore:


                if(hasBookstore == 0) {
                    item.setChecked(true);
                    startActivity(new Intent(PostListActivity.this, RegisterBookstore.class));
                }
                else
                {


                    AlertDialog.Builder alertDialogBuilder;
                    final AlertDialog dialog;
                    LayoutInflater inflater;

                    alertDialogBuilder = new AlertDialog.Builder(PostListActivity.this);

                    inflater = LayoutInflater.from(PostListActivity.this);
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
                            navigationView.setCheckedItem(R.id.home_book);
                        }
                    });


                }
                break;

            case R.id.my_bookstore:

                if(hasBookstore == 1){
                    item.setChecked(true);
                    startActivity(new Intent(PostListActivity.this,MyBookStoreActivity.class));

                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder;
                    final AlertDialog dialog;
                    LayoutInflater inflater;

                    alertDialogBuilder = new AlertDialog.Builder(PostListActivity.this);

                    inflater = LayoutInflater.from(PostListActivity.this);
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
                            navigationView.setCheckedItem(R.id.home_book);
                        }
                    });


                }
                break;
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_1);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
