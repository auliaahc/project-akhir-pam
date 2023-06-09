package com.example.projectakhirpam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegister,usernameRegister, passwordRegister;
    Button google;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference databaseReference;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        emailRegister = findViewById(R.id.emailRegister);
        usernameRegister = findViewById(R.id.usernameRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        Button registerButton = findViewById(R.id.buttonRegister);

        google = findViewById(R.id.GoogleLogin);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String emailtxt = emailRegister.getText().toString();
        String usernametxt = usernameRegister.getText().toString();
        String passwordtxt = passwordRegister.getText().toString();

        if (emailtxt.isEmpty() || usernametxt.isEmpty() || passwordtxt.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill the form correctly", Toast.LENGTH_SHORT).show();
        }

        auth.createUserWithEmailAndPassword(emailtxt, passwordtxt)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                DatabaseReference userRef = databaseReference.child(userId);
                                userRef.child("email").setValue(emailtxt);
                                userRef.child("name").setValue(usernametxt);
                                userRef.child("password").setValue(passwordtxt);
                                userRef.child("phoneNumber").setValue("-");

                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    int RC_SIGN_IN = 40;

    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setName(user.getDisplayName());
                            users.setProfile(user.getPhotoUrl().toString());

                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            Toast.makeText(RegisterActivity.this, "Register with Google Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}