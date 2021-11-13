package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class CustomerPhonenumber extends AppCompatActivity {
    ConstraintLayout otp;
    LinearLayout lay;
    TextView txt;
    EditText num, stnum, entercode;
    Button SendOTP, verify, Resend;
    String number, verificationId, OldNumber;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_phonenumber);
        setTitle("Change Phone Number");
        FAuth = FirebaseAuth.getInstance();
        otp = (ConstraintLayout) findViewById(R.id.cLay1);
        lay = (LinearLayout) findViewById(R.id.Lay2);
        verify = (Button) findViewById(R.id.Verify);
        Resend = (Button) findViewById(R.id.Resendotp);
        txt = (TextView) findViewById(R.id.text1);
        entercode = (EditText) findViewById(R.id.txtOTP);
        stnum = (EditText) findViewById(R.id.startnum);
        num = (EditText) findViewById(R.id.phonenumber);
        SendOTP = (Button) findViewById(R.id.sendotp);
        SendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = stnum.getText().toString().trim() + num.getText().toString().trim();
                sendverificationcode(number);
                otp.setVisibility(View.VISIBLE);
                lay.setVisibility(View.GONE);
                SendOTP.setVisibility(View.GONE);
                Resend.setVisibility(View.GONE);

                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Resend.setVisibility(View.GONE);
                        String code = entercode.getText().toString().trim();
                        if (code.isEmpty() && code.length() < 6) {
                            entercode.setError("Enter code");
                            entercode.requestFocus();
                            return;
                        }
                        verifyCode(code);
                    }

                });

                new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        txt.setVisibility(View.VISIBLE);
                        txt.setText("Resend Code within " + millisUntilFinished / 1000 + " Seconds");
                    }

                    @Override
                    public void onFinish() {
                        Resend.setVisibility(View.VISIBLE);
                        txt.setVisibility(View.INVISIBLE);

                    }
                }.start();

                Resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Resend.setVisibility(View.GONE);
                        Resendotp(number);

                        new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txt.setVisibility(View.VISIBLE);
                                txt.setText("Resend Code within " + millisUntilFinished / 1000 + " Seconds");
                            }

                            @Override
                            public void onFinish() {
                                Resend.setVisibility(View.VISIBLE);
                                txt.setVisibility(View.INVISIBLE);

                            }
                        }.start();

                    }
                });

                databaseReference = FirebaseDatabase.getInstance().getReference("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        OldNumber = stnum.getText().toString().trim() + dataSnapshot.child("Phone Number").getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(number,code);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CustomerPhonenumber.this, "phone number updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CustomerPhonenumber.this, "This 2: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(CustomerPhonenumber.this, "This 3: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(CustomerPhonenumber.this, "sent", Toast.LENGTH_SHORT).show();
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(CustomerPhonenumber.this, "Receive", Toast.LENGTH_SHORT).show();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                entercode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(CustomerPhonenumber.this, "This 1:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}