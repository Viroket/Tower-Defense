package naknikim.evilord.Enemies;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.List;

import naknikim.evilord.Game.DefenseGameActivity;
import naknikim.evilord.Game.GameActivity;
import naknikim.evilord.Logic.GameObject;
import naknikim.evilord.Logic.GameSurface;
import naknikim.evilord.R;
import naknikim.evilord.TowerShots.Shot;
import naknikim.evilord.Towers.GameTower;

public class Enemy extends GameObject {
    private static final int NEIGHBOR_DISTANCE = 50;
    private int posX;
    private int posY;
    private int currentHp;
    private ArrayList<GameTower> targetingTowers = new ArrayList<>();
    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;
    private int rowUsing = ROW_LEFT_TO_RIGHT;
    private int colUsing;
    private int pathCount = 0;
    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;
    private static final float VELOCITY = 0.1f;    // Velocity of game character (pixel/millisecond)
    private int movingVectorX = 10;
    private int movingVectorY = 5;
    private long lastDrawNanoTime =-1;
    private GameSurface gameSurface;
    public EnemyType type;
    private float speed;
    private int cost;
    private int goldWorth;
    private int photo;
    private boolean isDead = false;
    private int i;
    private ArrayList<Enemy> neighbors = new ArrayList<>();
    private GameActivity activity = null;
    private GameTower tower;

    public void setHealth(int health) {
        this.currentHp = health;
    }

    public enum EnemyType{
        Soldier(2f,150,100,10, R.drawable.soldier,R.string.soldier_description,3,R.drawable.soldierportrait),
        Knight(1.5f,400,400,20,R.drawable.knight,R.string.knight_description,7,R.drawable.knightportrait),
        Priest(2f,300,150,30,R.drawable.priest,R.string.priest_description,10,R.drawable.priestportrait),
        Mage(1f,400,80,50,R.drawable.mage,R.string.mage_description,12,R.drawable.mageportrait),
        Lord(1f,500,2000,100,R.drawable.lord,R.string.lord_description,30,R.drawable.lordportrait);
        protected float speed;
        protected int cost;
        public int hp;
        public int photo;
        protected int goldWorth;
        public int description;
        public int cooldown;
        public int portrait;
        EnemyType(float speed, int cost,int hp,int goldWorth, int photo,int description,int cooldown,int portrait){
            this.speed = speed;
            this.photo = photo;
            this.cost = cost;
            this.hp = hp;
            this.goldWorth = goldWorth;
            this.description = description;
            this.cooldown = cooldown;
            this.portrait = portrait;
        }
    }
    public Enemy(EnemyType type, GameSurface gameSurface, Bitmap image, int posX , int posY) {
        super(image, 4, 3, posX, posY);
        this.gameSurface= gameSurface;
        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        for(int col = 0; col< this.colCount; col++) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
        this.type = type;
        this.posX = posX;
        this.posY = posY;
        this.speed = type.speed;
        this.photo = type.photo;
        this.cost = type.cost;
        this.currentHp = type.hp;
        this.goldWorth = type.goldWorth;
    }
    public Enemy(DefenseGameActivity activity, EnemyType type, GameSurface gameSurface, Bitmap image, int posX , int posY) {
        super(image, 4, 3, posX, posY);
        this.gameSurface= gameSurface;
        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        for(int col = 0; col< this.colCount; col++) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
        this.type = type;
        this.posX = posX;
        this.posY = posY;
        this.speed = type.speed;
        this.photo = type.photo;
        this.cost = type.cost;
        this.currentHp = type.hp;
        this.goldWorth = type.goldWorth;
        this.activity = activity;
    }
    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    public void update()  {
        this.colUsing++;
        if(colUsing >= this.colCount)  {
            this.colUsing = 0;
        }
        long now = System.nanoTime();
        if(lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        int deltaTime = (int) ((now - lastDrawNanoTime)/ 1000000);
        float distance = (VELOCITY * deltaTime)*speed;
        double movingVectorLength = Math.sqrt(movingVectorX* movingVectorX + movingVectorY*movingVectorY);
        this.posX = posX + (int)(distance* movingVectorX / movingVectorLength);
        this.posY = posY +  (int)(distance* movingVectorY / movingVectorLength);


        int width = this.gameSurface.getWidth();
        int high = this.gameSurface.getHeight();
        int widthForAll = width/7;
        int highStartAndEnd = high/2;

        boolean[] pathConditions = {posX < widthForAll,posX >= widthForAll  //the enemy path is set according to these rules
                ,posY <= highStartAndEnd/3.8,posX >= widthForAll*2
                ,posY >= high/1.5,posX >= widthForAll*2.9
                ,posY <= highStartAndEnd/3.8,posX >= widthForAll*3.8
                ,posY >= high/1.5,posX >= widthForAll*4.6
                ,posY <= highStartAndEnd/3.8,posX >= widthForAll*5.5
                ,posY >= highStartAndEnd};
        int[][] path = {{1,0},{0,-1},{1,0},{0,1},{1,0},{0,-1},{1,0},{0,1},{1,0},{0,-1},{1,0},{0,1},{1,0}};

        if(pathCount >=0 && pathCount < path.length) {
            if (pathConditions[pathCount]) {
                this.movingVectorX = path[pathCount][0];
                this.movingVectorY = path[pathCount][1];
                pathCount++;
            }
        }
        int i = pathCount;
        if(i%2 != 0) {
            this.rowUsing = ROW_LEFT_TO_RIGHT;
        }
        else if(i == 2 || i == 6 || i == 10) {

            this.rowUsing = ROW_BOTTOM_TO_TOP;
        }
        else if(i == 4 || i == 8 || i == 12) {
            this.rowUsing = ROW_TOP_TO_BOTTOM;
        }
        if(posX >= width){
            gameSurface.removeEnemy(Enemy.this);
            for(int j = 0 ; j < targetingTowers.size() ; j++){
                try {
                    removeTargetingTower(targetingTowers.get(j));
                }catch (IndexOutOfBoundsException e){
                    continue;
                }
            }
            if(activity != null && activity instanceof DefenseGameActivity) {
                ((DefenseGameActivity) activity).subtractPlayerHealth();
            }
        }
    }

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,posX, posY, null);
        this.lastDrawNanoTime = System.nanoTime();
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setTargetingTower(GameTower targetingTower){
        if(targetingTower == null){
            return;
        }
        targetingTowers.add(targetingTower);
        startTakingDamage();
    }

