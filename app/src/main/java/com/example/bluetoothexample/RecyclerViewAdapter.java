package com.example.bluetoothexample;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.bluetoothexample.MainActivity.mBluetoothAdapter;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private ArrayList<BluetoothDevice> arrayList = new ArrayList<>();

    RecyclerViewAdapter(ArrayList<BluetoothDevice> m){arrayList=m;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        BluetoothDevice device=arrayList.get(i);
        viewHolder.name.setText(device.getName());
        viewHolder.address.setText(device.getAddress());
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter.cancelDiscovery();
                Log.d(TAG, "onClick: You clicked on a device");
                String name=arrayList.get(i).getName();
                String address=arrayList.get(i).getAddress();
                Log.d(TAG, "onClick: "+name+" : "+address);
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN_MR2)
                {
                    Log.d(TAG, "Trying to pair ");
                    arrayList.get(i).createBond();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView address;
        LinearLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container=itemView.findViewById(R.id.container);
            name=itemView.findViewById(R.id.name);
            address=itemView.findViewById(R.id.address);
        }
    }
}
