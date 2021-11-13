package my.edu.tarc.thebestburger.customerPanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Adapter.CartAdapter;
import my.edu.tarc.thebestburger.Adapter.ProductAdapter;
import my.edu.tarc.thebestburger.Domain.*;
import my.edu.tarc.thebestburger.*;

public class CustomerCartFragment extends Fragment {
    private ArrayList<CartDomain> cartList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewCartList;
    private DatabaseReference firebase,firebase2,firebase3;
    public static TextView rm;
    private Button checkout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Cart");
        View v = inflater.inflate(R.layout.fragment_customercart,null);

        adapter = new CartAdapter(cartList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewCartList = v.findViewById(R.id.recyclerView);
        recyclerViewCartList.setLayoutManager(linearLayoutManager);
        recyclerViewCartList.setAdapter(adapter);
        rm = v.findViewById(R.id.textViewRM);
        checkout = v.findViewById(R.id.button);


        firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        firebase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    rm.setText("RM 0.00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebase = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String cartid = snapshot1.child("cart_ID").getValue().toString();
                    String prodid = snapshot1.child("product_ID").getValue().toString();
                    String qty = snapshot1.child("qty").getValue().toString();
                    cartList.add(new CartDomain(cartid,prodid,qty));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Checkout.class);
                i.putExtra("totalpay",rm.getText().toString());
                startActivity(i);
            }
        });

        return v;
    }
}
