package com.example.notekeepeer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class  MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    EditText loginemail, loginpass;
    RelativeLayout loginbtn, signupnav;
    TextView forgotpass;

    ProgressBar mprogressbar_mainactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        loginemail = findViewById(R.id.email_login);
        loginpass = findViewById(R.id.pass_login);
        loginbtn = findViewById(R.id.loginBtn);
        forgotpass = findViewById(R.id.forget);
        signupnav = findViewById(R.id.signup);
        mprogressbar_mainactivity = findViewById(R.id.progressbarmainactivity);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser!=null){
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }

        signupnav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgetActivity.class));
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = loginemail.getText().toString().trim();
                String pass = loginpass.getText().toString().trim();


                if (mail.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "All Fields are required", Toast.LENGTH_SHORT).show();
                }
                else {

                    mprogressbar_mainactivity.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                checkVerification();
                            }else {
                                Toast.makeText(MainActivity.this, "Account Doesn't Exist", Toast.LENGTH_SHORT).show();
                                mprogressbar_mainactivity.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void checkVerification() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser.isEmailVerified()==true){
            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        }
        else {
            mprogressbar_mainactivity.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Email Verification is Pending", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();

        }
    }
}