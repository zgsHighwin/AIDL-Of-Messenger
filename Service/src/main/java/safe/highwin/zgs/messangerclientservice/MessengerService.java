package safe.highwin.zgs.messangerclientservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.concurrent.TimeUnit;

public class MessengerService extends Service {

    private static final int MSG_SUM = 0x110;

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessenger.getBinder();
    }

    private Messenger mMessenger = new Messenger(new Handler() {

        @Override
        public void handleMessage(Message msgFromClient) {
            Message msgToClient = Message.obtain(msgFromClient);
            switch (msgFromClient.what) {
                case MSG_SUM:
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                        msgToClient.what = MSG_SUM;
                        msgToClient.arg2 = msgFromClient.arg1 + msgFromClient.arg2;
                        msgFromClient.replyTo.send(msgToClient);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msgFromClient);
        }
    });

}
