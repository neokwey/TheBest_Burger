package my.edu.tarc.thebestburger.customerPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Adapter.OrderTrackAdapter;
import my.edu.tarc.thebestburger.Adapter.PaymentAddressAdapter;
import my.edu.tarc.thebestburger.Checkout;
import my.edu.tarc.thebestburger.R;

public class CustomerTrackFragment extends Fragment {

    private DatabaseReference db1;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewOrderList;
    private ArrayList<String> orderIDListComfirmed = new ArrayList<>();
    TextView txtinfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Track");
        View v = inflater.inflate(R.layout.fragment_customertrack,null);

        txtinfo = v.findViewById(R.id.textView36);
        adapter = new OrderTrackAdapter(getActivity(),orderIDListComfirmed);
        recyclerViewOrderList = v.findViewById(R.id.RecyclerOrderTrack);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOrderList.setLayoutManager(linearLayoutManager);
        recyclerViewOrderList.setAdapter(adapter);

        db1 = FirebaseDatabase.getInstance().getReference("OrderList");
        db1.child("Order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderIDListComfirmed.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String orID = snapshot1.child("Order_ID").getValue().toString();
                    String uid = snapshot1.child("Uid").getValue().toString();
                    String orStatus = snapshot1.child("Order_Status").getValue().toString();
                    if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && orStatus.equalsIgnoreCase("completed") || orStatus.equalsIgnoreCase("rejected")){
                        txtinfo.setText("There is no order to track");
                        txtinfo.setVisibility(View.VISIBLE);
                    }else{
                        txtinfo.setVisibility(View.GONE);
                        orderIDListComfirmed.add(orID);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        return v;
    }
}
