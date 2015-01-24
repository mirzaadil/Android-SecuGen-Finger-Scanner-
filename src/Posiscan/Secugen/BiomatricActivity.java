package Posiscan.Secugen;

import Posiscan.Secugen.utilies.HexConversion;
import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class BiomatricActivity extends Activity implements View.OnClickListener {

	private static final String TAG = "MIRZA";

	private Button mButtonRegister = null;

	private PendingIntent mPermissionIntent;
	private ImageView mImageViewFingerprint;
	private IntentFilter filter;
	private boolean mLed;
	private boolean mAutoOnEnabled;
	private SGAutoOnEventNotifier autoOn;
	HexConversion conversion;

	private JSGFPLib sgfplib;
	FingerPrintReader fingerPrintReader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.launcher);
		mButtonRegister = (Button) findViewById(R.id.buttonRegister);
		mButtonRegister.setOnClickListener(this);
		mImageViewFingerprint = (ImageView) findViewById(R.id.imageViewFingerprint);
		conversion = new HexConversion();
		usbPermission();

	}

	public void onClick(View v) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				fingerPrintReader.readFingerPrint();
				mImageViewFingerprint.setImageBitmap(fingerPrintReader
						.toGrayscale(fingerPrintReader.getFPBitMap()));

				byte[] abc = fingerPrintReader.getHexTemplate();
				String Temp = conversion.getHexString(abc);
				Log.d(TAG, "Template" + Temp);

			}
		});

	}

	private void usbPermission() {
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);
		sgfplib = new JSGFPLib(
				(UsbManager) getSystemService(Context.USB_SERVICE));
		mLed = false;
		mAutoOnEnabled = false;
		long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
		UsbDevice usbDevice = sgfplib.GetUsbDevice();
		sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
		error = sgfplib.OpenDevice(0);
		SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
		// error = sgfplib.GetDeviceInfo(deviceInfo);

		fingerPrintReader = new FingerPrintReader(mImageViewFingerprint,
				sgfplib);
	}

	// USB Device Attach Permission
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {

							Log.d(TAG, "Vender DI" + device.getVendorId());

							Log.d(TAG, "Producat ID " + device.getProductId());

						} else
							Log.e(TAG,
									"mUsbReceiver.onReceive() Device is null");
					} else
						Log.e(TAG,
								"mUsbReceiver.onReceive() permission denied for device "
										+ device);
				}
			}
		}
	};

}