package com.yanhamer.app_utils.logs;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Create Builder pattern to init @firebaseAnalytics
 * and
 * @event_name (name of event that target to log)
 */
public class FBLogEvents {

    private static FirebaseAnalytics firebaseAnalytics;

    /**
     * Creating class Builder to init FB Analytics Object
     * @Builder().createInstance(context_of_view).setEventName("name").build()
     *
     */
    public static class LogBuilder{

        private String event_name;

        public LogBuilder(){}

        public LogBuilder createInstance(Context context) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            return this;
        }

        public LogBuilder setEventName(String eventName){
            this.event_name = eventName;
            return this;
        }

        public void build(){
            Bundle bundle = new Bundle();
            bundle.putString("name", this.event_name);
            firebaseAnalytics.logEvent(this.event_name, bundle);
        }

    }
}
