package com.example.financemanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import Utils.FirebaseUtil;
import Utils.Globals;
import fragments.ChatFragment;
import fragments.MainFragment;
import fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private final MainFragment mainFragment = new MainFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    private final ChatFragment chatFragment = new ChatFragment();
    private ActivityResultLauncher<Intent> qrCodeLauncher;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static int REQUEST_PERMISSION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting the token of phone where push notification would be sent
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            FirebaseUtil.currentUserDetails().child("tokenMessage").setValue(task.getResult());
            Globals.getInstance().getUser().setTokenMessage(task.getResult());
        });

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this::handleNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.home);//by default

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String message = extras.getString("bottomNav");
            if (Objects.equals(message, "profile")) {
                bottomNavigationView.setSelectedItemId(R.id.profile);
            } else if (Objects.equals(message, "chat")) {
                bottomNavigationView.setSelectedItemId(R.id.chat);
            } else if (Objects.equals(message, "qrcode")) {
                bottomNavigationView.setSelectedItemId(R.id.qrcode);
            }
        }

        //launchers are up to date version of activity for result
        qrCodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String qrCodeValue = data.getStringExtra("SCAN_RESULT");
                            Intent intent = new Intent(this, TransferActivity.class);
                            intent.putExtra("receiver", qrCodeValue);
                            startActivity(intent);
                        }
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        if (REQUEST_PERMISSION == 2){
                            Intent intent = new Intent(this, com.journeyapps.barcodescanner.CaptureActivity.class);
                            qrCodeLauncher.launch(intent);
                        }
                    } else {
                        Toast.makeText(this, R.string.access_denied, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, mainFragment)
                    .commit();
            return true;
        } else if (itemId == R.id.qrcode) {
            REQUEST_PERMISSION = 2;
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return false;
        } else if (itemId == R.id.chat) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, chatFragment)
                    .commit();
            return true;
        } else if (itemId == R.id.profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, profileFragment)
                    .commit();
            return true;
        } else {
            return false;
        }
    }
}
