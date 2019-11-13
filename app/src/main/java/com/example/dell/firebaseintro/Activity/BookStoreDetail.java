package com.example.dell.firebaseintro.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dell.firebaseintro.Adapter.StoreRecyclerAdapter;
import com.example.dell.firebaseintro.Model.StoreBook;
import com.example.dell.firebaseintro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BookStoreDetail extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView itemCount;
    private TextView bookCount;
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private DatabaseReference mDatabaseReference;
    private StoreRecyclerAdapter recyclerViewAdapter;
    private StoreRecyclerAdapter searchRecyclerAdapter;
    private List<StoreBook> bookList;
    private List<StoreBook> searchList;
    private ValueEventListener mvalueEventListener;
    private SearchView searchView;
    private String storeName;
    private String storeID;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_store_detail);


        mToolbar = (Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(mToolbar);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {

            storeID = bundle.getString("storeID");
            storeName = bundle.getString("storeName");
            latitude = bundle.getString("latitude");
            longitude = bundle.getString("longitude");
        }


        getSupportActionBar().setTitle(storeName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        bookList = new ArrayList<>();
        searchList = new ArrayList<>();


        recyclerView = (RecyclerView)findViewById(R.id.bookstore_detail_recycler);
        bookCount = (TextView) findViewById(R.id.book_count);
        itemCount = (TextView) findViewById(R.id.item_count);


        mManager = new LinearLayoutManager(BookStoreDetail.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mManager);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("StoreBook");

        mDatabaseReference.keepSynced(true);


    }

    @Override
    protected void onStart() {
        super.onStart();


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    StoreBook book = ds.getValue(StoreBook.class);

                    if (book.getStoreID().equals(storeID))
                        bookList.add(book);

                }

                if(!bookList.isEmpty()) {
                    recyclerViewAdapter = new StoreRecyclerAdapter(BookStoreDetail.this, bookList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.notifyDataSetChanged();

                    int size = bookList.size();

                    itemCount.setText("Items : "+ String.valueOf(size));

                    int numberOfPieces = 0;

                    for(StoreBook book : bookList)
                    {
                        numberOfPieces += Integer.parseInt(book.getPieces());
                    }

                    bookCount.setText("Total Pieces : "+String.valueOf(numberOfPieces));
                }

                else {

                     Toast.makeText(BookStoreDetail.this, "No items!", Toast.LENGTH_LONG).show();

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!searchList.isEmpty())
                {
                    searchList.clear();
                    searchRecyclerAdapter.clearData();
                }

                if(newText!=null && !TextUtils.isEmpty(newText)) {
                    newText = newText.toLowerCase(Locale.getDefault());

                    for (StoreBook book : bookList)
                    {
                        if(book.getName().toLowerCase(Locale.getDefault()).contains(newText))
                        {
                            searchList.add(book);
                        }
                    }

                }
                else
                {
                    searchList.clear();
                }

                searchRecyclerAdapter = new StoreRecyclerAdapter(BookStoreDetail.this, searchList);
                recyclerView.setAdapter(searchRecyclerAdapter);
                // searchRecyclerAdapter.notifyDataSetChanged();

                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                searchView.onActionViewCollapsed();
                recyclerView.setAdapter(recyclerViewAdapter);
                //recyclerViewAdapter.notifyDataSetChanged();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            //case R.id.app_bar_search:

                //Search

              //  break;


            case android.R.id.home:

                startActivity(new Intent(BookStoreDetail.this,MyBookStoreActivity.class));
                finish();
                break;

            case R.id.add_book_bookstore:

                //ADD BOOK

                Intent intent = new Intent(BookStoreDetail.this,AddBookFromStore.class);
                intent.putExtra("storeID",storeID);
                intent.putExtra("storeName",storeName);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPause() {
        super.onPause();

        searchView.onActionViewCollapsed();

        if(mvalueEventListener!=null)
            mDatabaseReference.removeEventListener(mvalueEventListener);

        if(!bookList.isEmpty())
            recyclerViewAdapter.clearData();

        bookList.clear();

    }
}
