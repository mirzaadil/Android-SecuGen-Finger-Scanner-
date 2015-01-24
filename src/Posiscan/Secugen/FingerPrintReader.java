package Posiscan.Secugen;

import java.nio.ByteBuffer;

import Posiscan.Secugen.utilies.HexConversion;
import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

public class FingerPrintReader {

	private byte[] mRegisterImage;
	private byte[] mRegisterTemplate;
	private int[] mMaxTemplateSize;
	private int mImageWidth;
	private int mImageHeight;
	private int[] grayBuffer;
	private Bitmap grayBitmap;
	private boolean mAutoOnEnabled;
	HexConversion conversion;
	private JSGFPLib sgfplib;
	private boolean mLed;	

	public FingerPrintReader(ImageView imageView, JSGFPLib jsgfpLib) {
		conversion = new HexConversion();
		this.sgfplib = jsgfpLib;

		grayBuffer = new int[JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES
				* JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES];
		for (int i = 0; i < grayBuffer.length; ++i)
			grayBuffer[i] = android.graphics.Color.GRAY;

		grayBitmap = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES,
				JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES, Bitmap.Config.ARGB_8888);
		grayBitmap.setPixels(grayBuffer, 0,
				JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, 0, 0,
				JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES,
				JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES);
		mMaxTemplateSize = new int[1];
		imageView.setImageBitmap(grayBitmap);

		mAutoOnEnabled = false;
		long error;
		SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
		error = sgfplib.GetDeviceInfo(deviceInfo);
		mImageWidth = deviceInfo.imageWidth;
		mImageHeight = deviceInfo.imageHeight;
		sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378);
		sgfplib.GetMaxTemplateSize(mMaxTemplateSize);
		mRegisterTemplate = new byte[mMaxTemplateSize[0]];
		sgfplib.WriteData((byte) 5, (byte) 1);
		sgfplib.SetLedOn(true);
		// imageView.setImageBitmap(toGrayscale(grayBitmap));
	}

	private Bitmap fpbitmap;

	public void readFingerPrint() {
		sgfplib.WriteData((byte) 5, (byte) 1); // Enable Smart Capture
		if (mRegisterImage != null)
			mRegisterImage = null;
		mRegisterImage = new byte[mImageWidth * mImageHeight];

		ByteBuffer byteBuf = ByteBuffer.allocate(mImageWidth * mImageHeight);
		long result = sgfplib.GetImage(mRegisterImage);

		this.fpbitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
				Bitmap.Config.ARGB_8888);
		byteBuf.put(mRegisterImage);
		int[] intbuffer = new int[mImageWidth * mImageHeight];
		for (int i = 0; i < intbuffer.length; ++i)
			intbuffer[i] = (int) mRegisterImage[i];
		this.fpbitmap.setPixels(intbuffer, 0, mImageWidth, 0, 0, mImageWidth,
				mImageHeight);
		result = sgfplib
				.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378);
		// imageView.setImageBitmap(toGrayscale(fpbitmap));
		SGFingerInfo fpInfo = new SGFingerInfo();
		for (int i = 0; i < mRegisterTemplate.length; ++i)

			mRegisterTemplate[i] = 0;
		result = sgfplib.CreateTemplate(fpInfo, mRegisterImage,
				mRegisterTemplate);

	}

	public byte[] getHexTemplate() {
		return mRegisterTemplate;
	}

	public Bitmap getFPBitMap() {
		return this.fpbitmap;
	}

	public Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int color = bmpOriginal.getPixel(x, y);
				int r = (color >> 16) & 0xFF;
				int g = (color >> 8) & 0xFF;
				int b = color & 0xFF;
				int gray = (r + g + b) / 3;
				color = Color.rgb(gray, gray, gray);
				bmpGrayscale.setPixel(x, y, color);
			}
		}
		return bmpGrayscale;
	}

}
