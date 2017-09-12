package com.zkc.printertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.printertest.R;
import com.smartdevice.sdk.printer.BlueToothService;
import com.smartdevice.sdk.printer.Device;
import com.smartdevice.sdk.printer.PrintService;
import com.smartdevice.sdk.printer.PrinterClass;

public class MainActivity extends ListActivity {

	protected static final String TAG = "MainActivity";
	public static boolean checkState = true;
	private static BluetoothDevice remoteDevice = null;
	private Thread tv_update;
	TextView textView_state;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	Handler mhandler = null;
	Handler handler = null;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_items);
		textView_state = (TextView) findViewById(R.id.textView_state);
		setListAdapter(new SimpleAdapter(this, getData("simple-list-item-2"),
				android.R.layout.simple_list_item_2, new String[] { "title",
						"description" }, new int[] { android.R.id.text1,
						android.R.id.text2 })); 

		mhandler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					Log.e(TAG, "readBuf:" + readBuf[0]);
					if (readBuf[0] == 0x13) {
						PrintService.isFUll = true;
						ShowMsg(getResources().getString(
								R.string.str_printer_state)
								+ ":"
								+ getResources().getString(
										R.string.str_printer_bufferfull));
					} else if (readBuf[0] == 0x11) {
						PrintService.isFUll = false;
						ShowMsg(getResources().getString(
								R.string.str_printer_state)
								+ ":"
								+ getResources().getString(
										R.string.str_printer_buffernull));
					} else if (readBuf[0] == 0x08) {
						ShowMsg(getResources().getString(
								R.string.str_printer_state)
								+ ":"
								+ getResources().getString(
										R.string.str_printer_nopaper));
					} else if (readBuf[0] == 0x01) {
						// ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
					} else if (readBuf[0] == 0x04) {
						ShowMsg(getResources().getString(
								R.string.str_printer_state)
								+ ":"
								+ getResources().getString(
										R.string.str_printer_hightemperature));
					} else if (readBuf[0] == 0x02) {
						ShowMsg(getResources().getString(
								R.string.str_printer_state)
								+ ":"
								+ getResources().getString(
										R.string.str_printer_lowpower));
					} else {
						String readMessage = new String(readBuf, 0, msg.arg1);
						if (readMessage.contains("800"))// 80mm paper
						{
							PrintService.imageWidth = 72;
							Toast.makeText(getApplicationContext(), "80mm",
									Toast.LENGTH_SHORT).show();
						} else if (readMessage.contains("580"))// 58mm paper
						{
							PrintService.imageWidth = 48;
							Toast.makeText(getApplicationContext(), "58mm",
									Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case MESSAGE_STATE_CHANGE:// 蓝牙连接状
					switch (msg.arg1) {
					case PrinterClass.STATE_CONNECTED:// 已经连接
						break;
					case PrinterClass.STATE_CONNECTING:// 正在连接
						Toast.makeText(getApplicationContext(),
								"STATE_CONNECTING", Toast.LENGTH_SHORT).show();
						break;
					case PrinterClass.STATE_LISTEN:
					case PrinterClass.STATE_NONE:
						break;
					case PrinterClass.SUCCESS_CONNECT:
						PrintService.pl().write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
						Toast.makeText(getApplicationContext(),
								"SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
						break;
					case PrinterClass.FAILED_CONNECT:
						Toast.makeText(getApplicationContext(),
								"FAILED_CONNECT", Toast.LENGTH_SHORT).show();
						if (BlueToothService.autoConnect == 3) {
							startActivity(new Intent(MainActivity.this,
									DeviceListActivity.class));
						} else {
							BlueToothService.reconnect();
						}
						break;
					case PrinterClass.LOSE_CONNECT:
						Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
								Toast.LENGTH_SHORT).show();
						break;
					}
					break;
				case MESSAGE_WRITE:
					break;
				}
				super.handleMessage(msg);
			}
		};

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					break;
				case 1:// 扫描完毕
					Device d = (Device) msg.obj;
					if (d != null) {
						if (PrintSettingActivity.deviceList == null) {
							PrintSettingActivity.deviceList = new ArrayList<Device>();
						}

						if (!checkData(PrintSettingActivity.deviceList, d)) {
							PrintSettingActivity.deviceList.add(d);
						}
					}
					break;
				case 2:// 停止扫描
					break;
				}
			}
		};

		tv_update = new Thread() {
			public void run() {
				while (true) {
					if (checkState) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						textView_state.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (PrintService.pl() != null) {
									if (PrintService.pl().getState() == PrinterClass.STATE_CONNECTED) {
										textView_state
												.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_connected));
									} else if (PrintService.pl().getState() == PrinterClass.STATE_CONNECTING) {
										textView_state
												.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_connecting));
									} else if (PrintService.pl().getState() == PrinterClass.LOSE_CONNECT
											|| PrintService.pl().getState() == PrinterClass.FAILED_CONNECT) {
										checkState = false;
										textView_state
												.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_disconnected));
										Intent intent = new Intent();
										intent.setClass(MainActivity.this,
												PrintSettingActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivity(intent);
									} else {
										textView_state
												.setText(MainActivity.this
														.getResources()
														.getString(
																R.string.str_disconnected));
									}
								}
							}
						});
					}
				}
			}
		};
		tv_update.start();
	}

	

	/**
	 * 当List的项被选中时触发
	 */
	protected void onListItemClick(ListView listView, View v, int position,
			long id) {

		MainActivity.checkState = true;
		Intent intent = new Intent();
		intent.putExtra("position", position);
		intent.setClass(MainActivity.this, PrintActivity.class);

		PrintService.PrinterInit(position, this, mhandler, handler);
		switch (position) {
		case 0:
			startActivity(intent);
			break;
		case 1:
			startActivity(intent);
			break;
		
		}
	}

	/**
	 * 构造SimpleAdapter的第二个参数，类型为List<Map<?,?>>
	 * 
	 * @param title
	 * @return
	 */
	private List<Map<String, String>> getData(String title) {
		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

		Map<String, String> map = new HashMap<String, String>();
		map.put("title", getResources().getString(R.string.mode_bt));
		map.put("description", "");
		listData.add(map);

		map = new HashMap<String, String>();
		map.put("title", getResources().getString(R.string.mode_wifi));
		map.put("description", "");
		listData.add(map);

//		map = new HashMap<String, String>();
//		map.put("title", getResources().getString(R.string.mode_usb));
//		map.put("description", "");
//		listData.add(map);

		return listData;
	}

	private boolean checkData(List<Device> list, Device d) {
		for (Device device : list) {
			if (device.deviceAddress.equals(d.deviceAddress)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onRestart() {
		PrintService.isMain = true;
		checkState = true;
		super.onRestart();
	}

	private void ShowMsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

}
