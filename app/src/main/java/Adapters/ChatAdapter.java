package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import Utils.FirebaseUtil;
import Utils.Globals;
import models.Transfer;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Transfer> transfers;

    public ChatAdapter(ArrayList<Transfer> transfers) {
        this.transfers = transfers;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_row, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Transfer transfer = transfers.get(position);
        onQueryCompleted(holder, transfer);
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }

    private String formatTime(long timeInMillis) {
        // Format the time as needed, e.g., "10:00 AM"
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        return sdf.format(new java.util.Date(timeInMillis));
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout chatLayout;
        private TextView chatTransfer;
        private TextView chatSenderName;
        private TextView chatBalance;
        private TextView chatTime;
        private ImageView chatProfileImage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatLayout = itemView.findViewById(R.id.chat_layout);
            chatTransfer = itemView.findViewById(R.id.chat_transfer);
            chatSenderName = itemView.findViewById(R.id.chat_sender_name);
            chatBalance = itemView.findViewById(R.id.chat_balance);
            chatTime = itemView.findViewById(R.id.chat_time);
            chatProfileImage = itemView.findViewById(R.id.chat_profile_image);
        }
    }


    private synchronized void onQueryCompleted(@NonNull ChatViewHolder holder, Transfer transfer) {

        boolean isReceiver = transfer.getReceiverUID().equals(FirebaseUtil.currentUID());

        holder.chatTransfer.setText(R.string.new_transfer);
        holder.chatTransfer.append(": " + transfer.getAmount()+transfer.getCurrency());
        holder.chatSenderName.setText(transfer.getSenderFullName());
        holder.chatBalance.setText(R.string.available);
        holder.chatBalance.append(": " + Globals.getInstance().getUser().getBalance());
        holder.chatTime.setText(formatTime(transfer.getTime()));
        // Optionally set profile image if available
        if (transfer.getSenderEncodedAvatar() != null){
            holder.chatProfileImage.setImageBitmap(Globals.decodeBase64ToBitmap(transfer.getSenderEncodedAvatar()));
        }

        if (!isReceiver) { // if user is receiver of message constraint it to right side
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.chatProfileImage.getLayoutParams();
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.chatProfileImage.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) holder.chatLayout.getLayoutParams();
            params.endToEnd = holder.chatProfileImage.getId();
            holder.chatLayout.setLayoutParams(params);
        }
    }
}
