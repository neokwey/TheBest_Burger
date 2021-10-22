package my.edu.tarc.thebestburger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserRegistration extends AppCompatActivity {
    Button loginemail,loginphone,register;
    TextInputLayout name, phoneno, email, pass, cpass;
    String Name, PhoneNo, Email, Pass, Cpass, role = "Customer";
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        loginemail=(Button)findViewById(R.id.email);
        loginphone=(Button)findViewById(R.id.phone);
        register=(Button)findViewById(R.id.btnSignUp);

        name=(TextInputLayout)findViewById(R.id.txtName);
        phoneno=(TextInputLayout)findViewById(R.id.txtPhone);
        email=(TextInputLayout)findViewById(R.id.txtEmail);
        pass=(TextInputLayout)findViewById(R.id.Pwd);
        cpass=(TextInputLayout)findViewById(R.id.Cpass);

        databaseReference = firebaseDatabase.getInstance().getReference("Customer");
        FAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Name = name.getEditText().getText().toString().trim();
                PhoneNo = phoneno.getEditText().getText().toString().trim();
                Email = email.getEditText().getText().toString().trim();
                Pass = pass.getEditText().getText().toString().trim();
                Cpass = cpass.getEditText().getText().toString().trim();

                if (isValid()) {
                    final ProgressDialog mDialog = new ProgressDialog(UserRegistration.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Registering please wait...");
                    mDialog.show();

                    FAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference("User").child(useridd);
                                final HashMap<String,String> hashMap = new HashMap<>();
                                hashMap.put("Role", role);
                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        HashMap<String, String> hashMappp = new HashMap<>();
                                        hashMappp.put("Name", Name);
                                        hashMappp.put("Email", Email);
                                        hashMappp.put("Phone Number", PhoneNo);
                                        hashMappp.put("Password", Pass);

                                        firebaseDatabase.getInstance().getReference("Customer")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(hashMappp).addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDialog.dismiss();

                                                FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(UserRegistration.this);
                                                            builder.setMessage("Registered Successfully,Please Verify your Email");
                                                            builder.setCancelable(false);
                                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    dialog.dismiss();

                                                                    String phonenumber = "60" + PhoneNo;
                                                                    Intent b = new Intent(UserRegistration.this, VerifyPhone.class);
                                                                    b.putExtra("phonenumber", phonenumber);
                                                                    startActivity(b);

                                                                }
                                                            });
                                                            AlertDialog alert = builder.create();
                                                            alert.show();

                                                        } else {
                                                            mDialog.dismiss();
                                                            ReusableCodeForAll.ShowAlert(UserRegistration.this,"Error",task.getException().getMessage());

                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });


                }
            }
        });

        loginemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signemail = new Intent(UserRegistration.this,Userlogin.class);
                startActivity(signemail);
                finish();
            }
        });

        loginphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signphone = new Intent(UserRegistration.this,UserloginPhone.class);
                startActivity(signphone);
                finish();
            }
        });

    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid(){
        email.setErrorEnabled(false);
        email.setError("");
        name.setErrorEnabled(false);
        name.setError("");
        pass.setErrorEnabled(false);
        pass.setError("");
        cpass.setErrorEnabled(false);
        cpass.setError("");
        phoneno.setErrorEnabled(false);
        phoneno.setError("");

        boolean isValidemail=false,isValidname=false,isValidpass=false,isValidcpass=false,isvalid = false,isValidphoneno=false;
        if(TextUtils.isEmpty(Email)){
            email.setErrorEnabled(true);
            email.setError("Email is blank!");
        }else{
            if (Email.matches(emailpattern)) {
                isValidemail = true;
            } else {
                email.setErrorEnabled(true);
                email.setError("Enter a valid Email Address");
            }
        }

        if(TextUtils.isEmpty(Name)){
            name.setErrorEnabled(true);
            name.setError("Name is required!");
        }else{
            isValidname = true;
        }

        if(TextUtils.isEmpty(Pass)){
            pass.setErrorEnabled(true);
            pass.setError("Password is required!");
        }else{
            if (Pass.length() < 6) {
                pass.setErrorEnabled(true);
                pass.setError("password too weak");
            } else {
                isValidpass = true;
            }
        }

        if(TextUtils.isEmpty(Cpass)){
            cpass.setErrorEnabled(true);
            cpass.setError("Confirm password is required!");
        }else{
            if (!Pass.equals(Cpass)) {
                pass.setErrorEnabled(true);
                pass.setError("Password doesn't match");
            } else {
                isValidcpass = true;
            }
        }

        if(TextUtils.isEmpty(PhoneNo)){
            phoneno.setErrorEnabled(true);
            phoneno.setError("Phone number is required!");
        }else{
            if (PhoneNo.length() == 9 || PhoneNo.length() == 10) {
                isValidphoneno = true;
            } else {
                phoneno.setErrorEnabled(true);
                phoneno.setError("Invalid mobile number");
            }
        }
        isvalid = (isValidemail && isValidname && isValidpass && isValidcpass && isValidphoneno) ? true : false;
        return isvalid;
    }
}