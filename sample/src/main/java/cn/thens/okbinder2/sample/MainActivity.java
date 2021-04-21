package cn.thens.okbinder2.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;

import cn.thens.okbinder2.OkBinder;

/**
 * @author 7hens
 */
public class MainActivity extends Activity implements ServiceConnection {
    private static final String TAG = "@OkBinder";
    boolean isServiceConnected = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Toast.makeText(this, "please check the log", Toast.LENGTH_SHORT).show();

        IRemoteService remoteService = OkBinder.proxy(IRemoteService.class, service);
        try {
            remoteService.testError(false, ComponentName.unflattenFromString("a.b/.c"));
        } catch (Exception e) {
            Log.e(TAG, "MainActivity: testError => \n" + Log.getStackTraceString(e));
        }
        IRemoteService callback = new IRemoteServiceImpl("Callback");
        IRemoteService callback2 = new IRemoteServiceImpl("Callback2");
        SparseArray<IRemoteService> sparseArray = new SparseArray<>();
        sparseArray.put(1, callback);
        sparseArray.put(2, callback2);

        remoteService.testCallback(callback);
        remoteService.testList(Arrays.asList(callback, callback2));
        remoteService.testSparseArray(sparseArray);
        remoteService.testMap(Collections.singletonMap("mapKey", callback));
        remoteService.testArray(new IRemoteService[]{callback, callback2});
        remoteService.testObjectArray(new IRemoteService[]{callback, callback2});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.vTestLocalService).setOnClickListener(v -> rebindService(LocalService.class));
        findViewById(R.id.vTestRemoteService).setOnClickListener(v -> rebindService(RemoteService.class));
    }

    private void rebindService(Class<?> serviceClass) {
        if (isServiceConnected) {
            unbindService(this);
        }
        bindService(new Intent(this, serviceClass), this, Context.BIND_AUTO_CREATE);
        isServiceConnected = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}