package com.mistershorr.databases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendDetailActivity extends AppCompatActivity {

    private EditText friendName;
    private TextView clumsiness;
    private SeekBar clumsinessBar;
    private Switch awesome;
    private TextView gymFrequency;
    private SeekBar gymFrequencyBar;
    private TextView trustworthiness;
    private RatingBar trustworthinessBar;
    private EditText moneyOwed;
    private Button save;
    private Friend friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        wireWidgets();

        Intent lastIntent = getIntent();
        friend = lastIntent.getParcelableExtra(FriendListActivity.EXTRA_FRIEND);

        if(friend!= null){
            friendName.setText(friend.getName());
            clumsinessBar.setProgress(friend.getClumsiness());
            gymFrequencyBar.setProgress((int)friend.getGymFrequency());
            trustworthinessBar.setRating((friend.getTrustworthiness()));
            moneyOwed.setText(String.valueOf(friend.getMoneyOwed()));

        }
        else{
            friend = new Friend();
            friend.setOwnerId(Backendless.UserService.CurrentUser().getObjectId());
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewFriend();
            }
        });

    }


    public void saveNewFriend()
    {
        friend.setName(friendName.getText().toString());
        friend.setClumsiness(clumsinessBar.getProgress());
        friend.setAwesome(awesome.isChecked());
        friend.setGymFrequency(gymFrequencyBar.getProgress());
        friend.setTrustworthiness((int)trustworthinessBar.getRating());
        friend.setMoneyOwed(Double.valueOf(moneyOwed.getText().toString()));



        // save object asynchronously
        Backendless.Persistence.save( friend, new AsyncCallback<Friend>() {
            public void handleResponse( Friend response)
            {
                Toast.makeText(FriendDetailActivity.this, "Friend saved.", Toast.LENGTH_SHORT).show();
                finish();
                // new Friend instance has been saved
            }

            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(FriendDetailActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });
    }



    private void doLogout(){

    }


    private void wireWidgets(){
        friendName = findViewById(R.id.editText_friendDetail_name);
        clumsiness = findViewById(R.id.textView_friendDetail_clumsiness);
        clumsinessBar = findViewById(R.id.seekBar_friendDetail_clumsiness);
        awesome = findViewById(R.id.switch_friendDetail_awesome);
        gymFrequency = findViewById(R.id.textView_friendDetail_gym);
        gymFrequencyBar = findViewById(R.id.seekBar_friendDetail_gym);
        trustworthiness = findViewById(R.id.textView_friendDetail_trustworthiness);
        trustworthinessBar = findViewById(R.id.ratingBar_friendDetail_trustworthiness);
        moneyOwed = findViewById(R.id.editText_friendDetail_moneyOwed);
        save = findViewById(R.id.button_friendDetail_save);
    }
}