    private void removeTargetingTower(GameTower tower){
        if(tower != null && targetingTowers.contains(tower)) {
            try {
                tower.setTarget(null);
                targetingTowers.remove(tower);
            }catch (IndexOutOfBoundsException e){
            }
        }
    }

    private void startTakingDamage() {
        for(i = 0 ; i < targetingTowers.size() ; i++) {
            try {
                tower = targetingTowers.get(i);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GameTower tower2 = tower;
                        while (tower2 != null && tower2.getTarget() != null && currentHp > 0 && tower2.isInRange(Enemy.this)) {
                            identifyTowerDamage(tower2);
                        }
                        removeTargetingTower(tower2);
                    }
                }).start();
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
    }

    private void identifyTowerDamage(GameTower tower){
        gameSurface.addShot(new Shot(gameSurface,tower,Enemy.this));
        switch (tower.type){
            case Arrow:
            case Air:
                //damage target
                try {
                    Thread.sleep((long) (1000 * tower.getFireRate()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case Ice:
                //slow target
                speed /= 6;
                try {
                    Thread.sleep((long) (1000 * 3));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speed = type.speed;
                break;
            case Cannon:
            case Flame:
                findNeighbors(gameSurface.getEnemyList());
                transferToNearby(tower.getTowerDamage() * 0.6);
                //deal splash damage
                try {
                    Thread.sleep((long) (1000 * tower.getFireRate()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
        takeDamage(tower.getTowerDamage());
    }

    public void transferToNearby(double damage) {
        for(int i = 0 ; i < neighbors.size() ; i++){
            try {
                neighbors.get(i).takeDamage(damage);
            }catch (IndexOutOfBoundsException e){
                continue;
            }catch (NullPointerException e){
                continue;
            }
        }
        neighbors.clear();
    }

    public void takeDamage(double damage) {
        this.currentHp -= damage;
        if(currentHp <= 0){
            die();
        }
    }

    public void findNeighbors(List<Enemy> enemies){
        for(int i = 0 ; i < enemies.size() ; i++){
            try {
                Enemy enemy = enemies.get(i);
                if((enemy.getPosX() +NEIGHBOR_DISTANCE > posX && enemy.getPosX()-NEIGHBOR_DISTANCE < posX)
                        && (enemy.getPosY() +NEIGHBOR_DISTANCE > posY && enemy.getPosY()-NEIGHBOR_DISTANCE < posY)){
                    neighbors.add(enemy);
                }
            }catch (IndexOutOfBoundsException e){
                continue;
            }
        }
    }

    private void die(){
        if(!isDead) {
            gameSurface.removeEnemy(Enemy.this);
            if(activity != null && activity instanceof DefenseGameActivity) {
                ((DefenseGameActivity) activity).increasePlayerGold(goldWorth);
            }
            speed = 0;
            isDead = true;
        }
    }
}
