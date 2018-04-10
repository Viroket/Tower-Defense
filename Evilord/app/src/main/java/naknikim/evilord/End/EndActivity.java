package naknikim.evilord.End;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import naknikim.evilord.LoginActivity;
import naknikim.evilord.MainActivity;
import naknikim.evilord.MultiplayerActivity;
import naknikim.evilord.R;

public abstract class EndActivity extends Activity {
    protected ImageButton mainMenuButton;
    protected ImageButton restartButton;
    protected TextView score_v;
    protected ImageView winLose_v;
    protected RelativeLayout screen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void initMainMenuButton(){
        mainMenuButton = new ImageButton(screen.getContext());
        mainMenuButton.setBackground(getDrawable(R.drawable.backtomain));
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(EndActivity.this, MainActivity.class);
                startActivity(main);
            }
        });
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        screen.addView(mainMenuButton,param);
    }
    public void initRestartButton(){
        restartButton = new ImageButton(screen.getContext());
        restartButton.setBackground(getDrawable(R.drawable.roomscreen));
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        screen.addView(restartButton,param);
    }
    public void initScoreView(){
        score_v = new TextView(screen.getContext());
        score_v.setId(R.id.score_v);
        score_v.setTextSize(50);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        screen.addView(score_v,param);
    }
    public void initWinLoseView(){
        winLose_v = new ImageView(screen.getContext());
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        screen.addView(winLose_v,param);
    }
    public abstract void extractBundle();
}
