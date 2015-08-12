package info.sayederfanarefin.boovchatv2;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

public class Login extends AppCompatActivity {
    Button buttonSignIn;
    Button buttonSignup;
    EditText editTextEmail;
    EditText editTextPassword;
    private MobileServiceTable<users> users_table;
    private MobileServiceList<users> result;
    private MobileServiceClient mClient;
    String user_email, pass;
    ProgressDialog progress;
   // ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("info.sayederfanarefin.boovchatv2.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point you should start the login activity and finish this one
                Intent myIntent = new Intent(Login.this, Login.class);
                startActivity(myIntent);
                finish();
            }
        }, intentFilter);
       // pb = (ProgressBar) findViewById(R.id.progressBarLogin);
       // pb.setVisibility();
        progress= new ProgressDialog(this);
        Context context = getApplicationContext();
        Toast toast;
        buttonSignIn = (Button) findViewById(R.id.buttonLogin);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        editTextPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        try{
            mClient = new MobileServiceClient(
                    "https://boovchat.azure-mobile.net/",
                    "mlDOQRtfCWzENVjrROuNlehjtDCMse49",
                    this
            );

            users_table = mClient.getTable(users.class);
            Log.w("booooooooovchat", "connection stablished");

        }catch(java.net.MalformedURLException e){
             toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();

        }
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("booooovchat", "sign in button clicked");
                user_email = editTextEmail.getText().toString();
                pass = editTextPassword.getText().toString();


                if (user_email.length() > 0 && pass.length() > 0) {

                    progress.setMessage("Logging in . . . ");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.show();
                    try {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {

                                try {
                                    final MobileServiceList<users> result =  users_table.where().field("email").eq(user_email).execute().get();

                                    if(result.size()>0){

                                        if(user_email.equals(result.get(0).email) && pass.equals(result.get(0).password)){
                                            Log.w("booooooooovchat", "logging in...");
                                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putBoolean("isChecked", true);
                                            editor.putString("user_email", user_email);
                                            editor.commit();
                                            Intent myIntent = new Intent(Login.this, MainActivity.class);
                                            startActivity(myIntent);
                                        }else{
                                            progress.dismiss();
                                            Context context = getApplicationContext();
                                            Toast  toast = Toast.makeText(context, "wrong password", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }else{
                                        progress.dismiss();
                                        Context context = getApplicationContext();
                                        Toast  toast = Toast.makeText(context, "wrong email or password", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                } catch (Exception exception) {
                                    Log.w("booooooooooovchat", "exception1");
                                    runOnUiThread(new Runnable(){

                                        @Override
                                        public void run(){
                                            progress.dismiss();
                                            Context context = getApplicationContext();
                                            Toast  toast = Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT);
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

                } else {
                    Context context = getApplicationContext();
                    Toast  toast = Toast.makeText(context, "Please enter email and pass ", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Login.this, Signup.class);
                startActivity(myIntent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
