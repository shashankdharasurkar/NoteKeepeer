package com.example.notekeepeer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private EditText mforgetpassword;
    Button recoverbtn;
    TextView gobacktologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        getSupportActionBar().hide();

        mforgetpassword = findViewById(R.id.forgetpassemail);
        recoverbtn = findViewById(R.id.forgetpassbtn);
        gobacktologin = findViewById(R.id.return_login);

        firebaseAuth = FirebaseAuth.getInstance();

        gobacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetActivity.this, MainActivity.class));
            }
        });

        recoverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mforgetpassword.getText().toString().trim();

                if (mail.isEmpty()) {
                    Toast.makeText(ForgetActivity.this, "Enter Your Email ", Toast.LENGTH_SHORT).show();
                } else {


                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetActivity.this, "Password Recovery E-Mail Sent Please chech your Email", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgetActivity.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(ForgetActivity.this, "Please Check Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }
}