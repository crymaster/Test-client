package com.tv.xeeng.dataLayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.utils.DataInput;
import com.tv.xeeng.dataLayer.utils.DataOutput;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.manager.Connectivity;
import com.tv.xeeng.manager.Logger;

public class GameSocket {

	// TODO: Max ping count is too high???
	private static final int MAX_PING_CNT = 300;

	public enum SocketState {

		UNINITIAL("UN-INITIAL"), INITIALING("INITALING"), READY("READY"), FAIL(
				"FAIL"), FORCE_CLOSE("FORCE_CLOSE"), CLOSING("CLOSING"), RECONNECT(
				"RECONNECT");

		private final String description;

		private SocketState(String pDesc) {

			description = pDesc;
		}

		public String toString() {

			return description;
		}
	}

	private static final int MAX_WRITE_BUFFER = 50 * 1024;
	private static final int MAX_READ_BUFFER = 50 * 1024;
	public static final int BUFFER_SIZE = 1024;

	static GameSocket instance;
	private String host;
	private int port;
	private Socket mSocket;
	private InputStream mis;
	private OutputStream mos;
	private boolean isRunning;
	private ListeningSocket mlistenSocket;
	private TalkingSocket mtalkSocket;
	private LocalBroadcastManager mLocalBroadcastManager;
	// private ArrayList<JSONObject> msgQueue;
	private ArrayBlockingQueue<JSONObject> msgQueue;
	private SocketState mstate;
	public boolean isLoggedOut = false;
	private boolean isNeedReconnection = false;
	private int mtimeToPing = 60000;

	public static GameSocket shareSocket() {

		if (instance == null) {
			initialize();
		}

		return instance;
	}

	public static void initialize() {
		// instance = new GameSocket("123.30.187.51", 6996);
		// instance = new GameSocket("183.91.3.104", 6868);
		// instance = new GameSocket("14.160.64.50", 6868); // tinhvan

		// instance = new GameSocket("192.168.93.202", 6868); // local hungdt
		// instance = new GameSocket("192.168.93.201", 6868); // local hungdt2
//		instance = new GameSocket("192.168.50.107", 6868); // local
		// instance = new GameSocket("192.168.137.1", 6868); // local mycomputer
		// for shared
		// network connected
		// devices
		// instance = new GameSocket("10.0.3.2", 6868); // local mycomputer
		// for genemotion
		// devices
		
//		 instance = new GameSocket("10.0.2.2", 6868); // local mycomputer
//		 instance = new GameSocket("192.168.101.125", 6868); // pin network
//		 instance = new GameSocket("192.168.159.1", 6868); // miniwifi network
//		 instance = new GameSocket("192.168.1.5", 6868); // home network
		 instance = new GameSocket("192.168.137.187", 6868); // WifiTest network
//		 instance = new GameSocket("123.30.179.204", 6868); // new server
	}

	private GameSocket(String host, int post) {

		this.host = host;
		this.port = post;
		mstate = SocketState.UNINITIAL;
		// msgQueue = new ArrayList<JSONObject>();
		msgQueue = new ArrayBlockingQueue<JSONObject>(128);
		try {
			mLocalBroadcastManager = LocalBroadcastManager
					.getInstance(CustomApplication.shareApplication()
							.getApplicationContext());
		} catch (Exception e) {

		}
	}

	public SocketState getState() {
		return mstate;
	}

	public boolean open() {
		if (isConnected()) {
			return true;
		}
		boolean result = true;
		close();
		new Thread(new Runnable() {
			public void run() {
				Logger.getInstance().info(GameSocket.this,
						"Will check network device");
				reallyOpen();
				/*
				 * State networkInfo = NetworkUtils.getWifiState(); if
				 * (networkInfo == State.CONNECTING) {
				 * 
				 * Logger.getInstance().warn(this, "device is connecting"); long
				 * wait = 0; while (wait < 2000) {
				 * 
				 * wait += 100; try {
				 * 
				 * Thread.sleep(100); networkInfo = NetworkUtils.getWifiState();
				 * Logger.getInstance().info( this, "check at time: " + wait +
				 * " state: " + networkInfo.name()); if (networkInfo ==
				 * State.CONNECTED) {
				 * 
				 * break; } } catch (InterruptedException e) {
				 * 
				 * sendNetworkErrorToUser(); return; } } }
				 * 
				 * if (networkInfo == State.CONNECTED) {
				 * 
				 * reallyOpen(); } else {
				 * 
				 * sendNetworkErrorToUser(); }
				 */

			}
		}).start();
		return result;
	}

