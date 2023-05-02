package com.example.notekeepeer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNote extends AppCompatActivity {

    EditText mcreatetitleofnote, mcontentofnote;
    FloatingActionButton msavenote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;

    ProgressBar mprogressbarofcreatenote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        msavenote = findViewById(R.id.savenote);
        mcontentofnote = findViewById(R.id.createcontentofnote);
        mcreatetitleofnote = findViewById(R.id.createtitleofnoteapp);
        mprogressbarofcreatenote = findViewById(R.id.progressbarcreatenote);

        Toolbar toolbar = findViewById(R.id.toolbarofcreatenote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        msavenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mcreatetitleofnote.getText().toString();
                String content = mcontentofnote.getText().toString();

                if (title.isEmpty() || content.isEmpty()) {

                    Toast.makeText(CreateNote.this, "Both Field are required", Toast.LENGTH_SHORT).show();
                } else {

                    mprogressbarofcreatenote.setVisibility(View.VISIBLE);
                    DocumentReference documentReference = firestore.collection("notes").
                            document(firebaseUser.getUid()).collection("mynotes").document();
                    Map<String, Object> notes = new HashMap<>();
                    notes.put("title", title);
                    notes.put("content", content);

                    documentReference.set(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreateNote.this, "Note Created Successfully", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(CreateNote.this, NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(CreateNote.this, "Failed to Create Note ", Toast.LENGTH_SHORT).show();
                            Log.d("Note Creation Error",e.toString());
                            mprogressbarofcreatenote.setVisibility(View.INVISIBLE);

                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}