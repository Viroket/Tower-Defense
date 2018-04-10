package naknikim.evilord;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TableActivity extends Activity {
    private DatabaseReference root;
    private static final int STARTING_RECORD_VALUE = 0;
    private static final int MAX_RECORDS = 10;
    private TextView[][] views = new TextView[MAX_RECORDS][2];
    private RelativeLayout table;
    private int[][] ids = {{R.id.first,R.id.first1},{R.id.second,R.id.second1},{R.id.third,R.id.third1},{R.id.fourth,R.id.fourth1}
                            ,{R.id.fifth,R.id.fifth1},{R.id.sixth,R.id.sixth1},{R.id.seventh,R.id.seventh1},{R.id.eighth,R.id.eighth1}
                            ,{R.id.ninth,R.id.ninth1},{R.id.tenth,R.id.tenth1}};
    private String[] s;
    private int key;
    private ImageButton mainButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        table = new RelativeLayout(getApplicationContext());
        table.setBackground(getDrawable(R.drawable.back2));
        setContentView(table);

        for(int i = 0 ; i < MAX_RECORDS ; i++) {
            views[i][0] = new TextView(table.getContext());
            views[i][1] = new TextView(table.getContext());

            views[i][0].setId(ids[i][0]);
            views[i][1].setId(ids[i][1]);

            views[i][0].setPadding(0,0,100,10);
            views[i][1].setPadding(0,0,0,10);

            views[i][0].setTextColor(Color.BLACK);
            views[i][1].setTextColor(Color.BLACK);

            views[i][0].setText("NULL");
            views[i][1].setText("0");

            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                    , RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                    , RelativeLayout.LayoutParams.WRAP_CONTENT);

            if(i == 0){
                param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);

                param2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                param2.addRule(RelativeLayout.RIGHT_OF,ids[i][0]);
            }else{
                param.addRule(RelativeLayout.BELOW,ids[i-1][0]);
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);

                param2.addRule(RelativeLayout.BELOW,ids[i-1][1]);
                param2.addRule(RelativeLayout.RIGHT_OF,ids[i][0]);
            }
            table.addView(views[i][0],param);
            table.addView(views[i][1],param2);
        }
        mainButton = new ImageButton(table.getContext());
        mainButton.setBackground(getDrawable(R.drawable.backtomain));
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        table.addView(mainButton,param);

        root = FirebaseDatabase.getInstance().getReference();
        if(table != null) {
            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("Records")) {
                            Log.d("REACHED", "RECORDS");
                            for (DataSnapshot kid : child.getChildren()) {
                                s = kid.getValue(String.class).split(" ");
                                key = Integer.parseInt(kid.getKey());
                                Log.d(s[0], s[1]);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int time = Integer.parseInt(s[0]);
                                        views[key - 1][0].setText(s[1]);
                                        views[key - 1][1].setText(String.format("%02d:%02d", time / 60, time % 60));
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
