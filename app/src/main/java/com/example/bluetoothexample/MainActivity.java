package com.example.bluetoothexample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";
    public static BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> bluetoothDevices=new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;

    private final BroadcastReceiver mBrodcastReceiver1=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mBluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()))
            {
                final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,mBluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"State Off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"State ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"State Turning On");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"State Turning Off");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBrodcastReceiver2=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction()))
            {
                final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG,"Discoverability enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG,"Able to receive connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG,"Not able to receive connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG,"Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"Connected");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBrodcastReceiver3=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: called" );
            if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
            {
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                recyclerViewAdapter.notifyDataSetChanged();
                Log.d(TAG, "onReceive: "+device.getName()+" : "+device.getAddress());
            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.e(TAG, "onReceive: :discovery started ");
            }
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.e(TAG, "onReceive: :discovery finished ");
            }
        }
    };

    private final BroadcastReceiver mBrodcastReceiver4=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction()))
            {
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()==BluetoothDevice.BOND_BONDED)
                {
                    Log.d(TAG, "BOND_BONDED: ");
                }
                if (device.getBondState()==BluetoothDevice.BOND_BONDING)
                {
                    Log.d(TAG, "BOND_BONDING: ");
                }
                if (device.getBondState()==BluetoothDevice.BOND_NONE)
                {
                    Log.d(TAG, "BOND_NONE: ");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called. ");
        super.onDestroy();
        unregisterReceiver(mBrodcastReceiver1);
        unregisterReceiver(mBrodcastReceiver2);
        unregisterReceiver(mBrodcastReceiver3);
        unregisterReceiver(mBrodcastReceiver4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=findViewById(R.id.button);
        Button discoverable=findViewById(R.id.discoverable);
        Button discover=findViewById(R.id.discover);

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerViewAdapter=new RecyclerViewAdapter(bluetoothDevices);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        IntentFilter intentFilter1=new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBrodcastReceiver4,intentFilter1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling Bluetooth");
                enableDisableBT();
            }
        });

        discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Making device discoverable for 300 seconds.");
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
                startActivity(intent);

                IntentFilter intentFilter=new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(mBrodcastReceiver2,intentFilter);
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Looking for unpaired devices");
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d(TAG, "onClick: Cancel discovery");
                    mBluetoothAdapter.startDiscovery();
                    int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    checkBTPermissions();
                    IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    //intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    registerReceiver(mBrodcastReceiver3,intentFilter);
                }
                else if(!mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.startDiscovery();
                    checkBTPermissions();
                    //int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    //intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    registerReceiver(mBrodcastReceiver3,intentFilter);
                }

            }
        });
    }

    public void enableDisableBT()
    {
        if(mBluetoothAdapter==null) Log.d(TAG,"Does not have Bluetooth");

        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT");
            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            IntentFilter BTIntent=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBrodcastReceiver1 , BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: disabling BT");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBrodcastReceiver1 , BTIntent);
        }
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

}
