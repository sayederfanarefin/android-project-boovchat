package info.sayederfanarefin.boovchatv2;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by SayedErfan on 8/9/2015.
 */
public class searched_friends_adapter extends ArrayAdapter<users> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public searched_friends_adapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final users currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.activity_add_friend_unit, parent, false);
        }

        row.setTag(currentItem);

        final Button add_button = (Button) row.findViewById(R.id.button_add_friend_unit);
        final TextView tv = (TextView) row.findViewById(R.id.user_name_display_unit);
        tv.setText(currentItem.getName());
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext instanceof addfriend) {
                    addfriend activity = (addfriend) mContext;
                    activity.addFriendReq(currentItem);
                }
            }
        });
        return row;
    }
}
