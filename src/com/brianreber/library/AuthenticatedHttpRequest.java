package com.brianreber.library;

import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author breber
 */
public class AuthenticatedHttpRequest extends AsyncTask<String, Integer, String> {

	/**
	 * The context to get preferences with
	 */
	protected Context mContext;

	/**
	 * The URL to request
	 */
	protected String url = "";
	
	/**
	 * Whether this is a post request
	 */
	protected boolean isPost = false;
	
	public AuthenticatedHttpRequest(Context ctx) {
		this.mContext = ctx;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String r) {
		Log.e(AuthenticatedHttpRequest.class.getName(), "Result: " + r);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(String... params) {
		HttpClient client = new DefaultHttpClient();
		HttpUriRequest request;
		
		if (isPost) {
			request = new HttpPost(url);
		} else {
			request = new HttpGet(url);
		}
		
		// Don't handle redirects - redirects get us into trouble when not logged in
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		request.setHeader("Content-Type", "application/json;charset=UTF-8");
		request.setHeader("Cookie", AuthUtil.getAuthCookie(mContext));

		try {
			HttpResponse response = client.execute(request);

			if (HttpURLConnection.HTTP_OK == response.getStatusLine().getStatusCode()) {
				String contents = HttpUtils.readStreamAsString(response.getEntity().getContent());
				return contents;
			} else if (302 == response.getStatusLine().getStatusCode()) {
				// We are redirecting - instead, invalidate the auth token and retry the request
				// TODO:
			} else {
				Log.d("ART", "Status Code: " + response.getStatusLine().getStatusCode());
				// TODO:
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}