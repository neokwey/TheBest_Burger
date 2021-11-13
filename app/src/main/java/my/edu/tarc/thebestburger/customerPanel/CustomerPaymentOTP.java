package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import my.edu.tarc.thebestburger.*;

public class CustomerPaymentOTP extends AppCompatActivity {
    EditText otp;
    Button place;
    String ot, verificationId, phone, orderID;
    TextInputLayout entercode;
    FirebaseAuth FAuth;
    DatabaseReference firebase,firebase2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_payment_otp);
        otp = (EditText) findViewById(R.id.OTP);
        place = (Button) findViewById(R.id.place);
        FAuth = FirebaseAuth.getInstance();

        firebase = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phone = "+60" + snapshot.child("Phone Number").getValue().toString();
                sendverificationcode(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ot = otp.getText().toString().trim();
                if (ot.isEmpty() && ot.length() < 6) {
                    otp.setError("Enter code");
                    otp.requestFocus();
                    return;
                }
                verifyCode(ot);
            }
        });
    }

    private void sendverificationcode(String number) {
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions
                        .newBuilder(FirebaseAuth.getInstance())
                        .setActivity(this)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setCallbacks(mCallBack)
                        .build());
    }

    private void Resendotp(String phonenumber) {
        sendverificationcode(phonenumber);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInwithCredential(credential);
    }

    private void signInwithCredential(PhoneAuthCredential credential) {
        try {
            FAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        int zero = 0;
                        orderID = getIntent().getStringExtra("orderID");
                        firebase2 = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        firebase2.child("Cart").removeValue();
                        firebase2.child("cartCount").setValue(zero);
                        firebase2.child("Order").child(orderID).child("Order_Status").setValue("Processing");
                        Toast.makeText(CustomerPaymentOTP.this, "Payment successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CustomerPaymentOTP.this, CustomerPanel_BottomNavigation.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ReusableCodeForAll.ShowAlert(CustomerPaymentOTP.this, "Error", task.getException().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                entercode.getEditText().setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(CustomerPaymentOTP.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}