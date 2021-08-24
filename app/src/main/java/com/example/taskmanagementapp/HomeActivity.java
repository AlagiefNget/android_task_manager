package com.example.taskmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagementapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;

    private FirebaseAuth myAuth;
    private DatabaseReference myDb;

    //Recycler
    private RecyclerView recyclerView;

    //Delete/Update data

    private EditText titleUpdate;
    private EditText noteUpdate;
    private Button updateBtn;
    private Button deleteBtn;

    // Global variables to store data for details view

    private String title;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task Manager");

        myAuth = FirebaseAuth.getInstance();
        FirebaseUser myUser = myAuth.getCurrentUser();
        String user_id = myUser.getUid();

        myDb = FirebaseDatabase.getInstance().getReference().child("TasksNote").child(user_id);
        myDb.keepSynced(true);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn = findViewById(R.id.fab_btn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For dialog when you click fab add button
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                View myView = inflater.inflate(R.layout.custominputfield, null);
                myAlertDialog.setView(myView);
                AlertDialog dialog = myAlertDialog.create();

                EditText title = myView.findViewById(R.id.editTitle);
                EditText note = myView.findViewById(R.id.editNote);

                Button btnSave = myView.findViewById(R.id.saveText);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String _title = title.getText().toString().trim();
                        String _note = note.getText().toString().trim();

                        if(TextUtils.isEmpty(_title)){
                            title.setError("Please specify title");
                            return;
                        }
                        if(TextUtils.isEmpty(_note)){
                            note.setError("Please specify note");
                            return;
                        }

                        String id = myDb.push().getKey();
                        String date = DateFormat.getDateInstance().format(new Date());
                        Data data = new Data(_title, _note, date, id);
                        myDb.child(id).setValue(data);
                        Toast.makeText(getApplicationContext(), "Task successfully added", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setTitle(String title){
            TextView myTitle = myView.findViewById(R.id.titleField);
            myTitle.setText(title);
        }

        public void setNote(String note){
            TextView myNote = myView.findViewById(R.id.noteField);
            myNote.setText(note);
        }

        public void setDate(String date){
            TextView myDate = myView.findViewById(R.id.dateField);
            myDate.setText(date);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.item_data,
                MyViewHolder.class,
                myDb
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data data, int position) {
                myViewHolder.setTitle(data.getTitle());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setDate(data.getDate());

                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();
                        title = data.getTitle();
                        note = data.getNote();

                        updateData(); // To populate the details view
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                myAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myView = inflater.inflate(R.layout.updateinputfield, null);
        myDialog.setView(myView);
        AlertDialog dialog = myDialog.create();

        titleUpdate = myView.findViewById(R.id.updateTitle);
        noteUpdate = myView.findViewById(R.id.updateNote);

        deleteBtn = myView.findViewById(R.id.deleteUpdate);
        updateBtn = myView.findViewById(R.id.saveUpdate);

        titleUpdate.setText(title);
        titleUpdate.setSelection(title.length()); // To set the cursor at the end of the text

        noteUpdate.setText(note);
        noteUpdate.setSelection(note.length());

        // Method to update task
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleUpdate.getText().toString().trim();
                note = noteUpdate.getText().toString().trim();
                String upDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(title, note, upDate, post_key);
                myDb.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}