package xyz.canardoux.fluttersound;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.Result;

public class FlutterSoundRecorderService extends Service {
    private static final String TAG = "FlutterSoundRecorderService";
    private static final String CHANNEL_ID = "flutter_sound_recorder_channel";
    private static final int NOTIFICATION_ID = 12345;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        FlutterSoundRecorderService getService() {
            return FlutterSoundRecorderService.this;
        }
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, FlutterSoundRecorderService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, FlutterSoundRecorderService.class);
        context.stopService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getRecordingNotification());
        return START_STICKY;
    }

    public static void onMethodCall(final MethodCall call, final Result result, FlutterSoundRecorder mRecorder) {
        switch (call.method) {
            case "isEncoderSupported": {
                mRecorder.isEncoderSupported(call, result);
            }
            break;

            case "startRecorder": {
                mRecorder.startRecorder(call, result);
            }
            break;

            case "stopRecorder": {
                mRecorder.stopRecorder(call, result);
            }
            break;
            case "setSubscriptionDuration": {
                mRecorder.setSubscriptionDuration(call, result);
            }
            break;

            case "pauseRecorder": {
                mRecorder.pauseRecorder(call, result);
            }
            break;


            case "resumeRecorder": {
                mRecorder.resumeRecorder(call, result);
            }
            break;

            case "getRecordURL": {
                mRecorder.getRecordURL(call, result);
            }
            break;

            case "deleteRecord": {
                mRecorder.deleteRecord(call, result);
            }
            break;

            case "setLogLevel": {
                mRecorder.setLogLevel(call, result);
            }
            break;

            default: {

            }
            break;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Audio Recording Service", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Service for background audio recording");

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification getRecordingNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Smartup ERP").setContentText("Recording in progress").setSmallIcon(android.R.drawable.ic_btn_speak_now).setPriority(NotificationCompat.PRIORITY_LOW).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }
}