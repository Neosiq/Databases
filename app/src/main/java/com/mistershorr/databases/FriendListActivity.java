package com.mistershorr.databases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FriendListActivity extends AppCompatActivity {

    private ListView listViewFriend;
    private List<Friend> friendsList;
    private ArrayAdapter<Friend> friendAdapter;
    public static final String EXTRA_FRIEND = "Friend";
    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);


        wireWidgets();
        setListeners();
    }


    private void deleteFriend(Friend friend) {


        Backendless.Persistence.of(Friend.class).remove(friend,
                new AsyncCallback<Long>() {
                    public void handleResponse(Long response) {
                        Toast.makeText(FriendListActivity.this, "Friend removed.", Toast.LENGTH_SHORT).show();
                    }

                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(FriendListActivity.this, fault.getCode(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case (R.id.longClick_deleteMenu_delete): {
                Friend friend = friendsList.get(info.position);
                deleteFriend(friend);
                friendsList.remove(info.position);
                friendAdapter.notifyDataSetChanged();
            }


        }
        return super.onContextItemSelected(item);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_name:
                Collections.sort(friendsList);
                friendAdapter.notifyDataSetChanged();
                return true;
            case R.id.sort_money:
                Collections.sort(friendsList, new Comparator<Friend>() {
                    @Override
                    public int compare(Friend friend, Friend t1) {
                        return (int) (friend.getMoneyOwed() - t1.getMoneyOwed());

                    }
                });

                friendAdapter.notifyDataSetChanged();
                return true;
            case R.id.logout:
                finish();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sorting_menu, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        String userId = Backendless.UserService.CurrentUser().getObjectId();
        String whereClause = "";

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        final DataQueryBuilder dataQueryBuilder = queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(Friend.class).find(queryBuilder, new AsyncCallback<List<Friend>>() {
            @Override
            public void handleResponse(List<Friend> foundFriends) {
                // every loaded object from the "Contact" table is now an individual java.util.Map
                Log.d("LOADED FRIENDS", "handleResponse: " + foundFriends.toString());
                friendsList = foundFriends;
                friendAdapter = new FriendAdapter(friendsList);
                listViewFriend = findViewById(R.id.listView_friendList_list);
                listViewFriend.setAdapter(friendAdapter);

                listViewFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Intent targetIntent = new Intent(FriendListActivity.this, FriendDetailActivity.class);
                        targetIntent.putExtra(EXTRA_FRIEND, friendsList.get(position));
                        startActivity(targetIntent);
                        fileList();
                    }
                });

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                Toast.makeText(FriendListActivity.this, fault.getDetail(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setListeners() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FriendIntent = new Intent(FriendListActivity.this, FriendDetailActivity.class);
                startActivity(FriendIntent);
            }
        });
        listViewFriend.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        registerForContextMenu(listViewFriend);


    }

    private void wireWidgets() {
        add = findViewById(R.id.imageButton_friendList_add);
        listViewFriend = findViewById(R.id.listView_friendList_list);
    }

    private class FriendAdapter extends ArrayAdapter<Friend> {
        // make an instance variable to keep track of the hero list
        private List<Friend> friendsList;

        public FriendAdapter(List<Friend> friendsList) {
            super(FriendListActivity.this, -1, friendsList);
            this.friendsList = friendsList;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_friend, parent, false);
            }

            TextView textViewName = convertView.findViewById(R.id.textView_friendList_name);
            TextView textViewRanking = convertView.findViewById(R.id.textView_friendList_moneyOwed);
            TextView textViewDescription = convertView.findViewById(R.id.textView_friendList_clumsiness);

            textViewName.setText(friendsList.get(position).getName());
            textViewRanking.setText("$" + friendsList.get(position).getMoneyOwed());
            textViewDescription.setText("" + friendsList.get(position).getClumsiness());


            return convertView;
        }


    }
}
