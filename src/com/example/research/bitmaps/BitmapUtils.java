package com.example.research.bitmaps;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapUtils {

	public static Bitmap resizeAndCropCenter(Bitmap paramBitmap, int paramInt,
			boolean paramBoolean) {
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		while (true) {
			if ((i == paramInt) && (j == paramInt))
				return paramBitmap;
			float f = paramInt / Math.min(i, j);
			Bitmap localBitmap = Bitmap.createBitmap(paramInt, paramInt,
					getConfig(paramBitmap));
			int k = Math.round(f * paramBitmap.getWidth());
			int l = Math.round(f * paramBitmap.getHeight());
			Canvas localCanvas = new Canvas(localBitmap);
			localCanvas.translate((paramInt - k) / 2.0F, (paramInt - l) / 2.0F);
			localCanvas.scale(f, f);
			localCanvas.drawBitmap(paramBitmap, 0.0F, 0.0F, new Paint(6));
			paramBitmap.recycle();
			paramBitmap = localBitmap;
		}
	}
	
	private static Bitmap.Config getConfig(Bitmap paramBitmap) {
		Bitmap.Config localConfig = paramBitmap.getConfig();
		if (localConfig == null)
			localConfig = Bitmap.Config.ARGB_8888;
		return localConfig;
	}
}
