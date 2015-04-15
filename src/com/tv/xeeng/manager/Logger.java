package com.tv.xeeng.manager;

import android.annotation.SuppressLint;
import android.util.Log;
import com.tv.xeeng.CustomApplication;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Logger {

	private boolean isLog = true;
	Map<String, Boolean> tagRegister;

	static Logger instance;

	public static Logger getInstance() {

		if (instance == null) {

			instance = new Logger();
		}

		return instance;
	}

	private Logger() {

		tagRegister = new HashMap<String, Boolean>();

	}

	public boolean isEnable() {

		return isLog;
	}

	@SuppressLint("DefaultLocale")
	public void warn(String tag, String msg) {

		if (!isLogPermit(tag)) {

			return;
		}

		Log.w(tag.toUpperCase(), msg);
	}

	@SuppressLint("DefaultLocale")
	public void error(String tag, String msg) {

		if (!isLogPermit(tag)) {

			return;
		}

		Log.e(tag.toUpperCase(), msg);
	}

	@SuppressLint("DefaultLocale")
	public void info(String tag, String msg) {

		if (!isLogPermit(tag)) {

			return;
		}
		Log.i(tag.toUpperCase(), msg);
	}

	public void info(Object obj, String msg) {

		registerLogForClassIfNeed(obj.getClass().getSimpleName());
		if (isLogPermit(obj.getClass().getSimpleName())) {

			Log.i(obj.getClass().getSimpleName(), msg);
		}
	}

	public void warn(Object obj, String msg) {

		registerLogForClassIfNeed(obj.getClass().getSimpleName());
		if (isLogPermit(obj.getClass().getSimpleName())) {

			Log.w(obj.getClass().getSimpleName(), msg);
		}
	}

	public void error(Object obj, String msg) {

		registerLogForClassIfNeed(obj.getClass().getSimpleName());
		if (isLogPermit(obj.getClass().getSimpleName())) {

			Log.e(obj.getClass().getSimpleName(), msg);
		}
	}

	public void enableLog(boolean isenable) {

		Log.w("Logger", "set enable: " + isenable);
		if (isenable == isLog)
			return;
		isLog = isenable;
		for (String key : tagRegister.keySet()) {

			tagRegister.put(key, Boolean.valueOf(isLog));
		}
	}

	public void enableLogForTag(String tag, boolean isenable) {

		tagRegister.put(tag, Boolean.valueOf(isenable));
	}

	public void registerLogForClass(String classDes) {

		tagRegister.put(classDes, Boolean.TRUE);
	}

	private void registerLogForClassIfNeed(String classdes) {

		if (tagRegister.get(classdes) == null) {

			tagRegister.put(classdes, Boolean.TRUE);
		}
	}

	private boolean isLogPermit(String tag) {

		boolean result = false;
		if (isLog) {

			Boolean islogtag = tagRegister.get(tag);
			if (islogtag == null) {

				result = false;
			} else {

				if (islogtag.booleanValue()) {

					result = true;
				} else {

					result = false;
				}
			}
		} else {

			result = false;
		}
		return result;
	}

	private String getTag() {

		String result = null;
		StackTraceElement[] stackTraceElements = Thread.currentThread()
				.getStackTrace();
		result = stackTraceElements[stackTraceElements.length - 1]
				.getFileName();
		if (result.endsWith(".java")) {

			result = result.substring(0, result.length() - 5);
		}
		return result;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Class[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = null;
		try {
			classLoader = CustomApplication.shareApplication().getClassLoader();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
