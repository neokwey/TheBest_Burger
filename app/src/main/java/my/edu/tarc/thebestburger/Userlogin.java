package my.edu.tarc.thebestburger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Userlogin extends AppCompatActivity {
    Button loginphone,login;
    TextInputLayout email, pass;
    TextView Forgotpassword, txt;
    FirebaseAuth FAuth;
    String em, pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        try{
            email = (TextInputLayout) findViewById(R.id.Lemail);
            pass = (TextInputLayout) findViewById(R.id.Lpassword);
            txt = (TextView) findViewById(R.id.textView3);
            Forgotpassword=(TextView)findViewById(R.id.forgotpass);
            loginphone=(Button)findViewById(R.id.btnphone);
            login=(Button)findViewById(R.id.btnlogin);
            FAuth = FirebaseAuth.getInstance();

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    em = email.getEditText().getText().toString().trim();
                    pwd = pass.getEditText().getText().toString().trim();

                    if (isValid()) {
                        final ProgressDialog mDialog = new ProgressDialog(Userlogin.this);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.setMessage("Logging in...");
                        mDialog.show();
                        FAuth.signInWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mDialog.dismiss();
                                    if (FAuth.getCurrentUser().isEmailVerified()) {
                                        Toast.makeText(Userlogin.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                        Intent z = new Intent(Userlogin.this, CustomerPanel_BottomNavigation.class);
                                        startActivity(z);
                                        finish();
                                    } else {
                                        ReusableCodeForAll.ShowAlert(Userlogin.this,"","Please Verify your Email");
                                    }
                                }else{
                                    mDialog.dismiss();
                                    ReusableCodeForAll.ShowAlert(Userlogin.this,"Error",task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
            });

            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent Register = new Intent(Userlogin.this, UserRegistration.class);
                    startActivity(Register);
                }
            });

            Forgotpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a=new Intent(Userlogin.this,ForgetPassword.class);
                    startActivity(a);
                }
            });

            loginphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signphone = new Intent(Userlogin.this,UserloginPhone.class);
                    startActivity(signphone);
                    finish();
                }
            });
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public boolean isValid() {
        email.setErrorEnabled(false);
        email.setError("");
        pass.setErrorEnabled(false);
        pass.setError("");

        boolean isvalidemail=false,isvalidpassword=false,isvalid=false;
        if (TextUtils.isEmpty(em))
        {
            email.setErrorEnabled(true);
            email.setError("Email is required");
        }
        else {
            if (em.matches(emailpattern))
            {
                isvalidemail=true;
            }
            else {
                email.setErrorEnabled(true);
                email.setError("Enter a valid Email Address");
            }

        }
        if (TextUtils.isEmpty(pwd))
        {
            pass.setErrorEnabled(true);
            pass.setError("Password is required");
        }
        else {
            isvalidpassword=true;
        }
        isvalid = (isvalidemail && isvalidpassword) ? true : false;
        return isvalid;
    }
}