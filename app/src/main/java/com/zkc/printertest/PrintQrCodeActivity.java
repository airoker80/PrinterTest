package com.zkc.printertest;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.printertest.R;
import com.smartdevice.sdk.printer.BarcodeCreater;
import com.smartdevice.sdk.printer.PrintService;
import com.smartdevice.sdk.printer.PrinterClass;

public class PrintQrCodeActivity extends Activity {
	private Bitmap btMap = null;
	private ImageView iv;
	private TextView et_input;
	private Button bt_2d;
	private Button bt_image;
	private Button bt_order;
	private String message = null;
	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1){
				btMap = null;
				iv.setImageBitmap(btMap);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_qrcode);

		iv = (ImageView) findViewById(R.id.iv_test);
		et_input = (EditText) findViewById(R.id.et_input);
		bt_2d = (Button) findViewById(R.id.bt_2d);
		et_input.setText("请输入打印指令");
		bt_2d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (PrintService.pl().getState() != PrinterClass.STATE_CONNECTED) {
					Toast.makeText(
							PrintQrCodeActivity.this,
							PrintQrCodeActivity.this.getResources().getString(
									R.string.str_unconnected), 2000).show();
					return;
				}
				message = et_input.getText().toString();
				if (message.length() > 0) {
					try {
						message = new String(message.getBytes("utf8"));
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}

					btMap = BarcodeCreater.encode2dAsBitmap(message,
							PrintService.imageWidth * 8,
							PrintService.imageWidth * 8, 2);
					BarcodeCreater.saveBitmap2file(btMap, "mypic1.JPEG");
					iv.setImageBitmap(btMap);
				}

			}
		});

		bt_image = (Button) findViewById(R.id.bt_imageQr);
		bt_image.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (btMap != null) {
					new Thread() {
						public void run() {
							PrintService.pl().printImage(btMap);
							myHandler.sendEmptyMessage(1);
						}
					}.start();
					return;
				}
			}
		});

		bt_order = (Button) findViewById(R.id.bt_order);
		bt_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (PrintService.pl().getState() != PrinterClass.STATE_CONNECTED) {
					Toast.makeText(
							PrintQrCodeActivity.this,
							PrintQrCodeActivity.this.getResources().getString(
									R.string.str_unconnected), 2000).show();
					return;
				}
				message = et_input.getText().toString();
				Log.e("zkc====", message + "");
				PrintService.pl().printQrCode(message);
			}
		});

		btMap = BarcodeCreater.encode2dAsBitmap("打印机测试Printer Testing",
				PrintService.imageWidth * 8, PrintService.imageWidth * 8, 2);
		BarcodeCreater.saveBitmap2file(btMap, "mypic1.JPEG");
		iv.setImageBitmap(btMap);
	}

}
