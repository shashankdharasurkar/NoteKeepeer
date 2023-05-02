package com.example.notekeepeer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    FirebaseAuth firebaseAuth;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        floatingActionButton = findViewById(R.id.createnoteapp);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("All Notes");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this, CreateNote.class));
            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebaseModel> allusernotes = new FirestoreRecyclerOptions.Builder<firebaseModel>().setQuery(query, firebaseModel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebaseModel model) {

                ImageView popupbutton = holder.itemView.findViewById(R.id.menupopupbutton);

                int colorcode = getRandomColor();
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colorcode, null));

                holder.notetitles.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(v.getContext(), noteDetails.class);
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getContent());
                        intent.putExtra("noteId", docId);

                        v.getContext().startActivity(intent);

                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {

                                Intent intent = new Intent(v.getContext(), editNoteActivity.class);
                                intent.putExtra("title", model.getTitle());
                                intent.putExtra("content", model.getContent());
                                intent.putExtra("noteId", docId);
                                v.getContext().startActivity(intent);

                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete ").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {

                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("mynotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(NotesActivity.this, "Note Deleted Successfully", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NotesActivity.this, "Failed to Delete Note", Toast.LENGTH_SHORT).show();

                                    }
                                });


                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recycl);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView notetitles;
        TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitles = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logoutuser) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(NotesActivity.this, MainActivity.class));
        }

//        switch (item.getItemId()){
//            case R.id.logoutuser:
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.grey);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());

        return colorcode.get(number);

    }

}