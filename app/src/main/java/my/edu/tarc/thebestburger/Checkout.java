package my.edu.tarc.thebestburger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.edu.tarc.thebestburger.Adapter.AddressAdapter;
import my.edu.tarc.thebestburger.Adapter.OrderAdapter;
import my.edu.tarc.thebestburger.Adapter.PaymentAddressAdapter;
import my.edu.tarc.thebestburger.Domain.AddressDomain;
import my.edu.tarc.thebestburger.Domain.Cart2Domain;
import my.edu.tarc.thebestburger.Domain.OrderSumDomain;
import my.edu.tarc.thebestburger.customerPanel.CustomerCartFragment;
import my.edu.tarc.thebestburger.customerPanel.CustomerHomeFragment;
import my.edu.tarc.thebestburger.customerPanel.CustomerTrackFragment;
import my.edu.tarc.thebestburger.customerPanel.DisplayAddress;

public class Checkout extends AppCompatActivity {
    private DatabaseReference firebase,firebase2,firebase3,db1,db2,db3,db4;
    private TextView totalpay,subtotal,deliveryfee,mDisplayDate,discount;
    private RadioButton COD,card;
    private Button checkout,edit;
    private OrderAdapter adapter;
    private RecyclerView listView;
    private RecyclerView.Adapter adapter2;
    private RecyclerView recyclerViewAddrList;
    private ArrayList<OrderSumDomain> ordersum = new ArrayList<>();
    private ArrayList<String> cartIDList = new ArrayList<>();
    private ArrayList<String> qtyList = new ArrayList<>();
    private ArrayList<String> priceList = new ArrayList<>();
    private ArrayList<String> prodIDList = new ArrayList<>();
    private ArrayList<AddressDomain> addrList = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public static ConstraintLayout layoutaddr;
    public static TextView address,addrID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setTitle("Checkout");
        initView();

