package com.example.financemanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import NotificationUtils.FCMNotificationSender;
import Utils.FirebaseUtil;
import Utils.Globals;
import models.Transfer;
import models.User;

public class TransferActivity extends AppCompatActivity {
    private TextView textViewFullName;
    private EditText editTextAmount, editTextPhoneNumber;
    private Button buttonSend;
    private ImageView imageView;
    private User receiver;
    private String UID;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private int REQUEST_PERMISSION = -1;
    private TextView amount, account;
    private ActivityResultLauncher<Intent> contactsLauncher;
    private String contactID;
    private static final String prefix = "+7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        textViewFullName = findViewById(R.id.fullName);
        editTextAmount = findViewById(R.id.amount);
        editTextPhoneNumber = findViewById(R.id.phoneNumber);
        buttonSend = findViewById(R.id.createTransfer);
        imageView = findViewById(R.id.contact);
        account = findViewById(R.id.account);
        amount = findViewById(R.id.amountTV);

        account.setText(Globals.getInstance().getUser().getAccounts().get(0).getName());
        amount.setText(Globals.getInstance().getUser().getAccounts().get(0).getAmount() + "");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("receiver") == null) {
                editTextPhoneNumber.setFocusable(true);
            } else {//activity started by scanning a qrcode
                UID = extras.getString("receiver");
                String[] arr = UID.split("###");

                if (arr.length == 2) {
                    editTextAmount.setText(arr[1]);
                    editTextAmount.setFocusable(false);
                }

                UID = arr[0];
                editTextPhoneNumber.setFocusable(false);
                Query query = FirebaseUtil.allUserCollectionReference().orderByKey().equalTo(UID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            receiver = data.getValue(User.class);
                            editTextPhoneNumber.setText(receiver.getPhoneNumber());
                            textViewFullName.setText(receiver.getFullName());
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        textViewFullName.setText(error.getMessage());
                    }
                });
            }
        }

        buttonSend.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editTextAmount.getText().toString())) {
                Toast.makeText(getApplicationContext(), R.string.enter_aom, Toast.LENGTH_SHORT).show();
                return;
            }
            if (receiver == null) {
                Toast.makeText(getApplicationContext(), R.string.enter_phone_number, Toast.LENGTH_SHORT).show();
                return;
            }
            if (Objects.equals(receiver.getUID(), FirebaseUtil.currentUID())) {
                Toast.makeText(getApplicationContext(), R.string.enter_themself, Toast.LENGTH_SHORT).show();
                return;
            }
            int amount = Integer.parseInt(editTextAmount.getText().toString());
            new Transfer(Globals.getInstance().getUser(), receiver, amount);
            Toast.makeText(getApplicationContext(), R.string.pos_transfer, Toast.LENGTH_SHORT).show();

            HashMap<String, String> data = new HashMap<>();
            data.put("destiny", "chat");
            data.put("chat", "transfers");

            FCMNotificationSender fcmNotificationSender = new FCMNotificationSender(
                    receiver.getTokenMessage(), "New Transfer", "Refill: " + amount, this, new JSONObject(data)
            );
            fcmNotificationSender.sendNotification();

            finish();
        });

        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneChanged(s + "");
            }
        });

        imageView.setOnClickListener(v -> {
            REQUEST_PERMISSION = 3;
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        if (REQUEST_PERMISSION == 3){
                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            contactsLauncher.launch(intent);
                        }
                    } else {
                        Toast.makeText(this, R.string.access_denied, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        contactsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @SuppressLint("Range")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {

                                // getting contacts ID
                                Cursor cursorID = getContentResolver().query(result.getData().getData(),
                                        new String[]{ContactsContract.Contacts._ID},
                                        null, null, null);

                                if (cursorID.moveToFirst()) {
                                    contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
                                }

                                cursorID.close();

                                String phoneNumber = "";
                                Cursor cursor = getContentResolver().query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[]{contactID}, null);

                                if (cursor != null && cursor.moveToFirst()) {
                                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                    phoneNumber = cursor.getString(numberIndex);
                                    cursor.close();
                                }

                                phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
                                if (phoneNumber.startsWith("8")){
                                    phoneNumber = phoneNumber.replaceFirst("8", "+7");
                                } else if (phoneNumber.startsWith("7")){
                                    phoneNumber = phoneNumber.replaceFirst("7", "+7");
                                }



                                editTextPhoneNumber.setText(phoneNumber);
                                phoneChanged(phoneNumber);
                            }
                        }
                    }
                }
        );
    }
    private void phoneChanged(String s){
        if (!s.startsWith(prefix)) {
            editTextPhoneNumber.setText(prefix + editTextPhoneNumber.getText());
            Selection.setSelection(editTextPhoneNumber.getText(), editTextPhoneNumber.getText().length());
        }
        if (s.length() == 12) {
            Query query = FirebaseUtil.allUserCollectionReference().orderByChild("phoneNumber").equalTo(s);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        receiver = data.getValue(User.class);
                        textViewFullName.setText(receiver.getFullName());
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    textViewFullName.setText(error.getMessage());
                }
            });
            textViewFullName.setText("");
        }
    }
}
