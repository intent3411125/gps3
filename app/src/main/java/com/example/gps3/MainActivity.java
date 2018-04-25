package com.example.gps3;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.view.View;
import com.google.firebase.database.ChildEventListener;

public class MainActivity extends MapsActivity {
    private DatabaseReference messageRef;
    private static final String TAG = MainActivity.class.getSimpleName();
    public TextView textView;
    private ChildEventListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textview);
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");

        textView.setText("value");
        // Read from the database
        messageRef.child("lat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                textView = (TextView) findViewById(R.id.textview);
                DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
               // String key = dataSnapshot.getKey();
                //String foo = dataSnapshot.getValue(String.class);
                String value = dataSnapshot.getValue(String.class);
               // String lat = dataSnapshot.getValue(String.class);

               // Log.d("Firebase", String.format("sender:%s, body:%s", foo, value));
               // String title = (String) dataSnapshot.child("message").getValue();
               // Boolean isDone = (Boolean) dataSnapshot.child("isDone").getValue();

               // String str2 = value;
               // int result3 = str2.length();
               // Log.d("Firebase", String.format("sender:%s, body:%s", str2, value));
                Log.d(TAG, "Value is: " );

                textView.setText(value);
                Log.d(TAG, "Value is: " );

                // 追加されたTodoのkey、title、isDoneが取得できているので、
                // 保持しているデータの更新や描画処理を行う。
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                //String foo = dataSnapshot.getValue(String.class);
               // String str2 = value;
               // int result3 = str2.length();
                // textView.setText(String.valueOf(foo));

                textView.setText(value);
                Log.d(TAG, "Value is: " );

               // Log.d("Firebase", String.format("sender:%s, body:%s", foo, value));
                // Changed
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // Moved
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error

            }
        });




    }

}
