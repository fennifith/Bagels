package com.james.bagels;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.JsonReader;

import com.james.bagels.data.Bagel;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Bagels extends Application {

    private static final String BAGELS_URL = "https://jfenn.me/images/bagels/bagels.json";

    public static final String BAGELS_SIZE_KEY = "com.james.bagels.BAGELS_SIZE_KEY";
    public static final String BAGELS_KEY = "com.james.bagels.BAGELS_KEY";

    private List<BagelsListener> listeners;
    private List<Bagel> bagels;

    @Override
    public void onCreate() {
        super.onCreate();
        listeners = new ArrayList<>();
        bagels = new ArrayList<>();
    }

    public void addListener(BagelsListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BagelsListener listener) {
        listeners.remove(listener);
    }

    public void getBagels(final BagelsListener listener) {
        bagels.clear();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int size = prefs.getInt(Bagels.BAGELS_SIZE_KEY, 0);
        for (int i = 0; i < size; i++) {
            if (prefs.contains(BAGELS_KEY + i)) bagels.add(new Bagel(prefs.getString(BAGELS_KEY + i, null)));
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(BAGELS_URL);
                    HttpURLConnection request = (HttpURLConnection) url.openConnection();
                    request.connect();

                    JsonReader reader = new JsonReader(new InputStreamReader((InputStream) request.getContent(), "UTF-8"));

                    reader.beginArray();
                    while (reader.hasNext()) {
                        bagels.add(new Bagel(reader.nextString()));
                    }
                    reader.endArray();
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onBagels(bagels);
                        for (BagelsListener listener : listeners) {
                            listener.onBagels(bagels);
                        }
                    }
                });
            }
        }.start();
    }

    public interface BagelsListener {
        void onBagels(List<Bagel> bagels);
    }
}
