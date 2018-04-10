package naknikim.evilord;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import naknikim.evilord.Game.NormalGameActivity;

public class MainActivity extends Activity {
    private ImageButton StartNormalButton;
    private ImageButton StartMultiplayerButton;
    private ImageButton logOutButton;
    private ImageButton recordsButton;
    private FirebaseAuth firebaseAuth;
    private static final int STARTING_RECORD_VALUE = 0;
    private static final int MAX_RECORDS = 10;
    private DatabaseReference root;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        gameSound();
        startGame();
        root = FirebaseDatabase.getInstance().getReference();
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Records").hasChildren())) {
                    for (int i = 1; i <= MAX_RECORDS; i++) {
                        root.child("Records").child("" + i).setValue(STARTING_RECORD_VALUE + " " + "NULL");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void startGame() {
        logOutButton = (ImageButton) findViewById(R.id.buttonLogOut);
        StartNormalButton = (ImageButton) findViewById(R.id.normalButton);
        StartMultiplayerButton = (ImageButton) findViewById(R.id.multiplayerButton);
        recordsButton = (ImageButton)findViewById(R.id.recordsButton);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //the button to logout to another profile
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startLoginActivity();
            }
        });

        //the button that start the normal game activity
        StartNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNormalGameActivity();
            }
        });

        //the button that start the multiplayer game activity
        StartMultiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMultiplayerGameActivity();
            }
        });
        recordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TableActivity.class);
                startActivity(intent);
            }
        });
    }

    public void startLoginActivity() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    //start a normal game
    public void startNormalGameActivity() {
        Intent normalGame = new Intent(this, NormalGameActivity.class);
        startActivity(normalGame);
    }

    //start a m
    public void startMultiplayerGameActivity() {
        Intent multiplayerGame = new Intent(this, MultiplayerActivity.class);
        startActivity(multiplayerGame);
    }
    private void gameSound() {
        mp = MediaPlayer.create(this, R.raw.demo_track_1);
        mp.start();
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    protected void onPause() {
        stopPlaying();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        stopPlaying();
        super.onBackPressed();
    }
}

