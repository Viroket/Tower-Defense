package naknikim.evilord.Game;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Timer;

import naknikim.evilord.Logic.GameSurface;

public abstract class GameActivity extends Activity {
    protected GameSurface gameSurface;
    protected RelativeLayout gameMenu;
    protected int screenHeight = -1;
    protected int screenWidth;
    protected boolean gameIsLost = false;
    protected int gameTime = 0;
    protected int difficulty = 1;
    protected int waveCounter = 0;
    protected Timer timer;
    protected TextView timer_v;
    protected TableLayout table;
    protected TableRow row;
    protected TextView description_v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

    }

    protected abstract void startGameLoop();
    protected abstract void endActivity();
    protected void initTimer(){
        timer_v = new TextView(gameMenu.getContext());
        timer_v.setTextColor(Color.RED);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        gameMenu.addView(timer_v,param);
    }
    protected void initDescriptionView(){
        description_v = new TextView(gameMenu.getContext());
        description_v.setTextSize(20);
        description_v.setTextColor(Color.BLACK);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT
                , TableRow.LayoutParams.WRAP_CONTENT,0.5f);
        row.addView(description_v,params);
    }
}
