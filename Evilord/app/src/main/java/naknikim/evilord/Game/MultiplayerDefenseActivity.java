package naknikim.evilord.Game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import naknikim.evilord.End.MultiplayerEndActivity;
import naknikim.evilord.End.NormalGameEndActivity;
import naknikim.evilord.Enemies.Enemy;
import naknikim.evilord.Logic.GameSurface;
import naknikim.evilord.MainActivity;
import naknikim.evilord.R;
import naknikim.evilord.Towers.GameTower;

public class MultiplayerDefenseActivity extends DefenseGameActivity {
    private static final int FIVE_MINUTES = 60*5;
    private boolean gameIsWon = false;
    private Enemy.EnemyType enemy;

    private String user_name , room_name;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        user_name = getIntent().getExtras().get("userName").toString();
        room_name = getIntent().getExtras().get("roomName").toString();

        root = FirebaseDatabase.getInstance().getReference().child("Multiplayer").child(room_name);

        FrameLayout game = new FrameLayout(this);
        gameSurface = new GameSurface(getApplicationContext());
        gameSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        gameSurface.setZOrderOnTop(true);
        gameMenu = new RelativeLayout(getApplicationContext());

        gameTime = FIVE_MINUTES;
        initTowerButtons();
        initDescriptionView();
        initTimer();
        initGoldView();
        initHealthView();
        initSurfaceListener();
        startGameLoop();
        startFireBaseListener();
        ImageView bgImagePanel = new ImageView(game.getContext());
        bgImagePanel.setBackgroundResource(R.drawable.gamebackground);
        FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        game.addView(gameSurface,fill);
        game.addView(bgImagePanel,fill);
        game.addView(gameMenu);
        setContentView(game);
    }

    private void startFireBaseListener() {
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Enemies").exists() && !dataSnapshot.child("Enemies").getValue(String.class).equals("")) {
                    switch (dataSnapshot.child("Enemies").getValue(String.class)) {
                        case "Soldier":
                            enemy = Enemy.EnemyType.Soldier;
                            break;
                        case "Knight":
                            enemy = Enemy.EnemyType.Knight;
                            break;
                        case "Priest":
                            enemy = Enemy.EnemyType.Priest;
                            break;
                        case "Mage":
                            enemy = Enemy.EnemyType.Mage;
                            break;
                        case "Lord":
                            enemy = Enemy.EnemyType.Lord;
                            break;
                    }
                    root.child("Enemies").setValue("");
                    createNewEnemy(enemy);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void startGameLoop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!gameIsLost && !gameIsWon){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    playerGold++;
                    gameTime--;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            health_v.setText(getString(R.string.health)+playerHP);
                            gold_v.setText(getString(R.string.gold)+playerGold);
                        }
                    });
                    if(gameTime<=0){
                        gameIsWon = true;
                        endActivity();
                    }
                    for(int i = 0 ; i < towerList.size() ; i++){
                        try {
                            towerList.get(i).setTarget(null);
                            towerList.get(i).lookForTarget(gameSurface.getEnemyList());
                        }catch (IndexOutOfBoundsException e){
                            continue;
                        }
                    }
                }
            }
        }).start();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timer_v.setText(String.format("%02d:%02d", gameTime / 60, gameTime % 60));
                    }
                });
            }
        }, 1000, 1000);
    }
    @Override
    public void subtractPlayerHealth(){
        synchronized ((Integer)playerHP) {
            playerHP--;
            if (playerHP <= 0) {
                gameIsLost = true;
                root.child("DefenseLost").setValue(true);
                endActivity();
            }
        }
    }

    protected void initSurfaceListener() {
        gameSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (currentTypePicked != null) {
                        if (currentTypePicked.cost <= playerGold && checkIsTaken((int) event.getX(), (int) event.getY())) {
                            tower = new GameTower(currentTypePicked, (int) event.getX(),(int) event.getY());
                            int height = GameTower.getRectSize()/2;
                            int width = GameTower.getRectSize()/2;
                            for(int i = (int)tower.getY()-height ; i< tower.getY()+height ; i++){
                                for (int j = (int)tower.getX()-width ; j < tower.getX()+width ; j++){
                                    if(i < 0 || j < 0 || i > locationsTaken.length-1 || j > locationsTaken[0].length-1){
                                        return false;
                                    }
                                    locationsTaken[i][j] = 1;
                                }
                            }
                            String towerString = "";
                            switch (currentTypePicked){
                                case Arrow:
                                    towerString = "Arrow";
                                    break;
                                case Cannon:
                                    towerString = "Cannon";
                                    break;
                                case Ice:
                                    towerString = "Ice";
                                    break;
                                case Air:
                                    towerString = "Air";
                                    break;
                                case Flame:
                                    towerString = "Flame";
                                    break;
                            }
                            towerString= towerString.concat(" "+event.getX()+" "+event.getY()
                                                            +" "+screenWidth+" "+screenHeight);
                            root.child("Towers").setValue(towerString);
                            playerGold -= tower.getTowerCost();
                            towerList.add(tower);
                            gameSurface.addTower(tower);
                            currentTypePicked = null;
                            return true;
                        } else {
                            Toast.makeText(getBaseContext(), getString(R.string.can_not_place_tower), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        for(int i = 0 ; i < towerList.size(); i++){
                            try{
                                GameTower tower = towerList.get(i);
                                if(checkIfTower(tower,(int) event.getX(), (int) event.getY())){
                                    String s = event.getX()+" "+event.getY()+" "+screenWidth+" "+screenHeight;
                                    root.child("RemoveTower").setValue(s);

                                    removeTower(tower);
                                    increasePlayerGold((int) (tower.getTowerCost()*0.4));
                                }
                            }catch (ArrayIndexOutOfBoundsException e){
                                continue;
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    private void createNewEnemy(Enemy.EnemyType enemyType) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),enemyType.photo);
        gameSurface.addEnemy(new Enemy(this,enemyType,gameSurface,bitmap,0,screenHeight/2));
    }

    @Override
    public void onBackPressed() {
        root.removeValue();
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        gameIsLost = true;
        finish();
    }

    @Override
    protected void endActivity() {
        timer.cancel();
        Intent endGame = new Intent(this, MultiplayerEndActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean("WinOrLose", gameIsWon);

        endGame.putExtra(getString(R.string.bundle_key) , bundle);
        startActivity(endGame);
        finish();
    }

    @Override
    protected void onStop() {
        root.removeValue();
        super.onStop();
    }
}
