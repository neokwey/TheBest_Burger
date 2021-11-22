package my.edu.tarc.thebestburger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import my.edu.tarc.thebestburger.customerPanel.CustomerPaymentOTP;
import my.edu.tarc.thebestburger.customerPanel.CustomerTrackFragment;

public class CardActivity extends AppCompatActivity {
    TextInputLayout cardname, cardnumber, expirydate, cvv;
    Button Addcard;
    String name, number, date, CVV;
    String orderID;
    FirebaseAuth FAuth;
    DatabaseReference firebase,firebase2,db1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        setTitle("Add new card");

        Addcard = (Button) findViewById(R.id.addcard);
        cardname = (TextInputLayout) findViewById(R.id.nameoncard);
        cardnumber = (TextInputLayout) findViewById(R.id.cardnumber);
        expirydate = (TextInputLayout) findViewById(R.id.expirydate);
        cvv = (TextInputLayout) findViewById(R.id.CVV);

        Addcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = cardname.getEditText().getText().toString().trim();
                number = cardnumber.getEditText().getText().toString().trim();
                date = expirydate.getEditText().getText().toString().trim();
                CVV = cvv.getEditText().getText().toString().trim();
                if (valid()) {
                    int zero = 0;
                    orderID = getIntent().getStringExtra("orderID");
                    db1 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Payment");
                    firebase = FirebaseDatabase.getInstance().getReference("OrderList");
                    firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    firebase2.child("Cart").removeValue();
                    firebase2.child("cartCount").setValue(zero);
                    firebase.child("Order").child(orderID).child("Order_Status").setValue("Processing");
                    db1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                final int count = (int) snapshot.getChildrenCount();
                                int number = count;
                                db1.child("count").setValue(count);
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
                                db1.child(PayID).child("Payment_ID").setValue(PayID);
                                db1.child(PayID).child("Payment_Type").setValue("Credit / Debit Card");
                                db1.child(PayID).child("CardName").setValue(name);
                                db1.child(PayID).child("CardNumber").setValue(number);
                                db1.child(PayID).child("CardExp").setValue(date);
                                db1.child(PayID).child("CardCVV").setValue(CVV);

                            }else{
                                int one = 1;
                                db1.child("count").setValue(one);
                                db1.child("P0001").child("Payment_ID").setValue("P0001");
                                db1.child("P0001").child("Payment_Type").setValue("Credit / Debit Card");
                                db1.child("P0001").child("CardName").setValue(name);
                                db1.child("P0001").child("CardNumber").setValue(number);
                                db1.child("P0001").child("CardExp").setValue(date);
                                db1.child("P0001").child("CardCVV").setValue(CVV);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
                    builder.setMessage("Payment Success...");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(CardActivity.this, CustomerPanel_BottomNavigation.class);
                            startActivity(i);

                        }

                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });
    }

    private boolean valid() {


        cardname.setErrorEnabled(false);
        cardname.setError("");
        cardnumber.setErrorEnabled(false);
        cardnumber.setError("");
        expirydate.setErrorEnabled(false);
        expirydate.setError("");
        cvv.setErrorEnabled(false);
        cvv.setError("");


        boolean isValidname = false, isValidlnumber = false, isValidexpiry = false, isValidcvv = false, isvalid = false;
        if (TextUtils.isEmpty(name)) {
            cardname.setErrorEnabled(true);
            cardname.setError("Cardname is required");
        } else {
            isValidname = true;
        }
        if (TextUtils.isEmpty(number)) {
            cardnumber.setErrorEnabled(true);
            cardnumber.setError("Cardnumber is required");
        } else {
            if (number.length() < 16) {
                cardnumber.setErrorEnabled(true);
                cardnumber.setError("Invalid number");
            } else {
                isValidlnumber = true;
            }
        }
        if (TextUtils.isEmpty(date)) {
            expirydate.setErrorEnabled(true);
            expirydate.setError("Expiry date is required");
        } else {
            isValidexpiry = true;

        }
        if (TextUtils.isEmpty(CVV)) {
            cvv.setErrorEnabled(true);
            cvv.setError("CVV is required");
        } else {
            if (CVV.length() < 3) {
                cvv.setErrorEnabled(true);
                cvv.setError("Invalid CVV number");
            } else {
                isValidcvv = true;
            }
        }
        isvalid = (isValidname && isValidlnumber && isValidexpiry && isValidcvv) ? true : false;
        return isvalid;

    }
}