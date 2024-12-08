package com.example.financemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import Utils.FirebaseUtil;
import Utils.Globals;
import models.User;

public class RegisterByPhoneActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextFullName, editTextPassword, editTextName;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView loginNow;

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseUtil.isLoggedIn()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_phone);

        init();

        loginNow.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String phoneNumber, fullName, password, username;
            phoneNumber = String.valueOf(editTextEmail.getText());
            fullName = String.valueOf(editTextFullName.getText());
            password = String.valueOf(editTextPassword.getText());
            username = String.valueOf(editTextName.getText());


            //check if user write phone number with country code of Kazakhstan
            if (phoneNumber.startsWith("+77")){
                phoneNumber = phoneNumber.substring(2);
                if (!Globals.getInstance().isPhoneNumber(phoneNumber)){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterByPhoneActivity.this, R.string.enter_phone_number, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (phoneNumber.startsWith("87")){
                phoneNumber = phoneNumber.substring(1);
                if (!Globals.getInstance().isPhoneNumber(phoneNumber)){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterByPhoneActivity.this, R.string.enter_phone_number, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterByPhoneActivity.this, R.string.enter_phone_number, Toast.LENGTH_SHORT).show();
                return;
            }


            if (TextUtils.isEmpty(username)){
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterByPhoneActivity.this, R.string.enter_username, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(fullName)){
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterByPhoneActivity.this, R.string.enter_full_name, Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)){
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterByPhoneActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                return;
            }



            String finalPhoneNumber = phoneNumber + "@finance-manager.kz";//requirements of mAuth
            String finalPhoneNumber1 = phoneNumber;
            mAuth.createUserWithEmailAndPassword(finalPhoneNumber, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterByPhoneActivity.this, R.string.auth_app, Toast.LENGTH_SHORT).show();

                    User newUser = new User(finalPhoneNumber1, username, Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), fullName);
                    Globals.getInstance().setUser(newUser);
                    FirebaseUtil.allUserCollectionReference().child(mAuth.getCurrentUser().getUid()).setValue(newUser);

                    Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                    intent.putExtra("activity", "setPIN");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterByPhoneActivity.this, R.string.auth_fail, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    /**
     * initialize all views
     */
    private void init(){
        mAuth= FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextFullName = findViewById(R.id.fullName);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.username);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        loginNow = findViewById(R.id.loginNow);
    }
}