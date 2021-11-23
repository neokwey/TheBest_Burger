package my.edu.tarc.thebestburger.Adapter;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Domain.CartDomain;
import my.edu.tarc.thebestburger.R;
import my.edu.tarc.thebestburger.customerPanel.CustomerCartFragment;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    ArrayList<CartDomain> cartDomains;
    private DatabaseReference firebase,firebase2,firebase3;

    public CartAdapter(ArrayList<CartDomain> cartDomains){
        this.cartDomains = cartDomains;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder,@SuppressLint("RecyclerView") int position) {

        holder.num.setText(String.valueOf(cartDomains.get(position).getQty()));
        firebase3 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        firebase = FirebaseDatabase.getInstance().getReference("Product").child(cartDomains.get(position).getProduct_ID());
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(cartDomains.get(position).getCart_ID());
                Glide.with(holder.itemView.getContext()).load(snapshot.child("product_Photo").getValue().toString()).into(holder.pic);
                double price = Double.parseDouble(snapshot.child("product_Price").getValue().toString());
                holder.title.setText(snapshot.child("product_Name").getValue().toString());
                double total = price * Double.parseDouble(String.valueOf(cartDomains.get(position).getQty()));
                holder.fee.setText(format("%.2f",total));
                firebase2.child("total").setValue(String.valueOf(total));

                firebase3 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
                firebase3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            double total = 0;
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                total += Double.parseDouble(snapshot1.child("total").getValue().toString());
                            }
                            CustomerCartFragment.rm.setText(String.format("RM %.2f",total));
                        }else{
                            CustomerCartFragment.rm.setText("RM 0.00");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase = FirebaseDatabase.getInstance().getReference("Product").child(cartDomains.get(position).getProduct_ID());
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int numberOrder = Integer.parseInt(holder.num.getText().toString()) + 1;
                        holder.num.setText(String.valueOf(numberOrder));
                        double price = Double.parseDouble(snapshot.child("product_Price").getValue().toString());
                        double total = price*(double) numberOrder;
                        holder.fee.setText(format("%.2f",total));
                        firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(cartDomains.get(position).getCart_ID());
                        firebase2.child("total").setValue(String.valueOf(total));
                        firebase2.child("qty").setValue(String.valueOf(numberOrder));
                        firebase3 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
                        firebase3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    double total = 0;
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                        total += Double.parseDouble(snapshot1.child("total").getValue().toString());
                                    }
                                    CustomerCartFragment.rm.setText(String.format("RM %.2f",total));
                                }else{
                                    CustomerCartFragment.rm.setText("RM 0.00");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase = FirebaseDatabase.getInstance().getReference("Product").child(cartDomains.get(position).getProduct_ID());
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(cartDomains.get(position).getCart_ID());
                        int numberOrder = Integer.parseInt(holder.num.getText().toString()) - 1;
                        holder.num.setText(String.valueOf(numberOrder));
                        double price = Double.parseDouble(snapshot.child("product_Price").getValue().toString());
                        double total = price*(double) numberOrder;
                        holder.fee.setText(format("%.2f",total));
                        firebase2.child("total").setValue(String.valueOf(total));
                        firebase2.child("qty").setValue(String.valueOf(numberOrder));

                        if (numberOrder==0){
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.itemView.getContext());
                            alertDialog.setMessage("Are you sure want to remove this meal from cart?");
                            alertDialog.setTitle("Warning");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(cartDomains.get(position).getCart_ID());
                                    firebase2.removeValue();
                                    cartDomains.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                            alertDialog.show();
                        }
                        firebase3 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
                        firebase3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    double total = 0;
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                        total += Double.parseDouble(snapshot1.child("total").getValue().toString());
                                    }
                                    CustomerCartFragment.rm.setText(String.format("RM %.2f",total));
                                }else{
                                    CustomerCartFragment.rm.setText("RM 0.00");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(cartDomains.get(position).getCart_ID());
                firebase2.removeValue();
                cartDomains.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,fee,num;
        ImageView pic,plus,minus,del;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.title);
            fee = itemView.findViewById(R.id.fee);
            pic = itemView.findViewById(R.id.pic);
            plus = itemView.findViewById(R.id.plusBtn);
            minus = itemView.findViewById(R.id.minusBtn);
            num = itemView.findViewById(R.id.numberOrderTxt);
            del = itemView.findViewById(R.id.deleteAddress);
        }
    }
}
