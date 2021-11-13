package my.edu.tarc.thebestburger.customerPanel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import my.edu.tarc.thebestburger.Domain.CustomerDomain;
import my.edu.tarc.thebestburger.MainMenu;
import my.edu.tarc.thebestburger.R;


public class CustomerProfileFragment extends Fragment {
    EditText name;
    Spinner State, City;
    TextView mobileno, Email;
    Button Update;
    LinearLayout password, LogOut;
    DatabaseReference databaseReference, data, data1;
    FirebaseDatabase firebaseDatabase;
    String statee, cityy, suburban, email, passwordd, confirmpass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile");
        View v = inflater.inflate(R.layout.fragment_customerprofile,null);
        name = (EditText) v.findViewById(R.id.fnamee);
        Email = (TextView) v.findViewById(R.id.emailID);
        mobileno = (TextView) v.findViewById(R.id.mobilenumber);
        Update = (Button) v.findViewById(R.id.update);
        password = (LinearLayout) v.findViewById(R.id.passwordlayout);
        LogOut = (LinearLayout) v.findViewById(R.id.logout_layout);

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("Name").getValue().toString());
                mobileno.setText("+60" + dataSnapshot.child("Phone Number").getValue().toString());
                Email.setText(dataSnapshot.child("Email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UpdateDetail();

        return v;
    }

    public void UpdateDetail(){

        mobileno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CustomerPhonenumber.class);
                startActivity(i);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomerPassword.class);
                startActivity(intent);
            }
        });
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to Logout ?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), MainMenu.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        onDestroy();
                    }

                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
