package com.example.traffika;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.traffika.db.BackgroundWorker;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername=findViewById(R.id.etUserName);
        etPassword=findViewById(R.id.etPassword);
    }

    public void onLogin(View view){
        String type="login";

        String username=etUsername.getText().toString();
        String password=etPassword.getText().toString();
        BackgroundWorker backgroundWorker=new BackgroundWorker(this, username);
        backgroundWorker.execute(type,username, password);

    }
}