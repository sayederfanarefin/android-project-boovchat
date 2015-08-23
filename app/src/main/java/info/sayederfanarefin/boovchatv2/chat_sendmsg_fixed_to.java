package info.sayederfanarefin.boovchatv2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;

public class chat_sendmsg_fixed_to extends AppCompatActivity {
String to, from;
    Button send;
    TextView to_user_dispaly;
    EditText msg;
    String the_msg;
    MobileServiceTable<friendrequests> friend_table;
    MobileServiceClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_sendmsg_fixed_to);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             to = extras.getString("to");
             from = extras.getString("from");
        }
        to_user_dispaly = (TextView) findViewById(R.id.textView_msg_to);
        to_user_dispaly.setText("To: "+to);
        send = (Button) findViewById(R.id.button_send_msg);
        msg = (EditText)findViewById(R.id.editText_msg);
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
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                the_msg = msg.getText().toString();
                chatss c = new chatss();
                c.to = to;
                c.from = from;
                c.msg = the_msg;
                c.seen = false;
                mClient.getTable(chatss.class).insert(c, new TableOperationCallback<chatss>() {
                    public void onCompleted(chatss entity, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            // Insert succeeded
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "Message send!", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {

                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "Something went wrong! Please try again.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
                Intent intent = new Intent(chat_sendmsg_fixed_to.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_sendmsg_fixed_to, menu);
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
