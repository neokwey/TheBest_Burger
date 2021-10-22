package my.edu.tarc.thebestburger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserloginPhone extends AppCompatActivity {
    Button otp,Verify,Resendotp,btnemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin_phone);
        btnemail=(Button)findViewById(R.id.btnEmail);
        otp=(Button)findViewById(R.id.otp);
        Verify=(Button)findViewById(R.id.Verify);
        Resendotp=(Button)findViewById(R.id.Resendotp);

        btnemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signemail = new Intent(UserloginPhone.this,Userlogin.class);
                startActivity(signemail);
                finish();
            }
        });
    }
}