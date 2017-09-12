package com.zkc.printertest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.printertest.R;
import com.smartdevice.sdk.printer.BarcodeCreater;
import com.smartdevice.sdk.printer.PrintService;

public class PrintTextActivity extends Activity {
	List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	private TextView et_input;
	private CheckBox checkBoxAuto;
	private Button bt_print, btnUnicode, bt_receipt, bt_receipt2;
	private Thread autoprint_Thread;

	int times = 0;// Automatic print time interval
	boolean isPrint = true;
	Bitmap btMap2, btMap3;
	private Bitmap btMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_text);
		isPrint = true;
		et_input = (EditText) findViewById(R.id.et_input);
		et_input.setText("打印测试(Printer Testing");
		createQrcode();
		btMap2 = BarcodeCreater.creatBarcode(PrintTextActivity.this,
				"98080048121070401259", 400, 100, true, 1);
		btMap3 = BarcodeCreater.encode2dAsBitmap("98080048121070401259", 150,
				150, 2);

		bt_print = (Button) findViewById(R.id.bt_print);
		bt_print.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = et_input.getText().toString();
				PrintService.pl().printText(message + "\r\n\r\n");
 				
				String textStr="中国";

				//获取文字十六进制数据，GBK编码
				byte[] btStr = null;
				btStr = textStr.getBytes();
				//获取文字数据长度
				int msgSize=btStr.length;

				//初始化发送数据十六进制数组大小，指令+文字数据长度
				byte[] btcmd = new byte[4+msgSize];
				btcmd[0] = 0x1F;
				btcmd[1] = 0x11;
				btcmd[2] = (byte) (msgSize >>> 8);
				btcmd[3] = (byte) (msgSize & 0xff);

				//合并数组
				System.arraycopy(btStr, 0, btcmd, 4, btStr.length);

				//转换十进制
				String sendString=new String(btcmd);

				//发送
				PrintService.pl().printText(sendString);
			}
		});

		bt_receipt = (Button) findViewById(R.id.bt_receipt);
		bt_receipt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap btMap = BitmapFactory.decodeResource(getResources(),
						R.drawable.logoimage);
				btMap = PrintService.resizeImage(btMap, 150, 150);
				// //PrintService.pl().printImage(btMap);
				btMap.recycle();
				PrintService
						.pl()
						.printText(
								"LOGO: 2,0\r"
										+ "001040\r\n"
										+ "美健牌環奶瓶洗潔液(補充裝)           59.90\r\n"
										+ "合計(港幣)                           59.90\r\n"
										+ "------------------------------------------\r\n"
										+ "現金                                500.00\r\n"
										+ "------------------------------------------\r\n"
										+ "找贖                                440.10\r\n"
										+ "LOGO: 0,0"
										+ "14天購物保障,(憑單據可享退/換貨保障,\r\n"
										+ "萬寧禮券除外,印花及會員積分須一併退回,"
										+ "詳情請參閱店內海報)\r\n"
										+ "康橋大廈(0495) 電話: 22844811 #28\r\n");

				// //PrintService.pl().printImage(btMap2);

				PrintService
						.pl()
						.printText(
								"\r\nTransaction Number : 20980\r"
										+ "04-08-2015 16:02:10 R#01 C:1467 T#81007921\r"
										+ "LOGO: 10,0\r");

				// //PrintService.pl().printImage(btMap3);

				PrintService.pl().printText("\r\n\r\n\r\n");
			}
		});

		bt_receipt2 = (Button) findViewById(R.id.bt_receipt2);
		bt_receipt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bitmap btMap = BitmapFactory.decodeResource(getResources(),
						R.drawable.logoimage);
				btMap = PrintService.resizeImage(btMap, 150, 150);
				PrintService.pl().printImage2(btMap);
				btMap.recycle();
				PrintService
						.pl()
						.printText(
								"LOGO: 2,0\r"
										+ "001040\r\n"
										+ "美健牌環奶瓶洗潔液(補充裝)           59.90\r\n"
										+ "合計(港幣)                           59.90\r\n"
										+ "------------------------------------------\r\n"
										+ "現金                                500.00\r\n"
										+ "------------------------------------------\r\n"
										+ "找贖                                440.10\r\n"
										+ "LOGO: 0,0"
										+ "14天購物保障,(憑單據可享退/換貨保障,\r\n"
										+ "萬寧禮券除外,印花及會員積分須一併退回,"
										+ "詳情請參閱店內海報)\r\n"
										+ "康橋大廈(0495) 電話: 22844811 #28\r\n");
				// //PrintService.pl().printImage2(btMap2);

				PrintService
						.pl()
						.printText(
								"\r\nTransaction Number : 20980\r"
										+ "04-08-2015 16:02:10 R#01 C:1467 T#81007921\r"
										+ "LOGO: 10,0\r");

				PrintService.pl().printImage2(btMap3);

				PrintService.pl().printText("\r\n\r\n\r\n");
			}
		});

		btnUnicode = (Button) findViewById(R.id.btnUnicode);
		btnUnicode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = et_input.getText().toString();

				PrintService.pl().printUnicode(message);
				PrintService.pl().printText("\r\n");
			}
		});

		checkBoxAuto = (CheckBox) findViewById(R.id.checkBoxAuto);

		// Auto Print
		autoprint_Thread = new Thread() {
			// public void run() {
			// while (isPrint) {
			// if (checkBoxAuto.isChecked()) {
			// String message = et_input.getText().toString();
			// PrintService.pl().printText(message);
			// try {
			// Thread.sleep(times);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			// }
			// }
			public void run() {
				Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.gl);
				Bitmap myBitmap = PrintService.resizeImage(mBitmap, 360, 80);
				Bitmap mbtmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.qrcode);
				while (isPrint) {
					if (checkBoxAuto.isChecked()) {
						PrintService.pl().printImage2(myBitmap);
						PrintService.pl().write(
								PrintService.pl().CMD_ALIGN_MIDDLE);
						PrintService.pl().printText(
								"H7UJ787-JU78UU6-JJ785J6-J876K76\n");
						PrintService.pl().write(
								PrintService.pl().CMD_FONTSIZE_DOUBLE);
						PrintService.pl().printText("Quick Lotto (5/11)\n");

						PrintService.pl().write(
								PrintService.pl().CMD_ALIGN_LEFT);
						PrintService.pl().write(
								PrintService.pl().CMD_FONTSIZE_NORMAL);
						PrintService.pl().printText(
								"Terminal No: 85010002    SN:1\n");
						PrintService.pl().printText("Draw No: 20160503011\n");
						PrintService.pl().printText(
								"Sales Time 03/05/2016-09:27:55\n");

						PrintService
								.pl()
								.printText(
										"--------------------------------------------\n");
						PrintService.pl().printText("Option3\n");
						PrintService.pl().write(
								PrintService.pl().CMD_FONTSIZE_DOUBLE);
						PrintService.pl().printText("A. 02 06 08 ￥200\n");
						PrintService.pl().printText("B. 02 06 08 ￥200\n");
						PrintService.pl().printText("C. 02 06 08 ￥200\n");
						PrintService.pl().printText("D. 02 06 08 ￥200\n");
						PrintService.pl().printText("E. 02 06 08 ￥200\n");

						PrintService.pl().write(
								PrintService.pl().CMD_FONTSIZE_NORMAL);
						PrintService
								.pl()
								.printText(
										"--------------------------------------------\n");
						PrintService.pl().printText("Total Price: ￥1000\n");
						PrintService
								.pl()
								.printText(
										"............................................\n");
						PrintService.pl().write(
								PrintService.pl().CMD_ALIGN_MIDDLE);
						PrintService.pl().write(
								PrintService.pl().CMD_FONTSIZE_DOUBLE_HIGH);
						PrintService.pl()
								.printText("I AM NOT UNDER 18 YEARS\n");
						PrintService.pl().write(
								PrintService.pl().CMD_FONTSIZE_NORMAL);
						PrintService
								.pl()
								.printText(
										"............................................\n");
						PrintService.pl().printText(
								"HYHR6666-YHYH6666-T6G5Y6T5-H775G455\n");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						PrintService.pl().printImage2(btMap);
						PrintService.pl().printText("\n\n");

					}
				}
			}
		};
		autoprint_Thread.start();
	}

	public void createQrcode() {
		try {
			btMap = BarcodeCreater.encode2dAsBitmap(new String("你好啊".getBytes("utf8")),
					PrintService.imageWidth * 8, PrintService.imageWidth * 8, 2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Resources res = getResources();
		String[] cmdStr = res.getStringArray(R.array.cmd);
		for (int i = 0; i < cmdStr.length; i++) {
			String[] cmdArray = cmdStr[i].split(",");
			if (cmdArray.length == 2) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", cmdArray[0]);
				map.put("description", cmdArray[1]);
				menu.add(0, i, i, cmdArray[0]);
				listData.add(map);
			}
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		Map map = listData.get(item.getItemId());
		String cmd = map.get("description").toString();
		byte[] bt = PrintService.hexStringToBytes(cmd);
		PrintService.pl().write(bt);
		Toast toast = Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT);
		toast.show();
		return false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		isPrint = false;
		super.onStop();
	}

}
