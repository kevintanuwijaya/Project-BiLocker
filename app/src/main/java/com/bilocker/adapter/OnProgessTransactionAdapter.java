package com.bilocker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bilocker.R;
import com.bilocker.model.Transaction;
import com.bilocker.utils.Convert;

import java.util.Vector;

public class OnProgessTransactionAdapter extends RecyclerView.Adapter<OnProgessTransactionAdapter.MyViewHolder> {

    private Context context;
    private Vector<Transaction> transactions;

    public OnProgessTransactionAdapter(Context context, Vector<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @Override
    public OnProgessTransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.main_progress_transaction,parent,false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.location.setText("Location: "+transactions.get(position).getLocation().getLocker());

        long diff = transactions.get(position).getFinishTime().getTime() - transactions.get(position).getStartTime().getTime();

        int diffHours = (int) (diff / (60 * 60 * 1000) + 1);

        holder.time.setText("Time: " + diffHours + " Hours");

        transactions.get(position).setPrice(diffHours*1000);

        holder.price.setText("Est Price: Rp."+ transactions.get(position).getPrice());

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView statusTransaction;
        TextView location, time, price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            statusTransaction = itemView.findViewById(R.id.on_progress_image);
            location = itemView.findViewById(R.id.on_progress_location);
            time = itemView.findViewById(R.id.on_progress_time);
            price = itemView.findViewById(R.id.on_progress_price);
        }
    }
}
