package naknikim.evilord;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import naknikim.evilord.UserInfo.User;

public class LoginActivity extends Activity implements View.OnClickListener{

    private ImageButton buttonRegister;
    private ImageButton buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    //my database in the fire base
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        //inishelize the fire base
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            //start main activity
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        progressDialog =  new ProgressDialog(this);

        buttonRegister = (ImageButton) findViewById(R.id.buttonRegister);
        buttonSignIn = (ImageButton) findViewById(R.id.buttonSignin);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        editTextEmail.setTextColor(Color.WHITE);
        editTextEmail.setHintTextColor(Color.WHITE);

        editTextPassword.setTextColor(Color.WHITE);
        editTextPassword.setHintTextColor(Color.WHITE);

        buttonRegister.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
    }

    //starting a new user white 500 gold and on defense mode
    private void startingNewUser() {
        User newUser = new User(500);


        FirebaseUser user = firebaseAuth.getCurrentUser();

        //getting the unick id of the user
        //the setValue is getting an Object and place it inside of the user
        databaseReference.child(user.getUid()).setValue(newUser);
    }

    private void regusterUser() {
        String email =  editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this , "please enter email" , Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this , "please enter password" , Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        //if validations are ok
        //we will first show a progressbar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        //this lissened will pot the things on the fire base
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //user is successfully registered and logged in
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(LoginActivity.this , "Registered Successfully" ,Toast.LENGTH_SHORT).show();
                    //adding a new user information
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    startingNewUser();
                }
                else {
                    Toast.makeText(LoginActivity.this , "Could not register." ,Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });



    }

    private void userLogin() {
        String email =  editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this , "please enter email" , Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this , "please enter password" , Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        progressDialog.setMessage("signed in");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    //start the main activity
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else {
                    progressDialog.setMessage("account doesn't exist");
                }
            }
        });

    }



    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            regusterUser();
        }
        if(view == buttonSignIn) {
            userLogin();
        }



        // if(view == textViewSignin) {
        //    finish();
        //   startActivity(new Intent(this, MainActivity.class));
        //}


    }
}