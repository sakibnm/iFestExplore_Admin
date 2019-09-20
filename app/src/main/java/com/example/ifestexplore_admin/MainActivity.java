package com.example.ifestexplore_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_Root;
    static AdAdapter adAdapter;
    private static ArrayList<Ad> adArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Observe iFest");

        String userEmail = "ab@z.com";
        String userPassword = "abc123";
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth
                        .signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
            //                            Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        showActivity();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }
                            }
                        });




    }

    private void showActivity() {
        rv_Root = findViewById(R.id.rv_received_posts);
        rv_Root.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_Root.setLayoutManager(linearLayoutManager);

        adAdapter = new AdAdapter(adArrayList, this, new AdAdapter.MyClickListener() {
            @Override
            public void onApproveClicked(int position, View view) {

            }

            @Override
            public void onDeleteClicked(int position, View view) {

            }

        });
        rv_Root.setAdapter(adAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("adminCheck")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
//                    Log.e(TAG, "Failed reloading Data: ", e);
                            return;
                        }
                        ArrayList<Ad> tempAds = new ArrayList<>();
                        for (QueryDocumentSnapshot ad: queryDocumentSnapshots){
                            if (ad!=null){
                                Ad tads = new Ad(ad.getData());
//                        Log.d(TAG, "MYADS ARRAY: "+tads.toString());
                                tempAds.add(new Ad(ad.getData()));
                            }
                        }
                        adArrayList.clear();
                        adArrayList.addAll(tempAds);
                        adAdapter.setAdArrayList(adArrayList);
                        adAdapter.notifyDataSetChanged();
                    }
                });
    }
}
