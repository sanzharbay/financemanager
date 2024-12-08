package com.example.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Utils.FirebaseUtil;
import Utils.Globals;
import models.Transfer;
import models.User;

public class OpenActivity extends AppCompatActivity {

    private int completedQueries = 0;
    private ArrayList<Transfer> transfers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);


        if (!FirebaseUtil.isLoggedIn()){
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(() -> {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                scheduler.shutdown();
            }, 100, TimeUnit.MILLISECONDS);//take a short time out so as not to be instant

        } else {
            FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Globals.getInstance().setUser(task.getResult().getValue(User.class));

                    loadTransfers();
                } else {
                    Toast.makeText(OpenActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**
     * loads and sets transfers of current user
     * then starts auth activity by the end of queries
     */
    private void loadTransfers() {
        DatabaseReference transferRef = FirebaseUtil.allTransferCollectionReference();
        transferRef.orderByChild("senderUID").equalTo(FirebaseUtil.currentUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Transfer t = data.getValue(Transfer.class);
                    transfers.add(t);
                }
                onQueryCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        transferRef.orderByChild("receiverUID").equalTo(FirebaseUtil.currentUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Transfer t = data.getValue(Transfer.class);
                    transfers.add(t);
                }
                onQueryCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private synchronized void onQueryCompleted() {
        completedQueries++;
        if (completedQueries >= 2) { // Both queries have completed
            Globals.getInstance().setTransfers(transfers);
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);

            Bundle extras = getIntent().getExtras();
            if (extras != null){
                intent.putExtras(extras);
            }

            startActivity(intent);
        }
    }
}