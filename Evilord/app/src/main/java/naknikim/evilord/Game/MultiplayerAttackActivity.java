package naknikim.evilord.Game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import naknikim.evilord.End.MultiplayerEndActivity;
import naknikim.evilord.Enemies.Enemy;
import naknikim.evilord.Logic.GameSurface;
import naknikim.evilord.End.NormalGameEndActivity;
import naknikim.evilord.MainActivity;
import naknikim.evilord.R;
import naknikim.evilord.Towers.GameTower;

public class MultiplayerAttackActivity extends GameActivity {
    private static final int NUM_OF_ENEMIES = Enemy.EnemyType.values().length;
    private ImageButton[] buttons = new ImageButton[NUM_OF_ENEMIES];
    private int i;
    private Enemy.EnemyType[] enemyTypes = Enemy.EnemyType.values();
    private int[] lastUsed = new int[NUM_OF_ENEMIES];
    private static final int FIVE_MINUTES = 60*5;
    private boolean gameIsWon = false;
    private GameTower.TowerType tower;
    private ArrayList<GameTower> towers = new ArrayList<>();


    private String user_name , room_name;
    private DatabaseReference root;
    //private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //gameSound();
        user_name = getIntent().getExtras().get("userName").toString();
        room_name = getIntent().getExtras().get("roomName").toString();

        root = FirebaseDatabase.getInstance().getReference().child("Multiplayer").child(room_name);

