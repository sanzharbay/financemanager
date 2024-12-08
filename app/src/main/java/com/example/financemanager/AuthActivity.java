package com.example.financemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;

import Utils.FirebaseUtil;
import Utils.Globals;

/**
 * This activity can be used in 2 different purposes
 * 1) by default to check pin of user
 * 2) to enter needed amount to generate qr
 */
public class AuthActivity extends AppCompatActivity {
    private ImageView i1, i2, i3, i4, profileImage;
    private AppCompatButton biometricLoginButton, backspace, showQR;
    private AppCompatImageButton signOut;
    private final ArrayList<AppCompatButton> numbers = new ArrayList<>();
    private AppCompatTextView textView, fullName, amount;
    private TextView toolbar;
    private String pinCode = "";
    private boolean isSetPIN = false, isTransfer = false;
    private Intent intent;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        intent = new Intent(getApplicationContext(), MainActivity.class);

        init();

        //check if user enabled biometrics auth
        SharedPreferences sharedPref = getSharedPreferences("Auth", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("biometrics", true)){
            biometricLoginButton.setVisibility(View.VISIBLE);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {//defining the purpose of the activity
            String message = extras.getString("activity");
            if (message !=null){
                isSetPIN = message.contains("setPIN");
                isTransfer = message.contains("transfer");
                if (isSetPIN) {
                    textView.setText(R.string.set_pin);
                    biometricLoginButton.setVisibility(View.INVISIBLE);
                }
                if (isTransfer){
                    toolbar.setText("Enter Amount");
                    textView.setVisibility(View.INVISIBLE);
                    signOut.setVisibility(View.INVISIBLE);
                    ll.setVisibility(View.INVISIBLE);
                    profileImage.setVisibility(View.INVISIBLE);
                    fullName.setVisibility(View.INVISIBLE);
                    amount.setVisibility(View.VISIBLE);
                    showQR.setVisibility(View.VISIBLE);
                }
            } else {
                isSetPIN = false;
                textView.setText(R.string.check_pin);
                biometricLoginButton.setVisibility(View.VISIBLE);
            }


            String destiny = extras.getString("destiny");
            if (Objects.equals(destiny, "toProfileCheck")){//where user would go after this activity
                intent = new Intent(getApplicationContext(), AuthActivity.class);
                intent.putExtra("destiny", "toProfileSet");
                intent.putExtra("activity", "setPIN");
            } else if (Objects.equals(destiny, "toProfileSet")){
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("bottomNav", "profile");
            } else if (Objects.equals(destiny, "chat")){
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("bottomNav", "chat");
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }
        }
    }

    private void authenticateWithBiometrics() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
            Executor executor = ContextCompat.getMainExecutor(this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(AuthActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(AuthActivity.this, R.string.auth_error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use account PIN")
                    .build();

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(this, R.string.bio_auth_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void buttonClick(int num) {//code after listener of buttons 0-9
        if (isTransfer){
            amount.append(num+"");
        } else {
            if (pinCode.length() < 4){
                pinCode+=num;
                enteredPinChanged();
            }
        }
    }

    private void enteredPinChanged(){
        switch (pinCode.length()) {
            case 4:
                i4.setImageResource(R.drawable.circle2);
                if (isSetPIN){
                    setPIN(pinCode);
                    startActivity(intent);
                    finish();
                } else {
                    if (checkPIN(pinCode)){
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.inc_pin, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 3:
                i4.setImageResource(R.drawable.circle);
                i3.setImageResource(R.drawable.circle2);
                break;
            case 2:
                i3.setImageResource(R.drawable.circle);
                i2.setImageResource(R.drawable.circle2);
                break;
            case 1:
                i2.setImageResource(R.drawable.circle);
                i1.setImageResource(R.drawable.circle2);
                break;
            default:
                i1.setImageResource(R.drawable.circle);
        }
    }

    private Boolean checkPIN(String pin) {
        try {
            return Objects.equals(Globals.getInstance().getUser().getPinHash(),
                    Globals.Hashing(pin));
        } catch(NoSuchAlgorithmException e){
            return false;
        }
    }

    private void setPIN(String pin){
        try {
            pin = Globals.Hashing(pin);
        } catch (NoSuchAlgorithmException ignored){

        }
        FirebaseUtil.currentUserDetails().child("pinHash").setValue(pin);
        Globals.getInstance().getUser().setPinHash(pin);
    }

    /**
     * Initialize all views
     */
    private void init(){
        toolbar = findViewById(R.id.main_toolbar_TV);
        showQR = findViewById(R.id.showQR);
        amount = findViewById(R.id.amount);
        ll = findViewById(R.id.ll);
        textView = findViewById(R.id.pinTextView);
        i1 = findViewById(R.id.imageview_circle1);
        i2 = findViewById(R.id.imageview_circle2);
        i3 = findViewById(R.id.imageview_circle3);
        i4 = findViewById(R.id.imageview_circle4);

        fullName = findViewById(R.id.fullName);
        profileImage = findViewById(R.id.profileImage);
        backspace = findViewById(R.id.backspace);
        biometricLoginButton = findViewById(R.id.biometricLoginButton);
        signOut = findViewById(R.id.signOut);


        // Optionally if user has avatar
        if (Globals.getInstance().getUser().getEncodedAvatar() != null){
            profileImage.setImageBitmap(Globals.decodeBase64ToBitmap(
                    Globals.getInstance().getUser().getEncodedAvatar()));
        }
        fullName.setText(Globals.getInstance().getUser().getFullName());

        backspace.setOnClickListener(v -> {
            if (isTransfer){
                if (amount.getText() != null && amount.getText().length() > 0) {
                    // Remove the last character
                    CharSequence newText = amount.getText().subSequence(0, amount.getText().length() - 1);
                    // Set the modified text back to the TextView
                    amount.setText(newText);
                }
            } else {
                pinCode = Globals.removeLastChar(pinCode);
                enteredPinChanged();
            }

        });
        biometricLoginButton.setOnClickListener(v -> authenticateWithBiometrics());
        signOut.setOnClickListener(v -> {
            FirebaseUtil.logout();
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        });
        showQR.setOnClickListener(v -> {
            Intent intent = new Intent(this, QRCodeActivity.class);
            intent.putExtra("key", amount.getText() + "");
            startActivity(intent);
            finish();
        });


        int[] buttonIds = {
                R.id.num0, R.id.num1, R.id.num2, R.id.num3,
                R.id.num4, R.id.num5, R.id.num6, R.id.num7,
                R.id.num8, R.id.num9
        };


        for (int i = 0; i < buttonIds.length; i++) {
            numbers.add(findViewById(buttonIds[i]));
            int finalI = i;
            numbers.get(i).setOnClickListener(v -> buttonClick(finalI));
        }

    }

}