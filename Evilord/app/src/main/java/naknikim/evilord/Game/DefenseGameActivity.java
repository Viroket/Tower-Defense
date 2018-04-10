package naknikim.evilord.Game;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import naknikim.evilord.End.NormalGameEndActivity;
import naknikim.evilord.R;
import naknikim.evilord.Towers.GameTower;

public abstract class DefenseGameActivity extends GameActivity {
    private static final int STARTING_HP = 20;
    private static final int STARTING_GOLD = 500;
    protected GameTower.TowerType[] types = GameTower.TowerType.values();
    protected static List<GameTower> towerList = new ArrayList<>();
    protected static final int TOWER_TYPE_AMOUNT = GameTower.TowerType.values().length;
    protected ImageButton[] buttons = new ImageButton[TOWER_TYPE_AMOUNT];
    protected GameTower.TowerType currentTypePicked = null;
    protected int i;
    protected int[][] locationsTaken;
    protected GameTower tower;
    protected int playerHP = STARTING_HP;
    protected int playerGold = STARTING_GOLD;
    protected TextView gold_v;
    protected TextView health_v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        while(screenHeight == -1 && screenWidth == -1){

        }
        locationsTaken = new int[screenHeight][screenWidth];
    }

    protected boolean checkIsTaken(int x , int y){
        if(towerList.isEmpty()){
            Log.v("First Tower","True");
            return true;
        }
        int width = GameTower.getRectSize()/2;
        int height = GameTower.getRectSize()/2;
        for(int i = y-height ; i< y+height ; i++){
            for (int j = x-width ; j < x+width ; j++){
                if(i < 0 || j < 0 || i >= locationsTaken.length || j >= locationsTaken[0].length) {
                    Log.v("NAKNIK", "INDEX OUT OF BOUNDS IN CHECK");
                    return false;
                }
                if(locationsTaken[i][j] == 1){
                    Log.v("Tower","False");
                    return false;
                }
            }
        }
        Log.v("Tower","True");
        return true;
    }
    public void subtractPlayerHealth(){
        synchronized ((Integer)playerHP) {
            playerHP--;
            if (playerHP <= 0) {
                gameIsLost = true;
                endActivity();
            }
        }
    }
    protected void endActivity() {
        timer.cancel();
        Intent endGame = new Intent(this, NormalGameEndActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.game_time), gameTime);

        endGame.putExtra(getString(R.string.bundle_key) , bundle);
        startActivity(endGame);
        finish();
    }
    protected void removeTower(GameTower tower){
        Rect rect = tower.getRectangle();
        for(int i = rect.top ; i < rect.bottom ; i++){
            for(int j = rect.left ; j < rect.right; j++){
                if(i < locationsTaken.length && j < locationsTaken[0].length) {
                    locationsTaken[i][j] = 0;
                }
            }
        }
        towerList.remove(tower);
        gameSurface.removeTower(tower);
        tower.setTarget(null);
    }
    public void increasePlayerGold(int goldWorth) {
        playerGold += goldWorth;
    }
    protected void markEnemyPath(){
        for (int i = locationsTaken.length - 150; i < locationsTaken.length; i++) {
            for(int j = 0 ; j < locationsTaken[0].length ; j++) {
                locationsTaken[i][j] = 1;
            }
        }
        int[] xs = {(locationsTaken[0].length/7),locationsTaken[0].length};
        int[] ys = {locationsTaken.length/8,(int) (locationsTaken.length/1.2),locationsTaken.length/8,(int)(locationsTaken.length/1.2)
                , locationsTaken.length/8 ,locationsTaken.length/2};
        int w = 0 ,h = locationsTaken.length/2;
        int k;
        for(int i = 0 ; i < ys.length ; i++){   // Mark the walking trail so no towers can block it
            for(;w< xs[0]*(i+1) ; w++){
                for(k = 25 ; k < 75; k ++){
                    if(w == xs[0]*(i+1) || w+k < 0){
                        locationsTaken[h+k][w] = 1;
                    }else{
                        locationsTaken[h+k][w+k] = 1;
                    }
                }
            }
            if(i%2 == 0){
                for(; h > ys[i] ; h--){
                    for(k = -25 ; k < 25; k ++){
                        locationsTaken[h+k][w+k] = 1;
                    }
                }
            }else{
                for(; h < ys[i] ; h++){
                    for(k = -25 ; k < 25; k ++){
                        locationsTaken[h+k][w+k] = 1;
                    }
                }
            }
            if(i == ys.length-1){
                for(;w< xs[1]; w++){
                    for(k = 25 ; k < 75; k ++){
                        if(k<0){
                            locationsTaken[h+k][w+k] = 1;
                        }else{
                            locationsTaken[h+k][w] = 1;
                        }
                    }
                }
            }
        }
    }
    protected boolean checkIfTower(GameTower tower , int x , int y) {
        Rect rect = tower.getRectangle();
        return rect.top < y && rect.bottom > y
                && rect.left < x && rect.right > x;
    }
    protected void initTowerButtons() {
        table = new TableLayout(gameMenu.getContext());
        row = new TableRow(table.getContext());
        row.setWeightSum(1);
        for(i = 0 ; i < TOWER_TYPE_AMOUNT ; i++){
            buttons[i] = new ImageButton(gameMenu.getContext());
            buttons[i].setId(i);
            buttons[i].setPadding(0,0,screenWidth/10,0);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                int j = i;
                @Override
                public void onClick(View v) {
                    description_v.setText(getString(types[j].description));
                    if(types[j].cost <= playerGold){
                        currentTypePicked = types[j];
                    }else{
                        Toast.makeText(getBaseContext(), getString(R.string.not_enough_gold)+types[j]+getString(R.string.tower)+types[j].cost+getString(R.string.required), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            buttons[i].setBackground(getDrawable(types[i].photo));
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
    protected void initHealthView(){
        health_v = new TextView(gameMenu.getContext());
        health_v.setTextColor(Color.RED);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        gameMenu.addView(health_v,param);
    }
    protected void initGoldView() {
        gold_v = new TextView(gameMenu.getContext());
        gold_v.setTextColor(Color.RED);
        gold_v.setId(R.id.gold_v);
        gold_v.setPadding(0,0,100,0);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        gameMenu.addView(gold_v,param);
    }
}
