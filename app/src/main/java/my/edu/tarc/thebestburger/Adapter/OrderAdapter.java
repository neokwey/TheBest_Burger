package my.edu.tarc.thebestburger.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Domain.CartDomain;
import my.edu.tarc.thebestburger.Domain.OrderSumDomain;
import my.edu.tarc.thebestburger.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
    ArrayList<OrderSumDomain> orderSumDomains;
    public OrderAdapter(ArrayList<OrderSumDomain> orderSumDomains){
        this.orderSumDomains = orderSumDomains;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_ordersum, parent, false);
        return new OrderAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder,@SuppressLint("RecyclerView") int position) {
        holder.name.setText(orderSumDomains.get(position).getProduct_name());
        holder.price.setText("RM " + orderSumDomains.get(position).getPrice() + "0");
        holder.qty.setText(orderSumDomains.get(position).getQty() + "x");
    }

    @Override
    public int getItemCount() {
        return orderSumDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,qty,price;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.textView21);
            qty = itemView.findViewById(R.id.textView19);
            price = itemView.findViewById(R.id.textView20);
        }
    }
}
