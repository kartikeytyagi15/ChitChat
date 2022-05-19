package info.androidhive.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import info.androidhive.chitchat.Models.Users;
import info.androidhive.chitchat.databinding.ActivitySettingsBinding;


public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = binding.etStatus.getText().toString();
                String username = binding.etUsername.getText().toString();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("userName", username);
                hashMap.put("status", status);
                database.getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(hashMap);
                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getProfilePic())
                                .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);
                        binding.etUsername.setText(users.getUserName());
                        binding.etStatus.setText(users.getStatus());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null && data.getData() != null){
            Uri file = data.getData();
            binding.profileImage.setImageURI(file);

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Uploading image to cloud");
            progressDialog.setCancelable(false);
            progressDialog.show();
            final StorageReference reference = storage.getReference().child("profile pictures")
                    .child(FirebaseAuth.getInstance().getUid());
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "Image Updated",Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profilePic").setValue(uri.toString());
                        }
                    });
                }
            });
        }

    }

}