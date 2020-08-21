package vd.android.vdNotifications.receiver;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import app.vd.framework.activity.PageActivity;
import app.vd.framework.extend.bean.PageStatus;
import app.vd.framework.extend.module.vdJson;
import app.vd.framework.ui.vd;
import vd.android.vdNotifications.module.vdNotificationsAppModule;

public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //todo 跳转之前要处理的逻辑
        int whatId = intent.getIntExtra("whatId", 0);
        if (whatId > 0) {
            clickHandleMessage(vdJson.parseObject(vdNotificationsAppModule.parameter.get("whatId" + whatId)));
        }

        Activity mActivity = null;
        Intent newIntent;
        try {
            LinkedList<Activity> mLinkedList = vd.getActivityList();
            mActivity = mLinkedList.getLast();
        } catch (NoSuchElementException ignored) { }
        if (mActivity != null) {
            newIntent = new Intent(context, mActivity.getClass());
        } else {
            newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        }
        if (newIntent != null) {
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }

    private void clickHandleMessage(JSONObject json) {
        if (json == null) {
            return;
        }
        JSONObject object = new JSONObject();
        object.put("messageType", "notifyClick");
        object.put("rawData", json);
        postMessage(object);
    }

    private void postMessage(Object message) {
        LinkedList<Activity> activityList = vd.getActivityList();
        for (Activity mContext : activityList) {
            if (mContext instanceof PageActivity) {
                ((PageActivity) mContext).onAppStatusListener(new PageStatus("page", "message", null, message));
            }
        }
    }
}