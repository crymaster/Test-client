package com.tv.xeeng.manager;

import android.content.Context;
import com.tv.xeeng.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class ConfigData {
	private String partnerId = "96";
	private String refCode = "08";

	private static ConfigData INSTANCE;

	public static ConfigData getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ConfigData();
		}

		return INSTANCE;
	}

	public ConfigData() {

	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getRefCode() {
		return refCode;
	}

	public void setParterId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}

	public void readConfigData(Context context) {
		try {
			FileInputStream fis = context.openFileInput("configs");
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader buffer = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}

			if (sb != null && sb.length() > 0) {
				JSONObject object = new JSONObject(sb.toString());
				partnerId = object.getString("partnerId");
				refCode = object.getString("refCode");
			}

		} catch (Exception e) {
			e.printStackTrace();

			partnerId = CommonUtils.getPartnerIdFromAssets();
			refCode = CommonUtils.getRefcodeFromAssets();
		}
	}

	public void saveConfigData(Context context) {
		String filename = "configs";
		FileOutputStream outputStream;

		JSONObject object = new JSONObject();
		try {
			object.put("partnerId", partnerId);
			object.put("refCode", refCode);

			String content = object.toString();
			outputStream = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			outputStream.write(content.getBytes());
			outputStream.close();

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