	public boolean isConnected() {

		return mstate == SocketState.READY ? true : false;
	}

	public synchronized void reconnect() {

		Logger.getInstance().warn(this, "Need reconnect");
		mstate = SocketState.RECONNECT;
		Logger.getInstance().info(this, "Will reconnect");
		sendReconnectingIntent();
		open();

		if (msgQueue != null)
			msgQueue.clear();
		isNeedReconnection = true;
	}

	public void close() {

		Logger.getInstance().warn(this, "SOCKET IS CLOSED");
		mstate = SocketState.FORCE_CLOSE;
		// tuanda
		this.msgQueue.clear();

		resetNetworkTimer();
		setRunning(false);
		disconnect();
		reallyClose();
		cancelPingTimer();
	}

	public void changeTimeToPing(boolean isInGame) {

		// if (isInGame) {
		//
		// mtimeToPing = 30000;
		// } else {
		//
		// mtimeToPing = 60000;
		// }

		mtimeToPing = 60000;
		try {
			mSocket.setSoTimeout(mtimeToPing);
		} catch (SocketException e) {

		}
	}

	public void addRequestToQueue(JSONObject msg) {

		if (msg == null)
			return;

		if (mstate == SocketState.FAIL) {

			Logger.getInstance().warn(this,
					"Notify network error at addRequestToQueue Method");
			sendNetworkErrorToUser();
			return;
		}

		synchronized (msgQueue) {
			Logger.getInstance().info(this, "REQUEST: " + msg.toString());
			msgQueue.add(msg);
			// if (mtalkSocket != null)
			// mtalkSocket.interrupt();
		}
	}

	private Timer mNetworkTimer;
	private TimerTask mNetworkTask;

	private Timer mPingTimer;
	private TimerTask mPingTask;

	private void startPingTimer() {
		mPingTask = new TimerTask() {
			public void run() {
				sendPing();
			}
		};
		mPingTimer = new Timer();

		try {
			mPingTimer.schedule(mPingTask, mtimeToPing / 2, mtimeToPing / 2);
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
		}
	}

	private void cancelPingTimer() {
		if (mPingTask != null) {
			mPingTask.cancel();
		}

		if (mPingTimer != null) {
			mPingTimer.cancel();
			mPingTimer.purge();
		}
	}

	private void startNetworkTimer() {
		mNetworkTask = new TimerTask() {

			@Override
			public void run() {

				reconnect();
			}
		};
		mNetworkTimer = new Timer();
		mNetworkTimer.schedule(mNetworkTask, mtimeToPing);
	}

	private void resetNetworkTimer() {

		if (mNetworkTask != null) {

			mNetworkTask.cancel();
			// mNetworkTask = null;
		}

		if (mNetworkTimer != null) {

			mNetworkTimer.cancel();
			// mNetworkTimer = null;
		}
	}

	private void createSocket() {

		try {

			mSocket = new Socket();
			mSocket.setSoLinger(true, 1);
			mSocket.setSoTimeout(mtimeToPing);
			mSocket.setReceiveBufferSize(MAX_READ_BUFFER);
			mSocket.setSendBufferSize(MAX_WRITE_BUFFER);
			mSocket.setTcpNoDelay(true);
		} catch (SocketException e) {

			Logger.getInstance().error(this,
					"Socket exception when create socket");
		}
	}

