package me.eugenekoh.skylightapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LoginCardLight extends AppCompatActivity {

    private View parent_view;

    private TextInputEditText name;
    private TextInputEditText password;
    private TextView info;
    private Button login;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    SharedPreferences sp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Access a Cloud Firestore instance from your Activity

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_card_light);
        parent_view = findViewById(android.R.id.content);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            goToMainActivity();
        }

        monitorLogin();
    }


    private void monitorLogin(){
        name = (TextInputEditText) findViewById(R.id.etName);
        password = (TextInputEditText) findViewById(R.id.etPassword);
        login = (Button) findViewById(R.id.email_sign_in_button);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginCardLight.this);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Logging In..");
                progressDialog.show();
                validate(name.getText().toString(), password.getText().toString(), checkBox);
            }
        });
    }


    private void validate(String userName, String userPassword, final CheckBox checkBox) {

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(LoginCardLight.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginCardLight.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    keepSignedIn(checkBox);
                    goToMainActivity();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginCardLight.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void keepSignedIn(CheckBox checkBox){
        if (checkBox.isChecked()){
            sp.edit().putBoolean("logged",true).apply();
        }
    }

    private void goToMainActivity(){
        Intent i = new Intent();
        startActivity(new Intent(LoginCardLight.this, Flights.class));
        this.finish();
    }
}


