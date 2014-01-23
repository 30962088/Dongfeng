package com.media.dongfeng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.media.dongfeng.exception.ZhiDaoApiException;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.exception.ZhiDaoParseException;
import com.media.dongfeng.model.User;
import com.media.dongfeng.net.NetDataSource;
import com.media.dongfeng.utils.Utils;

public class SettingActivity extends BaseActivity {

	private EditText mEtName;
	private EditText mEtMedia;
	private EditText mEtMailBox;
	private TextView mSaveBtn;
	private TextView mCancelBtn;

	private boolean mIsLoginTaskFree = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		mEtName = (EditText) findViewById(R.id.etName);
		mEtMedia = (EditText) findViewById(R.id.etMedia);
		mEtMailBox = (EditText) findViewById(R.id.etMailBox);
		mSaveBtn = (TextView) findViewById(R.id.saveBtn);
		mSaveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String name = mEtName.getText().toString();
				final String media = mEtMedia.getText().toString();
				final String mail = mEtMailBox.getText().toString();
				if (TextUtils.isEmpty(name)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingActivity.this);
					builder.setMessage("记者姓名不能为空").setCancelable(false)
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
					return;
				}
				if (TextUtils.isEmpty(media)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingActivity.this);
					builder.setMessage("媒体名称不能为空").setCancelable(false)
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
					return;
				}
				if (TextUtils.isEmpty(mail)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingActivity.this);
					builder.setMessage("邮箱地址不能为空").setCancelable(false)
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
					return;
				}
				if (!validateEmail(mail)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingActivity.this);
					builder.setMessage("邮箱地址填写有误").setCancelable(false)
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
					return;
				}

				if (MainTabActivity.mUser != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SettingActivity.this);
					builder.setMessage("您的操作可能会修改您原来的资料确定保存资料?")
							.setCancelable(false)
							.setPositiveButton("保存资料", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									login(name, media, mail);
								}
							}).setNegativeButton("暂不保存", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();
				} else {
					login(name, media, mail);
				}
			}
		});

		mCancelBtn = (TextView) findViewById(R.id.cancelBtn);
		mCancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainTabActivity) getParent())
						.selectTab(MainTabActivity.SUCAI_TAG);
			}
		});
	}

	public static boolean validateEmail(String email) {
		String check = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern regex = Pattern.compile(check);
		return regex.matcher(email).matches();
	}

	private void login(String name, String media, String email) {
		if (!mIsLoginTaskFree) {
			return;
		}
		try {
			new LoginTask().execute(name, media, email);
		} catch (RejectedExecutionException e) {

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MainTabActivity.mUser = Utils.loadUser(this);
		if (MainTabActivity.mUser != null) {
			mEtName.setText(MainTabActivity.mUser.name);
			mEtMedia.setText(MainTabActivity.mUser.media);
			mEtMailBox.setText(MainTabActivity.mUser.email);
			mSaveBtn.setText("修改资料");
			mCancelBtn.setVisibility(View.VISIBLE);
		} else {
			mSaveBtn.setText("保存资料");
			mCancelBtn.setVisibility(View.GONE);
		}
	}

	private class LoginTask extends AsyncTask<String, Void, User> {
		private String name;
		private String media;
		private String email;

		@Override
		protected User doInBackground(String... params) {
			name = params[0];
			media = params[1];
			email = params[2];
			try {
				if(MainTabActivity.mUser != null){
					PushManager.delTags(SettingActivity.this, new ArrayList<String>(){{
						add(MainTabActivity.mUser.mid);
					}});
				}
				
				User user = NetDataSource.getInstance(getApplicationContext())
						.login(name, media, email,"");
				List<String> tags = new ArrayList<String>();
				tags.add(user.mid+"_"+Secure.getString(getContentResolver(), Secure.ANDROID_ID));
				
				PushManager.setTags(SettingActivity.this, tags);
				return user;
			} catch (ZhiDaoParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ZhiDaoApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ZhiDaoIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			mIsLoginTaskFree = true;
			if (result == null || TextUtils.isEmpty(result.mid)) {
				Toast.makeText(getApplicationContext(), "保存失败", 0).show();
				return;
			}
			result.name = name;
			result.media = media;
			result.email = email;
			MainTabActivity.mUser = result;
			Utils.saveUser(getApplicationContext(), result);
			Toast.makeText(getApplicationContext(), "保存成功", 0).show();
			MainTabActivity activity = ((MainTabActivity) getParent());
			activity.getmSucaiIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.getmInfoIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.selectTab(MainTabActivity.SUCAI_TAG);
			
		}
	}

}
