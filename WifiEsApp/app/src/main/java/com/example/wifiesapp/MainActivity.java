package com.example.wifiesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wifiesapp.datamodel.UserModal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public DrawerLayout drawerLayout;
    private boolean auth;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseFirestore db;
    public static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.login_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationViewListener();

        db = FirebaseFirestore.getInstance();

        Button loginBtn = (Button) findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username= (EditText) findViewById(R.id.unm);
                EditText password= (EditText) findViewById(R.id.pd);
                authenticate(username.getText().toString(),password.getText().toString());
            }
        });

        Button registerBtn = (Button) findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username= (EditText) findViewById(R.id.unm);
                EditText password= (EditText) findViewById(R.id.pd);
                register(username.getText().toString(),password.getText().toString());
            }
        });

        Button switchPageBtn = (Button) findViewById(R.id.switchPage);
        switchPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loginBtn.getVisibility()==View.GONE)
                {
                    loginBtn.setVisibility(View.VISIBLE);
                    registerBtn.setVisibility(View.GONE);
                    switchPageBtn.setText(R.string.switch_state_register);
                }
                else {
                    registerBtn.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.GONE);
                    switchPageBtn.setText(R.string.switch_state_login);
                }
            }
        });
    }

    private void register(String username, String password){
        Map<String, Object> data = new HashMap<>();
        data.put("name", username);
        data.put("password", password);
        db.collection("Users").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor Ed=sp.edit();
                        Ed.putString("username",username);
                        Ed.commit();
                        Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                        startActivity(intent);
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void authenticate(String username, String password){
        Query query = db.collection("Users");
        query = query.whereEqualTo("username",username);
        query = query.whereEqualTo("password",password);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
                            SharedPreferences.Editor Ed=sp.edit();
                            Ed.putString("username",username);
                            Ed.commit();
                            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                            startActivity(intent);
                        }
                        else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(MainActivity.this, "Username or Password Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(MainActivity.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            NavigationMenuItemView logout = (NavigationMenuItemView) findViewById(R.id.nav_logout);
            logout.setTitle("Exit");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                finish();
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}