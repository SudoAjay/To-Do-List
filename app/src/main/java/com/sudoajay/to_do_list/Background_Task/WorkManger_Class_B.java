package com.sudoajay.to_do_list.Background_Task;


import android.content.Context;
import android.support.annotation.NonNull;

import com.sudoajay.to_do_list.ForegroundService.TraceBackgroundService;
import com.sudoajay.to_do_list.Notification.Notify_Notification;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WorkManger_Class_B extends Worker {
    private final Context context;


    public WorkManger_Class_B(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context =context;
    }

    @NonNull
    @Override
    public Result doWork() {

        RunThis(getApplicationContext());

        return Result.success();
    }

    public static void RunThis(final Context context){
        Notify_Notification notify_notification = new Notify_Notification();
        notify_notification.notify(context, "Due ", "Due List Alert");

        // set next date
        TraceBackgroundService traceBackgroundService = new TraceBackgroundService(context);
        traceBackgroundService.setDueTask();
    }
}
