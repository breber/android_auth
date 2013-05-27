package com.brianreber.library;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

/**
 * @author breber
 */
public class AuthenticateTask extends AsyncTask<String, Integer, String> {
	public static final String TAG = AuthenticateTask.class.getName();

	private final Activity mActivity;
	private final String mUrl;
	private String mAuthToken;

	public AuthenticateTask(Activity activity, String url) {
		mActivity = activity;

		if (!url.endsWith("/")) {
			mUrl = url + "/";
		} else {
			mUrl = url;
		}
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			if (params.length == 1) {
				mAuthToken = GoogleAuthUtil.getToken(mActivity, params[0], "ah");
			} else if (params.length == 2) {
				mAuthToken = GoogleAuthUtil.getToken(mActivity, params[0], params[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UserRecoverableAuthException recoverableException) {
			Intent recoveryIntent = recoverableException.getIntent();
			mActivity.startActivityForResult(recoveryIntent, Constants.USER_RECOVERY_INTENT);
		} catch (GoogleAuthException e) {
			e.printStackTrace();
		}

		return getAuthCookie(mAuthToken);
	}

	/**
     * Retrieves the authorization cookie associated with the given token. This
     * method should only be used when running against a production appengine
     * backend (as opposed to a dev mode server).
     */
    private String getAuthCookie(String authToken) {
    	final String AUTH_COOKIE_NAME = "ACSID";
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
	        // Get SACSID cookie
			httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
			String uri = mUrl + "_ah/login?continue=http://localhost/&auth=" + authToken;

			HttpGet method = new HttpGet(uri);
			HttpResponse res = httpClient.execute(method);
			StatusLine statusLine = res.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			Header[] headers = res.getHeaders("Set-Cookie");

			if (statusCode != 302 || headers.length == 0) {
		        return null;
			}

			for (Cookie cookie : httpClient.getCookieStore().getCookies()) {
				if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
					return AUTH_COOKIE_NAME + "=" + cookie.getValue();
				}
			}
		} catch (IOException e) {
			Log.w(TAG, "Got IOException " + e);
			Log.w(TAG, Log.getStackTraceString(e));
		} finally {
			httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
		}

		return null;
    }

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.d(TAG, "Result: " + result);

		SharedPreferences prefs = mActivity.getSharedPreferences(Constants.AUTH_PREFS, 0);
		Editor edit = prefs.edit();
		edit.putString(Constants.PREF_AUTHTOKEN, mAuthToken);
		edit.putString(Constants.PREF_AUTHCOOKIE, result);
		edit.commit();
	}

}
