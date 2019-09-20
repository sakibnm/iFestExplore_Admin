package com.example.ifestexplore_admin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdHolder> {

    private static final String TAG = "demo";
    private ArrayList<Ad> adArrayList;
        private Context mContext;
    public MyClickListener myClickListener;
    FirebaseFirestore db;
    FirebaseUser user;

    public AdAdapter(ArrayList<Ad> adArrayList, Context mContext, MyClickListener myClickListener) {
        this.adArrayList = adArrayList;

        this.mContext = mContext;
        this.myClickListener = myClickListener;


    }

    public void clear(){
        adArrayList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Ad> adsList){
        adArrayList.addAll(adsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.received_ad_cell, parent, false);
        db = FirebaseFirestore.getInstance();
        AdAdapter.AdHolder holder = new AdAdapter.AdHolder(view, new MyClickListener() {

            @Override
            public void onApproveClicked(int position, View view) {
                final Ad adClicked = adArrayList.get(position);
                final String clickedAdSerial = adClicked.getAdSerialNo();

                db.collection("adminCheck").document(clickedAdSerial).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("adsRepo").document(clickedAdSerial)
                                        .set(adClicked)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext, "Approved!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Could not Approve! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDeleteClicked(int position, View view) {
                final Ad adClicked = adArrayList.get(position);
                final String clickedAdSerial = adClicked.getAdSerialNo();
                db.collection("adminCheck").document(clickedAdSerial).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Not Approved!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Try again!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdHolder holder, int position) {
        Ad ad = adArrayList.get(position);

        holder.tv_Title.setText(ad.getTitle());
        holder.tv_Comment.setText(ad.getComment());
        Picasso.get().load(ad.getItemPhotoURL()).into(holder.iv_Content);

    }



    @Override
    public int getItemCount() {
        return adArrayList == null? 0: adArrayList.size();
    }

    public void setAdArrayList(ArrayList<Ad> adArrayList) {
        this.adArrayList = adArrayList;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }


    public class AdHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private TextView tv_rec_creator;

        private ImageView iv_Content;
        private TextView tv_Title;
        private TextView tv_Comment;
        private Button button_Approve;
        private Button button_Remove;

        private MyClickListener myClickListener;


        public AdHolder(@NonNull View itemView, MyClickListener myClickListener) {
            super(itemView);
            iv_Content = itemView.findViewById(R.id.iv_contentPhoto);
            tv_Title = itemView.findViewById(R.id.tv_title);
            tv_Comment  = itemView.findViewById(R.id.tv_comment);
            button_Approve = itemView.findViewById(R.id.button_Approve);
            button_Remove = itemView.findViewById(R.id.button_Delete);
            this.myClickListener = myClickListener;

            button_Remove.setOnClickListener(this);
            button_Approve.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_Approve:
                    myClickListener.onApproveClicked(this.getLayoutPosition(), view);
                    break;
                case R.id.button_Delete:
                    myClickListener.onDeleteClicked(this.getLayoutPosition(), view);
                    break;
                default:
                    break;

            }
        }
    }


    public interface MyClickListener{
        void onApproveClicked(int position, View view);
        void onDeleteClicked(int position, View view);
    }
}
