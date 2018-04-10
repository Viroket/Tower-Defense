package naknikim.evilord.Logic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import naknikim.evilord.Enemies.Enemy;
import naknikim.evilord.TowerShots.Shot;
import naknikim.evilord.Towers.GameTower;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback  {
    private GameThread gameThread;
    private List<Enemy> enemyList = new ArrayList<>();
    private List<GameTower> towerList = new ArrayList<>();
    private List<GameTower> towerUpdates = new ArrayList<>();
    private List<Shot> towerShotsList = new ArrayList<>();


    public GameSurface(Context context)  {
        super(context);
        this.setFocusable(true);
        this.getHolder().addCallback(this);
        Constants.CURRENT_CONTEXT = context;
    }

    public void addShot(Shot shot){
        towerShotsList.add(shot);
    }
    public  void addTower(GameTower tower){
        towerList.add(tower);
        towerUpdates.add(tower);
    }
    public  void addEnemy(Enemy enemy){
        enemyList.add(enemy);
    }
    public void update()  {
        for(int i = 0 ; i < enemyList.size() ; i++){
            try {
                enemyList.get(i).update();
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
        for(int i = 0 ; i < towerShotsList.size() ; i++){
            try {
                towerShotsList.get(i).update();
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
    }
    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        for(int i = 0 ; i < enemyList.size() ; i++){
            try {
                enemyList.get(i).draw(canvas);
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
        for(int i = 0 ; i < towerShotsList.size() ; i++){
            try {
                towerShotsList.get(i).draw(canvas);
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
        for(int i = 0 ; i < towerList.size() ; i++){
            try {
                towerList.get(i).draw(canvas);
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= false;
        }
    }
    public void removeShot(Shot shot){
        if(towerShotsList.contains(shot)){
            try {
                towerShotsList.remove(shot);
            }catch (IndexOutOfBoundsException e){

            }
        }
    }
    public void removeEnemy(Enemy enemy){

        if(enemyList.contains(enemy)){
            try {
                enemyList.remove(enemy);
            }catch (IndexOutOfBoundsException e){

            }
        }
    }
    public void removeTower(GameTower tower){
        if(towerList.contains(tower)){
            try {
                towerList.remove(tower);
            }catch (IndexOutOfBoundsException e){

            }
        }
    }
    public List<Enemy> getEnemyList(){
        return enemyList;
    }
}






