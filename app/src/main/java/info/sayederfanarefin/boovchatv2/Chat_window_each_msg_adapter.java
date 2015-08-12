package info.sayederfanarefin.boovchatv2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by SayedErfan on 8/11/2015.
 */
public class Chat_window_each_msg_adapter  extends ArrayAdapter<chatss> {
    String user_email;
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public Chat_window_each_msg_adapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
        user_email = sharedPreferences.getString("user_email", "none");

    }



    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final chatss currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.activity_chat_window_per_unit, parent, false);
        }

        row.setTag(currentItem);


        final TextView friend_text = (TextView) row.findViewById(R.id.textView_friend_text);
        final TextView your_text = (TextView) row.findViewById(R.id.textView_you_text);
        final TextView your_name = (TextView) row.findViewById(R.id.textView_you_name);
        final TextView friend_name = (TextView) row.findViewById(R.id.textView_friend_name);
if(currentItem.to.equals(user_email)){
    //received msg
    friend_name.setText(currentItem.from);
    friend_text.setText(currentItem.msg);
    your_text.setText("");
    your_name.setText("");
}else{
    //sent msg
    friend_name.setText("");
    friend_text.setText("");
    your_text.setText(currentItem.msg);
    your_name.setText("you");
}
/*
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof Chat_window) {
                    Chat_window activity = (Chat_window) mContext;
                    activity.openChatWindow(currentItem);
                }
            }
        });
*/
        return row;
    }

}
