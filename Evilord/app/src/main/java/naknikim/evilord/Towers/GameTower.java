package naknikim.evilord.Towers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.List;
import naknikim.evilord.Enemies.Enemy;
import naknikim.evilord.Logic.Constants;
import naknikim.evilord.R;

public class GameTower {
    private Rect rectangle;
    private static final int RECT_SIZE = 100;
    private double x;
    private double y;
    public TowerType type;
    private int damage;
    private int cost;
    private float fireRate;
    private double range;
    private Enemy target;
    private Bitmap towerImg;

    public enum TowerType {
        Arrow(10,500,1f,150,R.drawable.archertower,R.string.arrow_description),
        Cannon(25,500,5f,450,R.drawable.cannontower,R.string.cannon_description),
        Ice(0,500,2f,400,R.drawable.frosttower,R.string.ice_description),
        Air(20,500,1f,500,R.drawable.bansheetower,R.string.air_description),
        Flame(5,500,0.1f,1000,R.drawable.flametower,R.string.flame_description);

        protected int damage;
        public int cost;
        protected float fireRate;
        protected double range;
        public int photo;
        public int description;
        TowerType(int damage, int range ,float fireRate,int cost, int photo,int description){
            this.damage = damage;
            this.range = range;
            this.fireRate = fireRate;
            this.photo = photo;
            this.cost = cost;
            this.description = description;
        }
    }

    public GameTower(TowerType type,double x, double y){
        this.rectangle = new Rect((int)x-(RECT_SIZE/2),(int) y-(RECT_SIZE/2),(int) x+(RECT_SIZE/2), (int)y+(RECT_SIZE/2));
        this.type = type;
        this.x = x;
        this.y = y;
        this.damage = type.damage;
        this.range = type.range;
        this.fireRate = type.fireRate;
        this.cost = type.cost;
        this.target = null;
        towerImg = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources() , this.type.photo);
    }

    public int getTowerDamage() {
        return damage;
    }

    public float getFireRate(){
        return fireRate;
    }

    public int getTowerCost() {
        return cost;
    }

    public void setTarget(Enemy target){
        if(target == null){
            this.target = null;
            return;
        }
        this.target = target;
        this.target.setTargetingTower(this);
    }

    public void draw(Canvas canvas) {
        rectangle.set((int)x-(RECT_SIZE/2),(int) y-(RECT_SIZE/2),(int) x+(RECT_SIZE/2),(int) y+(RECT_SIZE/2));
        canvas.drawBitmap(towerImg,rectangle.left,rectangle.top,null);
    }

    public void lookForTarget(List<Enemy> enemies){
        if(target != null){
            return;
        }
        double dist = range+1;
        double newDist;
        Enemy target = null;

        for(int i = 0 ; i < enemies.size() ; i++){
            try {
                Enemy enemy = enemies.get(i);
                if (isInRange(enemy)) {
                    newDist = Math.sqrt(Math.pow((this.x - enemy.getPosX()), 2) + Math.pow((this.y - enemy.getPosY()), 2));
                    if (newDist < dist) {
                        dist = newDist;
                        target = enemy;

                    }
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
        if(target != null){
            setTarget(target);
        }
    }

    public boolean isInRange(Enemy enemy){
        return Math.sqrt(Math.pow((this.x - enemy.getPosX()), 2) + Math.pow((this.y - enemy.getPosY()), 2)) < range;
    }

    public double getX()  {
        return this.x;
    }

    public double getY()  {
        return this.y;
    }

    public Rect getRectangle(){
        return rectangle;
    }

    public static int getRectSize(){
        return RECT_SIZE;
    }

    public Enemy getTarget(){
        return target;
    }

}
