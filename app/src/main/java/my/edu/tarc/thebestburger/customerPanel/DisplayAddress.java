package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Adapter.AddressAdapter;
import my.edu.tarc.thebestburger.AddressBook;
import my.edu.tarc.thebestburger.Domain.AddressDomain;
import my.edu.tarc.thebestburger.R;

public class DisplayAddress extends AppCompatActivity {

    FloatingActionButton fab;
    private ArrayList<AddressDomain> addrList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewAddrList;
    private DatabaseReference db1,db2,db3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_address);
        setTitle("My Addresses");

        adapter = new AddressAdapter(addrList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DisplayAddress.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewAddrList = findViewById(R.id.addrRecycler);
        recyclerViewAddrList.setLayoutManager(linearLayoutManager);
        recyclerViewAddrList.setAdapter(adapter);
        fab = findViewById(R.id.fabAdd);

        db1 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Address");
        db2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addrList.clear();
                final int count = (int) snapshot.getChildrenCount();

                for(int i = 1; i < count; i++){
                    final String addrid = "Address" + i;
                    db1.child(addrid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String addrID = snapshot.child("AddressID").getValue().toString();
                            String addr1 = snapshot.child("Address").getValue().toString();
                            String postcode = String.valueOf(snapshot.child("Postcode").getValue());
                            String city = snapshot.child("City").getValue().toString();
                            String state = snapshot.child("State").getValue().toString();
                            String lat = snapshot.child("latitude").getValue().toString();
                            String lon = snapshot.child("longitude").getValue().toString();
                            String addr2 = postcode+", "+city+", "+state+".";
                            db2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name = snapshot.child("Name").getValue().toString();
                                    String phone = "+60" + snapshot.child("Phone Number").getValue().toString();
                                    addrList.add(new AddressDomain(addrID,addr1,addr2,lat,lon,name,phone));
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {  }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {  }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DisplayAddress.this, AddressBook.class);
                startActivity(i);
            }
        });
    }
}