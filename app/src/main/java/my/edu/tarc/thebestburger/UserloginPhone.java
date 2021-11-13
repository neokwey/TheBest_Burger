package my.edu.tarc.thebestburger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.TimeUnit;

public class UserloginPhone extends AppCompatActivity {
    private DatabaseReference firebase;
    Button otp, Verify, Resendotp, btnemail;
    TextView signup, sign, txt;
    TextInputLayout entercode;
    EditText phoneno;
    LinearLayout Lay1;
    ConstraintLayout Clay1;
    String phone, verificationId;
    FirebaseAuth FAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin_phone);


        btnemail = (Button) findViewById(R.id.btnEmail);
        otp = (Button) findViewById(R.id.otp);
        Verify = (Button) findViewById(R.id.Verify);
        Resendotp = (Button) findViewById(R.id.Resendotp);
        phoneno = (EditText) findViewById(R.id.txtPhone1);
        signup = (TextView) findViewById(R.id.acsignup);
        Lay1 = (LinearLayout) findViewById(R.id.lay1);
        sign = (TextView) findViewById(R.id.signphone);
        Clay1 = (ConstraintLayout) findViewById(R.id.cLay1);
        entercode = (TextInputLayout) findViewById(R.id.txtOTP);
        txt = (TextView) findViewById(R.id.text1);
        Resendotp.setVisibility(View.GONE);
        txt.setVisibility(View.INVISIBLE);
        FAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserloginPhone.this, UserRegistration.class);
                startActivity(i);
            }
        });

        btnemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signemail = new Intent(UserloginPhone.this, Userlogin.class);
                startActivity(signemail);
                finish();
            }
        });

        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = "+60" + phoneno.getText().toString().trim();
                Resendotp(phone);
                otp.setVisibility(View.GONE);
                Lay1.setVisibility(View.GONE);
                sign.setVisibility(View.GONE);
                btnemail.setVisibility(View.GONE);
                signup.setVisibility(View.GONE);
                phoneno.setEnabled(false);
                phoneno.setFocusable(true);
                Clay1.setVisibility(View.VISIBLE);

                Verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code = entercode.getEditText().getText().toString().trim();
                        Resendotp.setVisibility(View.GONE);
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
                        Resendotp.setVisibility(View.VISIBLE);
                        txt.setVisibility(View.INVISIBLE);

                    }
                }.start();

                Resendotp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Resendotp.setVisibility(View.INVISIBLE);
                        Resendotp(phone);

                        new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txt.setVisibility(View.VISIBLE);
                                txt.setText("Resend Code within " + millisUntilFinished / 1000 + " Seconds");
                            }

                            @Override
                            public void onFinish() {
                                Resendotp.setVisibility(View.VISIBLE);
                                txt.setVisibility(View.INVISIBLE);

                            }
                        }.start();

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
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInwithCredential(credential);
    }

    private void signInwithCredential(PhoneAuthCredential credential) {
        try {
            FAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserloginPhone.this, "You are logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserloginPhone.this, CustomerPanel_BottomNavigation.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ReusableCodeForAll.ShowAlert(UserloginPhone.this, "Error", task.getException().getMessage());
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
            Toast.makeText(UserloginPhone.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}