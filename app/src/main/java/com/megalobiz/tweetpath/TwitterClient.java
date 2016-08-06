package com.megalobiz.tweetpath;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "0tqephMqSPgnbStIfVSOOltQv";       // Change this
	public static final String REST_CONSUMER_SECRET = "5H99lJH07ReKhC90Eev37laJyL0EAlpWKv9gcyuPnsamaX1ZVk"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cptweetpath"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	// METHOD == ENDPOINT

	// HomeTimeline - get us to the Home Timeline
	// GET statuses/home_timeline.json
	//    count = 25
	//    since_id = 1
	public void getHomeTimeline(long oldestId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");

        // specify the params
        RequestParams params = new RequestParams();
        params.put("count", 25);

		if(oldestId == 0) {
            params.put("since_id", 1);
        } else {
            // populate fetch tweets whose Id are lower than tweet with the oldest Id
			// since Twitter take tweets with id lower than max_id, but including max_id
			// max_id must be equal to oldestId -1
            params.put("max_id", oldestId - 1);
        }
        // Execute the request
        getClient().get(apiUrl, params, handler);

    }

    // VerifyCredentials - get information of the authenticated user
    // GET account/verify_credentials.json
    public void getVerifyCredentials(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");

        // Execute the request
        getClient().get(apiUrl, handler);
    }

	// HomeTimeline - get us to the Home Timeline
	// POST statuses/update.json
	//    status = variable
	public void postUpdateStatus(String status, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");

		// specify the params
		RequestParams params = new RequestParams();
		params.put("status", status);

		// Execute the request
		getClient().post(apiUrl, params, handler);
	}

	// MentionsTimeline - get us to the Home Timeline
	// GET statuses/mentions_timeline.json
	//    count = 25
	public void getMentionsTimeline(long oldestId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");

		// specify the params
		//RequestParams params = new RequestParams();
		//params.put("count", 25);
		// Execute the request
		getClient().get(apiUrl, handler);
	}
	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}