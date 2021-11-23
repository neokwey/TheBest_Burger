package my.edu.tarc.thebestburger.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.R;
import my.edu.tarc.thebestburger.customerPanel.CustomerTrackFragment;
import my.edu.tarc.thebestburger.customerPanel.OrderStatus;

public class OrderTrackAdapter extends RecyclerView.Adapter<OrderTrackAdapter.ViewHolder> {
    ArrayList<String> OrderIDList;
    private Activity mcontext;

    public OrderTrackAdapter(Activity context, ArrayList<String> OrderIDList){
        this.mcontext = context;
        this.OrderIDList = OrderIDList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_orderstatus, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final String orderID = OrderIDList.get(position);
        holder.orderID.setText(orderID);
        holder.orderIDlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mcontext, OrderStatus.class);
                i.putExtra("OrderID",orderID);
                mcontext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return OrderIDList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView orderID;
        ConstraintLayout orderIDlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.orderID);
            orderIDlay = itemView.findViewById(R.id.orderIDlay);
        }
    }
}
