package info.sayederfanarefin.boovchatv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class Friends extends AppCompatActivity {
Button add_friend, friend_requests;
ListView listView_myfriends_data;
    MobileServiceClient mClient;
    MobileServiceTable<friendsss> friendsss_table;
    myfriend_adapter friendsss_Adapter;
    String user_email;
    ProgressBar loading_friend_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        SharedPreferences isloggedin_SharedPreferences = getSharedPreferences("PREFS_NAME", 0);
        user_email = isloggedin_SharedPreferences.getString("user_email", "default");
        add_friend = (Button) findViewById(R.id.button_addfriend);
        friend_requests = (Button) findViewById(R.id.button_friendrequests);
        listView_myfriends_data = (ListView) findViewById(R.id.listView_myfriends);
        loading_friend_list = (ProgressBar) findViewById(R.id.progressBar_loading_friendlists);
        loading_friend_list.setVisibility(View.VISIBLE);
        try{
            mClient = new MobileServiceClient(
                     /*credentials from azure mobile services*/
                    this
            );
            friendsss_table = mClient.getTable(friendsss.class);
            Log.w("booooooooovchat", "connection stablished request");

        }catch(java.net.MalformedURLException e){
            Context context = getApplicationContext();
            Toast toast;
            toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();

        }



        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Friends.this, addfriend.class);
                startActivity(myIntent);
            }
        });
        friend_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Friends.this, requests.class);
                startActivity(myIntent);
            }
        });
        friendsss_Adapter = new myfriend_adapter(this, R.layout.activity_friends_display_unit);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        MobileServiceTable<friendrequests> friend_table;
                        friend_table = mClient.getTable(friendrequests.class);
                        final MobileServiceList<friendrequests> result =  friend_table.where().field("to").eq(user_email).execute().get();

                        if(result != null && result.size()>0) {
                            Log.v("booovchat", "freiwnd req found!");
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                int count =0;
                                for(friendrequests i : result){
                                    if(!i.approved && !i.rejected){
                                        count = count+1;
                                    }
                                }
                                if(count>0){
                                    String friendreq_button_string = "Requests (" +String.valueOf(count)+ ")"; //
                                    friend_requests.setText(friendreq_button_string);
                                }
                            }
                        });

                    } catch (Exception exception) {
                        Log.w("booooooooooovchat", "exception1");
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run(){
                                Context context = getApplicationContext();
                                Toast  toast = Toast.makeText(context, "something went wrong please try again later", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                    return null;
                }
            }.execute();

        } catch (Exception ex) {
            Log.w("booooooooooovchat", "exception2");

        }

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        final MobileServiceList<friendsss> result =  friendsss_table.where().field("friend1").eq(user_email).execute().get();

                        final MobileServiceList<friendsss> result2 =  friendsss_table.where().field("friend2").eq(user_email).execute().get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                friendsss_Adapter.clear();
                                for (friendsss item2 : result) {
                                    friendsss_Adapter.add(item2);
                                }
                                for (friendsss item3 : result2) {
                                    friendsss fr = item3;
                                    String tempo = fr.friend1;
                                    fr.friend1 = fr.friend2;
                                    fr.friend2 = tempo;
                                    friendsss_Adapter.add(fr);
                                }

                                listView_myfriends_data.setAdapter(friendsss_Adapter);
                                loading_friend_list.setVisibility(View.INVISIBLE);
                            }
                        });
                    } catch (Exception exception) {
                        Log.w("booooooooooovchat", "exception1");
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run(){
                                Context context = getApplicationContext();
                                Toast  toast = Toast.makeText(context, "something went wrong please try again later", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                    return null;
                }
            }.execute();

        } catch (Exception ex) {
            Log.w("booooooooooovchat", "exception2");

        }
    }

    public void chat_window(friendsss currentItem) {
        String to = currentItem.friend2;
        String from = currentItem.friend1;
        Intent intent = new Intent(Friends.this, chat_sendmsg_fixed_to.class);
        intent.putExtra("to", to);
        intent.putExtra("from", from);
        startActivity(intent);
    }
}