	private void reallyOpen() {

		isLoggedOut = false;
		mstate = SocketState.INITIALING;
		try {

			// delay for next time open
			Logger.getInstance().info(this, "Will open socket...");
			if (mSocket == null || mSocket.isClosed()) {

				Logger.getInstance().info(this, "Need create socket object");
				createSocket();
				mSocket.setReuseAddress(false);
			} else {

				Logger.getInstance().info(this, "Socket was exist before");
			}

			if (!mSocket.isConnected()) {

				Logger.getInstance().info(this, "Need connect to host");
				mSocket.connect(new InetSocketAddress(host, port), 10000);
			} else {

				Logger.getInstance().info(this,
						"Socket did connect to host before");
			}

			mis = mSocket.getInputStream();
			mos = mSocket.getOutputStream();

			Logger.getInstance().info(this, "Socket opened successfully");
			Logger.getInstance().info(this,
					"Start listening and talking socket");
			isRunning = true;
			if (mlistenSocket != null) {

				mlistenSocket.isRunning = false;
				mlistenSocket.interrupt();
			}
			mlistenSocket = new ListeningSocket();
			mlistenSocket.start();

			if (mtalkSocket != null) {
				mtalkSocket.isRunning = false;
				mtalkSocket.interrupt();
			}
			mtalkSocket = new TalkingSocket();
			mtalkSocket.start();

			mstate = SocketState.READY;
			Logger.getInstance().info(this,
					"Started successfully with state: " + mstate.toString());

			if (isNeedReconnection) {
				sendNeedReconnectionIntent();
			}
		} catch (Exception e) {

			setSocketError();
			Logger.getInstance()
					.warn(this,
							"Notify network error at reallyOpen Method when catch Exception");
			sendNetworkErrorToUser();
		}
	}

	/**
	 * Get packet length
	 * 
	 * @throws IOException
	 */
	private int getPacketLength() throws IOException {

		int packageLength = 0;
		byte header[] = new byte[4];
		byte[] packageLen = new byte[1];
		int count = 0;
		// long readThreadStart = System.currentTimeMillis() + 15000;

		while (mis.read(packageLen) == 1) {
			header[count++] = packageLen[0];
			if (count == 4) {
				packageLength = (header[0] & 0xff) << 24;
				packageLength += (header[1] & 0xff) << 16;
				packageLength += (header[2] & 0xff) << 8;
				packageLength += (header[3] & 0xff);
				return packageLength;
			}
		}
		return 0;
		//
		// while (true) {
		//
		// if (!isRunning) {
		// return -2;
		// }
		//
		// int i = mis.read(packageLen);
		//
		// if (i == 1) {
		//
		// header[count++] = packageLen[0];
		// if (count == 4) {
		// packageLength = (header[0] & 0xff) << 24;
		// packageLength += (header[1] & 0xff) << 16;
		// packageLength += (header[2] & 0xff) << 8;
		// packageLength += (header[3] & 0xff);
		// break;
		// }
		// } else if (i < 0) {
		// return -1;
		// } else if (count == 0) {
		// return 0;
		// } else {
		//
		// // throw when timeout
		// if (System.currentTimeMillis() > readThreadStart) {
		// throw new Exception("Socket read package length timeout.");
		// }
		//
		// try {
		// Thread.sleep(20L);
		// } catch (Exception exception) {
		// }
		// }
		// }
		//
		// return packageLength;
	}

	/**
	 * Read image byte array data
	 */
	private byte[] readData() throws IOException {

		// get packet length
		int packageLength = getPacketLength();
		Logger.getInstance().info(this,
				"Data package has " + packageLength + " bytes");
		if (packageLength <= 0) {

			return null;
		} else if (packageLength > 100000) {

			return null;
		}

		// start read data
		// long readThreadStart = System.currentTimeMillis() + 15000;

		// init image byte array
		byte responseData[] = new byte[packageLength];
		int numRead = 0;
		int offset = 0;
		int len = 1024;
		do {

			if (packageLength - offset < len) {
				len = packageLength - offset;
			}

			// read data
			numRead = mis.read(responseData, offset, len);
			// increase byte read
			offset += numRead;

			// check end of packet
			if ((numRead <= -1 || offset >= packageLength)) {
				// return

			} else {
				// Thread.sleep(20);
			}

		} while (numRead > 0 && offset < packageLength);

		// throw timeout exception
		// throw new Exception("Socket timeout exception");
		return responseData;
	}

