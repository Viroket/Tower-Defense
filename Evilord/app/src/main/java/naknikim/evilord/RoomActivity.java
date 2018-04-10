package naknikim.evilord;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RoomActivity extends AppCompatActivity {

    public boolean attackOrDefense;
    private Button addRoom;
    private EditText roomName;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listOfRooms = new ArrayList<>();
    private String userName = "";
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("Multiplayer");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        extractDataFromBundle();

        addRoom = (Button)findViewById(R.id.addRommButton);
        roomName = (EditText)findViewById(R.id.roomNameText);
        listView = (ListView)findViewById(R.id.findRoomList);

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1 , listOfRooms);
        listView.setAdapter(arrayAdapter);

        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = roomName.getText().toString();
                if(name.equals(""))
                    return;
                root.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.child("roomAvailable").getValue(Boolean.class)){
                                root.child(name).child("roomAvailable").setValue(false);
                                listOfRooms.remove(name);
                                arrayAdapter.notifyDataSetChanged();
                                goToGameRoom(roomName.getText().toString());
                            }else{
                                Toast.makeText(getBaseContext(), "Room is full", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            root.child(name).child("roomAvailable").setValue(true);
                            root.child(name).child("attackOrDefend").setValue(attackOrDefense);
                            listOfRooms.add((name));
                            arrayAdapter.notifyDataSetChanged();
                            goToGameRoom(roomName.getText().toString());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView view1 = (TextView) view;
                root.child((view1).getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals("roomAvailable")){
                                if(child.getValue(Boolean.class)){
                                    root.child((view1).getText().toString()).child("roomAvailable").setValue(false);
                                    listOfRooms.remove((view1).getText().toString());
                                    goToGameRoom((view1).getText().toString());
                                }else{
                                    Toast.makeText(getBaseContext(), "Room is full", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    boolean add = true;
                    if(child.getKey().equals("roomAvailable")){
                        if(child.exists()
                                && child.getValue(Boolean.class)!= null
                                && child.getValue(Boolean.class) == false){
                        }else{
                            add = false;
                        }
                    }else if(child.getKey().equals("attackOrDefend")){
                        if(child.getValue(Boolean.class) != attackOrDefense){
                        }else{
                            add = false;
                        }
                    }
                    if(add){
                        set.add(child.getKey());
                    }
                }
                listOfRooms.clear();
                listOfRooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void goToGameRoom(String roomName) {
        Intent intent = new Intent(getApplicationContext() , MultiplayerGameRoom.class);
        intent.putExtra("roomName",roomName);
        intent.putExtra("userName" , userName);
        intent.putExtra("attackOrDefense",attackOrDefense);
        startActivity(intent);
    }

    //getting the data from main activity
    private void extractDataFromBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(getString(R.string.bundle_key));
        attackOrDefense = bundle.getBoolean(getString(R.string.attacking));
    }
}
