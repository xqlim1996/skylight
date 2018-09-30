package me.eugenekoh.skylightapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginCardLight extends AppCompatActivity {

    private View parent_view;

    private TextInputEditText name;
    private TextInputEditText password;
    private TextView info;
    private Button login;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_card_light);
        parent_view = findViewById(android.R.id.content);

        monitorLogin();
    }


    private void monitorLogin(){
        name = (TextInputEditText) findViewById(R.id.etName);
        password = (TextInputEditText) findViewById(R.id.etPassword);
        login = (Button) findViewById(R.id.email_sign_in_button);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginCardLight.this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Logging In..");
                progressDialog.show();
                validate(name.getText().toString(), password.getText().toString());
            }
        });
    }


    private void validate(String userName, String userPassword) {

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(LoginCardLight.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginCardLight.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginCardLight.this, Flights.class));
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginCardLight.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


