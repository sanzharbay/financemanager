package com.example.financemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import Utils.FirebaseUtil;
import Utils.Globals;
import library.QRGContents;
import library.QRGEncoder;

public class QRCodeActivity extends AppCompatActivity {
    private String enc = FirebaseUtil.currentUID();
    private ImageView qrCodeIV;
    private Button goHome;
    private QRGEncoder qrgEncoder;
    private Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        qrCodeIV = findViewById(R.id.qrCode);
        goHome = findViewById(R.id.goHome);

        goHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) qrCodeIV.getLayoutParams();
        params.height = params.width;//making it square
        qrCodeIV.setLayoutParams(params);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            enc += "###" + extras.getString("key");
        }
        qrgEncoder = new QRGEncoder(enc, null, QRGContents.Type.TEXT, 500);
        bitmap = qrgEncoder.getBitmap();
        bitmap = Globals.addStrokeToBitmap(bitmap, 4, R.color.black);
        qrCodeIV.setImageBitmap(bitmap);
    }
}