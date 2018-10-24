package com.mvp.testmvp.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public class ToastUtil {
    public static void showToast(Context context, String msg) {
        if (msg == null || "".equals(msg)) {
            return;
        }
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Fragment fragment, String msg) {
        if (msg == null || "".equals(msg)) {
            return;
        }
        Toast.makeText(fragment.getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String msg, int time) {
        if (msg == null || "".equals(msg)) {
            return;
        }
        Toast.makeText(context.getApplicationContext(), msg, time).show();
    }

    public static void showToast(Context context, int resid) {
        Toast.makeText(context.getApplicationContext(), resid, Toast.LENGTH_SHORT).show();
    }
}
