package com.tv.xeeng;

// dialog_create_table

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.andengine.entity.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.manager.ConfigData;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class CommonUtils {
	public static final boolean GOOGLE_PLAY_VER = getPartnerId()
			.equalsIgnoreCase("95");
	public static final boolean APPOTA_VER = getPartnerId().equalsIgnoreCase("93");
	public synchronized static String getDeviceUniqueId() {
		// String id = Secure.getString(CustomApplication.shareApplication()
		// .getContentResolver(), Secure.ANDROID_ID);
		// if (id == null) {
		// id = Build.SERIAL;
		// }
		// return id;
		if (Build.FINGERPRINT.contains("generic")) {
			return "emulator";
		}
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId(); // Requires READ_PHONE_STATE

		String pseudoId = "35" + Build.BOARD.length() % 10
				+ Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
				+ Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
				+ Build.HOST.length() % 10 + Build.ID.length() % 10
				+ Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
				+ Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
				+ Build.TYPE.length() % 10 + Build.USER.length() % 10; // 13
																		// digits

		String androidId = Secure.getString(CustomApplication
				.shareApplication().getContentResolver(), Secure.ANDROID_ID);

		String combinedId = imei + pseudoId + androidId;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");

			m.update(combinedId.getBytes(), 0, combinedId.length());
			// get md5 bytes
			byte md5Data[] = m.digest();
			// create a hex string
			StringBuffer uniqueId = new StringBuffer();
			for (int i = 0; i < md5Data.length; i++) {
				int b = (0xFF & md5Data[i]);
				// if it is a single digit, make sure it have 0 in front (proper
				// padding)
				if (b <= 0xF)
					uniqueId.append("0");
				// add number to string
				uniqueId.append(Integer.toHexString(b));
			}
			// hex string to uppercase
			return uniqueId.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return combinedId;
	}

	private static Object getSystemService(String name) {
		return CustomApplication.shareApplication().getSystemService(name);
	}

	private static LruCache<Long, Bitmap> avatarCache = new LruCache<Long, Bitmap>(
			(int) (Runtime.getRuntime().maxMemory() / (1024 * 8))) {
		@Override
		protected int sizeOf(Long key, Bitmap bitmap) {
			// The cache size will be measured in kilobytes rather than
			// number of items.
			return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
		}
	};

	public static String getPartnerId() {
		ConfigData.getInstance().readConfigData(
				CustomApplication.shareApplication());

		return ConfigData.getInstance().getPartnerId();
	}

	public static String getRefCode() {
		ConfigData.getInstance().readConfigData(
				CustomApplication.shareApplication());
		return ConfigData.getInstance().getRefCode();
	}

	public static String getPartnerIdFromAssets() {
		String result = "96";
		try {
			AssetManager assetManager = CustomApplication.shareApplication()
					.getAssets();
			InputStream input = assetManager.open("configs/configs.xml");
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(input, null);
			int eventType = parser.getEventType();
			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("partner_id")) {
						result = parser.nextText();
					} else if (name.equalsIgnoreCase("ref_id")) {
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		return result;

	}

	public static String getRefcodeFromAssets() {

		String result = "08";

		try {

			AssetManager assetManager = CustomApplication.shareApplication()
					.getAssets();

			InputStream input = assetManager.open("configs/configs.xml");

			Log.d("CommomUtils: ", "Get ref code");

			XmlPullParser parser = Xml.newPullParser();

			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(input, null);

			int eventType = parser.getEventType();

			String name = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:

					name = parser.getName();
					if (name.equalsIgnoreCase("partner_id")) {
					} else if (name.equalsIgnoreCase("ref_id")) {
						result = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:

					break;
				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		Log.d("CommonUtils", new StringBuffer("return: ").append(result)
				.toString());

		return result;

	}

	public static Point getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		@SuppressWarnings("deprecation")
		Point size = new Point(display.getWidth(), display.getHeight());

		return size;
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static String getDeviceName() {

		String result = null;
		result = android.os.Build.MODEL;
		Log.d("", "getDeviceName " + result);
		return result;

	}

	public static String getAppVersion() {
		return CustomApplication.shareApplication().getResources()
				.getString(R.string.app_version);
	}
	
	public static Map<String, List<String>> parseUriParams(String query) throws UnsupportedEncodingException {
		query = URLDecoder.decode(query, "UTF-8");
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		
		for (String param : query.split("&")) {
            String pair[] = param.split("=");
            String key = URLDecoder.decode(pair[0], "UTF-8");
            String value = "";
            if (pair.length > 1) {
                value = URLDecoder.decode(pair[1], "UTF-8");
            }
            List<String> values = params.get(key);
            if (values == null) {
                values = new ArrayList<String>();
                params.put(key, values);
            }
            values.add(value);
        }

		return params;
	}

	public static int getRegisterCount() {
		return UserPreference.sharePreference().readRegCount();
	}

	public static String formatCash(long cash) {
		String cashStr = "" + Math.abs(cash);
		int alpha = 3;
		int quoteIndex = cashStr.length();
		while ((quoteIndex = quoteIndex - alpha) > 0) {

			String sTo = cashStr.substring(0, quoteIndex);
			String sFrom = cashStr.substring(quoteIndex);
			cashStr = sTo + "." + sFrom;
		}
		return cash >= 0 ? cashStr : "-" + cashStr;
	}

	public static String formatExp(long exp) {

		String expStr = "" + Math.abs(exp);
		int alpha = 3;
		int quoteIndex = expStr.length();
		while ((quoteIndex = quoteIndex - alpha) > 0) {

			String sTo = expStr.substring(0, quoteIndex);
			String sFrom = expStr.substring(quoteIndex);
			expStr = sTo + "." + sFrom;
		}
		return exp >= 0 ? expStr + " exp" : "-" + expStr + " exp";
	}

	public static String formatCash2(long cash) {

		String cashStr = "" + Math.abs(cash);
		int alpha = 3;
		int quoteIndex = cashStr.length();
		while ((quoteIndex = quoteIndex - alpha) > 0) {

			String sTo = cashStr.substring(0, quoteIndex);
			String sFrom = cashStr.substring(quoteIndex);
			cashStr = sTo + "." + sFrom;
		}
		return cash >= 0 ? cashStr + "xu" : "-" + cashStr + "xu";
	}

	public static String formatRewardCash(long cash) {

		String cashStr = "" + Math.abs(cash);
		int alpha = 3;
		int quoteIndex = cashStr.length();
		while ((quoteIndex = quoteIndex - alpha) > 0) {

			String sTo = cashStr.substring(0, quoteIndex);
			String sFrom = cashStr.substring(quoteIndex);
			cashStr = sTo + "." + sFrom;
		}
		return cash >= 0 ? "+" + cashStr : "-" + cashStr;
	}

	public static String formatTableInfo(int index, long cash) {

		return "Bàn " + index + "\n" + formatCash(cash);
	}

	public static String formatSingleLineTableInfo(int index, long cash) {

		return "Bàn " + index + " - " + formatCash(cash);
	}

	public static String groupingCash(long cash) {

		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setGroupingSeparator('.');
		format.setDecimalFormatSymbols(custom);

		return format.format(cash);
	}

	public static void setViewEnabled(View view, boolean enabled) {

		float alpha = 1.0f;
		if (!enabled) {
			alpha = 0.5f;
		}

		AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
		alphaUp.setFillAfter(true);
		alphaUp.setDuration(0);

		view.startAnimation(alphaUp);
		view.setEnabled(enabled);
	}

	public static String formPlayerName(String fullName) {

		if (fullName.length() < 9)
			return fullName;
		fullName = fullName.substring(0, 8) + "...";
		return fullName;
	}

	public static String getGameName(int gameId) {

		try {
			switch (gameId) {
			case GameData.ALTP_TYPE:
				return CustomApplication.shareApplication().getString(
						R.string.label_altp);
			case GameData.BACAY_TYPE:
				return CustomApplication.shareApplication().getString(
						R.string.label_bacay);
			case GameData.PHOM_TYPE:
				return CustomApplication.shareApplication().getString(
						R.string.label_phom);
			case GameData.PIKACHU_TYPE:
				return CustomApplication.shareApplication().getString(
						R.string.label_pikachu);
			case GameData.TLMN_TYPE:
				return CustomApplication.shareApplication().getString(
						R.string.label_tlmn);
			case GameData.BAUCUA_TYPE:
				return CustomApplication.shareApplication().getString(
						R.string.label_baucua);
			default:
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void setVisibleEntity(boolean pVisible, Entity... pEntities) {

		for (Entity entity : pEntities) {

			if (entity == null)
				continue;
			entity.setIgnoreUpdate(!pVisible);
			entity.setVisible(pVisible);
		}
	}

	public static ArrayList<String> getTemplateMessageList(int gameId) {

		ArrayList<String> result = null;
		switch (gameId) {
		case GameData.ALTP_TYPE:
			result = getTemplateMessageFrom("altp.xml");
			break;
		case GameData.TLMN_TYPE:
			result = getTemplateMessageFrom("tlmn.xml");
			break;
		case GameData.BACAY_TYPE:
			result = getTemplateMessageFrom("bacay.xml");
			break;
		case GameData.PHOM_TYPE:
			result = getTemplateMessageFrom("phom.xml");
			break;
		case GameData.PIKACHU_TYPE:
			result = getTemplateMessageFrom("pikachu.xml");
			break;
		case GameData.BAUCUA_TYPE:
			result = getTemplateMessageFrom("baucua.xml");
			break;
		case GameData.SAM_TYPE:
			result = getTemplateMessageFrom("sam.xml");
			break;
		case GameData.MAU_BINH_TYPE:
			result = getTemplateMessageFrom("maubinh.xml");
			break;
		default:
			result = getTemplateMessageFrom("pikachu.xml");
			break;
		}

		return result;
	}

	private static ArrayList<String> getTemplateMessageFrom(String fileName) {

		ArrayList<String> result = null;
		InputStream fis;
		try {

			fis = BaseXeengGame.INSTANCE.getAssets().open(
					"templates/" + fileName);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document adoc = docBuilder.parse(fis);
			result = new ArrayList<String>();
			NodeList list = adoc.getElementsByTagName("root");
			NodeList childList = list.item(0).getChildNodes();
			for (int i = 0; i < childList.getLength(); i++) {

				Node anode = childList.item(i);
				if (anode.getNodeType() == Node.ELEMENT_NODE) {

					Element e = (Element) anode;
					result.add(e.getTextContent());
				}
			}
			fis.close();
		} catch (IOException e) {
		} catch (ParserConfigurationException e) {
		} catch (SAXException spe) {
		} finally {
		}

		return result;
	}

	public static double round(double unrounded, int precision, int roundingMode) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	}

	public static double roundByStep(float input, double step) {

		return (Math.round(round(input, 2, BigDecimal.ROUND_HALF_DOWN) / step) * step);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
		}
		return -1;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();
		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		// matrix.postScale(scale, scale);
		matrix.preScale(scale, scale);

		// recreate the new Bitmap
		// int scaledWidth = (int) ((float)newWidth/scale);
		// int scaledHeight = (int) ((float)newHeight/scale);
		// Bitmap croppedBitmap = Bitmap.createBitmap(bm, 0, 0, scaledWidth,
		// scaledHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		// Bitmap croppedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0,
		// newWidth, newHeight);
		return resizedBitmap;
	}

	public static String capitalize(String line) {
		if (line.length() < 1) {
			return line;
		}
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	/**
	 * Format given gold to nearest thousands
	 * 
	 * @param number
	 * @return
	 */
	public static String formatGold(long number) {
		String[] units = { "", "K", "M", "B" };
		int i = 0;
		while (number >= 1000) {
			number /= 1000;
			i++;
		}
		return number + " " + units[i];
	}

	/**
	 * Format gold to nearest thousands. Will provide 1 digit fraction precision
	 * 
	 * @param number
	 * @return
	 */
	public static String formatGold(float number) {
		// Format to nearest thousands
		String[] units = { "", "K", "M", "B" };
		int i = 0;
		while (number >= 1000 && i < units.length - 1) {
			number /= 1000;
			i++;
		}
		if (String.valueOf(number).contains(".0")) {
			return (int) number + units[i];
		}
		return number + units[i];
	}

	/**
	 * Round a number to nearest thousands
	 * 
	 * @param number
	 * @return
	 */
	public static long roundNumber(long number) {
		int i = 0;
		while (number > 1000) {
			number /= 1000;
			i++;
		}
		while (i > 0) {
			number *= 1000;
			i--;
		}
		return number;
	}

	/**
	 * Given an input photo stored in a uri, save it to a destination uri Note:
	 * This piece of code is copied from android Contact application source.
	 */
	public static boolean savePhotoFromUriToUri(Context context, Uri inputUri,
			Uri outputUri, boolean deleteAfterSave) {
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			outputStream = context.getContentResolver()
					.openAssetFileDescriptor(outputUri, "rw")
					.createOutputStream();
			inputStream = context.getContentResolver()
					.openInputStream(inputUri);

			final byte[] buffer = new byte[16 * 1024];
			int length;
			int totalLength = 0;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
				totalLength += length;
			}
			Log.v("CommonUtils", "Wrote " + totalLength + " bytes for photo "
					+ inputUri.toString());

			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			Log.e("CommonUtils",
					"Failed to write photo: " + inputUri.toString()
							+ " because: " + e);
			return false;
		} finally {
			if (deleteAfterSave) {
				context.getContentResolver().delete(inputUri, null, null);
			}
		}
		return true;
	}

	/**
	 * Will crop the bitmap to specified width height ratio
	 * 
	 * @param cr
	 * @param uri
	 * @param width
	 * @param height
	 * @param size
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getBitmapFromImageUri(ContentResolver cr, Uri uri,
			int width, int height, int size) throws IOException {
		Cursor cursor = cr.query(uri,
				new String[] { MediaStore.Images.Media.ORIENTATION }, null,
				null, null);
		int orientation = -1;
		if (cursor.moveToFirst() && cursor.getColumnCount() > 0) {
			orientation = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
		}
		Matrix matrix = new Matrix();
		matrix.postRotate(orientation);

		InputStream is = cr.openInputStream(uri);

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = true;

		Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
		is.close();

		int realWidth = 0;
		int realHeight = 0;
		int x = 0, y = 0;

		if (((float) (opts.outWidth / opts.outHeight)) >= ((float) (width / height))) {
			realHeight = opts.outHeight;
			realWidth = realHeight * width / height;
			x = (opts.outWidth - realWidth) / 2;
		} else {
			realWidth = opts.outWidth;
			realHeight = realWidth * height / width;
			y = (opts.outHeight - realHeight) / 2;
		}

		while (realWidth > width) {
			opts.inSampleSize *= 2;
			is = cr.openInputStream(uri);
			bm = BitmapFactory.decodeStream(is, null, opts);
			realWidth /= 2;
			realHeight /= 2;
			x /= 2;
			y /= 2;
			is.close();
		}

		opts.inJustDecodeBounds = false;
		is = cr.openInputStream(uri);
		bm = BitmapFactory.decodeStream(is, null, opts);
		// bm = Bitmap.createBitmap(bm, x, y, realWidth, realHeight, matrix,
		// true);
		bm = Bitmap.createBitmap(bm, x, y, realWidth, realHeight);
		is.close();

		while (bm.getRowBytes() * bm.getHeight() > size) {
			opts.inSampleSize *= 2;
			is = cr.openInputStream(uri);
			bm = BitmapFactory.decodeStream(is, null, opts);
			bm = Bitmap.createBitmap(bm, x / opts.inSampleSize, y
					/ opts.inSampleSize, realWidth / opts.inSampleSize,
					realHeight / opts.inSampleSize, matrix, true);
			is.close();
		}

		if (cursor != null) {
			cursor.close();
		}

		return bm;
	}

	public static void addBitmapToCache(long userId, Bitmap bitmap) {
		synchronized (avatarCache) {
			avatarCache.put(userId, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(long userId) {
		return avatarCache.get(userId);
	}

	public static void clearCache() {
		avatarCache.evictAll();
	}
}