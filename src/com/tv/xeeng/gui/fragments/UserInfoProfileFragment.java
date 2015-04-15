package com.tv.xeeng.gui.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.utils.Base64;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.BaseLayoutXeengActivity;
import com.tv.xeeng.R;

public class UserInfoProfileFragment extends Fragment {
	private static final int MAX_FILE_SIZE = 1 * 256 * 1024; // 256kb

	private static final int USER_AVATAR_SIZE = 200;

	private static final int REQUEST_CODE_CAMERA = 1;
	private static final int REQUEST_CODE_GALLERY = 2;
	private static final int REQUEST_CODE_PICTURE_CROP = 4;

	private static final String FILE_PROVIDER_AUTHORITY = "com.tv.xeeng.fileprovider";

	private ImageView avatar;
	private TextView username;
	private TextView xeengValue;
	private TextView goldValue;
	private TextView level;
	private TextView viplevel;
	// private TextView stats;
	private TextView gender;

	private Button chooseAvatarFromGallery;
	private Button takePhoto;

	private Handler uiHandler;

	private Uri tempUri;
	private Uri tempCroppedUri;

	public static UserInfoProfileFragment newInstance() {
		UserInfoProfileFragment fragment = new UserInfoProfileFragment();

		return fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("UserInfoProfileFragment", "OnActivityResult called");

		if (resultCode != Activity.RESULT_OK) {
			Log.d("UserInfoProfile", "Activity result not OK");
			return;
		}

		if (requestCode == REQUEST_CODE_GALLERY
				|| requestCode == REQUEST_CODE_CAMERA) {
			final Uri uri;
			boolean isWritable = false;
			if (data != null && data.getData() != null) {
				uri = data.getData();
			} else {
				uri = tempUri;
				isWritable = true;
			}
			final Uri toCrop;
			if (isWritable) {
				// Since this uri belongs to our file provider, we know that it
				// is writable
				// by us. This means that we don't have to save it into another
				// temporary
				// location just to be able to crop it.
				toCrop = uri;
			} else {
				toCrop = tempUri;
				try {
					CommonUtils.savePhotoFromUriToUri(getActivity(), uri,
							toCrop, false);
				} catch (SecurityException e) {
					Log.d("UserInfoProfile",
							"Did not have read-access to uri : " + uri);
					return;
				}
			}
			try {
				startCropPictureActivity(toCrop, tempCroppedUri);
			} catch (Exception ex) {
				Toast.makeText(getActivity(),
						"Cropping is not supported. Auto crop instead",
						Toast.LENGTH_SHORT).show();
				requestCode = REQUEST_CODE_PICTURE_CROP;
				ex.printStackTrace();
			}
		}
		if (requestCode == REQUEST_CODE_PICTURE_CROP) {
			final Uri uri;
			if (data != null && data.getData() != null) {
				uri = data.getData();
			} else {
				uri = tempCroppedUri;
			}

			try {
				getActivity().getContentResolver().delete(tempUri, null, null);

				Bitmap bitmap = CommonUtils.getBitmapFromImageUri(getActivity()
						.getContentResolver(), uri, USER_AVATAR_SIZE,
						USER_AVATAR_SIZE, MAX_FILE_SIZE);
				if (bitmap != null) {
					Log.d("UserInfoProfileFragment",
							"Add bitmap to cache id = "
									+ GameData.shareData().getMyself().id);
					Log.d("UserInfoProfileFragment",
							"Image width: " + bitmap.getWidth()
									+ " --- Image height" + bitmap.getHeight());
					CommonUtils.addBitmapToCache(GameData.shareData()
							.getMyself().id, bitmap);

					setAvatar();

					new UploadAvatarTask().execute();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_info_profile,
				container, false);

		avatar = (ImageView) view.findViewById(R.id.imv_profile_avatar);
		username = (TextView) view.findViewById(R.id.tv_profile_name);
		xeengValue = (TextView) view.findViewById(R.id.tv_xeeng_value);
		goldValue = (TextView) view.findViewById(R.id.tv_gold_value);
		gender = (TextView) view.findViewById(R.id.tv_gender);
		level = (TextView) view.findViewById(R.id.tv_level);
		viplevel = (TextView) view.findViewById(R.id.tv_viplevel);
		chooseAvatarFromGallery = (Button) view
				.findViewById(R.id.btn_choose_from_gallery);
		takePhoto = (Button) view.findViewById(R.id.btn_take_photo);

		Player myself = GameData.shareData().getMyself();

		username.setText(myself.character);
		xeengValue.setText(CommonUtils.formatCash(myself.xeeng));
		goldValue.setText(CommonUtils.formatCash(myself.cash));
		gender.setText(myself.sex ? "Nam" : "Nữ");
		level.setText(myself.level.getName());

		if (myself.vipId == 0) {
			viplevel.setText("");
		} else {
			viplevel.setText("Vip " + myself.vipId);
		}
		setAvatar();

		// tempUri = createTempImageUri();
		tempCroppedUri = createTempCroppedImageUri();

		chooseAvatarFromGallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startGalleryActivity();
				// Toast.makeText(getActivity(),
				// getResources().getString(R.string.text_developing),
				// Toast.LENGTH_SHORT).show();
			}
		});
		takePhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startCameraActivity();
				// Toast.makeText(getActivity(),
				// getResources().getString(R.string.text_developing),
				// Toast.LENGTH_SHORT).show();
			}
		});

		uiHandler = new Handler();

		return view;
	}

	private void startCameraActivity() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);

		tempUri = createTempImageUri();
		// tempCroppedUri = createTempCroppedImageUri();

		if (tempUri != null) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
			addUriPermission(intent, tempUri);

			// try {
			// intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT,
			// tempUri));
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }
		}

		startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}

	private void startGalleryActivity() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");

		tempUri = createTempImageUri();
		// tempCroppedUri = createTempCroppedImageUri();

		if (tempUri != null) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
			addUriPermission(intent, tempUri);
			// try {
			// intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT,
			// tempUri));
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// }
		}

		startActivityForResult(intent, REQUEST_CODE_GALLERY);
	}

	private void addUriPermission(Intent intent, Uri uri) {
		intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
				| Intent.FLAG_GRANT_READ_URI_PERMISSION);

		List<ResolveInfo> resInfoList = getActivity().getPackageManager()
				.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);

		for (ResolveInfo resolveInfo : resInfoList) {
			getActivity().grantUriPermission(
					resolveInfo.activityInfo.packageName,
					uri,
					Intent.FLAG_GRANT_READ_URI_PERMISSION
							| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}
	}

	private void startCropPictureActivity(Uri inputUri, Uri outputUri)
			throws ActivityNotFoundException {
		Intent intent = new Intent("com.android.camera.action.CROP");

		// intent.setData(inputUri);
		intent.setDataAndType(inputUri, "image/*");

		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", USER_AVATAR_SIZE);
		intent.putExtra("outputY", USER_AVATAR_SIZE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
		addUriPermission(intent, outputUri);
		addUriPermission(intent, inputUri);

		// try {
		// intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT,
		// outputUri));
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }

		startActivityForResult(intent, REQUEST_CODE_PICTURE_CROP);
	}

	private Uri createTempImageUri() {
		String filePath = pathForTempPhoto(getActivity(), "avatar_temp_"
				+ System.currentTimeMillis() + ".jpg");
		if (filePath != null) {
			return FileProvider.getUriForFile(getActivity(),
					FILE_PROVIDER_AUTHORITY, new File(filePath));
		}
		return null;
	}

	private Uri createTempCroppedImageUri() {
		String filePath = pathForTempPhoto(getActivity(), "avatar_temp_"
				+ System.currentTimeMillis() + "_cropped.jpg");
		if (filePath != null) {
			return FileProvider.getUriForFile(getActivity(),
					FILE_PROVIDER_AUTHORITY, new File(filePath));
		}
		return null;
	}

	private String pathForTempPhoto(Context context, String fileName) {
		File dir = context.getCacheDir();

		if (dir == null) {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				dir = context.getExternalCacheDir();
			}
		}
		if (dir != null) {
			dir.mkdirs();
			final File f = new File(dir, fileName);
			return f.getAbsolutePath();
		}
		return null;
	}

	private void setAvatar() {
		avatar.setImageResource(R.drawable.avatar_default);
		if (GameData.shareData().getMyself().id < 1) {
			return;
		}
		Bitmap bitmap = CommonUtils.getBitmapFromMemCache(GameData.shareData()
				.getMyself().id);
		if (bitmap != null) {
			avatar.setImageBitmap(bitmap);
		} else {
			// Try get it from server
			Log.d("UserInfoProfile",
					"Avatar cached file not found. Will try get it from server");
			BackgroundThreadManager.post(new Runnable() {

				@Override
				public void run() {
					if (BusinessRequester.getInstance().getUserAvatar(
							GameData.shareData().getMyself().id)) {
						uiHandler.post(new Runnable() {

							@Override
							public void run() {
								setAvatar();
							}
						});
					}
				}
			});
		}
	}

	private class UploadAvatarTask extends AsyncTask<Void, Void, HttpResponse> {
		@Override
		protected HttpResponse doInBackground(Void... params) {
			if (GameData.shareData().getMyself().id < 1) {
				return null;
			}
			String url = "http://avatar."
					+ getActivity().getResources().getString(
							R.string.domain_name)
					+ "/XeengUserAvatar/data?a=upload&uid="
					+ GameData.shareData().getMyself().id;
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);

			try {
				Bitmap bitmap = CommonUtils.getBitmapFromMemCache(GameData
						.shareData().getMyself().id);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 100, os);

				StringEntity se = new StringEntity(Base64.encode(os
						.toByteArray()));
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"image/jpeg"));

				request.setEntity(se);
				return client.execute(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(HttpResponse result) {
			super.onPostExecute(result);
			((BaseLayoutXeengActivity) getActivity()).hideLoading();
			if (result != null) {
				try {
					byte[] bytes = new byte[1024];
					result.getEntity().getContent().read(bytes);
					String response = new String(bytes);
					if (response != null && response.contains("UPLOAD_OK")) {
						Toast.makeText(getActivity(),
								"Cập nhật avatar thành công",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(),
								"Cập nhật avatar thất bại",
								Toast.LENGTH_SHORT).show();
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			((BaseLayoutXeengActivity) getActivity()).showLoading(
					"Uploading Avatar...", new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							UploadAvatarTask.this.cancel(true);
							((BaseLayoutXeengActivity) getActivity())
									.hideLoading();
						}
					}, false);
		}
	}

}
