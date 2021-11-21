package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import my.edu.tarc.thebestburger.AddressBook;
import my.edu.tarc.thebestburger.R;
import my.edu.tarc.thebestburger.UserRegistration;

public class NewAddress extends AppCompatActivity {

    TextView txtLL;
    EditText no,addr1,addr2,post,city,state;
    Button save;
    DatabaseReference db1;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        setTitle("New Address");
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        txtLL = findViewById(R.id.txtlatlon);
        no = findViewById(R.id.etxtNo);
        addr1 = findViewById(R.id.etxtAddress1);
        addr2 = findViewById(R.id.etxtAddress2);
        post = findViewById(R.id.etxtPos);
        city = findViewById(R.id.etxtCity);
        state = findViewById(R.id.etxtState);
        save = findViewById(R.id.button4);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()){
                    final ProgressDialog mDialog = new ProgressDialog(NewAddress.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Adding address please wait...");
                    mDialog.show();
                    String address = "";
                    if (addr2.getText().toString().isEmpty()){
                        address = no.getText().toString().trim()+", "+addr1.getText().toString().trim();
                    }else{
                        address = no.getText().toString().trim()+", "+addr1.getText().toString().trim()+", "+addr2.getText().toString().trim();
                    }
                    final String Address = address;
                    db1 = FirebaseDatabase.getInstance().getReference("Customer").child(userid).child("Address");
                    db1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                final int count = (int) snapshot.getChildrenCount();
                                int number = count;
                                db1.child("count").setValue(count);
                                final String addrID = "Address" + number;
                                db1.child(addrID).child("AddressID").setValue(addrID);
                                db1.child(addrID).child("Address").setValue(Address);
                                db1.child(addrID).child("Postcode").setValue(post.getText().toString().trim());
                                db1.child(addrID).child("City").setValue(city.getText().toString().trim());
                                db1.child(addrID).child("State").setValue(state.getText().toString().trim());
                                db1.child(addrID).child("latitude").setValue(getIntent().getStringExtra("lat"));
                                db1.child(addrID).child("longitude").setValue(getIntent().getStringExtra("lon"));
                                Toast.makeText(NewAddress.this, "Address has been added.", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                                Intent i = new Intent(NewAddress.this, DisplayAddress.class);
                                startActivity(i);
                            }else{
                                db1.child("count").setValue("1");
                                db1.child("Address1").child("AddressID").setValue("Address1");
                                db1.child("Address1").child("Address").setValue(Address);
                                db1.child("Address1").child("Postcode").setValue(post.getText().toString().trim());
                                db1.child("Address1").child("City").setValue(city.getText().toString().trim());
                                db1.child("Address1").child("State").setValue(state.getText().toString().trim());
                                db1.child("Address1").child("latitude").setValue(getIntent().getStringExtra("lat"));
                                db1.child("Address1").child("longitude").setValue(getIntent().getStringExtra("lon"));
                                Toast.makeText(NewAddress.this, "Address has been added.", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                                Intent i = new Intent(NewAddress.this, DisplayAddress.class);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }

    public boolean isValid(){
        boolean isValidno=false,isValidaddr1=false,isValidpost=false,isValidcity=false,isvalid = false,isValidstate=false;
        if(no.getText().toString().isEmpty()){
            no.setError("Lot / No is required!");
        }else{
            isValidno = true;
        }

        if(addr1.getText().toString().isEmpty()){
            addr1.setError("Lot / No is required!");
        }else{
            isValidaddr1 = true;
        }

        if(city.getText().toString().isEmpty()){
            city.setError("Lot / No is required!");
        }else{
            isValidcity = true;
        }

        if(state.getText().toString().isEmpty()){
            state.setError("Lot / No is required!");
        }else{
            isValidstate = true;
        }

        if(post.getText().toString().isEmpty()){
            post.setError("Postcode is required!");
        }else{
            if (post.length() < 5) {
                post.setError("Postcode incorrect format");
            } else {
                isValidpost = true;
            }
        }
        isvalid = (isValidno && isValidaddr1 && isValidcity && isValidstate && isValidpost) ? true : false;
        return isvalid;
    }
}