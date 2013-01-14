package com.brianreber.library;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

/**
 * @author breber
 */
public class AuthUtil {

	private static final String TAG = AuthUtil.class.getName();

	public static boolean isLoggedIn(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.AUTH_PREFS, 0);
		return (!"".equals(prefs.getString(Constants.PREF_USERNAME, "")));
	}

	public static boolean hasInvalidAuthToken(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.AUTH_PREFS, 0);
		return (!"".equals(prefs.getString(Constants.PREF_USERNAME, "")) &&
				 "".equals(prefs.getString(Constants.PREF_AUTHTOKEN, "")));
	}

	public static void startLogin(Activity activity) {
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
				false, null, null, null, null);
		activity.startActivityForResult(intent, Constants.ACCOUNT_CHOOSER_REQUEST);
	}

	public static void performLoginFromResult(Activity activity, Intent result, String url) {
		String accountName = result.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		Log.d(TAG, "Account Name: " + accountName);

		SharedPreferences prefs = activity.getSharedPreferences(Constants.AUTH_PREFS, 0);
		prefs.edit().putString(Constants.PREF_USERNAME, accountName).commit();

		performLoginFromPrefs(activity, url);
	}

	public static void performLoginFromPrefs(Activity activity, String url) {
		SharedPreferences prefs = activity.getSharedPreferences(Constants.AUTH_PREFS, 0);
		String accountName = prefs.getString(Constants.PREF_USERNAME, "");

		AuthenticateTask authTask = new AuthenticateTask(activity, url);
		authTask.execute(accountName);
	}

	public static String getAuthCookie(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.AUTH_PREFS, 0);
		return prefs.getString(Constants.PREF_AUTHCOOKIE, "");
	}

	public static void invalidateToken(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.AUTH_PREFS, 0);
		GoogleAuthUtil.invalidateToken(ctx, prefs.getString(Constants.PREF_AUTHTOKEN, ""));

		prefs.edit().remove(Constants.PREF_AUTHTOKEN).commit();

		Intent intent = new Intent(Constants.TOKEN_INVALIDATED);
		ctx.sendBroadcast(intent);
	}
}
