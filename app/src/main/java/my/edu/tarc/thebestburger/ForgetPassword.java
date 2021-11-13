package my.edu.tarc.thebestburger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {
    TextInputLayout emaillid;
    Button Reset;
    FirebaseAuth Fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        emaillid=(TextInputLayout)findViewById(R.id.email);
        Reset=(Button)findViewById(R.id.reset);

        Fauth = FirebaseAuth.getInstance();
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(ForgetPassword.this);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setCancelable(false);
                mDialog.setMessage("Loading ... ...");
                mDialog.show();

                Fauth.sendPasswordResetEmail(emaillid.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            ReusableCodeForAll.ShowAlert(ForgetPassword.this,"","Password has been sent to your Email");
                        } else {
                            mDialog.dismiss();
                            ReusableCodeForAll.ShowAlert(ForgetPassword.this,"Error",task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }
}