	/**
	 * Process response
	 * 
	 * @param aResponseData
	 * @throws Exception
	 */
	private void processResponses(byte[] aResponseData) throws JSONException {

		if (aResponseData == null) {

			if (!CustomApplication.shareApplication().wasInBackground
					&& !isLoggedOut) {

				Logger.getInstance().error(this,
						"Connect is closed, need reconnect");
				isNeedReconnection = true;
				setRunning(false);
				mstate = SocketState.FAIL;
				reconnect();
			}
			return;
		}
		// buffer to parse response data
		DataInput bufferResponse = new DataInput(aResponseData);
		// format

		bufferResponse.readUTF();
		// package
		String serializedObj = bufferResponse.readUTF();

		// convert to json object
		JSONObject resPkg = new JSONObject(serializedObj);
		// response package
		ArrayList<JSONObject> decodedMessages = new ArrayList<JSONObject>();
		if (resPkg.has("r")) {
			JSONArray arrMsgs = resPkg.getJSONArray("r");

			int size = arrMsgs.length();

			Log.e("" + this.getClass(), "size: " + size);
			if (size <= 0) {
				return;
			}

			for (int i = 0; i < size; i++) {

				// encoded response message
				JSONObject resMsg = arrMsgs.getJSONObject(i);
				// put into the current package
				if (resMsg.has("v")) {

					// Sometime ping response is not prioritized. We may not
					// receive ping response but we still receiver other
					// responses
					// instead.
					// In that case the connection is still alive.
					// Added by NguyenNK
					this.mlistenSocket.resetPingCnt();

					// ignore ping response
					if (size == 1
							&& NetworkUtils.isPingResponse(resMsg
									.getString("v"))) {

						Logger.getInstance().warn(this,
								"Ping, server still hold socket connection");

						this.mlistenSocket.resetPingCnt();
						return;
					}

					decodedMessages.add(resMsg);
				}
			}

			MessageService.getInstance().processMessage(decodedMessages);
		}
	}

	/*
	 * destroy 2 threads read and write
	 */
	private void disconnect() {

		setRunning(false);
		if (mlistenSocket != null) {

			mlistenSocket.interrupt();
		}
		if (mtalkSocket != null) {

			mtalkSocket.interrupt();
		}
		Logger.getInstance().info(this, "Disconnect successfully");
	}

	private void reallyClose() {

		mstate = SocketState.CLOSING;
		Logger.getInstance()
				.info(this, "Will create thread for closing socket");
		if (mSocket != null) {

			Thread closeThread = new Thread(new Runnable() {

				@Override
				public void run() {

					Logger.getInstance().info(this, "Begin closing socket");
					try {

						if (mSocket != null) {

							mSocket.close();
							mSocket = null;
						}

						mstate = SocketState.UNINITIAL;
						Logger.getInstance().info(this,
								"Really close Socket successfully");
					} catch (IOException e) {

						Logger.getInstance().error(this,
								"Cannot really close socket");
						mstate = SocketState.UNINITIAL;
					}
				}
			});

			closeThread.start();
		}
	}

	private void sendReconnectingIntent() {

		Intent i = new Intent(MessageService.INTENT_RECONECTING);
		i.putExtra(NetworkUtils.MESSAGE_STATUS, true);
		mLocalBroadcastManager.sendBroadcast(i);
	}

	private void sendNetworkErrorToUser() {
		Logger.getInstance().warn(this, "Network error");
		isConnectionSlowSent = true;
		if (CustomApplication.shareApplication().wasInBackground)
			return;
		if (BaseXeengGame.INSTANCE != null) {

			Logger.getInstance().info(this, "Network error in game");
			BaseXeengGame.INSTANCE.showNetWorkError();
		} else {

			Logger.getInstance().info(this, "Network error in UI");
			Intent i = new Intent(MessageService.INTENT_NETWORK_DEVICE_PROBLEM);
			i.putExtra(NetworkUtils.MESSAGE_STATUS, true);
			mLocalBroadcastManager.sendBroadcast(i);
		}
	}

