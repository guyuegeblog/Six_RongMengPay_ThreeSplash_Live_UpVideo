package com.app.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class UpdateService extends Service {
    //Android 中的定时任务一般有两种实现方式，一种是使用Java API 里提供的Timer 类，
    //一种是使用Android 的Alarm 机制。
    //这两种方式在多数情况下都能实现类似的效果，但Timer有一个明显的短板，它并不太适用于
    // 那些需要长期在后台运行的定时任务。
    // 我们都知道，为了能让电池更加耐用，每种手机都会有自己的休眠策略，Android 手机就会在长时间
    // 不操作的情况下自动让CPU 进入到睡眠状态，这就有可能导致Timer 中的定时任务无法正常运行。
    // 而Alarm 机制则不存在这种情况，它具有唤醒CPU 的功能，即可以保证每次需要执行定时任务的时候
    // CPU 都能正常工作。需要注意，这里唤醒CPU 和唤醒屏幕完全不是同一个概念，千万不要产生混淆。
    AlarmManager mAlarmManager = null;//不间断轮询服务
    PendingIntent mPendingIntent = null;

    @Override
    public void onCreate() {
        //start the service through alarm repeatly
        //http://blog.csdn.net/csd_xiaojin/article/details/50814234alerm机制详解
        Intent intent = new Intent(getApplicationContext(), UpdateService.class);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        long now = System.currentTimeMillis();
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 10000, mPendingIntent);
        super.onCreate();
    }

    private int startCommand = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startCommand++;
        if (startCommand != 1) {
            //时段更新数据
            Intent intent1 = new Intent("updateinterface");
            //实际业务需传送数据
            sendBroadcast(intent1);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
