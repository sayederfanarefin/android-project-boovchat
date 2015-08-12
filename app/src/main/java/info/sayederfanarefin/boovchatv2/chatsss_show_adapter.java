package info.sayederfanarefin.boovchatv2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by SayedErfan on 8/11/2015.
 */
public class chatsss_show_adapter extends ArrayAdapter<chatss>

    {
        Context mContext;

        /**
         * Adapter View layout
         */
        int mLayoutResourceId;

        public chatsss_show_adapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final chatss currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.activity_chatsss_show, parent, false);
        }

        row.setTag(currentItem);


        final TextView tv = (TextView) row.findViewById(R.id.textView_chat_name);
        final TextView tv_seen = (TextView) row.findViewById(R.id.textView_new);
        tv.setText(currentItem.from);
        if(!currentItem.seen){
            tv_seen.setText("new");
        }

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof Chats) {
                    Chats activity = (Chats) mContext;
                    activity.openChatWindow(currentItem);
                }
            }
        });

        return row;
    }


}
