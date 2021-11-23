package my.edu.tarc.thebestburger.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Domain.OrderSumDomain;
import my.edu.tarc.thebestburger.R;

public class OrderDetailsAdapter  extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>{
    ArrayList<OrderSumDomain> orderSumDomains;
    public OrderDetailsAdapter(ArrayList<OrderSumDomain> orderSumDomains){
        this.orderSumDomains = orderSumDomains;
    }
    @NonNull
    @Override
    public OrderDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_ordersum, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.ViewHolder holder, int position) {
        holder.name.setText(orderSumDomains.get(position).getProduct_name());
        holder.qty.setText("x" + orderSumDomains.get(position).getQty());
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
            qty = itemView.findViewById(R.id.textView20);
            price = itemView.findViewById(R.id.textView19);
        }
    }
}
