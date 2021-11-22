package my.edu.tarc.thebestburger.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Domain.AddressDomain;
import my.edu.tarc.thebestburger.R;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    ArrayList<AddressDomain> addressDomains;
    private DatabaseReference db1,db2;

    public AddressAdapter(ArrayList<AddressDomain> addressDomains){ this.addressDomains = addressDomains; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_address, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,@SuppressLint("RecyclerView") int position) {
        holder.name.setText(addressDomains.get(position).getName());
        holder.phone.setText(addressDomains.get(position).getPhone());
        holder.address1.setText(addressDomains.get(position).getAddr1());
        holder.address2.setText(addressDomains.get(position).getAddr2());
        holder.latlon.setText("Latitude : " + addressDomains.get(position).getLan()+",\nLongitude : " + addressDomains.get(position).getLon());
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db1 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Address").child(addressDomains.get(position).getAddrID());
                db1.removeValue();
                addressDomains.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() { return addressDomains.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,phone,address1,address2,latlon;
        ImageView del;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name1);
            phone = itemView.findViewById(R.id.phonenum1);
            address1 = itemView.findViewById(R.id.address11);
            address2 = itemView.findViewById(R.id.address21);
            latlon = itemView.findViewById(R.id.address3);
            del = itemView.findViewById(R.id.deleteAddress);
        }
    }
}
