package com.example.miche_000.stak;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.GestureDetector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.InputStream;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity  extends AppCompatActivity implements  DownloadCallback, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    private TextView text;
    private GestureDetectorCompat gestureDetector;
    private ImageView iv;

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private String currentSubreddit;
    private TagList list;
    private String currentAfter;
    private SubList sublist;
    private boolean isPopular = true;

    private SharedPreferences.Editor tagPref;
    private SharedPreferences.Editor placePref;
    private SharedPreferences tagSettings;
    private SharedPreferences placeSettings;




    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text1);
        iv = (ImageView) findViewById(R.id.imageView) ;
        gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);


        list = new TagList();
        sublist = new SubList();

        tagSettings = getSharedPreferences("stakTagSave", Context.MODE_PRIVATE);
        placeSettings = getSharedPreferences("stakPlaceSave", Context.MODE_PRIVATE);
        tagPref = tagSettings.edit();
        placePref = placeSettings.edit();
        if(tagSettings.contains("stakTagSave"))
            restore();
        else
            save();



        text.setText("Start Swiping");
        String newSub = list.getTag();
        isPopular = newSub.equals("popular");

        if(sublist.getAfter(newSub).equals("notIn")) {
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/" + newSub + ".json?limit=1");
            System.out.println("https://www.reddit.com/r/" + newSub + ".json?limit=1");
        }
        else {
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/" + newSub + ".json?limit=1;after=" + sublist.getAfter(newSub));
            System.out.println("https://www.reddit.com/r/" + newSub + ".json?limit=1;after=" + sublist.getAfter(newSub));
        }
        mNetworkFragment.onCreate(null);
        mNetworkFragment.setmCallback(this);
        startDownload();




    }

    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }


    public void updateFromDownload(String result) {
        text.setText(result);
    }

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     *
     * @param result
     */
    @Override
    public void updateFromDownload(Object result) {
        String json = (String) result;
        save();
        if(getDomain(json).equals("youtube.com")){

        }
        else {
        int dataStart = json.lastIndexOf("\"data\":")+7;
        int dataEnd = json.lastIndexOf("after")-5;
        String title = json.substring(dataStart, dataEnd);

        int afterStart = json.lastIndexOf("after")+9;
        int afterEnd = json.lastIndexOf("before")-4;
        currentAfter = json.substring(afterStart, afterEnd);



            Gson gson = new Gson();
            listing d;
            try {
                d = gson.fromJson(title, listing.class);
            } catch (JsonSyntaxException e) {
                d = new listing();
                text.setText(e.toString());
            }
            text.setText(d.getTitle());
            currentSubreddit = d.getSubreddit();
            Picasso.with(this).load(d.getUrl()).into(iv);
        }

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                text.setText("error");
                break;
            case Progress.CONNECT_SUCCESS:
                text.setText("connected");
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
        System.out.println("finished");
    }

    public String getDomain(String j){
        int start = j.lastIndexOf("\"domain\":")+11;
        int end = j.lastIndexOf("\"hidden\":") -3;
        return j.substring(start, end);
    }


    public boolean save(){
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        try{
            String tagValue = gson.toJson(list);
            String placeValue = gson.toJson(sublist);

            tagPref.putString("stakTagSave", tagValue);
            tagPref.commit();

            placePref.putString("stakPlaceSave", placeValue);
            placePref.commit();
            return true;

        }
        catch (Exception e) {
            System.out.println("save error");
            return false;
        }
    }

    public void restore(){
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        String tagLoad = tagSettings.getString("stakTagSave", "");
        list = gson.fromJson(tagLoad, TagList.class);

        String placeLoad = placeSettings.getString("stakPlaceSave", "");
        sublist = gson.fromJson(placeLoad, SubList.class);

        System.out.println(placeLoad);


    }



    public void onLeftSwipe(){
        list.dislike(new PersonalTag(currentSubreddit));
        if(isPopular){
            sublist.setAfter("popular", currentAfter);
        }
        else
            sublist.setAfter(currentSubreddit, currentAfter);

        String newSub = list.getTag();
        isPopular = newSub.equals("popular");

        if(sublist.getAfter(newSub).equals("notIn")) {
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/" + newSub + ".json?limit=1");
            System.out.println("https://www.reddit.com/r/" + newSub + ".json?limit=1");
        }
        else {
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/" + newSub + ".json?limit=1;after=" + sublist.getAfter(newSub));
            System.out.println("https://www.reddit.com/r/" + newSub + ".json?limit=1;after=" + sublist.getAfter(newSub));
        }
        mNetworkFragment.onCreate(null);
        mNetworkFragment.setmCallback(this);
        startDownload();
        System.out.println("left swipe");

    }

    public void onRightSwipe(){

        list.like(new PersonalTag(currentSubreddit));
        if(isPopular){
            sublist.setAfter("popular", currentAfter);
        }
        else
            sublist.setAfter(currentSubreddit, currentAfter);

        String newSub = list.getTag();
        isPopular = newSub.equals("popular");

        if(sublist.getAfter(newSub).equals("notIn"))
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/"+newSub+".json?limit=1");
        else
            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://www.reddit.com/r/"+newSub+".json?limit=1;after="+sublist.getAfter(newSub));
        mNetworkFragment.onCreate(null);
        mNetworkFragment.setmCallback(this);
        startDownload();
        System.out.println("right swipe");
    }



    /**
     * Notified when a single-tap occurs.
     * <p>
     * Unlike ] this
     * will only be called after the detector is confident that the user's
     * first tap is not followed by a second tap leading to a double-tap
     * gesture.
     *
     * @param e The down motion event of the single-tap.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
    }

    /**
     * Notified when a double-tap occurs.
     *
     * @param e The down motion event of the first tap of the double-tap.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    /**
     * Notified when an event within a double-tap gesture occurs, including
     * the down, move, and up events.
     *
     * @param e The motion event that occurred during the double-tap gesture.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return true;
    }

    /**
     * Notified when a tap occurs with the down {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event. All other events should be preceded by this.
     *
     * @param e The down motion event.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        //sublist = new SubList();
        return true;
    }

    /**
     * The user has performed a down {@link MotionEvent} and not performed
     * a move or up yet. This event is commonly used to provide visual
     * feedback to the user to let them know that their action has been
     * recognized i.e. highlight an element.
     *
     * @param e The down motion event
     */
    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * Notified when a tap occurs with the up {@link MotionEvent}
     * that triggered it.
     *
     * @param e The up motion event that completed the first tap
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    /**
     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
     * current move {@link MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    /**
     * Notified when a long press occurs with the initial on down {@link MotionEvent}
     * that trigged it.
     *
     * @param e The initial on down motion event that started the longpress.
     */
    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * Notified of a fling event when it occurs with the initial on down {@link MotionEvent}
     * and the matching up {@link MotionEvent}. The calculated velocity is supplied along
     * the x and y axis in pixels per second.
     *
     * @param e1        The first down motion event that started the fling.
     * @param e2        The move motion event that triggered the current onFling.
     * @param velocityX The velocity of this fling measured in pixels per second
     *                  along the x axis.
     * @param velocityY The velocity of this fling measured in pixels per second
     *                  along the y axis.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                return false;
            }
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onLeftSwipe();
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onRightSwipe();
            }
        } catch (Exception e) {

        }
        return false;

    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
