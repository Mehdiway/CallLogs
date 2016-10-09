package com.wickerlabs.logmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LogsManager {

    public static final int INCOMING = 93;
    public static final int OUTGOING = 594;
    public static final int TOTAL = 579;

    private Context context;
    private Activity activity;

    public LogsManager(Activity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;

    }

    public int getOutgoingDuration() {
        int sum = 0;

        Cursor cursor;
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            cursor = activity.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE, null, null);

        } else {
            throw new IllegalStateException("permission READ_CALL_LOG not granted");
        }
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            String callDuration = cursor.getString(duration);
            sum += Integer.parseInt(callDuration);
        }

        cursor.close();
        return sum;
    }

    public int getIncomingDuration() {
        int sum = 0;
        Cursor cursor;
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            cursor = activity.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE, null, null);

        } else {
            throw new IllegalStateException("permission READ_CALL_LOG not granted");
        }
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            String callDuration = cursor.getString(duration);
            sum += Integer.parseInt(callDuration);
        }

        cursor.close();
        return sum;
    }

    public int getTotalDuration() {
        int sum = 0;
        Cursor cursor;
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            cursor = activity.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        } else {
            throw new IllegalStateException("permission READ_CALL_LOG not granted");
        }
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            String callDuration = cursor.getString(duration);
            sum += Integer.parseInt(callDuration);
        }

        cursor.close();
        return sum;
    }

    public String getCoolDuration(int type) {
        float sum;

        switch (type) {
            case INCOMING:
                sum = getIncomingDuration();
                break;
            case OUTGOING:
                sum = getOutgoingDuration();
                break;
            case TOTAL:
                sum = getTotalDuration();
                break;
            default:
                throw new IllegalStateException("Invalid type provided");
        }

        String duration = "";
        String result;

        if (sum >= 0 && sum < 3600) {

            result = String.valueOf(sum / 60);
            String decimal = result.substring(0, result.lastIndexOf("."));
            String point = "0" + result.substring(result.lastIndexOf("."));

            int minutes = Integer.parseInt(decimal);
            float seconds = Float.parseFloat(point) * 60;

            DecimalFormat formatter = new DecimalFormat("#");
            duration = minutes + " min " + formatter.format(seconds) + " secs";

        } else if (sum >= 3600) {

            result = String.valueOf(sum / 3600);
            String decimal = result.substring(0, result.lastIndexOf("."));
            String point = "0" + result.substring(result.lastIndexOf("."));

            int hours = Integer.parseInt(decimal);
            float minutes = Float.parseFloat(point) * 60;

            DecimalFormat formatter = new DecimalFormat("#");
            duration = hours + " hrs " + formatter.format(minutes) + " min";

        }

        return duration;
    }

    public List<LogObject> getLogs(){
        List<LogObject> logs = new ArrayList<>();

        Cursor cursor;
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            cursor = activity.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        } else {
            throw new IllegalStateException("permission READ_CALL_LOG not granted");
        }

        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while(cursor.moveToNext()){
            LogObject log = new LogObject(context);

            log.setNumber(cursor.getString(number));
            log.setType(cursor.getString(type));
            log.setDuration(cursor.getInt(duration));
            log.setDate(cursor.getString(date));

            logs.add(log);
        }

        cursor.close();
        return logs;
    }

}