        adapter2 = new PaymentAddressAdapter(addrList);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(Checkout.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewAddrList.setLayoutManager(linearLayoutManager2);
        recyclerViewAddrList.setAdapter(adapter2);

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
                                    adapter2.notifyDataSetChanged();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutaddr.setVisibility(View.VISIBLE);
            }
        });

        adapter = new OrderAdapter(ordersum);
        listView = findViewById(R.id.listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);
        subtotal.setText(getIntent().getStringExtra("totalpay").substring(3));
        double subtotal_int = Double.parseDouble(subtotal.getText().toString());
        double deliveryfee_int = Double.parseDouble(deliveryfee.getText().toString());
        int sub =  (int) subtotal_int;
        if (sub < 25){
            double finaltotal = subtotal_int + deliveryfee_int;
            totalpay.setText("RM " + finaltotal+"0");
        }else{
            discount.setText("5.00");
            double discountPrice = Double.parseDouble(discount.getText().toString());
            double finaltotal = subtotal_int + deliveryfee_int - discountPrice;
            totalpay.setText("RM " + finaltotal+"0");
        }
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(Checkout.this, android.R.style.Theme_Dialog,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1 + 1;
                String date = i2+"/"+i1+"/"+i;
                mDisplayDate.setText(date);
            }
        };
        firebase = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        firebase2 = FirebaseDatabase.getInstance().getReference("Product");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersum.clear();
                cartIDList.clear();
                qtyList.clear();
                prodIDList.clear();
                priceList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String price = snapshot1.child("total").getValue().toString();
                    String prodid = snapshot1.child("product_ID").getValue().toString();
                    String qty = snapshot1.child("qty").getValue().toString();
                    String cartid = snapshot1.child("cart_ID").getValue().toString();
                    cartIDList.add(cartid);
                    qtyList.add(qty);
                    prodIDList.add(prodid);
                    priceList.add(price);
                    firebase2.child(prodid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child("product_Name").getValue().toString();
                            ordersum.add(new OrderSumDomain(name,price,qty));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(Checkout.this);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setCancelable(false);
                mDialog.setMessage("Creating the order...");
                mDialog.show();
                firebase3 = FirebaseDatabase.getInstance().getReference("OrderList");
                db3 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                firebase3.child("Order").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int zero = 0;
                        if (snapshot.exists()){
                            final int count = (int)snapshot.getChildrenCount();
                            int number = count + 1;
                            firebase3.child("count").setValue(number);
                            String orderID = "";
                            if (number < 10) {
                                orderID = "OR000" + number;
                            }
                            else if (number < 100) {
                                orderID = "OR00" + number;
                            }
                            else if (number < 1000) {
                                orderID = "OR0" + number;
                            }
                            else if (number < 10000) {
                                orderID = "OR" + number;
                            }
                            final String OrderID = orderID;
                            firebase3.child("Order").child(OrderID).child("Order_ID").setValue(OrderID);
                            firebase3.child("Order").child(OrderID).child("Order_Date").setValue(mDisplayDate.getText().toString());
                            firebase3.child("Order").child(OrderID).child("Uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            firebase3.child("Order").child(OrderID).child("AddressID").setValue(addrID.getText().toString());
                            firebase3.child("Order").child(OrderID).child("TotalPay").setValue(totalpay.getText().toString());
                            firebase3.child("Order").child(OrderID).child("feedback").setValue("false");
                            for (int i = 0; i < cartIDList.size(); i++){
                                final String cartid = cartIDList.get(i);
                                final String qty = qtyList.get(i);
                                final String prod = prodIDList.get(i);
                                final String rm = priceList.get(i);
                                firebase3.child("Order").child(OrderID).child("Order_Cart").child(cartid).child("Cart_ID").setValue(cartid);
                                firebase3.child("Order").child(OrderID).child("Order_Cart").child(cartid).child("Product_ID").setValue(prod);
                                firebase3.child("Order").child(OrderID).child("Order_Cart").child(cartid).child("Cart_Qty").setValue(qty);
                                firebase3.child("Order").child(OrderID).child("Order_Cart").child(cartid).child("Cart_RM").setValue(rm);
                            }
                            if (COD.isChecked()){

                                firebase3.child("Order").child(OrderID).child("Order_Status").setValue("Processing with Pending");
                                firebase3.child("Order").child(OrderID).child("payment_method").setValue("Cash On Delivery");
                                db3.child("Cart").removeValue();
                                db3.child("cartCount").setValue(zero);
                                db4 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Payment");
                                db4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            final int count = (int) snapshot.getChildrenCount();
                                            int number = count;
                                            db4.child("count").setValue(count);
                                            String payID = "";
                                            if (number < 10) {
                                                payID = "P000" + number;
                                            }
                                            else if (number < 100) {
                                                payID = "P00" + number;
                                            }
                                            else if (number < 1000) {
                                                payID = "P0" + number;
                                            }
                                            else if (number < 10000) {
                                                payID = "P" + number;
                                            }
                                            final String PayID = payID;
                                            db4.child(PayID).child("Payment_ID").setValue(PayID);
                                            db4.child(PayID).child("Payment_Type").setValue("Cash on delivery");
                                        }else{
                                            int one = 1;
                                            db4.child("count").setValue(one);
                                            db4.child("P0001").child("Payment_ID").setValue("P0001");
                                            db4.child("P0001").child("Payment_Type").setValue("Cash on delivery");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                mDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this);
                                builder.setMessage("Order success...");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(Checkout.this, CustomerPanel_BottomNavigation.class);
                                        startActivity(i);

                                    }

                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else if (card.isChecked()) {
                                firebase3.child("Order").child(OrderID).child("Order_Status").setValue("Pending");
                                firebase3.child("Order").child(OrderID).child("payment_method").setValue("Card");
                                mDialog.dismiss();
                                Intent i = new Intent(Checkout.this, CardActivity.class);
                                i.putExtra("orderID",OrderID);
                                startActivity(i);
                            }
                        }else{
                            firebase3.child("count").setValue("1");
                            firebase3.child("Order").child("OR0001").child("Order_ID").setValue("OR0001");
                            firebase3.child("Order").child("OR0001").child("Order_Date").setValue(mDisplayDate.getText().toString());
                            firebase3.child("Order").child("OR0001").child("Uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            firebase3.child("Order").child("OR0001").child("AddressID").setValue(addrID.getText().toString());
                            firebase3.child("Order").child("OR0001").child("TotalPay").setValue("RM " + totalpay.getText().toString());
                            firebase3.child("Order").child("OR0001").child("feedback").setValue("false");
                            for (int i = 0; i < cartIDList.size(); i++){
                                final String cartid = cartIDList.get(i);
                                final String qty = qtyList.get(i);
                                final String prod = prodIDList.get(i);
                                final String rm = priceList.get(i);
                                firebase3.child("Order").child("OR0001").child("Order_Cart").child(cartid).child("Cart_ID").setValue(cartid);
                                firebase3.child("Order").child("OR0001").child("Order_Cart").child(cartid).child("Product_ID").setValue(prod);
                                firebase3.child("Order").child("OR0001").child("Order_Cart").child(cartid).child("Cart_Qty").setValue(qty);
                                firebase3.child("Order").child("OR0001").child("Order_Cart").child(cartid).child("Cart_RM").setValue(rm);
                            }
                            if (COD.isChecked()){
                                firebase3.child("Order").child("OR0001").child("Order_Status").setValue("Processing with Pending");
                                firebase3.child("Order").child("OR0001").child("payment_method").setValue("Cash On Delivery");
                                db3.child("Cart").removeValue();
                                db3.child("cartCount").setValue(zero);
                                db4 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Payment");
                                db4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            final int count = (int) snapshot.getChildrenCount();
                                            int number = count;
                                            db4.child("count").setValue(count);
                                            String payID = "";
                                            if (number < 10) {
                                                payID = "P000" + number;
                                            }
                                            else if (number < 100) {
                                                payID = "P00" + number;
                                            }
                                            else if (number < 1000) {
                                                payID = "P0" + number;
                                            }
                                            else if (number < 10000) {
                                                payID = "P" + number;
                                            }
                                            final String PayID = payID;
                                            db4.child(PayID).child("Payment_ID").setValue(PayID);
                                            db4.child(PayID).child("Payment_Type").setValue("Cash on delivery");
                                        }else{
                                            int one = 1;
                                            db4.child("count").setValue(one);
                                            db4.child("P0001").child("Payment_ID").setValue("P0001");
                                            db4.child("P0001").child("Payment_Type").setValue("Cash on delivery");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                mDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this);
                                builder.setMessage("Order success...");
                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(Checkout.this, CustomerPanel_BottomNavigation.class);
                                        startActivity(i);

                                    }

                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else if (card.isChecked()) {
                                firebase3.child("Order").child("OR0001").child("Order_Status").setValue("Pending");
                                firebase3.child("Order").child("OR0001").child("payment_method").setValue("Card");
                                mDialog.dismiss();
                                Intent i = new Intent(Checkout.this, CardActivity.class);
                                i.putExtra("orderID","OR0001");
                                startActivity(i);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
    private void initView() {
        totalpay = findViewById(R.id.textView8);
        discount = findViewById(R.id.textView31);
        subtotal = findViewById(R.id.textView14);
        deliveryfee = findViewById(R.id.textView15);
        mDisplayDate = findViewById(R.id.txtDate);
        checkout = findViewById(R.id.button2);
        COD = findViewById(R.id.radioButton);
        card = findViewById(R.id.radioButton2);
        edit = findViewById(R.id.button3);
        recyclerViewAddrList = findViewById(R.id.RecyclerPaymentAddress);
        address = findViewById(R.id.textView28);
        addrID = findViewById(R.id.textView33);
        layoutaddr = findViewById(R.id.layAddress);
    }

}