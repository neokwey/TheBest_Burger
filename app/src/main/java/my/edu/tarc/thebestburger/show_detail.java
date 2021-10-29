package my.edu.tarc.thebestburger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.thebestburger.Domain.PopularDomain;
import my.edu.tarc.thebestburger.Domain.ProductDomain;

public class show_detail extends AppCompatActivity {
    private TextView addToCardBtn;
    private TextView titleTxt, feeTxt, descriptionTxt, numberOrderTxt;
    private ImageView plusBtn, minusBtn, picFood;
    private int numberOrder = 1;
    DatabaseReference databaseReference;
    String ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);

        initView();
        getFood();
        numberOrderTxt.setText(String.valueOf(numberOrder));

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOrder = numberOrder + 1;
                numberOrderTxt.setText(String.valueOf(numberOrder));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOrder > 1) {
                    numberOrder = numberOrder - 1;
                }
                numberOrderTxt.setText(String.valueOf(numberOrder));
            }
        });

        addToCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATC();
            }
        });
    }

    private void ATC(){
        ID = getIntent().getStringExtra("ProductID");
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final int count = (int)snapshot.getChildrenCount();
                int number = count;
                databaseReference.child("count").setValue(count);
                String cart_ID = "";
                if (number < 10) {
                    cart_ID = "C000" + number;
                }
                else if (number < 100) {
                    cart_ID = "C00" + number;
                }
                else if (number < 1000) {
                    cart_ID = "C0" + number;
                }
                else if (number < 10000) {
                    cart_ID = "C" + number;
                }
                final String Cart_ID = cart_ID;
                databaseReference.child(Cart_ID).child("product_ID").setValue(ID);
                databaseReference.child(Cart_ID).child("cart_ID").setValue(Cart_ID);
                databaseReference.child(Cart_ID).child("qty").setValue(numberOrderTxt.getText());
                Toast.makeText(show_detail.this, "Your meal has been added to cart.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFood() {
        ID = getIntent().getStringExtra("ProductID");
        databaseReference = FirebaseDatabase.getInstance().getReference("Product").child(ID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(show_detail.this).load(snapshot.child("product_Photo").getValue().toString()).into(picFood);
                titleTxt.setText(snapshot.child("product_Name").getValue().toString());
                feeTxt.setText("RM " + snapshot.child("product_Price").getValue().toString());
                descriptionTxt.setText(snapshot.child("product_Desc").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        addToCardBtn = findViewById(R.id.addToCardBtn);
        titleTxt = findViewById(R.id.titleTxt);
        feeTxt = findViewById(R.id.priceTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        numberOrderTxt = findViewById(R.id.numberOrderTxt);
        plusBtn = findViewById(R.id.plusBtn);
        minusBtn = findViewById(R.id.minusBtn);
        picFood = findViewById(R.id.foodPic);
    }
}