	private void sendConnectionSlowWarningToUser() {
		Logger.getInstance().warn(this, "Slow connection");
		if (CustomApplication.shareApplication().wasInBackground)
			return;
		if (BaseXeengGame.INSTANCE != null) {
			Logger.getInstance().info(this, "Slow connection in game");
			// BaseXeengGame.INSTANCE.showConnectionSlowWarning();
		} else {

			Logger.getInstance().info(this, "Slow connection in UI");
			Intent i = new Intent(MessageService.INTENT_CONNECTION_SLOW);
			i.putExtra(NetworkUtils.MESSAGE_STATUS, true);
			mLocalBroadcastManager.sendBroadcast(i);
		}
	}

	private void sendNeedReconnectionIntent() {

		if (CustomApplication.shareApplication().wasInBackground)
			return;
		isNeedReconnection = false;

		if (BaseXeengGame.INSTANCE != null) {

			BaseXeengGame.INSTANCE.sendReconnection(true);
		} else {

			Intent i = new Intent(MessageService.INTENT_NEED_RECONNECTION);
			i.putExtra(NetworkUtils.MESSAGE_STATUS, true);
			mLocalBroadcastManager.sendBroadcast(i);
		}
	}

	private void setSocketError() {

		mstate = SocketState.FAIL;
		setRunning(false);
	}

	private void setRunning(boolean pIsRunning) {

		isRunning = pIsRunning;
		if (mlistenSocket != null) {

			mlistenSocket.isRunning = pIsRunning;
		}

		if (mtalkSocket != null) {

			mtalkSocket.isRunning = pIsRunning;
		}
	}

	@SuppressWarnings("unused")
	private boolean isNetworkLatency() throws IOException {

		boolean isLantency = false;
		for (int i = 0; i < 5; i++) {

			long before = System.currentTimeMillis();
			InetAddress.getByName(host).isReachable(5);
			long after = System.currentTimeMillis();
			long timeDiff = after - before;
			Logger.getInstance().warn(
					this,
					"SPEED TEST: before: " + before + " after: " + after
							+ " timeDiff: " + timeDiff);
		}

		return isLantency;
	}

	private long previousTS;
	private boolean firstPing = true;
	private volatile int pingCnt = 0;

	/*
	 * private void sendPing() { Calendar c = Calendar.getInstance(); long now =
	 * c.getTimeInMillis();
	 * 
	 * if (firstPing) { BusinessRequester.getInstance().ping(); firstPing =
	 * false; previousTS = now; Log.d("PING", "PING is sent -- first PING " +
	 * pingCnt); pingCnt++; } else { if(now - previousTS > (mtimeToPing * 9 /
	 * 10) ) { BusinessRequester.getInstance().ping(); previousTS = now;
	 * Log.d("PING", "PING is sent -- first PING (" + pingCnt + ")"); pingCnt++;
	 * } } }
	 */

	private boolean isConnectionSlowSent = false;;

	private void sendPing() {
		BusinessRequester.getInstance().ping();
		Log.d("PING", "PING is sent -- pingCnt:" + pingCnt);
		pingCnt++;
		if (pingCnt > MAX_PING_CNT) {
			Log.d("PING", "Ping errors exceed limit " + pingCnt);
			// throw new IOException("Networking error");
			setSocketError();
			cancelPingTimer();
			if (!isLoggedOut) {
				Logger.getInstance()
						.warn(this,
								"Notify network error at Listening Thread when catch Exception");
				sendNetworkErrorToUser();
			}
		}

		if (Connectivity.isConnectedFast(CustomApplication.shareApplication()
				.getApplicationContext())) {
			isConnectionSlowSent = false;
		} else if (!isConnectionSlowSent) {
			isConnectionSlowSent = true;
			sendConnectionSlowWarningToUser();
		}
	}

	private class ListeningSocket extends Thread {

		private boolean isRunning;

		public ListeningSocket() {
			firstPing = true;
			isRunning = true;
			pingCnt = 0;
		}

		public void resetPingCnt() {
			pingCnt = 0;
		}

