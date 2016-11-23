package pg.eti.inz.engineer.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.sql.Time;

import pg.eti.inz.engineer.R;

/**
 * Created by ubap on 2016-11-07.
 */

public class TripAdapter extends CursorAdapter {

    public TripAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.adapter_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        final TextView tripStartTimeView = (TextView) view.findViewById(R.id.tripStartTimeView);
        final TextView tripFinishTimeView = (TextView) view.findViewById(R.id.tripFinishTimeView);
        final TextView tripDistanceView = (TextView) view.findViewById(R.id.tripDistanceView);

        // Extract properties from cursor
        final Time tripStartTime = new Time(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_START_TIME)));
        final Time tripFinishTime = new Time(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_FINISH_TIME)));
        final double distance = cursor.getDouble(cursor.getColumnIndexOrThrow(DbHelper.TripContract.Trip.COLUMN_NAME_DISTANCE));

        // Populate fields with extracted properties
        tripStartTimeView.setText(tripStartTime.toString());
        tripFinishTimeView.setText(tripFinishTime.toString());
        tripDistanceView.setText(String.format("%.1f", distance / 1000));
    }


}
