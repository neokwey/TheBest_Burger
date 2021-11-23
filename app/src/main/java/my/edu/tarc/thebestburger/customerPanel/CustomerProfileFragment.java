package my.edu.tarc.thebestburger.customerPanel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import my.edu.tarc.thebestburger.*;


public class CustomerProfileFragment extends Fragment {
    EditText name,mobileno;
    TextView Email;
    Button Update;
    LinearLayout password, LogOut, Addresses, history;
    DatabaseReference databaseReference, data, data1;
    FirebaseDatabase firebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("My Profile");
        View v = inflater.inflate(R.layout.fragment_customerprofile,null);
        name = (EditText) v.findViewById(R.id.fnamee);
        Email = (TextView) v.findViewById(R.id.emailID);
        mobileno = (EditText) v.findViewById(R.id.etxtmobileno);
        Update = (Button) v.findViewById(R.id.update);
        password = (LinearLayout) v.findViewById(R.id.passwordlayout);
        LogOut = (LinearLayout) v.findViewById(R.id.logout_layout);
        Addresses = (LinearLayout) v.findViewById(R.id.Address_layout);
        history = (LinearLayout) v.findViewById(R.id.OrderHistory_layout);

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("Name").getValue().toString());
                mobileno.setText(dataSnapshot.child("Phone Number").getValue().toString());
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
        Addresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(getActivity(), DisplayAddress.class);
                startActivity(a);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent o = new Intent(getActivity(), OrderHistory.class);
                startActivity(o);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomerPassword.class);
                startActivity(intent);
            }
        });
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()){
                    data = firebaseDatabase.getReference().child("Customer");
                    data.child("Name").setValue(name.getText().toString());
                    data.child("Phone Number").setValue(mobileno.getText().toString());
                    Toast.makeText(getActivity(), "Profile has been updated.", Toast.LENGTH_SHORT).show();
                }
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

    public boolean isValid(){
        boolean isValidemail=false,isValidname=false,isValidpass=false,isValidcpass=false,isvalid = false,isValidphoneno=false;


        if(name == null){
            name.setError("Name is required!");
        }else{
            isValidname = true;
        }

        if(mobileno == null){
            mobileno.setError("Phone number is required!");
        }else{
            if (mobileno.length() == 9 || mobileno.length() == 10) {
                isValidphoneno = true;
            } else {
                mobileno.setError("Invalid mobile number");
            }
        }
        isvalid = (isValidname && isValidphoneno) ? true : false;
        return isvalid;
    }
}
