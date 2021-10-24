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

import java.util.Vector;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private Vector<Transaction> transactions;

    public HistoryAdapter(Context context,Vector<Transaction> transactions){
        this.context = context;
        this.transactions = transactions;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.main_history_items,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.location.setText("Location: "+transactions.get(position).getLocation().getLocker());

        if (transactions.get(position).getFinishTime().getTime() == transactions.get(position).getStartTime().getTime()){
            holder.statusImage.setImageResource(R.drawable.cancelled);
            holder.time.setText("CANCELED");
            holder.price.setText("");
        }else {
            holder.statusImage.setImageResource(R.drawable.checked);
            holder.price.setText("Price: Rp."+transactions.get(position).getPrice());

            long diff = transactions.get(position).getFinishTime().getTime() - transactions.get(position).getStartTime().getTime();

            long diffHours = diff / (60 * 60 * 1000);

            holder.time.setText("Time: " + diffHours + " Hours");
        }


    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView statusImage;
        private TextView location, time, price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            statusImage = itemView.findViewById(R.id.history_image);
            location = itemView.findViewById(R.id.history_location);
            time = itemView.findViewById(R.id.history_time);
            price = itemView.findViewById(R.id.history_price);
        }
    }
}
