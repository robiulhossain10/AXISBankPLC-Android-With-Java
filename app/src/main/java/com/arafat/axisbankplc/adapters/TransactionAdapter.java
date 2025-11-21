package com.arafat.axisbankplc.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arafat.axisbankplc.R;
import com.arafat.axisbankplc.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction tx = transactionList.get(position);

        holder.txtType.setText(tx.getType());
        holder.txtAmount.setText("৳ " + tx.getAmount());

        // Format date
        String formattedDate = formatDate(tx.getDate());
        holder.txtDate.setText(formattedDate);

        if (tx.getRecipientAccount() != null && !tx.getRecipientAccount().isEmpty()) {
            holder.txtRecipient.setText("To: " + tx.getRecipientAccount());
        } else {
            holder.txtRecipient.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView txtType, txtAmount, txtDate, txtRecipient;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            txtType = itemView.findViewById(R.id.txtType);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtRecipient = itemView.findViewById(R.id.txtRecipient);
        }
    }

    private String formatDate(String millisStr) {
        try {
            long millis = Long.parseLong(millisStr);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            return sdf.format(new Date(millis));
        } catch (Exception e) {
            return millisStr;
        }
    }
}
