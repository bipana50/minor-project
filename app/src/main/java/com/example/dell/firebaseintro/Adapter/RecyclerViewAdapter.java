package com.example.dell.firebaseintro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dell.firebaseintro.Activity.PostListActivity;
import com.example.dell.firebaseintro.R;

import java.util.List;

import com.example.dell.firebaseintro.Model.Book;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    protected Context context;
    private List<Book> listItems;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List listItem)
    {
        this.context = context;
        this.listItems = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ViewHolder viewHolder= null;

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
            viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, int position) {

          Book item = listItems.get(position);
          String imageurl = null;

          holder.name.setText(item.getName());
          holder.author.setText("Author : " + item.getAuthor());
          holder.genre.setText("Genre : " + item.getGenre());
          holder.price.setText("Rs. " + item.getPrice());
          holder.condition.setText("Condition : " + item.getCondition());

          imageurl = item.getImage();

          final String url = imageurl;

        Picasso.get().load(imageurl).networkPolicy(NetworkPolicy.OFFLINE).resize(120,150).into(holder.image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

                Picasso.get().load(url).resize(120,150).into(holder.image);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void clearData(){

        final int size = listItems.size();

        if(size > 0)
        {
            listItems.clear();
        }
        notifyItemRangeRemoved(0,size);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView name;
        public TextView author;
        public TextView genre;
        public TextView price;
        public TextView condition;
        public String usrID;
        public ImageView image;
        public ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.bookTitleList);
            author = (TextView) itemView.findViewById(R.id.bookAuthorList);
            genre = (TextView) itemView.findViewById(R.id.bookGenreList);
            price = (TextView) itemView.findViewById(R.id.bookPriceList);
            image = (ImageView) itemView.findViewById(R.id.bookImageList);
            condition = (TextView) itemView.findViewById(R.id.bookConditionList);
            usrID = null;
            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButtonID);

            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            Book book = listItems.get(position);

            String bookid = book.getBookid();
            String name = book.getName();
            String image = book.getImage();

            deleteBook(bookid,position,image,name);

        }

    }

    public void deleteBook(final String id, final int position, final String imageURL,final String name) {

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

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Book");


                //Deleting image from Storage

                StorageReference sRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);

                sRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                  //      Log.d("Image Delete:", "Image Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                //        Log.d("Image Delete:", "Image Not Deleted");
                    }
                });



                //Deleting book from database

                Query query = ref.orderByChild("bookid").equalTo(id);

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


                listItems.remove(position);
                notifyItemRemoved(position);

                context.startActivity(new Intent(context.getApplicationContext(),PostListActivity.class));

            }
        });

    }
}



