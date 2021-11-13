package my.edu.tarc.thebestburger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth Fauth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Fauth = FirebaseAuth.getInstance();
                if (Fauth.getCurrentUser() != null){
                    if(Fauth.getCurrentUser().isEmailVerified()){
                        Fauth = FirebaseAuth.getInstance();
                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getUid() + "/Role");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String role = snapshot.getValue(String.class);
                                if(role.equals("Customer")){
                                    Intent i = new Intent(MainActivity.this, CustomerPanel_BottomNavigation.class);
                                    startActivity(i);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Check whether you have verified your details, Otherwise please verify");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Fauth.signOut();
                    }
                }else{
                    Intent intent = new Intent(MainActivity.this,MainMenu.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}