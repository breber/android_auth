package com.brianreber.library;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class AuthenticateTask extends AsyncTask<String, Integer, String> {

	private Activity mActivity;

	public AuthenticateTask(Activity activity) {
		mActivity = activity;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			if (params.length == 1) {
				return GoogleAuthUtil.getToken(mActivity, params[0], "ah");
			} else if (params.length == 2) {
				return GoogleAuthUtil.getToken(mActivity, params[0], params[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UserRecoverableAuthException recoverableException) {
			Intent recoveryIntent = recoverableException.getIntent();
			mActivity.startActivityForResult(recoveryIntent, Constants.USER_RECOVERY_INTENT);
		} catch (GoogleAuthException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// TODO Auto-generated method stub
	}

}
