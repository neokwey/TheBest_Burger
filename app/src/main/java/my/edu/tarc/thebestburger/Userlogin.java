package my.edu.tarc.thebestburger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Userlogin extends AppCompatActivity {
    Button loginphone,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        loginphone=(Button)findViewById(R.id.btnphone);
        login=(Button)findViewById(R.id.btnlogin);

        loginphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signphone = new Intent(Userlogin.this,UserloginPhone.class);
                startActivity(signphone);
                finish();
            }
        });
    }
}