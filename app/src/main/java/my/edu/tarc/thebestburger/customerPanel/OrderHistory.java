package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Adapter.OrderTrackAdapter;
import my.edu.tarc.thebestburger.R;

public class OrderHistory extends AppCompatActivity {

    private DatabaseReference db1;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewOrderList;
    private ArrayList<String> orderIDListComfirmed = new ArrayList<>();
    private TextView noHis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        setTitle("Order History");
        noHis = findViewById(R.id.textView35);

        adapter = new OrderTrackAdapter(OrderHistory.this,orderIDListComfirmed);
        recyclerViewOrderList = findViewById(R.id.RecyclerOrderHistory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderHistory.this, LinearLayoutManager.VERTICAL, false);
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
                        orderIDListComfirmed.add(orID);
                        adapter.notifyDataSetChanged();
                    }else if(uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && orStatus!="Completed" || orStatus!="completed"){
                        noHis.setText("There is no order history.");
                    }
                    if (orderIDListComfirmed.size() == 0){
                        noHis.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });
    }
}