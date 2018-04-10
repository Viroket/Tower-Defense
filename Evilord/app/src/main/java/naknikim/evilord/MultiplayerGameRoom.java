package naknikim.evilord;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import naknikim.evilord.Game.MultiplayerAttackActivity;
import naknikim.evilord.Game.MultiplayerDefenseActivity;
import naknikim.evilord.Logic.MiniGameSurface;

public class MultiplayerGameRoom extends Activity {

    private String user_name , room_name;
    private boolean attackOrDefend;
    private DatabaseReference root;
    private MiniGameSurface gameSurface;
    private RelativeLayout gameMenu;
    private ValueEventListener listener;
    //private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        user_name = getIntent().getExtras().get("userName").toString();
        room_name = getIntent().getExtras().get("roomName").toString();
        attackOrDefend = getIntent().getExtras().getBoolean("attackOrDefense");

        root = FirebaseDatabase.getInstance().getReference().getRoot().child("Multiplayer").child(room_name);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("roomAvailable").getValue(Boolean.class)){
                    root.removeEventListener(this);
                    goToGame(attackOrDefend);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        root.addValueEventListener(listener);

        /*FrameLayout game = new FrameLayout(getBaseContext());
        gameSurface = new MiniGameSurface(game.getContext());
        gameMenu = new RelativeLayout(game.getContext());

        TextView view = new TextView(MultiplayerGameRoom.this);
        view.setText(getString(R.string.waiting_room) +room_name+ getString(R.string.another_player));
        view.setTextColor(Color.WHITE);
        view.setTextSize(20);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        gameMenu.addView(view,param);

        game.addView(gameSurface);
        game.addView(gameMenu);
        setContentView(game);*/

        setContentView(R.layout.activity_multyplayer_game_room);
    }

    private void goToGame(boolean attackOrDefend) {
        if(attackOrDefend){
            Intent intent = new Intent(getApplicationContext() , MultiplayerAttackActivity.class);
            intent.putExtra("roomName",room_name);
            intent.putExtra("userName" , user_name);
            intent.putExtra("attackOrDefense",attackOrDefend);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext() , MultiplayerDefenseActivity.class);
            intent.putExtra("roomName",room_name);
            intent.putExtra("userName" , user_name);
            intent.putExtra("attackOrDefense",attackOrDefend);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        root.removeEventListener(listener);
        root.removeValue();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        root.removeEventListener(listener);
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
