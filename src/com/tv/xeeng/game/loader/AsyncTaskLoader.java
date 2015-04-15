package com.tv.xeeng.game.loader;

import android.os.AsyncTask;
import com.tv.xeeng.manager.Logger;

public class AsyncTaskLoader extends
		AsyncTask<IAsyncCallback, Integer, Boolean> {

	IAsyncCallback[] _params;
	private int mprogress = 0;

	@Override
	protected Boolean doInBackground(IAsyncCallback... params) {

		this._params = params;
		int count = params.length;
		mprogress = 0;
		for (int i = 0; i < count; i++) {

			Logger.getInstance().warn(this, "will load resource : " + i);
			params[i].workToDo();
			Logger.getInstance().info(this, "done load resource : " + i);
			mprogress = i;
			onProgressUpdate(mprogress);
		}
		return true;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

		super.onProgressUpdate(values);
		Logger.getInstance().warn(this, "will notify");
		_params[mprogress].onComplete();
		Logger.getInstance().info(this, "done notify");
	}
}
