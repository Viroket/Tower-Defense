package naknikim.evilord.TowerShots;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import naknikim.evilord.Enemies.Enemy;
import naknikim.evilord.Logic.Constants;
import naknikim.evilord.Logic.GameObject;
import naknikim.evilord.Logic.GameSurface;
import naknikim.evilord.R;
import naknikim.evilord.Towers.GameTower;

/**
 * Created by irenus on 3/23/2017.
 */

public class Shot {
    private long lastDrawNanoTime =-1;
    private Bitmap bitmap;
    private ShotType type;
    private static final float VELOCITY = 0.6f;
    private double movingVectorX = 1;
    private double movingVectorY = 1;
    private Enemy target;
    private GameSurface surface;
    private double x;
    private double y;
    private int targetX;
    private int targetY;
    private GameTower tower;
    private boolean isSmallerX = true;
    private boolean isSmallerY = true;


    public enum ShotType{
        Arrow(R.drawable.shuriken),
        Cannon(R.drawable.cannonshot),
        Ice(R.drawable.iceshot),
        Air(R.drawable.airshot),
        Flame(R.drawable.flameshot);

        private int photo;
        ShotType(int photo){
            this.photo = photo;
        }
     }
    public Shot(GameSurface surface, GameTower tower, Enemy target) {
        this.target = target;
        this.targetX = target.getPosX();
        this.targetY = target.getPosY();
        switch (tower.type){
            case Arrow:
                this.type = ShotType.Arrow;
                break;
            case Cannon:
                this.type = ShotType.Cannon;
                break;
            case Ice:
                this.type = ShotType.Ice;
                break;
            case Air:
                this.type = ShotType.Air;
                break;
            case Flame:
                this.type = ShotType.Flame;
                break;
        }
        bitmap = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources() , this.type.photo);
        this.x = tower.getX()-50;
        this.y = tower.getY()-10;
        this.surface = surface;
        this.tower = tower;
        initMovingAngle();
    }

    private void initMovingAngle() {
        movingVectorX *= (targetX-x)/(targetY-y);
        movingVectorY *= (targetY-y)/(targetX-x);
        if(x>targetX){
            isSmallerX = false;
            movingVectorY*=-1;
        }
        if(y>targetY){
            isSmallerY = false;
            movingVectorX*=-1;
        }
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap,(int)x, (int)y, null);
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void update(){
        long now = System.nanoTime();
        if(lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        int deltaTime = (int) ((now - lastDrawNanoTime)/ 1000000);
        float distance = (VELOCITY * deltaTime)*2;
        double movingVectorLength = Math.sqrt(movingVectorX* movingVectorX + movingVectorY*movingVectorY);
        this.x = x + (int)(distance* movingVectorX / movingVectorLength);
        this.y = y +  (int)(distance* movingVectorY / movingVectorLength);

        if((this.x >= targetX && isSmallerX && this.y >= targetY && isSmallerY)
                ||(this.x >= targetX && isSmallerX && this.y <= targetY && !isSmallerY)
                ||(this.x <= targetX && !isSmallerX && this.y >= targetY && isSmallerY)
                ||(this.x <= targetX && !isSmallerX && this.y <= targetY && !isSmallerY)) {
            surface.removeShot(this);
        }
    }
}
