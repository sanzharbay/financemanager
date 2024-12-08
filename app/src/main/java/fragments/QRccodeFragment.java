package fragments;

import Utils.FirebaseUtil;
import library.QRGContents;
import library.QRGEncoder;
import Utils.Globals;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financemanager.R;
import com.example.financemanager.TransferActivity;

public class QRccodeFragment extends Fragment {
    private ImageView qrCodeIV;

    private EditText dataEdt;
    private Button generateQrBtn;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private String enc = FirebaseUtil.currentUID();



    public QRccodeFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        qrCodeIV = view.findViewById(R.id.idIVQrcode);
        dataEdt = view.findViewById(R.id.idEdt);
        generateQrBtn = view.findViewById(R.id.idBtnGenerateQR);

        qrgEncoder = new QRGEncoder(enc, null, QRGContents.Type.TEXT, 500);
        bitmap = qrgEncoder.getBitmap();
        qrCodeIV.setImageBitmap(bitmap);


        generateQrBtn.setOnClickListener(v -> {
            enc = null;
            if (TextUtils.isEmpty(dataEdt.getText().toString())) {

            } else {
                enc+=dataEdt.getText().toString();
            }
            qrgEncoder = new QRGEncoder(enc, null, QRGContents.Type.TEXT, 500);
            bitmap = qrgEncoder.getBitmap();
            qrCodeIV.setImageBitmap(bitmap);
        });



    }
}
