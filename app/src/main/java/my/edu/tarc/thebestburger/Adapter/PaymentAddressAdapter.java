package my.edu.tarc.thebestburger.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Checkout;
import my.edu.tarc.thebestburger.Domain.AddressDomain;
import my.edu.tarc.thebestburger.R;

public class PaymentAddressAdapter extends RecyclerView.Adapter<PaymentAddressAdapter.ViewHolder> {
    ArrayList<AddressDomain> payaddressDomains;

    public PaymentAddressAdapter(ArrayList<AddressDomain> payaddressDomains){ this.payaddressDomains = payaddressDomains; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_paymentaddress, parent, false);
        return new PaymentAddressAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(payaddressDomains.get(position).getName());
        holder.phone.setText(payaddressDomains.get(position).getPhone());
        holder.address1.setText(payaddressDomains.get(position).getAddr1());
        holder.address2.setText(payaddressDomains.get(position).getAddr2());
        holder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Checkout.address.setText(payaddressDomains.get(position).getAddr1()+"\n"+payaddressDomains.get(position).getAddr2());
                Checkout.layoutaddr.setVisibility(View.GONE);
                Checkout.addrID.setText(payaddressDomains.get(position).getAddrID());
            }
        });
    }

    @Override
    public int getItemCount() { return payaddressDomains.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,phone,address1,address2;
        ConstraintLayout lay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name1);
            phone = itemView.findViewById(R.id.phonenum1);
            address1 = itemView.findViewById(R.id.address11);
            address2 = itemView.findViewById(R.id.address21);
            lay = itemView.findViewById(R.id.addrlay);
        }
    }
}
