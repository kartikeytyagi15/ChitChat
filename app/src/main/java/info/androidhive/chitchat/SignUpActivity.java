package info.androidhive.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import info.androidhive.chitchat.Models.Users;
import info.androidhive.chitchat.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Users user = new Users(binding.etUserName.getText().toString(), binding.etEmail.getText().toString(),
                                                binding.etPassword.getText().toString());
                            String uid = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(uid).setValue(user);

                            Toast.makeText(info.androidhive.chitchat.SignUpActivity.this, "User created successfully", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(info.androidhive.chitchat.SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        binding.tvClickSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}