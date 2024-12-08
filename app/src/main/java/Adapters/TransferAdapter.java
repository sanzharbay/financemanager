package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Utils.Globals;
import models.Transfer;

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.ViewHolder>{
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault());

    public interface OnTransferClickListener{
        void onTransferClick(Transfer transfer, int position);
    }
    private final OnTransferClickListener onClickListener;
    private final LayoutInflater inflater;
    private final List<Transfer> transfers;

    public TransferAdapter(Context context, List<Transfer> transfers, OnTransferClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.transfers = transfers;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public TransferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.trans_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransferAdapter.ViewHolder holder, int position) {
        Transfer transfer = transfers.get(holder.getLayoutPosition());

        if (transfer.getSenderEncodedAvatar() != null){ // user has avatar
            holder.iconView.setImageBitmap(Globals.decodeBase64ToBitmap(transfer.getSenderEncodedAvatar()));
        } else {
            holder.iconView.setImageResource(R.drawable.contact_icon);
        }
        holder.categoryView.setText(transfer.getSenderFullName());
        holder.accountView.setText(Globals.getInstance().getUser().getFullName());
        holder.amountView.setText(String.valueOf(transfer.getAmount()));
        holder.time.setText(sdf.format(new Date(transfer.getTime())));

        holder.itemView.setOnClickListener(v -> onClickListener.onTransferClick(transfer, holder.getLayoutPosition()));
    }

    @Override
    public int getItemCount() {
        return transfers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView categoryView, accountView, amountView, time;
        ViewHolder(View view){
            super(view);
            iconView = view.findViewById(R.id.categoryimg);
            categoryView = view.findViewById(R.id.category);
            accountView = view.findViewById(R.id.account);
            amountView = view.findViewById(R.id.amount);
            time = view.findViewById(R.id.time);
        }
    }
}
