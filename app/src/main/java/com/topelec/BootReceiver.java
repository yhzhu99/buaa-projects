package com.topelec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Amber on 2016/8/23.
 */
public class BootReceiver extends BroadcastReceiver {
    private final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub

        if (intent.getAction().equals(ACTION));
        {
            Intent intent2 = new Intent(context, it.moondroid.coverflowdemo.CoverFlowActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);

            //Intent intentService = new Intent();
            //intentService.setClass(context, MyService.class);
            //context.startService(intentService);

        }

    }

//    public class BootReceiver extends BroadcastReceiver {
//        private PendingIntent mAlarmSender;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // 在这里干你想干的事（启动一个Service，Activity等），本例是启动一个定时调度程序，每30分钟启动一个Service去更新数据
//            mAlarmSender = PendingIntent.getService(context, 0, new Intent(context,
//                    com.topelec.smarthomewall.SmarthomeWallActivity.class), 0);
//            long firstTime = SystemClock.elapsedRealtime();
//            AlarmManager am = (AlarmManager) context
//                    .getSystemService(Activity.ALARM_SERVICE);
//            am.cancel(mAlarmSender);
//            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
//                    30 * 60 * 1000, mAlarmSender);
//        }
//    }
}