        FrameLayout game = new FrameLayout(this);
        gameSurface = new GameSurface(getApplicationContext());
        gameSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        gameSurface.setZOrderOnTop(true);
        gameMenu = new RelativeLayout(getApplicationContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        gameTime = FIVE_MINUTES;
        initLastUsed();
        initEnemyButtons();
        initTimer();
        initDescriptionView();
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
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getKey().equals("DefenseLost")){
                        if(dataSnapshot.child("DefenseLost").exists() && dataSnapshot.child("DefenseLost").getValue(Boolean.class)){
                            gameIsWon = true;
                            endActivity();
                        }
                    }else if(child.getKey().equals("Towers")){
                        if(dataSnapshot.child("Towers").exists()
                                &&dataSnapshot.child("Towers").getValue(String.class) != null
                                && !dataSnapshot.child("Towers").getValue(String.class).equals("")) {
                            String s = dataSnapshot.child("Towers").getValue(String.class);
                            String[] towerString = s.split(" ");
                            switch (towerString[0]) {
                                case "Arrow":
                                    tower = GameTower.TowerType.Arrow;
                                    break;
                                case "Cannon":
                                    tower = GameTower.TowerType.Cannon;
                                    break;
                                case "Ice":
                                    tower = GameTower.TowerType.Ice;
                                    break;
                                case "Air":
                                    tower = GameTower.TowerType.Air;
                                    break;
                                case "Flame":
                                    tower = GameTower.TowerType.Flame;
                                    break;
                            }
                            double x = Double.parseDouble(towerString[1]);
                            double y = Double.parseDouble(towerString[2]);
                            int otherWidth = Integer.parseInt(towerString[3]);
                            int otherHeight = Integer.parseInt(towerString[4]);

                            if(otherHeight < screenHeight){
                                y+= (screenHeight-otherHeight)/2;
                            }else if(otherHeight > screenHeight){
                                y-= (otherHeight-screenHeight)/2;
                            }
                            if(otherWidth < screenWidth){
                                x-= (screenWidth-otherWidth)/2;
                            }else if(otherWidth > screenWidth){
                                x-= (otherWidth-screenWidth)/2;
                            }
                            createNewTower(tower,x,y);
                            root.child("Towers").setValue("");
                        }
                    }else if(child.getKey().equals("RemoveTower")){
                        if(dataSnapshot.child("RemoveTower").exists()
                                &&dataSnapshot.child("RemoveTower").getValue(String.class) != null
                                && !dataSnapshot.child("RemoveTower").getValue(String.class).equals("")) {
                            String s = dataSnapshot.child("RemoveTower").getValue(String.class);
                            String[] towerString = s.split(" ");

                            double x = Double.parseDouble(towerString[0]);
                            double y = Double.parseDouble(towerString[1]);
                            int otherWidth = Integer.parseInt(towerString[2]);
                            int otherHeight = Integer.parseInt(towerString[3]);

                            if(otherHeight < screenHeight){
                                y+= (screenHeight-otherHeight)/2;
                            }else if(otherHeight > screenHeight){
                                y-= (otherHeight-screenHeight)/2;
                            }
                            if(otherWidth < screenWidth){
                                x-= (screenWidth-otherWidth)/2;
                            }else if(otherWidth > screenWidth){
                                x-= (otherWidth-screenWidth)/2;
                            }

                            for(int i = 0 ; i < towers.size() ; i++){
                                try {
                                    GameTower tower = towers.get(i);
                                    Rect rect = tower.getRectangle();
                                    if (rect.top < y && rect.bottom > y && rect.left < x && rect.right > x) {
                                        removeTower(tower);
                                    }
                                }catch (IndexOutOfBoundsException e){
                                    continue;
                                }
                            }
                            root.child("RemoveTower").setValue("");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createNewTower(GameTower.TowerType tower, double x, double y) {
        GameTower tow = new GameTower(tower,x,y);
        gameSurface.addTower(tow);
        towers.add(tow);
    }

    private void initLastUsed() {
        for(int i = 0 ; i < lastUsed.length ; i++){
            lastUsed[i] = enemyTypes[i].cooldown;
        }
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
                    gameTime--;
                    if(gameTime<=0){
                        gameIsLost = true;
                        endActivity();
                    }
                    for(int i = 0 ; i < towers.size() ; i++){
                        try {
                            GameTower tow = towers.get(i);
                            tow.lookForTarget(gameSurface.getEnemyList());
                        }catch (IndexOutOfBoundsException e){
                            continue;
                        }
                    }
                    for(int i = 0 ; i < lastUsed.length ; i ++){
                        lastUsed[i]--;
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
    protected void endActivity() {
        timer.cancel();
        root.removeValue();
        Intent endGame = new Intent(this, MultiplayerEndActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("WinOrLose", gameIsWon);
        gameSurface.destroyDrawingCache();
        endGame.putExtra(getString(R.string.bundle_key) , bundle);
        startActivity(endGame);
        finish();
    }

    private void initEnemyButtons() {
        table = new TableLayout(gameMenu.getContext());
        row = new TableRow(table.getContext());
        row.setWeightSum(1);
        for(i = 0 ; i < NUM_OF_ENEMIES ; i++){
            buttons[i] = new ImageButton(gameMenu.getContext());
            buttons[i].setId(i);
            buttons[i].setPadding(0,0,screenWidth/10,0);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                int j = i;
                @Override
                public void onClick(View v) {
                    description_v.setText(getString(enemyTypes[j].description));
                    if(lastUsed[j] <= 0){
                        lastUsed[j] = enemyTypes[j].cooldown;
                        createNewEnemy(enemyTypes[j]);
                        String enemy = "";
                        switch (enemyTypes[j]){
                            case Soldier:
                                enemy = "Soldier";
                                break;
                            case Knight:
                                enemy = "Knight";
                                break;
                            case Priest:
                                enemy = "Priest";
                                break;
                            case Mage:
                                enemy = "Mage";
                                break;
                            case Lord:
                                enemy = "Lord";
                                break;
                        }
                        root.child("Enemies").setValue(enemy);
                    }else{
                        Toast.makeText(getBaseContext(), enemyTypes[j]+getString(R.string.on_cooldown), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            buttons[i].setBackground(getDrawable(enemyTypes[i].portrait));
            row.addView(buttons[i]);
            buttons[i].getLayoutParams().height = 150;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        table.addView(row);
        gameMenu.addView(table , layoutParams);
    }

    private void createNewEnemy(Enemy.EnemyType enemyType) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),enemyType.photo);
        Enemy enemy = new Enemy(enemyType,gameSurface,bitmap,0,screenHeight/2);
        gameSurface.addEnemy(enemy);
    }

    @Override
    public void onBackPressed() {
        //stopPlaying();
        root.removeValue();
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        gameIsLost = true;
        gameSurface.destroyDrawingCache();
        finish();
    }

    @Override
    protected void onStop() {
        root.removeValue();
        super.onStop();
    }

    protected void removeTower(GameTower tower){
        towers.remove(tower);
        gameSurface.removeTower(tower);
        tower.getTarget().setTargetingTower(null);
        tower.setTarget(null);
    }

   // private void gameSound() {
    //    mp = MediaPlayer.create(this, R.raw.improvisation1);
    //    mp.start();
   // }

   // private void stopPlaying() {
   //     if (mp != null) {
    //        mp.stop();
    //        mp.release();
    //        mp = null;
    //    }
   // }

    @Override
    protected void onPause() {
      //  stopPlaying();
        super.onPause();
    }
}
