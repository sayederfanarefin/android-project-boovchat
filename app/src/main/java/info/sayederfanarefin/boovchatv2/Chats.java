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
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.notifications.NotificationsManager;
//import com.google.android.gcm.GCMRegistrar;
public class Chats extends AppCompatActivity {
    public static final String SENDER_ID = ""; //sender id
    ListView listView_chats_;
    public static MobileServiceClient mClient;
    MobileServiceTable<chatss> chatss_table;
    String user_email;
    ProgressBar loading_chats;
    private chatsss_show_adapter muaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        listView_chats_ = (ListView) findViewById(R.id.listView_chats);
        SharedPreferences isloggedin_SharedPreferences = getSharedPreferences("PREFS_NAME", 0);
        user_email = isloggedin_SharedPreferences.getString("user_email", "default");
        muaAdapter = new chatsss_show_adapter(this, R.layout.activity_chatsss_show);
        loading_chats = (ProgressBar) findViewById(R.id.progressBar_loading_chats);
        loading_chats.setVisibility(View.VISIBLE);
        try{
            mClient = new MobileServiceClient(
                     /*credentials from azure mobile services*/
                    this
            );
            NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);

            chatss_table = mClient.getTable(chatss.class);
            try {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        try {

                            final MobileServiceList<chatss> result =  chatss_table.where().field("to").eq(user_email).or().field("from").eq(user_email).execute().get();

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    muaAdapter.clear();
                                    for (chatss item : result) {
                                        Boolean has = false;
                                        for(int iii = 0; iii <muaAdapter.getCount(); iii++){
                                            if(item.equals(muaAdapter.getItem(iii))){
                                                has =true;
                                                break;
                                            }
                                        }
                                        if(!has){
                                            muaAdapter.add(item);
                                        }

                                    }
                                    listView_chats_.setAdapter(muaAdapter);
                                    loading_chats.setVisibility(View.INVISIBLE);
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

            }
        }catch(java.net.MalformedURLException e){
            Context context = getApplicationContext();
            Toast toast;
            toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chats, menu);
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

    public void openChatWindow(chatss currentItem) {
        String to = currentItem.to;
        String from = currentItem.from;
        Intent intent = new Intent(Chats.this, Chat_window.class);
        intent.putExtra("tooo", from);
        intent.putExtra("frommm", to);
        startActivity(intent);
    }
}
