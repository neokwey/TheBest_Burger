package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import my.edu.tarc.thebestburger.ForgetPassword;
import my.edu.tarc.thebestburger.R;

public class CustomerPassword extends AppCompatActivity {
    EditText current, neww, confirm;
    Button change_pwd;
    TextView forgot;
    String cur, ne, conf, email;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_password);
        setTitle("Change Password");

        current = (EditText) findViewById(R.id.current_pwd);
        neww = (EditText) findViewById(R.id.new_pwd);
        confirm = (EditText) findViewById(R.id.confirm_pwd);
        change_pwd = (Button) findViewById(R.id.change);
        forgot = (TextView) findViewById(R.id.forgot_pwd);

        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("Email").getValue().toString();
                change_pwd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cur = current.getText().toString().trim();
                        ne = neww.getText().toString().trim();
                        conf = confirm.getText().toString().trim();
                        if (isvalid()) {


                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(email, cur);

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(ne).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    String userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    FirebaseDatabase.getInstance().getReference("Customer").child(userid).child("Password").setValue(ne);
                                                    Toast.makeText(CustomerPassword.this, "Password updated.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(CustomerPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(CustomerPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });
                forgot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(CustomerPassword.this, ForgetPassword.class);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean isvalid() {

        boolean isValidnewpassword = false, isValidconfirmpasswoord = false, isvalid = false;
        if (TextUtils.isEmpty(ne)) {
            neww.setError("New Password is required");

        } else {
            if (ne.length() < 6) {
                neww.setError("Password too weak");
                confirm.setError("password too weak");
            } else {
                isValidnewpassword = true;
            }
        }

        if (TextUtils.isEmpty(conf)) {
            confirm.setError("Confirm Password is required");


        } else {
            if (!ne.equals(conf)) {
                neww.setError("Password doesn't match");
                confirm.setError("Password doesn't match");
            } else {
                isValidconfirmpasswoord = true;
            }
        }
        isvalid = (isValidnewpassword && isValidconfirmpasswoord) ? true : false;
        return isvalid;

    }
}