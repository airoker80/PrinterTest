package com.zkc.printertest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
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

public class PrintBarCodeActivity extends Activity {
	static String Tag="PrintBarCodeActivity";
	private Bitmap btMap = null;
	private ImageView iv;
	private TextView et_input;
	private Button bt_bar;
	private Button bt_image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_barcode);
		
		iv = (ImageView) findViewById(R.id.iv_test);
		et_input=(EditText)findViewById(R.id.et_input);
		bt_bar = (Button) findViewById(R.id.bt_bar);
		et_input.setText("1234567890");
		bt_bar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (PrintService.pl().getState() != PrinterClass.STATE_CONNECTED) {
					Toast.makeText(
							PrintBarCodeActivity.this,
							PrintBarCodeActivity.this.getResources().getString(
									R.string.str_unconnected), 2000).show();
					return;
				}
				String message = et_input.getText().toString();

				if (message.getBytes().length > message.length()) {
					Toast.makeText(
							PrintBarCodeActivity.this,
							PrintBarCodeActivity.this.getResources().getString(
									R.string.str_cannotcreatebar), 2000).show();
					return;
				}
				if (message.length() > 0) {

					btMap = BarcodeCreater.creatBarcode(PrintBarCodeActivity.this,
							message, PrintService.imageWidth*8, 100, true, 1);
					iv.setImageBitmap(btMap);
				}

			}
		});
		

		bt_image = (Button) findViewById(R.id.bt_imageBar);
		bt_image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (btMap != null) {
					PrintService.pl().printImage(btMap);
					btMap = null;
					iv.setImageBitmap(btMap);
					return;
				}
			}
		});
		
		btMap = BarcodeCreater.creatBarcode(PrintBarCodeActivity.this,
				"9787111291954", PrintService.imageWidth*8, 100, true, 1);
		iv.setImageBitmap(btMap);
	}
}
