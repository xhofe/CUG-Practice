package com.example.bluechat.ListAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bluechat.R;
import com.example.bluechat.User;
import com.example.bluechat.Utils.BluetoothHelper;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    int m_textViewId;


    public DeviceAdapter(Context context, int textViewId, List<BluetoothDevice> objects) {
        super(context,textViewId,objects);

        m_textViewId= textViewId;
    }

    /**
     * LIstView中每一个子项被滚动到屏幕的时候调用
     * position：滚到屏幕中的子项位置，可以通过这个位置拿到子项实例
     * convertView：之前加载好的布局进行缓存
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }
        // Lookup view for data population
        TextView device_name = convertView.findViewById(R.id.device_name);
        TextView device_address = convertView.findViewById(R.id.device_address);
        // Populate the data into the template view using the data object
        device_name.setText(device.getName());
        device_address.setText(device.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}
