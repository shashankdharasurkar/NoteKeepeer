package com.example.notekeepeer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

public class editNoteActivity extends AppCompatActivity {

    Intent data;

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    TextView medittitleofnote, meditcontentofnote;
    FloatingActionButton msaveditnote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        medittitleofnote = findViewById(R.id.edittitleofnote);
        meditcontentofnote = findViewById(R.id.editcontentofnote);
        msaveditnote = findViewById(R.id.saveeditnote);
        data = getIntent();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        msaveditnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newtitle = medittitleofnote.getText().toString();
                String newcontent = meditcontentofnote.getText().toString();

                if (newtitle.isEmpty() || newcontent.isEmpty()) {
                    Toast.makeText(editNoteActivity.this, "Something is empty", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes")
                            .document(firebaseUser.getUid()).collection("mynotes")
                            .document(data.getStringExtra("noteId"));


                    Log.d("Document Ref", documentReference.toString().trim());

                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newtitle);
                    note.put("content", newcontent);

                    documentReference.update("title", newtitle, "content", newcontent).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(editNoteActivity.this, "Note Updated", Toast.LENGTH_SHORT).show();


                            startActivity(new Intent(editNoteActivity.this, NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(editNoteActivity.this, "Failed to update note", Toast.LENGTH_SHORT).show();

                        }
                    });
                }


            }
        });

        String notetitle = data.getStringExtra("title");
        String notecontent = data.getStringExtra("content");

        meditcontentofnote.setText(notecontent);
        medittitleofnote.setText(notetitle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}