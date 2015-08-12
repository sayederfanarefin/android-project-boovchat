package info.sayederfanarefin.boovchatv2;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class MainActivity extends TabActivity {
    boolean isloggedin = false;
    String email="";
    String name;
    private MobileServiceTable<users> users_table;
    private MobileServiceList<users> result;
    private MobileServiceClient mClient;
    TextView user_name_Displayer ;
    Button logout;
    ProgressDialog progress;//= new ProgressDialog(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("info.sayederfanarefin.boovchatv2.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point you should start the login activity and finish this one
                Intent myIntent = new Intent(MainActivity.this, Login.class);
                startActivity(myIntent);
                finish();
            }
        }, intentFilter);
        SharedPreferences isloggedin_SharedPreferences = getSharedPreferences("PREFS_NAME", 0);
        isloggedin = isloggedin_SharedPreferences.getBoolean("isChecked", false);
        progress= new ProgressDialog(this);
        if (isloggedin) {
            Context context = getApplicationContext();
            Toast toast ;
            email = isloggedin_SharedPreferences.getString("user_email", "default");
            TabHost mTabHost =  getTabHost();
            mTabHost.addTab(mTabHost.newTabSpec("Chats").setIndicator("Chats").setContent(new Intent(this , Chats.class )));
            mTabHost.addTab(mTabHost.newTabSpec("Friends").setIndicator("Friends").setContent(new Intent(this  ,Friends.class )));

            mTabHost.setCurrentTab(0);

            try{
                mClient = new MobileServiceClient(
                        "https://boovchat.azure-mobile.net/",
                        "mlDOQRtfCWzENVjrROuNlehjtDCMse49",
                        this
                );

                users_table = mClient.getTable(users.class);
                Log.w("booooooooovchat", "connection stablished");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            final MobileServiceList<users> users_result =  users_table.where().field("email").eq(email).execute().get();
                            name = users_result.get(0).name;
                            initUserData();
                        } catch (Exception exception) {
                            Log.w("booooooooooovchat", "exception1");
                            runOnUiThread(new Runnable(){

                                @Override
                                public void run(){
                                    // progress.dismiss();
                                    Context context = getApplicationContext();
                                    Toast  toast = Toast.makeText(context, "something went wrong please try again later!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                        return null;
                    }
                }.execute();

            }catch(java.net.MalformedURLException e){
                toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Please login", Toast.LENGTH_SHORT);
            toast.show();
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void initUserData(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                user_name_Displayer = (TextView) findViewById(R.id.user_name_display);
                user_name_Displayer.setText(name);
                logout = (Button) findViewById(R.id.button_logout);
                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progress.setMessage("Logging out . . . ");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.show();
                        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("isChecked", false);
                        editor.putString("user_email", "gu");
                        editor.commit();
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("info.sayederfanarefin.boovchatv2.ACTION_LOGOUT");
                        sendBroadcast(broadcastIntent);
                    }
                });
            }
        });

    }
}
