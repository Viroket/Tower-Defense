package naknikim.evilord.End;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import naknikim.evilord.Enemies.Enemy;
import naknikim.evilord.MultiplayerActivity;
import naknikim.evilord.R;

public class MultiplayerEndActivity extends EndActivity {
    private boolean winOrLose;
    private int display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractBundle();
        if(winOrLose){
            display = R.drawable.win;
        }else{
            display = R.drawable.lose;
        }
        screen = new RelativeLayout(getApplicationContext());
        screen.setBackground(getDrawable(R.drawable.mainback));
        initRestartButton();
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(MultiplayerEndActivity.this, MultiplayerActivity.class);
                startActivity(main);
            }
        });
        initMainMenuButton();
        initWinLoseView();
        winLose_v.setBackground(getDrawable(display));
        setContentView(screen);
    }

    @Override
    public void extractBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(getString(R.string.bundle_key));
        winOrLose = bundle.getBoolean(getString(R.string.win_or_lose));
    }
}