		@Override
		public void run() {

			try {
				startPingTimer();
				while (isRunning) {
					// periodically send Ping
					processResponses(readData());
					// if (pingCnt > MAX_PING_CNT) {
					// Log.d("PING", "Ping errors exceed limit " + pingCnt);
					// throw new IOException("Networking error");
					// }
					// resetNetworkTimer();
				}
			}
			// catch (InterruptedIOException e) {
			// BusinessRequester.getInstance().ping();
			// startNetworkTimer();
			// Logger.getInstance()
			// .error(this,
			// "Interrupted ioexception, Need ping to server to know that it alive");
			// e.printStackTrace();
			// }
			catch (IOException e) {
				e.printStackTrace();
				Logger.getInstance().error(this,
						"IOException happen at Listening Socket");
				setRunning(false);
				if (mstate != SocketState.FORCE_CLOSE) {

					mstate = SocketState.FAIL;
				}
				if (!isLoggedOut
						&& !CustomApplication.shareApplication().wasInBackground) {

					reconnect();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// catch (Exception e) {
			// e.printStackTrace();
			// Logger.getInstance().error(this,
			// "General Exception: " + e.getMessage());
			// setSocketError();
			// cancelPingTimer();
			// if (!isLoggedOut) {
			// Logger.getInstance()
			// .warn(this,
			// "Notify network error at Listening Thread when catch Exception");
			// sendNetworkErrorToUser();
			// }
			// }
		}
	}

	private class TalkingSocket extends Thread {

		private boolean isRunning;

		public TalkingSocket() {

			isRunning = true;
		}

		@Override
		public void run() {

			while (isRunning) {

				Logger.getInstance().info(this, "talk is running");
				try {

					// copy all elements of msg queue
					ArrayList<JSONObject> copyQueue = new ArrayList<JSONObject>();
					// synchronized (msgQueue) {
					JSONObject obj = msgQueue.take();
					copyQueue.add(obj);
					// for (JSONObject obj : msgQueue) {
					//
					// copyQueue.add(obj);
					// }
					// msgQueue.clear();
					// }

					if (copyQueue.isEmpty()) {
						Logger.getInstance()
								.warn(this,
										"Talk thread will block for waiting next request");
						// this.join();
						continue;
					}

					Logger.getInstance().info(this,
							"Will send data to server...");
					// buffer out
					DataOutput bufferRequest = new DataOutput();
					// format
					bufferRequest.writeUTF("json");
					// requests package
					org.json.JSONArray pkgMsgs = new org.json.JSONArray();
					for (JSONObject jsonObject : copyQueue) {
						pkgMsgs.put(jsonObject);
					}
					// request data
					JSONObject encodedData = new JSONObject();
					try {
						encodedData.put("r", pkgMsgs);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// serialize data
					String serializedData = encodedData.toString();
					// put into buffer
					bufferRequest.writeUTF(serializedData);

					// write to output stream
					byte[] outData = bufferRequest.getBytes();

					byte[] pkgData;
					if (outData != null) {
						DataOutput bufferOut = new DataOutput();
						bufferOut.writeByte(1);
						bufferOut.writeInt(outData.length);
						bufferOut.write(outData);
						pkgData = bufferOut.getBytes();
						mos.write(pkgData);
						mos.flush();
					}

					Logger.getInstance().info(this, "Sent data successfully");
					Logger.getInstance().warn(this,
							"Talk thread will block for waiting next request");

					// Restart ping everytime we successfully send a packet.
					cancelPingTimer();
					startPingTimer();

					if (isLoggedOut) {
						close();
					}
					// this.join();
					Thread.sleep(150);
				} catch (InterruptedException e) {
					Logger.getInstance().warn(this, "Talk thread is interrupt");
				} catch (IOException ioe) {

					Logger.getInstance().error(this,
							"Error in outputstream socket");
					setSocketError();
				}
			}
		}
	}

	/*
	 * PLACE GETTER & SETTER IN THE LAST
	 */
	public boolean isListening() {
		return isRunning;
	}

	public void setListening(boolean isListening) {
		this.isRunning = isListening;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public OutputStream getMos() {
		return mos;
	}

	public int getTimeToPing() {
		return mtimeToPing;
	}
}
