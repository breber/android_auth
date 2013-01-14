package com.brianreber.library;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
public abstract class AuthenticatedHttpRequest extends AsyncTask<String, Integer, String> {

	public interface AuthenticatedHttpRequestCallback {
		void taskDidFinish();
	}

	/**
	 * The context to get preferences with
	 */
	protected Context mContext;

	/**
	 * The delegate to notify when the task is complete
	 */
	protected AuthenticatedHttpRequestCallback mDelegate;

	/**
	 * The URL to request
	 */
	protected String url = "";

	/**
	 * Whether this is a post request
	 */
	protected boolean isPost = false;

	public AuthenticatedHttpRequest(Context ctx) {
		this(ctx, null);
	}

	public AuthenticatedHttpRequest(Context ctx, AuthenticatedHttpRequestCallback delegate) {
		this.mContext = ctx;
		this.mDelegate = delegate;
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

		String result = null;

		try {
			HttpResponse response = client.execute(request);

			if (HttpURLConnection.HTTP_OK == response.getStatusLine().getStatusCode()) {
				result = HttpUtils.readStreamAsString(response.getEntity().getContent());
			} else if (302 == response.getStatusLine().getStatusCode()) {
				Log.d(AuthenticatedHttpRequest.class.getName(), "Status Code: 302 -- Redirection..." + response.getStatusLine().getStatusCode());
				// We are redirecting - instead, invalidate the auth token and retry the request
				AuthUtil.invalidateToken(mContext);
			}

			processData(result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}


	protected abstract void processData(String data);
}