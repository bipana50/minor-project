package com.example.dell.firebaseintro.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebaseintro.Activity.MyBookStoreActivity;
import com.example.dell.firebaseintro.Model.StoreBook;
import com.example.dell.firebaseintro.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoreRecyclerAdapter extends RecyclerView.Adapter<StoreRecyclerAdapter.StoreViewHolder> {

    private Context context;
    private List<StoreBook> bookList;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;



    public StoreRecyclerAdapter(Context context,List<StoreBook> bookList){

        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public StoreRecyclerAdapter.StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        StoreRecyclerAdapter.StoreViewHolder viewHolder= null;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_row, parent, false);
        viewHolder = new StoreRecyclerAdapter.StoreViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRecyclerAdapter.StoreViewHolder holder, int position) {




        StoreBook item = bookList.get(position);

        holder.name.setText(item.getName());
        holder.author.setText("Author : " + item.getAuthor());
        holder.genre.setText("Genre : " + item.getGenre());
        holder.price.setText("Rs. " + item.getCost());
        holder.pieces.setText("Pieces : " + item.getPieces());

    }

    @Override
    public int getItemCount() {        return bookList.size();}


    public void clearData(){

        final int size = bookList.size();

        if(size > 0)
        {
            bookList.clear();
        }
        notifyItemRangeRemoved(0,size);
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView author;
        public TextView genre;
        public TextView price;
        public TextView pieces;
        public ImageButton deleteBook;
        public ImageButton editBook;

        public StoreViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.bookTitleList3);
            author = (TextView) itemView.findViewById(R.id.bookAuthorList3);
            genre = (TextView) itemView.findViewById(R.id.bookGenreList3);
            price = (TextView) itemView.findViewById(R.id.bookPriceList3);
            pieces = (TextView) itemView.findViewById(R.id.bookPieceList3);
            deleteBook = (ImageButton) itemView.findViewById(R.id.deleteStoreBook);
            editBook = (ImageButton) itemView.findViewById(R.id.editStoreBook);

            editBook.setOnClickListener(this);
            deleteBook.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            int position = getAdapterPosition();
            StoreBook book = bookList.get(position);

            String bookid = book.getId().trim();
            String name = book.getName().trim();
            String pieces = book.getPieces().trim();
            String cost = book.getCost().trim();

            switch (v.getId())
            {

                case R.id.deleteStoreBook:

                    deleteBook(bookid,position,name);
                    break;

                case R.id.editStoreBook :

                    editBookFunc(bookid,name,pieces,cost);

                    break;
            }

        }

        private void editBookFunc(final String bookid,final String name, String pieces, String cost) {


            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.edit_book_store, null);

            Button editButton = (Button) view.findViewById(R.id.d_edit_button);
            Button cancelButton = (Button) view.findViewById(R.id.d_dismiss_button);
            final TextView bookName = (TextView) view.findViewById(R.id.d_name);
            final EditText bookPieces = (EditText) view.findViewById(R.id.d_edit_pieces);
            final EditText bookCost = (EditText) view.findViewById(R.id.d_edit_price);

            bookName.setText(name);
            bookPieces.setText(pieces);
            bookCost.setText(cost);


            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    String bsCost = bookPieces.getText().toString().trim();
                    String bsPiece = bookCost.getText().toString().trim();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("StoreBook").child(bookid);

                    databaseReference.child("cost").setValue(bsCost);
                    databaseReference.child("pieces").setValue(bsPiece);

                    Toast.makeText(context, "Book Edited",Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context.getApplicationContext(), MyBookStoreActivity.class));

                    ((Activity)context).finish();
                }
            });



        }


        private void deleteBook(final String bookid,final int position,final String name) {

            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirm_dialog, null);

            Button noButton = (Button) view.findViewById(R.id.noButton);
            Button yesButton = (Button) view.findViewById(R.id.yesButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();


            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    dialog.dismiss();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("StoreBook");


                    //Deleting book from database

                    Query query = ref.orderByChild("id").equalTo(bookid);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot book:dataSnapshot.getChildren())
                            {
                                book.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                        Toast.makeText(context ,  name + " Deleted !",Toast.LENGTH_SHORT).show();



                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Toast.makeText(context,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });


                    bookList.remove(position);
                    notifyItemRemoved(position);

                    context.startActivity(new Intent(context.getApplicationContext(), MyBookStoreActivity.class));

                    ((Activity)context).finish();
                }
            });

        }
    }

}
