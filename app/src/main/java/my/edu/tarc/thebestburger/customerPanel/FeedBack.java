package my.edu.tarc.thebestburger.customerPanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smileyrating.SmileyRating;

import my.edu.tarc.thebestburger.CustomerPanel_BottomNavigation;
import my.edu.tarc.thebestburger.R;

public class FeedBack extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        setTitle("Feedback");

        final SmileyRating smileyRating = (SmileyRating)findViewById(R.id.smile_rating);
        final EditText feedback = findViewById(R.id.editText);
        Button btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback.setText("");
                smileyRating.setRating(SmileyRating.Type.NONE);
            }
        });

        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback.getText().toString().isEmpty()) {
                    Toast.makeText(FeedBack.this, "Feedback cannot be empty.", Toast.LENGTH_SHORT).show();
                }
                else {
                    SmileyRating.Type type = smileyRating.getSelectedSmiley();
                    final int rating = type.getRating();
                    final String user_feedback = feedback.getText().toString();
                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference("Feedback");
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            int count = (int)snapshot.getChildrenCount();
                            int number = count;
                            firebase.child("Count").setValue(number);
                            String feedbackID = "";
                            if (number < 10) {
                                feedbackID = "F000" + number;
                            }
                            else if (number < 100) {
                                feedbackID = "F00" + number;
                            }
                            else if (number < 1000) {
                                feedbackID = "F0" + number;
                            }
                            else if (number < 10000) {
                                feedbackID = "F" + number;
                            }

                            firebase.child(feedbackID).child("FeedbackID").setValue(feedbackID);
                            firebase.child(feedbackID).child("OrderID").setValue(getIntent().getStringExtra("Order_ID"));
                            firebase.child(feedbackID).child("Ratings").setValue(rating);
                            firebase.child(feedbackID).child("Content").setValue(user_feedback);
                            firebase.child(feedbackID).child("Uid").setValue(uid);
                            Toast.makeText(FeedBack.this, "Your feedback is submitted.", Toast.LENGTH_SHORT).show();
                            feedback.setText("");
                            smileyRating.setRating(SmileyRating.Type.NONE);
                            Intent i = new Intent(FeedBack.this, CustomerPanel_BottomNavigation.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {  }
                    });
                }
            }
        });
    }
}