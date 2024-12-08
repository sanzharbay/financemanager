package models;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import Utils.FirebaseUtil;
import Utils.Globals;

public class Transfer {
    private String senderUID, receiverUID, currency="KZT";

    private long time;
    private String senderEncodedAvatar, senderFullName;



    private int amount;


    public Transfer(User sender, User receiver, int aom) {
        this.senderUID = sender.getUID();
        this.receiverUID = receiver.getUID();
        this.amount = aom;
        senderFullName = sender.getFullName();
        time = Calendar.getInstance().getTimeInMillis();

        if (sender.getEncodedAvatar() != null){
            Bitmap bitmap = Globals.decodeBase64ToBitmap(sender.getEncodedAvatar());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            senderEncodedAvatar = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }



        sender.newTransfer(this);
        receiver.newNegTransfer(this);
        FirebaseUtil.allTransferCollectionReference().push().setValue(this);

        Globals.getInstance().getTransfers().add(this);
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getCurrency() {
        return currency;
    }


    public int getAmount() {
        return amount;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public long getTime() {
        return time;
    }

    public String getSenderEncodedAvatar() {
        return senderEncodedAvatar;
    }

    public String getSenderFullName() {
        return senderFullName == null ? "N/A" : senderFullName;
    }

    public Transfer() {
    }
}
