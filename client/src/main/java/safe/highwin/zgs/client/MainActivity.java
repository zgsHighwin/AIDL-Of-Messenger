package safe.highwin.zgs.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int MSG_SUM = 0x110;

    private Button mBtnAdd;
    private LinearLayout mLyContainer;
    //显示连接状态
    private TextView mTvState;

    private Messenger mService;
    private boolean isConn;
    private int mA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();

        mTvState = (TextView) findViewById(R.id.id_tv_callback);
        mBtnAdd = (Button) findViewById(R.id.id_btn_add);
        mLyContainer = (LinearLayout) findViewById(R.id.id_ll_container);

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a = mA++;
                int b = (int) (Math.random() * 100);
                TextView tv = new TextView(MainActivity.this);
                tv.setText(a + " + " + b + " = caculating ...");
                tv.setId(a);
                mLyContainer.addView(tv);
                Message msgFromClient = Message.obtain(null, MSG_SUM, a, b);
                msgFromClient.replyTo = mMessenger;
                if (isConn) {
                    try {
                        mService.send(msgFromClient);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromServer) {
            switch (msgFromServer.what) {
                case MSG_SUM:
                    TextView tv = (TextView) mLyContainer.findViewById(msgFromServer.arg1);
                    tv.setText(tv.getText() + "=>" + msgFromServer.arg2);
                    break;

            }

            super.handleMessage(msgFromServer);
        }
    });


    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = new Messenger(iBinder);
            isConn = true;
            mTvState.setText("connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            isConn = false;
            mTvState.setText("disconnect");
        }
    };


    private void bindService() {
        Intent intent = new Intent();
        // intent.setAction("com.zhy.aidl.calc");
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction("zgs.highwin.calc");
        Intent eintent = new Intent(IntentServer.createExplicitFromImplicitIntent(this, intent));
        bindService(eintent, mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConn);
        super.onDestroy();
    }
}
