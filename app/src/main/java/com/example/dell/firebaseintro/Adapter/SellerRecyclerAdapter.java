package com.example.dell.firebaseintro.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.firebaseintro.Model.Book;
import com.example.dell.firebaseintro.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SellerRecyclerAdapter extends RecyclerView.Adapter<SellerRecyclerAdapter.SellerViewHolder> {

   private Context context;
   private List<Book> bookList;


   public SellerRecyclerAdapter(Context context,List<Book> bookList){

       this.context = context;
       this.bookList = bookList;
   }

    @NonNull
    @Override
    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SellerRecyclerAdapter.SellerViewHolder viewHolder= null;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_row, parent, false);
        viewHolder = new SellerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SellerViewHolder holder, int position) {

        Book item = bookList.get(position);
        String imageurl = null;

        holder.name.setText(item.getName());
        holder.author.setText("Author : " + item.getAuthor());
        holder.genre.setText("Genre : " + item.getGenre());
        holder.price.setText("Rs. " + item.getPrice());
        holder.condition.setText("Condition : " + item.getCondition());
        holder.sellerInfo.setText("Seller : " + item.getFname() + ", " + item.getPhone());

        imageurl = item.getImage();

        final String url = imageurl;
        //Picasso.get().load(imageurl).into(holder.image);

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
        return bookList.size();
    }


    public void clearData(){

        final int size = bookList.size();

        if(size > 0)
        {
            bookList.clear();
        }
        notifyItemRangeRemoved(0,size);
    }

    public class SellerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView author;
        public TextView genre;
        public TextView price;
        public TextView condition;
        public String usrID;
        public ImageView image;
        public TextView sellerInfo;

        public SellerViewHolder(View itemView) {
            super(itemView);

            sellerInfo = (TextView) itemView.findViewById(R.id.sellerDetail2);
            name = (TextView) itemView.findViewById(R.id.bookTitleList2);
            author = (TextView) itemView.findViewById(R.id.bookAuthorList2);
            genre = (TextView) itemView.findViewById(R.id.bookGenreList2);
            price = (TextView) itemView.findViewById(R.id.bookPriceList2);
            image = (ImageView) itemView.findViewById(R.id.bookImageList);
            condition = (TextView) itemView.findViewById(R.id.bookConditionList2);
            usrID = null;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            //Book item = listItems.getposition);

            //Intent intent = new Intent(context, DetailsActivity.class);
            //intent.putExtra("name",item.getName());
            //intent.putExtra("description", item.getDescription());
            //intent.putExtra("rating", item.getRating());

            //context.startActivity(intent);

            Toast.makeText(context,"" + position + " touched",Toast.LENGTH_SHORT).show();
        }

   }
}
