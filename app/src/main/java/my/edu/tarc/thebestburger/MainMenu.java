package my.edu.tarc.thebestburger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainMenu extends AppCompatActivity {
    Button loginemail,signup;
    ImageView bgimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        final Animation Zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        final Animation Zoomout = AnimationUtils.loadAnimation(this, R.anim.zoomout);

        bgimg=findViewById(R.id.back2);
        bgimg.setAnimation(Zoomin);
        bgimg.setAnimation(Zoomout);

        Zoomout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimg.startAnimation(Zoomin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {  }
        });

        Zoomin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimg.startAnimation(Zoomout);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {  }
        });

        loginemail=(Button)findViewById(R.id.SignwithEmail);
        signup=(Button)findViewById(R.id.Signup);

        loginemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signemail = new Intent(MainMenu.this,Userlogin.class);
                startActivity(signemail);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(MainMenu.this,UserRegistration.class);
                startActivity(signup);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}