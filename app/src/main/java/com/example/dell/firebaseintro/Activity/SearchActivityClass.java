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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebaseintro.Adapter.SellerRecyclerAdapter;
import com.example.dell.firebaseintro.Adapter.StoreRecyclerAdapter;
import com.example.dell.firebaseintro.Model.Book;
import com.example.dell.firebaseintro.Model.StoreBook;
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
import java.util.Locale;


public class SearchActivityClass extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private android.support.v7.widget.Toolbar mToolbar;
    private TextView userEmail;
    private TextView userName;
    private TextView userPhone;
    private RecyclerView recyclerView;
    private SellerRecyclerAdapter recyclerViewAdapter;
    private LinearLayoutManager mManager;
    private List<Book> resultBookList;
    private int hasBookstore;
    private SearchView searchView;
    private RadioGroup radioGroup;
    private int searchFlag =1;
    private String name;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_2);
        mToggle = new ActionBarDrawerToggle(SearchActivityClass.this, drawerLayout, R.string.open, R.string.close);
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.nav_bar);
        navigationView = (NavigationView) findViewById(R.id.nav_view_2);


        View header = navigationView.getHeaderView(0);
        userEmail = (TextView) header.findViewById(R.id.user_email_id);
        userName = (TextView) header.findViewById(R.id.user_full_name);
        userPhone = (TextView) header.findViewById(R.id.phone_number);


        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        // FOR USERs INFO

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

                Toast.makeText(SearchActivityClass.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        resultBookList = new ArrayList<>();

        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerSearchResult);

        mManager = new LinearLayoutManager(SearchActivityClass.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mManager);

        navigationView.setCheckedItem(R.id.search);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radioBookstore) {
                    searchFlag = 2;
                }

                else{
                    searchFlag = 1;
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        navigationView.setCheckedItem(R.id.search);


    }


    @Override
    protected void onStop() {
        super.onStop();


        searchView.onActionViewCollapsed();
//
//        if(mvalueEventListener!=null)
//            mDatabaseReference.removeEventListener(mvalueEventListener);
//
//        if(!bookList.isEmpty())
//            recyclerViewAdapter.clearData();
//
//        bookList.clear();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.search_all, menu);
        searchView = (SearchView) menu.findItem(R.id.app_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if(!resultBookList.isEmpty())
                {
                    resultBookList.clear();
                    recyclerViewAdapter.clearData();
                }

                if(newText!=null && !TextUtils.isEmpty(newText)) {

                    newText = newText.toLowerCase(Locale.getDefault());

                    if(searchFlag ==1) {
                     searchName(newText);
                    }

                    if(searchFlag == 2)
                    {
                        searchBookstore(newText);

                    }

                    recyclerViewAdapter = new SellerRecyclerAdapter(SearchActivityClass.this, resultBookList);
                    recyclerView.setAdapter(recyclerViewAdapter);

             }

                else
                {

                    resultBookList.clear();
                }

                return true;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                searchView.onActionViewCollapsed();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void searchBookstore(final String newText)
    {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("StoreBook");

        //Maybe not single value event

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    StoreBook storeBook = ds.getValue(StoreBook.class);

                    if(storeBook.getName().toLowerCase(Locale.getDefault()).contains(newText))
                    {
                        Book book = new Book();
                        book.setName(storeBook.getName());
                        book.setPrice(storeBook.getCost());
                        book.setGenre(storeBook.getGenre());
                        book.setCondition("Brand New");
                        book.setBookid(storeBook.getId());
                        book.setAuthor(storeBook.getAuthor());

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                .child("BookStore").child(storeBook.getStoreID());

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //TODO : CHECK HERE

                                name = (dataSnapshot.child("bookStoreName").getValue(String.class));

                                phone = (dataSnapshot.child("bookStorePhone").getValue(String.class));


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        book.setFname(name);
                        book.setPhone(phone);
                        resultBookList.add(book);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void searchName(final String newText){


            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Book");

            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        Book book = ds.getValue(Book.class);

                        if(book.getName().toLowerCase(Locale.getDefault()).contains(newText))
                        {
                            resultBookList.add(book);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

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
                    startActivity(new Intent(SearchActivityClass.this,PostListActivity.class));
                    finish();
                }
                break;

            case R.id.signOut:

                if (mUser != null && mAuth != null) {
                    mAuth.signOut();
                    startActivity(new Intent(SearchActivityClass.this, MainActivity.class));
                    finish();
                }
                break;

            case R.id.add_book:

                if (mUser != null && mAuth != null) {
                    startActivity(new Intent(SearchActivityClass.this, AddBookActivity.class));

                }
                break;

            case R.id.added_books:

                if (mUser != null && mAuth != null) {

                    item.setChecked(true);
                    startActivity(new Intent(SearchActivityClass.this,AddedBooksActivity.class));
                    finish();
                    break;
                }

            case R.id.search:

                item.setChecked(true);
                break;


            case R.id.register_bookstore:


                if(hasBookstore == 0) {
                    item.setChecked(true);
                    startActivity(new Intent(SearchActivityClass.this, RegisterBookstore.class));
                }
                else
                {

                    AlertDialog.Builder alertDialogBuilder;
                    final AlertDialog dialog;
                    LayoutInflater inflater;

                    alertDialogBuilder = new AlertDialog.Builder(SearchActivityClass.this);

                    inflater = LayoutInflater.from(SearchActivityClass.this);
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
                            navigationView.setCheckedItem(R.id.search);
                        }
                    });



                }
                break;

            case R.id.my_bookstore:


                if(hasBookstore == 1){
                    startActivity(new Intent(SearchActivityClass.this,MyBookStoreActivity.class));

                }
                else
                {

                    AlertDialog.Builder alertDialogBuilder;
                    final AlertDialog dialog;
                    LayoutInflater inflater;

                    alertDialogBuilder = new AlertDialog.Builder(SearchActivityClass.this);

                    inflater = LayoutInflater.from(SearchActivityClass.this);
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
                            navigationView.setCheckedItem(R.id.search);
                        }
                    });
                }
                break;
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_2);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }


}
