package naknikim.evilord.End;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import naknikim.evilord.MultiplayerActivity;
import naknikim.evilord.R;

public class NormalGameEndActivity extends EndActivity {
    private int gameTime = -1;
    private DatabaseReference root;
    private EditText userNameEdit;
    private String userName = "";
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        root = FirebaseDatabase.getInstance().getReference();

        screen = new RelativeLayout(getApplicationContext());
        screen.setBackground(getDrawable(R.drawable.mainback));




        extractBundle();
        initMainMenuButton();
        initRestartButton();
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(NormalGameEndActivity.this, MultiplayerActivity.class);
                startActivity(main);
            }
        });
        initScoreView();
        if(gameTime != -1){
            score_v.setText(String.format("Survival Time: %02d:%02d", gameTime / 60, gameTime % 60));
            score_v.setTextColor(Color.RED);
        }
        initEditText();
        initSubmitButton();
        setContentView(screen);
    }
    private void initEditText(){
        userNameEdit = new EditText(screen.getContext());
        userNameEdit.setId(R.id.user_name_edit);
        userNameEdit.setTextColor(Color.WHITE);
        userNameEdit.setHint(getString(R.string.user_name_hint));
        userNameEdit.setHintTextColor(Color.WHITE);
        userNameEdit.setPadding(0,10,0,0);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.addRule(RelativeLayout.BELOW,R.id.score_v);
        screen.addView(userNameEdit,param);
    }
    private void initSubmitButton(){
        submitButton = new Button(screen.getContext());
        submitButton.setText(getString(R.string.submit));
        submitButton.setPadding(0,10,0,0);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = userNameEdit.getText().toString();
                if(userName.length() > 8 || userName.equals("")){
                    return;
                }
                root.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("Records")){
                                for(DataSnapshot kid : child.getChildren()){
                                    String[] s = kid.getValue(String.class).split(" ");
                                    if(gameTime>Integer.parseInt(s[0])){
                                        root.child("Records").child(kid.getKey()).setValue(gameTime+" "+userName);
                                        gameTime = Integer.parseInt(s[0]);
                                        userName = s[1];
                                        userNameEdit.setVisibility(View.INVISIBLE);
                                        submitButton.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.addRule(RelativeLayout.BELOW,R.id.user_name_edit);
        screen.addView(submitButton,param);
    }
    @Override
    public void extractBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(getString(R.string.bundle_key));
        gameTime = bundle.getInt(getString(R.string.game_time));
    }
}
