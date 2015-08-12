package info.sayederfanarefin.boovchatv2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by SayedErfan on 8/10/2015.
 */
public class friend_requests_adapter extends ArrayAdapter<friendrequests> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public friend_requests_adapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final friendrequests currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.activity_friend_req_unit, parent, false);
        }

        row.setTag(currentItem);

        final Button add_button = (Button) row.findViewById(R.id.button_add_);
        final Button reject_button = (Button) row.findViewById(R.id.button_reject_);

        final TextView tv = (TextView) row.findViewById(R.id.textView_friend_req_unit);
        tv.setText(currentItem.getFrom());

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext instanceof requests) {
                    requests activity = (requests) mContext;
                    activity.approveReq(currentItem);
                }
            }
        });

        reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mContext instanceof requests) {
                    requests activity = (requests) mContext;
                    activity.rejectReq(currentItem);
                }
            }
        });
        return row;
    }


}
