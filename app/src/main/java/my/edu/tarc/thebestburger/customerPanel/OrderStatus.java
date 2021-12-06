package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firestore.v1.StructuredQuery;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Adapter.OrderAdapter;
import my.edu.tarc.thebestburger.Adapter.OrderDetailsAdapter;
import my.edu.tarc.thebestburger.AddressBook;
import my.edu.tarc.thebestburger.CardActivity;
import my.edu.tarc.thebestburger.CustomerPanel_BottomNavigation;
import my.edu.tarc.thebestburger.Domain.OrderSumDomain;
import my.edu.tarc.thebestburger.R;

public class OrderStatus extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference db1,db2,db3,db4;
    boolean isPermissionGranted;
    private int GPS_REQUEST_CODE = 9001;
    private GoogleMap mMap;
    private TextView ID;
    private Button Complete;
    String OrderID;
    private double lat,lon;
    private ArrayList<OrderSumDomain> ordersum = new ArrayList<>();
    private ArrayList<String> cartIDList = new ArrayList<>();
    private ArrayList<String> qtyList = new ArrayList<>();
    private ArrayList<String> priceList = new ArrayList<>();
    private ArrayList<String> prodIDList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        setTitle("Order Status");
        ID = findViewById(R.id.textView32);
        Complete = findViewById(R.id.button5);

        adapter = new OrderDetailsAdapter(ordersum);
        listView = findViewById(R.id.orderdetail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);

        OrderID = getIntent().getStringExtra("OrderID");
        db1 = FirebaseDatabase.getInstance().getReference("OrderList").child("Order").child(OrderID);
        db2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Address");
        db3 = FirebaseDatabase.getInstance().getReference("OrderList").child("Order").child(OrderID).child("Order_Cart");
        db4 = FirebaseDatabase.getInstance().getReference("Product");
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String addrID = snapshot.child("AddressID").getValue().toString();
                String orStatus = snapshot.child("Order_Status").getValue().toString();
                String check = snapshot.child("feedback").getValue().toString();
                ID.setText(orStatus);
                if (orStatus.equalsIgnoreCase("delivering")){
                    Complete.setEnabled(true);
                }else if (orStatus.equalsIgnoreCase("completed") && check.equalsIgnoreCase("false")){
                    Complete.setText("Rate");
                    Complete.setEnabled(true);
                }else if (check.equalsIgnoreCase("true")){
                    Complete.setVisibility(View.INVISIBLE);
                }
                if (Complete.getText().toString().equalsIgnoreCase("Order Received")){
                    Complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db1.child("Order_Status").setValue("Completed");
                            Toast.makeText(OrderStatus.this, "Your order has been completed.", Toast.LENGTH_SHORT).show();
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrderStatus.this);
                            builder.setMessage("Please give us a feedback.");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db1.child("feedback").setValue("true");
                                    Intent i = new Intent(OrderStatus.this, FeedBack.class);
                                    i.putExtra("Order_ID",OrderID);
                                    startActivity(i);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db1.child("feedback").setValue("false");
                                    Intent a = new Intent(OrderStatus.this, CustomerPanel_BottomNavigation.class);
                                    startActivity(a);
                                }
                            });
                            android.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
                else if (Complete.getText().toString().equalsIgnoreCase("rate")){
                    Complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db1.child("feedback").setValue("true");
                            Intent i = new Intent(OrderStatus.this, FeedBack.class);
                            i.putExtra("Order_ID",OrderID);
                            startActivity(i);
                        }
                    });
                }
                db2.child(addrID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        lon = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                        LatLng Destination = new LatLng(lat, lon);
                        mMap.addMarker(new MarkerOptions().position(Destination).title("Destination"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Destination,17));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                db3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ordersum.clear();
                        cartIDList.clear();
                        qtyList.clear();
                        prodIDList.clear();
                        priceList.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            String price = snapshot1.child("Cart_RM").getValue().toString();
                            String prodid = snapshot1.child("Product_ID").getValue().toString();
                            String qty = snapshot1.child("Cart_Qty").getValue().toString();
                            String cartid = snapshot1.child("Cart_ID").getValue().toString();
                            cartIDList.add(cartid);
                            qtyList.add(qty);
                            prodIDList.add(prodid);
                            priceList.add(price);
                            db4.child(prodid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name = snapshot.child("product_Name").getValue().toString();
                                    ordersum.add(new OrderSumDomain(name,price,qty));
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {  }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {  }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        checkPermission();
        initMap();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void initMap() {
        if (isPermissionGranted) {
            if (isGPSenable()) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapView);
                mapFragment.getMapAsync(this);
            }

        }
    }

    private boolean isGPSenable() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(gps, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    private void checkPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(OrderStatus.this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                i.setData(uri);
                startActivity(i);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}