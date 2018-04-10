package naknikim.evilord.Game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import naknikim.evilord.End.NormalGameEndActivity;
import naknikim.evilord.Enemies.Enemy;
import naknikim.evilord.Logic.GameSurface;
import naknikim.evilord.MainActivity;
import naknikim.evilord.R;
import naknikim.evilord.Towers.GameTower;

public class NormalGameActivity extends DefenseGameActivity {
    private static final int NUM_OF_ENEMIES = Enemy.EnemyType.values().length;
    private Enemy[][] waves = new Enemy[NUM_OF_ENEMIES][NUM_OF_ENEMIES];
    private Enemy.EnemyType[][] enemyTypes = {{Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier}
            ,{Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier, Enemy.EnemyType.Priest}
            ,{Enemy.EnemyType.Knight, Enemy.EnemyType.Knight,Enemy.EnemyType.Soldier,Enemy.EnemyType.Soldier,Enemy.EnemyType.Priest}
            ,{Enemy.EnemyType.Knight, Enemy.EnemyType.Knight, Enemy.EnemyType.Mage, Enemy.EnemyType.Knight,Enemy.EnemyType.Priest}
            ,{Enemy.EnemyType.Lord, Enemy.EnemyType.Knight, Enemy.EnemyType.Mage, Enemy.EnemyType.Priest,Enemy.EnemyType.Priest}};
    private TextView wave_v;
    private int currentWave = 0;
    private int k;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout game = new FrameLayout(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gameSound();
        gameSurface = new GameSurface(getApplicationContext());
        gameSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        gameSurface.setZOrderOnTop(true);
        gameMenu = new RelativeLayout(getApplicationContext());


        ImageView bgImagePanel = new ImageView(game.getContext());
        bgImagePanel.setBackgroundResource(R.drawable.gamebackground);

        new Thread(new Runnable() {
            @Override
            public void run() {
                markEnemyPath();
                initWaves();
            }
        }).start();

        initTowerButtons();
        initTimer();
        initGoldView();
        initHealthView();
        initWaveView();
        initDescriptionView();
        initSurfaceListener();
        startGameLoop();

        FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        game.addView(gameSurface,fill);
        game.addView(bgImagePanel,fill);
        game.addView(gameMenu);
        setContentView(game);
    }
    @Override
    public void onBackPressed() {
        stopPlaying();
        Intent main = new Intent(this, MainActivity.class);
        gameSurface.destroyDrawingCache();
        gameIsLost = true;
        startActivity(main);
        finish();
    }
    private void initWaveView(){
        wave_v = new TextView(gameMenu.getContext());
        wave_v.setTextColor(Color.RED);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.RIGHT_OF , gold_v.getId());
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        gameMenu.addView(wave_v,param);
    }
    private void initWaves(){
        for(int i = 0 ; i<waves.length ; i++){
            for(int j = 0 ; j < waves[0].length ; j++){
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),enemyTypes[i][j].photo);
                waves[i][j] = new Enemy(this,enemyTypes[i][j],gameSurface,bitmap,0,screenHeight/2);
                waves[i][j].setHealth(waves[i][j].type.hp*difficulty);
            }
        }
    }
    public void activateWave(){
        waveCounter++;
        for(int i = 0 ; i < waves[0].length  ; i++){
            gameSurface.addEnemy(waves[currentWave][i]);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentWave++;
        if(currentWave>=NUM_OF_ENEMIES){
            difficulty++;
            currentWave = 0;
            initWaves();
        }
    }
    protected void startGameLoop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!gameIsLost){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    playerGold++;
                    gameTime++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            health_v.setText(getString(R.string.health)+playerHP);
                            gold_v.setText(getString(R.string.gold)+playerGold);
                            wave_v.setText(getString(R.string.wave)+waveCounter);
                        }
                    });
                    if(gameTime%15 ==0){
                        activateWave();
                    }
                    for (k = 0; k < towerList.size(); k++) {
                        try {
                            towerList.get(k).setTarget(null);
                            towerList.get(k).lookForTarget(gameSurface.getEnemyList());
                        } catch (IndexOutOfBoundsException e) {
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
    private void gameSound() {
        mp = MediaPlayer.create(this, R.raw.determination);
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
}
