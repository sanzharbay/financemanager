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
import models.Transaction;

public class TransactionAdapter  extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    public interface OnTransactionClickListener{
        void onTransactionClick(Transaction transaction, int position);
    }
    private final OnTransactionClickListener onClickListener;
    private final LayoutInflater inflater;
    private final List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions, OnTransactionClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.transactions = transactions;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.trans_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(holder.getLayoutPosition());
        holder.iconView.setImageResource(transaction.getCategory().getImgID());
        holder.categoryView.setText(transaction.getCategory().getName());
        holder.accountView.setText(Globals.getInstance().getUser().getAccounts().get(transaction.getAccountID()).getName());
        holder.amountView.setText(String.valueOf(transaction.getAmount()));
        holder.time.setText(sdf.format(new Date(transaction.getTime())));
        holder.itemView.setOnClickListener(v -> onClickListener.onTransactionClick(transaction, holder.getLayoutPosition()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
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
