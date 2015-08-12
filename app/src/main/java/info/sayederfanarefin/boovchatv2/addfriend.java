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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.util.List;

public class addfriend extends AppCompatActivity {
Button search_for_friends;
    ProgressBar pb;
    EditText et_search;
    String search_string;private MobileServiceTable<users> users_table;
    private MobileServiceList<users> result;
    private MobileServiceClient mClient;
    private searched_friends_adapter mAdapter;
    ListView result_show;
    TextView tv;
    Boolean ans = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        pb = (ProgressBar) findViewById(R.id.progressBar_retriving_search_result_new_friends);
        pb.setVisibility(View.INVISIBLE);
        result_show = (ListView) findViewById(R.id.listView_results);
        et_search = (EditText) findViewById(R.id.editText_search_friend);
        search_for_friends = (Button) findViewById(R.id.button_search_newfriends);
        mAdapter = new searched_friends_adapter(this, R.layout.activity_add_friend_unit);
         tv = (TextView) findViewById(R.id.textView3);
        try{
            mClient = new MobileServiceClient(
                    "https://boovchat.azure-mobile.net/",
                    "mlDOQRtfCWzENVjrROuNlehjtDCMse49",
                    this
            );

            users_table = mClient.getTable(users.class);
            Log.w("booooooooovchat", "connection stablished");

        }catch(java.net.MalformedURLException e){
            Context context = getApplicationContext();
            Toast toast;
            toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();

        }
        search_for_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                search_string = et_search.getText().toString();
                if (search_string.contains("@")) {
                    search_data("email", search_string);
                } else {
                    search_data("phone_number", search_string);
                }

                tv.setText("Results: ");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addfriend, menu);
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
    public void search_data(final String field, final String value){
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pb.setVisibility(View.VISIBLE);
                            }
                        });
                        final MobileServiceList<users> result =  users_table.where().field(field).eq(value).execute().get();

                        if(result.size()>0){
                            Log.v("boooooooooooooovchat" , "results are here!!!");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.clear();
                                    for (users item : result) {
                                        mAdapter.add(item);
                                    }
                                    result_show.setAdapter(mAdapter);
                                }
                            });
                        }else{
                            Log.v("boooooooooooooovchat" , "results are not here, but it fucking works!!!!");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   tv.setText("Results: No matchs found!");
                                }
                            });
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pb.setVisibility(View.INVISIBLE);
                            }
                        });
                    } catch (Exception exception) {
                        Log.w("booooooooooovchat", "exception1");
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run(){
                                //progress.dismiss();
                                Context context = getApplicationContext();
                                Toast  toast = Toast.makeText(context, "wrong email or password", Toast.LENGTH_SHORT);
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

    public void addFriendReq(final users item) {

        final String friend_req_to = item.email;
        SharedPreferences isloggedin_SharedPreferences = getSharedPreferences("PREFS_NAME", 0);
        final String friend_req_from = isloggedin_SharedPreferences.getString("user_email", "default");
        if(!friend_req_to.equals(friend_req_from)){
            ////////////////////////////////////////////
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    String to = friend_req_to;
                    String from = friend_req_from;
                    try {
                        MobileServiceTable<friendrequests> friend_req_table = mClient.getTable(friendrequests.class);
                        final MobileServiceList<friendrequests> result4 =  friend_req_table.where().field("from").eq(from).execute().get();
                        if(result4 != null && result4.size()>0){
                        for (friendrequests fritem : result4) {
                            if(fritem.to.equals(to)){

                                if(fritem.approved){
                                    runOnUiThread(new Runnable(){

                                        @Override
                                        public void run(){
                                            //progress.dismiss();
                                            mAdapter.clear();
                                            result_show.setAdapter(mAdapter);
                                            Context context = getApplicationContext();
                                            Toast  toast = Toast.makeText(context, "you two are already friend!!", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable(){

                                        @Override
                                        public void run(){
                                            //progress.dismiss();
                                            mAdapter.clear();
                                            result_show.setAdapter(mAdapter);
                                            Context context = getApplicationContext();
                                            Toast  toast = Toast.makeText(context, "you have already send that user a friend request which was not approaved yet", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    });
                                }
                                break;
                            }else {

                                friendrequests frs = new friendrequests();
                                frs.to = friend_req_to;
                                frs.from = friend_req_from;
                                frs.approved = false;
                                frs.rejected = false;

                                mClient.getTable(friendrequests.class).insert(frs, new TableOperationCallback<friendrequests>() {
                                    users item22 = item;
                                    public void onCompleted(friendrequests entity, Exception exception, ServiceFilterResponse response) {
                                        if (exception == null) {
                                            // Insert succeeded
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAdapter.remove(item22);
                                                    Context context = getApplicationContext();
                                                    Toast toast = Toast.makeText(context, "Friend request sent", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            });
                                        } else {
                                            // Insert failed
                                            Context context = getApplicationContext();
                                            Toast toast = Toast.makeText(context, "Something went wrong! Sign up was unsucessful. Please try again.", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                });
                            }
                        }
                        }else{
                            friendrequests frs = new friendrequests();
                            frs.to = friend_req_to;
                            frs.from = friend_req_from;
                            frs.approved = false;
                            frs.rejected = false;

                            mClient.getTable(friendrequests.class).insert(frs, new TableOperationCallback<friendrequests>() {
                                users item22 = item;
                                public void onCompleted(friendrequests entity, Exception exception, ServiceFilterResponse response) {
                                    if (exception == null) {
                                        // Insert succeeded
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.remove(item22);
                                                Context context = getApplicationContext();
                                                Toast toast = Toast.makeText(context, "Friend request sent", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        });
                                    } else {
                                        // Insert failed
                                        Context context = getApplicationContext();
                                        Toast toast = Toast.makeText(context, "Something went wrong! Sign up was unsucessful. Please try again.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                }
                            });
                        }
                        String x = String.valueOf(result4.size());
                        Log.v("search for duplication", x);

                    } catch (Exception exception) {
                        Log.w("booooooooooovchat", "exception1");
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run(){
                                //progress.dismiss();
                                Context context = getApplicationContext();
                                Toast  toast = Toast.makeText(context, "please check your internet connection!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                    return null;
                }
            }.execute();
            /////////////////////////////////////////


        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.remove(item);
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "You can't add yourself as a friend in boovchat -_- Don't be such a forever alone, you can always find some one else rather thn yourself in boovchat..", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
