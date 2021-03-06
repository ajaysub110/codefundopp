package com.example.ajays.dirac.Forum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajays.dirac.Choice.ChoiceModel;
import com.example.ajays.dirac.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ForumActivity extends FragmentActivity {

    TextView forum_main_tv;
    RecyclerView forum_main_rv;
    Button add_post_btn;
    ArrayList<ForumModel> forum_list = new ArrayList<>();
    ForumAdapter forum_main_rv_adapter;
    private ChoiceModel forumObject;
    FirebaseDatabase database;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forum);

        forum_main_tv = findViewById(R.id.forum_main_title);
        forum_main_rv = findViewById(R.id.forum_main_list);
        add_post_btn = findViewById(R.id.add_post_btn);

        forumObject = (ChoiceModel) this.getIntent().getSerializableExtra("chatOrForumChosen");

        Toast.makeText(this, forumObject.getChoice_name(), Toast.LENGTH_SHORT).show();

        database = FirebaseDatabase.getInstance();
        sp=this.getSharedPreferences("login", MODE_PRIVATE);

        add_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("post_number",forum_list.size());
                bundle.putString("region",sp.getString("region",null));
                AddPostDialogFragment newFragment = new AddPostDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"add_post");
            }
        });

        DatabaseReference data_ref=database.getReference("posts/"+sp.getString("region",null)+'/');
        Toast.makeText(ForumActivity.this, data_ref.toString(), Toast.LENGTH_SHORT).show();
        data_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                forum_list.add(dataSnapshot.getValue(ForumModel.class));
                setAdapter();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                forum_list.remove(Integer.parseInt(dataSnapshot.getKey())-1);
                setAdapter();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setAdapter(){
        forum_main_rv.setLayoutManager(new LinearLayoutManager(ForumActivity.this,LinearLayoutManager.VERTICAL,false));
        forum_main_rv.setHasFixedSize(true);

        forum_main_rv_adapter = new ForumAdapter(this,forum_list);
        forum_main_rv.setAdapter(forum_main_rv_adapter);
    }

}
