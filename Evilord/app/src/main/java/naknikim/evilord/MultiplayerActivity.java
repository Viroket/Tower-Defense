package naknikim.evilord;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class MultiplayerActivity extends Activity {

    private ImageButton goToRoomAttack , gotToRoomDefense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_multiplayer);

        goToRoomAttack = (ImageButton) findViewById(R.id.attackButton);
        gotToRoomDefense = (ImageButton) findViewById(R.id.defenseButton);

        gotToRoomDefense.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRoomActivity(false);
            }

        });
        goToRoomAttack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRoomActivity(true);
            }

        });
    }


    private void startRoomActivity(boolean attackOrDefense) {
        Intent room = new Intent(this, RoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(getString(R.string.attacking), attackOrDefense);
        room.putExtra(getString(R.string.bundle_key) , bundle);
        startActivity(room);
        finish();
    }
}
