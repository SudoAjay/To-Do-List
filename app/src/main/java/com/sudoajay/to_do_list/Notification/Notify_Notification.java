package com.sudoajay.to_do_list.Notification;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.sudoajay.to_do_list.DataBase.Main_DataBase;
import com.sudoajay.to_do_list.MainActivity;
import com.sudoajay.to_do_list.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Helper class for showing and canceling alert
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class Notify_Notification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Alert_";
    private Context context;
    private NotificationManager notificationManager;
    private Main_DataBase mainDataBase;
    private ArrayList<String> save_All_Date, task_Name;
    private ArrayList<Integer> array_Id;
    private int total_Size;

    // there Are three Type of Notification
    // * Today List
    // * Due List
    public void notify(final Context context,
                       final String notification_Hint, final String which_Type) {

        // local variable
        String text = "", channel_id;
        final Resources res = context.getResources();
        this.context = context;
        final String title = notification_Hint + res.getString(
                R.string.notification_title_name);
        Intent passIntent;


        // Grab the data From Database
        // Store Into Array for Execution
        Grab_The_Data_From_Database(which_Type);
        passIntent = new Intent(context, MainActivity.class);
        // setup according Which Type
        // if There is no data match with query
        if (which_Type.equalsIgnoreCase("Today List Alert")) {
            channel_id = context.getString(R.string.Channel_Id_Today_List_Alert); // channel_id
            text = res.getString(R.string.today_Alert_Notification, total_Size + "");
            passIntent.putExtra("Passing", "TodayList");

        } else {
            channel_id = context.getString(R.string.Channel_Id_Due_List_Alert); // channel_id
            text = res.getString(R.string.due_Alert_Notification, total_Size + "");
            passIntent.putExtra("Passing", "DueList");
        }


        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        // this check for android Oero In which Channel Id Come as New Feature
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            assert notificationManager != null;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(channel_id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(channel_id, title, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)
                // Set required fields, including the small icon, the
                // notification title, and text.
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //     .setSound(uri)
                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                //  .setLargeIcon(picture)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(task_Name.get(0) + save_All_Date.get(0))
                        .addLine(task_Name.get(1) + save_All_Date.get(1))
                        .addLine(task_Name.get(2) + save_All_Date.get(2))
                        .addLine(task_Name.get(3) + save_All_Date.get(3))
                        .addLine(task_Name.get(4) + save_All_Date.get(4))
                        .setBigContentTitle(title)
                        .setSummaryText(""))
                // Set ticker text (preview) information for this notification.
                .setTicker(notification_Hint)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(total_Size)

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show an expanded list of items on devices running Android 4.1
                // or later.


                // Example additional actions for this notification. These will
                // only show on devices running Android 4.1 or later, so you
                // should ensure that the activity in this notification's
                // content intent provides access to the same actions in
                // another way.


                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        // check if there ia data with empty
        // more and view button classification
        if (task_Name.size() > 5 && !task_Name.get(0).equals("")) {
            builder.addAction(
                    R.drawable.more_white_icon,
                    res.getString(R.string.action_More),
                    PendingIntent.getActivity(
                            context,
                            0,
                            passIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT)
            );
        } else {
            builder.addAction(
                    R.drawable.view_list_icon,
                    res.getString(R.string.action_View),
                    PendingIntent.getActivity(
                            context,
                            0,
                            passIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT)
            );
        }

        if (which_Type.equalsIgnoreCase("Today List Alert")) {
            builder.setSmallIcon(R.drawable.overdo_icon);
        } else {
            builder.setSmallIcon(R.drawable.later_icon);
        }

        if (total_Size > 0) {
            notify(context, builder.build());
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void notify(final Context context, final Notification notification) {

        notificationManager.notify(NOTIFICATION_TAG, 0, notification);

    }


    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, String)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }

    private void Grab_The_Data_From_Database(final String which_Type) {
        mainDataBase = new Main_DataBase(context);

        Initialization();
        Grab_The_Data_From_DB(which_Type);
        total_Size = task_Name.size();


        // if array is not have size 5 fill it
        for (int i = task_Name.size(); i < 5; i++) {
            array_Id.add(0);
            task_Name.add("");
            save_All_Date.add("");
        }


    }

    private void Grab_The_Data_From_DB(final String which_Type) {
        // local variable
        StringBuilder temp;
        Cursor cursor;
        boolean pass = false;

        Calendar calendar = Calendar.getInstance();
        int current_Year = calendar.get(Calendar.YEAR);
        int current_Month = calendar.get(Calendar.MONTH);
        int current_Day = calendar.get(Calendar.DAY_OF_MONTH);
        int current_Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Date todayDate = calendar.getTime();

        // for today date
        String today_Date = current_Day + "-" + current_Month + "-" + current_Year;


        if (!mainDataBase.check_For_Empty()) {
            if (which_Type.equals("Today List Alert")) {
                cursor = mainDataBase.Get_The_Data_From_Today_Time(0, today_Date);
                pass = true;
            } else {
                cursor = mainDataBase.Get_The_Data_From_Done(0);
            }
            // calendar.get(Calendar.DATE)+"-"+
            //                        calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.YEAR)

            // complete formula to find due list
            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                do {
                    if (which_Type.equals("Due List Alert")) {
                        String[] split = cursor.getString(2).split("-");
                        String date = split[0] + "/" + (Integer.parseInt(split[1]) + 1) + "/" + split[2];
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        try {

                            Date getDate = formatter.parse(date);

                            if (getDate.before(todayDate)) {
                                if (!getTime(todayDate).equals(getTime(getDate)))
                                    pass = true;

                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                    if (pass) {
                        array_Id.add(cursor.getInt(0));
                        if (cursor.getString(1).length() < 20) {
                            temp = new StringBuilder(cursor.getString(1));
                            for (int i = temp.length() + 1; i <= 20; i++) {

                                temp.append(" ");
                            }
                            task_Name.add(temp.toString());
                        } else {
                            task_Name.add(cursor.getString(1).substring(0, 20));
                        }
                        if (!cursor.getString(3).isEmpty()) {
                            save_All_Date.add(cursor.getString(3));
                        } else {
                            save_All_Date.add("No Time");
                        }
                        if (which_Type.equals("Due List Alert"))
                            pass = false;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
    }

    private void Initialization() {
        save_All_Date = new ArrayList<>();
        task_Name = new ArrayList<>();
        array_Id = new ArrayList<>();
    }

    public static Date getTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


}
