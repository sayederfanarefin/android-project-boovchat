package info.sayederfanarefin.boovchatv2;

import android.content.Context;
import android.content.Intent;
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
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

public class Chat_window extends AppCompatActivity {
String to, from;
    MobileServiceClient mClient;
    MobileServiceTable<chatss> chats_table;
    Chat_window_each_msg_adapter cwema;
    ListView listView_chats_chat_window__;
    ProgressBar pb;
    ProgressBar pb_progressBar_sendingText;
Button send_msg;
    EditText the_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        pb = (ProgressBar) findViewById(R.id.progressBar_loadingChats_withafriend);
        pb_progressBar_sendingText = (ProgressBar) findViewById(R.id.progressBar_sendingText);
        pb.setVisibility(View.VISIBLE);
        pb_progressBar_sendingText.setVisibility(View.INVISIBLE);
        the_msg = (EditText) findViewById(R.id.editText_msg_chat_window);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            to = extras.getString("tooo");
            from = extras.getString("frommm");
        }
        TextView tv = (TextView) findViewById(R.id.textView_to_chat_user);
        tv.setText(to);
        send_msg = (Button) findViewById(R.id.button_send_chat_window);
        try{
            mClient = new MobileServiceClient(
                /*credentials from azure mobile services*/
                    this
            );
            chats_table = mClient.getTable(chatss.class);
            Log.w("booooooooovchat", "connection stablished request");

        }catch(java.net.MalformedURLException e){
            Context context = getApplicationContext();
            Toast toast;
            toast = Toast.makeText(context, "Something went wrong! Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();

        }
        listView_chats_chat_window__ = (ListView) findViewById(R.id.listView_chats_chat_window);
        cwema = new Chat_window_each_msg_adapter(this, R.layout.activity_chat_window_per_unit);
        checkChat();
        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_progressBar_sendingText.setVisibility(View.VISIBLE);

                if(the_msg.getText().toString().length()>0){
                    chatss u = new chatss();
                    u.to = to;
                    u.from = from;
                    u.msg = the_msg.getText().toString();
                    u.seen = false;
                    mClient.getTable(chatss.class).insert(u, new TableOperationCallback<chatss>() {
                        public void onCompleted(chatss entity, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                // Insert succeeded
                                the_msg.setText("");
                                the_msg.setHint("Type your message here");
                                Context context = getApplicationContext();
                                Toast toast = Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT);
                                toast.show();checkChat();
                            } else {
                                // Insert failed

                                Context context = getApplicationContext();
                                Toast toast = Toast.makeText(context, "Something went wrong! Please try again.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                }else{
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Please type something to send!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                pb_progressBar_sendingText.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_window, menu);
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
    public void checkChat(){
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        MobileServiceTable<friendrequests> friend_table;
                        friend_table = mClient.getTable(friendrequests.class);
                        final MobileServiceList<chatss> result =  chats_table.where().field("to").eq(to).and().field("from").eq(from).or().field("to").eq(from).and().field("from").eq(to).orderBy("__createdAt", QueryOrder.Descending).top(10).top(10).execute().get(); //.orderBy("_createdAt", QueryOrder.Ascending)
                        //final MobileServiceList<chatss> result2 =  chats_table.where().field("to").eq(from).and().field("from").eq(to).orderBy("__createdAt", QueryOrder.Ascending).top(10).execute().get();

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                cwema.clear();
                                for(int ii = result.size()-1; ii >=0 ; ii-- ){
                                    cwema.add(result.get(ii));
                                }


                                listView_chats_chat_window__.setAdapter(cwema);
                                listView_chats_chat_window__.setSelection(cwema.getCount() - 1);
                                pb.setVisibility(View.INVISIBLE);

                            }
                        });

                    } catch (Exception exception) {

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
}
