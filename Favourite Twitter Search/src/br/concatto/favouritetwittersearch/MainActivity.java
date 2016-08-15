package br.concatto.favouritetwittersearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import br.concatto.twittersearch.R;

public class MainActivity extends Activity {
	private static final String PROGRAM_TAG = "FavouriteTwitterSearch";
	private static final String KEY = "o8d4Mu5EpiFtbMj0NARpO1c3Z";
	private static final String SECRET = "BleVOCrcQVvu5gtVt5E9zNeWlb5rn7fqwycL4CrkfBKWmqLYbl";
	private static final String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=";
	private static final String TOKEN_URL = "https://api.twitter.com/oauth2/token";
	private static final int COUNT = 50;
	
	private TextView queryEditText;
	private TextView tagEditText;
	private Button saveButton;
	private Button multifunctionButton;
	private TextView mainTitleTextView;
	private ScrollView mainScrollView;
	private LinearLayout mainLinearLayout;
	private LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		queryEditText = (TextView) findViewById(R.id.queryEditText);
		tagEditText = (TextView) findViewById(R.id.tagEditText);
		saveButton = (Button) findViewById(R.id.saveButton);
		multifunctionButton = (Button) findViewById(R.id.multifunctionButton);
		mainTitleTextView = (TextView) findViewById(R.id.mainTitleTextView);
		mainScrollView = (ScrollView) findViewById(R.id.mainScrollView);
		mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = queryEditText.getText().toString();
				String tag = tagEditText.getText().toString();
				
				if (query.length() > 0 && tag.length() > 0) {
					queryEditText.setText("");
					tagEditText.setText("");
					
					InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(tagEditText.getWindowToken(), 0);
					
					createTag(query, tag);
				}
			}
		});
	}

	private void createTag(final String query, String tag) {
		View newTag = inflater.inflate(R.layout.new_tag_view, mainLinearLayout, false);
		Button queryButton = (Button) newTag.findViewById(R.id.queryButton);
		queryButton.setText(tag);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Teste").setMessage("").setCancelable(false);
		final AlertDialog dialog = builder.create();
		
		queryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
				mainTitleTextView.setText(query);
				new AsyncTask<String, String, List<Tweet>>() {
					@Override
					protected List<Tweet> doInBackground(String... params) {
						publishProgress("Obtendo token...");
						String token = getBearerToken();
						if (token != null) {
							publishProgress("Token obtido!");
							publishProgress("Obtendo tweets...");
							return searchTwitter(params[0], token);
						} else {
							publishProgress("Failed to get token.");
							return null;
						}
					}
					
					@Override
					protected void onProgressUpdate(String... values) {
						TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
						String message = messageView.getText().toString();
						if (!message.isEmpty()) message += "\n";
						message += values[0];
						dialog.setMessage(message);
					}
					
					@Override
					protected void onPostExecute(List<Tweet> result) {
						mainLinearLayout.removeAllViews();
						for (Tweet tweet : result) {
							View tweetView = inflater.inflate(R.layout.tweet_view, mainLinearLayout, false);
							TextView nameTextView = (TextView) tweetView.findViewById(R.id.name);
							TextView contentTextView = (TextView) tweetView.findViewById(R.id.content);
							nameTextView.setText(Html.fromHtml(tweet.getName()));
							contentTextView.setText(Html.fromHtml(tweet.getContent()));
							mainLinearLayout.addView(tweetView);
						}
						dialog.dismiss();
					}
				}.execute(query);
			}
		});
		mainLinearLayout.addView(newTag);
	}
	
	private String getBearerToken() {
		try {
			String code = URLEncoder.encode(KEY, "UTF-8") + ":" + URLEncoder.encode(SECRET, "UTF-8");
			code = Base64.encodeToString(code.getBytes(), Base64.NO_WRAP);
			
			HttpPost post = new HttpPost(TOKEN_URL);
			post.setHeader("Authorization", "Basic " + code);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			post.setEntity(new StringEntity("grant_type=client_credentials"));
			JSONObject postJson = new JSONObject(resolveRequest(post));
			return (postJson.optString("token_type").equals("bearer")) ? postJson.getString("access_token") : null;
		} catch (UnsupportedEncodingException e) {
			Log.e(PROGRAM_TAG, "Error in encoding.", e);
		} catch (JSONException e) {
			Log.e(PROGRAM_TAG, "Error in parsing the JSON.", e);
		}
		return null;
	}
	
	private List<Tweet> searchTwitter(String query, String token) {
		try {
			HttpGet get = new HttpGet(SEARCH_URL + URLEncoder.encode(query, "UTF-8") + "&count=" + COUNT);
			get.setHeader("Authorization", "Bearer " + token);
			Log.i(PROGRAM_TAG, token);
			get.setHeader("Content-Type", "application/json");
			JSONObject getJson = new JSONObject(resolveRequest(get));
			JSONArray tweets = getJson.optJSONArray("statuses");
			if (tweets != null) {
				return parseTweets(tweets);
			} else {
				Log.e(PROGRAM_TAG, "Error in GETting the tweets. Result: " + getJson.toString(2));
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(PROGRAM_TAG, "Error in encoding.", e);
		} catch (JSONException e) {
			Log.e(PROGRAM_TAG, "Error in parsing the JSON.", e);
		}
		return null;
	}
	
	@SuppressLint("SimpleDateFormat")
	private List<Tweet> parseTweets(JSONArray tweets) {
		long currentTime = System.currentTimeMillis();
		List<Tweet> tweetList = new ArrayList<Tweet>();
		
		for (int i = 0; i < tweets.length(); i++) {
			try {
				JSONObject object = tweets.getJSONObject(i);
				String content = object.getString("text");
				String timeString = object.getString("created_at");
				
				SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
				Date time = formatter.parse(timeString);
				
				JSONObject userObject = object.getJSONObject("user");
				String user = String.format(Locale.getDefault(), "<b>%s @%s</b> - há %ds",
						userObject.getString("name"),
						userObject.getString("screen_name"),
						((currentTime - time.getTime()) / 1000));
				
				tweetList.add(new Tweet(user, content));
			} catch (JSONException e) {
				Log.e(PROGRAM_TAG, "Error in JSON on parsing tweets.", e);
			} catch (ParseException e) {
				Log.e(PROGRAM_TAG, "Error in parsing dates.", e);
			}
		}
		
		return tweetList;
	}

	private String resolveRequest(HttpUriRequest request) {
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				return builder.toString();
			} else {
				return response.getStatusLine().getReasonPhrase();
			}
		} catch (ClientProtocolException e) {
			Log.e(PROGRAM_TAG, "Error in HTTP protocol.", e);
		} catch (IOException e) {
			Log.e(PROGRAM_TAG, "Error in reading HTTP response.", e);
		}
		return null;
	}
}
