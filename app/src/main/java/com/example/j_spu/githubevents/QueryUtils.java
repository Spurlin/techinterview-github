package com.example.j_spu.githubevents;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static Map<String, String> map;
    private static int count;
    private static String currentUrl;
    private static String repoInfoUrl;
    private static ArrayList<Map> mapArrayList = new ArrayList<>();
    private static String basicAuth;
    public static String[] creds = {};
    private static final String CLIENT_INFO = "?client_id=e53bb94f9c15a36afebd&client_secret=f23dfaa03e54dfbb4ca57740ba565d8043c4201b";
    private static String defaultUrl =
            "https://github.com/:username?tab=repositories";

    private QueryUtils() {}

    public static List<Event> fetchEventData(String requestUrl) {

        if (requestUrl.contains(":username") && creds.length > 0) {
//            basicAuth = "Basic " + Base64.encodeToString((creds[0] + ":" + creds[1]).getBytes(),Base64.NO_WRAP);
//            Log.e(LOG_TAG, "<<<UPDATING BASICAUTH");
            creds = null;
        }

        currentUrl = requestUrl;

        // Create URL object
        URL url = createUrl(requestUrl);

        count = 0;

        // Performs HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making the HTTP request");
        }

        List<Event> mEventList = extractEvents(jsonResponse);

        return mEventList;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // if the URL is null, then return early
        if (url == null) { return jsonResponse; }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestProperty("User-Agent", "GitHub_Events/1.0");
            urlConnection.setRequestProperty("connection", "close");
//            if(basicAuth != null) { urlConnection.setRequestProperty("Authorization", basicAuth); }
            urlConnection.connect();

            // if the request was successful (response code 200),
            // then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                Log.e(LOG_TAG, "<<<200: " + urlConnection.getHeaderFields().toString());
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                Log.e(LOG_TAG, "<<<" + urlConnection.getResponseCode() + ": " + urlConnection.getHeaderFields().toString());
                if (urlConnection.getResponseCode() == 404) {
                    Log.e(LOG_TAG, "<<<" + urlConnection.getResponseCode() + ": " + url);
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving the event json results", e);
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }
            if (inputStream != null) { inputStream.close(); }
            basicAuth = null;
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Event> extractEvents(String jsonResponse) {

        // if the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding events to
        final List<Event> events = new ArrayList<>();

        // try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        try {
            Log.i(LOG_TAG, "Trying to get the event data");

            JSONArray rootArray = new JSONArray(jsonResponse);
//            repoInfoUrl = new String[rootArray.length()];

            for (int i = 0; i < rootArray.length(); i++) {
                String tempType = "";
                Date tempDate;
                User tempUser;



                JSONObject event = rootArray.getJSONObject(i);

                tempType = event.getString("type");
                JSONObject user = event.getJSONObject("actor");
                tempUser = new User(user.getString("display_login"),
                        getUserAvatar(user.getString("avatar_url")));

                if (currentUrl.contains("/users") && i == 0
                        && (!currentUrl.contains(":username"))) {
                    PublicEventActivity.mUser = tempUser;
                }

                JSONObject repo = event.getJSONObject("repo");
                repoInfoUrl = repo.getString("url") + CLIENT_INFO;

                map = getRepoInfo(repoInfoUrl);

                if (map == null) {
                    // if the repository doesn't exist anymore
                    defaultUrl = defaultUrl.replace(":username", tempUser.getUsername());
                    events.add(new Event(tempUser, tempType, repo.getString("name"), defaultUrl, "Repository doesn't exist anymore.",-1 ,-1, -1, null));
                } else {
                    events.add(new Event(tempUser, tempType, repo.getString("name"),
                            map.get("repoUrl").toString(), map.get("description").toString(),
                            Integer.parseInt(map.get("stars").toString()), Integer.parseInt(map.get("watches").toString()),
                            Integer.parseInt(map.get("forks").toString()), formatDate(event.getString("created_at"))));
                    Log.e(LOG_TAG, "<<<DATE: " + event.getString("created_at"));
                    Log.e(LOG_TAG, "<<<FORMATED DATE: " + formatDate(event.getString("created_at")));
                }
            }

        } catch(JSONException e) {
            Log.e(LOG_TAG, "Error parsing the JSON Object");
        }

        return events;
    }

    private static String formatDate(String dateString) {
        dateString = dateString.substring(0, dateString.indexOf("T"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date newDate = format.parse(dateString);
            format = new SimpleDateFormat("MMM dd, yyyy");
            dateString = format.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    private static Map getRepoInfo(String repoInfoUrl) {

        // Create URL object
        URL url = createUrl(repoInfoUrl);

        // Performs HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making the HTTP request");
        }

        return extractRepoInfo(jsonResponse);
    }

    public static Map<String, String> extractRepoInfo(String jsonResponse) {

        // if the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding events to
        Map<String, String> map = new HashMap<>();

        // try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        try {
            Log.i(LOG_TAG, "Trying to get the repo data");

            JSONObject rootObject = new JSONObject(jsonResponse);

            Log.i(LOG_TAG, "Getting mapped strings");
            map.put("repoUrl", rootObject.getString("html_url"));
            map.put("description", rootObject.getString("description"));
            map.put("stars", rootObject.getString("stargazers_count"));
            map.put("watches", rootObject.getString("subscribers_count"));
            map.put("forks", rootObject.getString("forks"));

        } catch(JSONException e) {
            Log.e(LOG_TAG, "Error parsing the JSON Object");
        }

        Log.i(LOG_TAG, "Done getting mapped strings");
        return map;
    }

    private static Bitmap getUserAvatar(String url) {
        Bitmap avatarImage = null;

        try {
            InputStream inputStream = new URL(url).openStream();
            avatarImage = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error opening the input stream.", e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error opening the input.", e);
        }

        return avatarImage;
    }
}
