package info.sayederfanarefin.boovchatv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

import java.util.List;

public class requests extends AppCompatActivity {
    MobileServiceTable<friendrequests> friend_table;
    MobileServiceClient mClient;
    private friend_requests_adapter frmAdapter;
    String user_email;
    ListView friend_rq_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        SharedPreferences isloggedin_SharedPreferences = getSharedPreferences("PREFS_NAME", 0);
        user_email = isloggedin_SharedPreferences.getString("user_email", "default");
        frmAdapter = new friend_requests_adapter(this, R.layout.activity_friend_req_unit);
        friend_rq_listview = (ListView) findViewById(R.id.listView_friendRequests);
        try{
            mClient = new MobileServiceClient(
                     /*credentials from azure mobile services*/,
                    this
            );
            friend_table = mClient.getTable(friendrequests.class);
            Log.w("booooooooovchat", "connection stablished request");

        }catch(java.net.MalformedURLException e){
            Context context = getApplicationContext();
            Toast toast;
            toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();

        }
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        final MobileServiceList<friendrequests> result =  friend_table.where().field("to").eq(user_email).execute().get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                frmAdapter.clear();

                                for (friendrequests item : result) {
                                    if(!item.approved && !item.rejected){
                                        frmAdapter.add(item);
                                    }

                                }
                                friend_rq_listview.setAdapter(frmAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_requests, menu);
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
    public void rejectReq(final friendrequests fr){
        fr.rejected = true;
        if (mClient == null) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    friend_table.update(fr).get();
                    runOnUiThread(new Runnable() {
                        public void run() {

                            frmAdapter.remove(fr);

                            // refreshItemsFromTable();
                        }
                    });

                } catch (Exception exception) {
                    // createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();
    }
    public void approveReq(final friendrequests fr){
        fr.approved = true;

        final friendsss frds = new friendsss();
        frds.friend1 = fr.from;
        frds.friend2 = fr.to;
        if (mClient == null) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    friend_table.update(fr).get();
                    runOnUiThread(new Runnable() {
                        public void run() {

                            frmAdapter.remove(fr);

                            // refreshItemsFromTable();
                        }
                    });

                } catch (Exception exception) {
                    // createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();
        mClient.getTable(friendsss.class).insert(frds, new TableOperationCallback<friendsss>() {
            public void onCompleted(friendsss entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "You two are now friends", Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Something went wrong! Please try again.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

}
