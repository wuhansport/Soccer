package com.whs.soccer.utils;

import java.io.File;
import java.util.LinkedList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Class ViewUtils
 * @author antoniochen
 */
public final class ViewUtils {
    /**
     * Show a toast that just contains a text view with the text.
     * @param context The <tt>Context</tt>.
     * @param resId The resource id of the string to show.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT}
     * or {@link Toast#LENGTH_LONG}.
     * @see #showToast(Context, CharSequence, int)
     * @see #showToast(Context, CharSequence, int, int, int, int)
     */
    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getText(resId), duration, Gravity.CENTER, 0, 0);
    }

    /**
     * Show a toast that just contains a text view with the text.
     * @param context The <tt>Context</tt>.
     * @param text The text to show.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT}
     * or {@link Toast#LENGTH_LONG}.
     * @see #showToast(Context, int, int)
     * @see #showToast(Context, CharSequence, int, int, int, int)
     */
    public static void showToast(Context context, CharSequence text, int duration) {
        showToast(context, text, duration, Gravity.CENTER, 0, 0);
    }

    /**
     * Show a toast that just contains a text view with the text.
     * @param context The <tt>Context</tt>.
     * @param text The text to show.
     * @param duration How long to display the message. Either {@link Toast#LENGTH_SHORT}
     * or {@link Toast#LENGTH_LONG}.
     * @param gravity The gravity. See {@link android.view.Gravity}.
     * @param xOffset The x-offset on the screen.
     * @param yOffset The y-offset on the screen.
     * @see #showToast(Context, int, int)
     * @see #showToast(Context, CharSequence, int)
     */
    public static void showToast(Context context, CharSequence text, int duration, int gravity, int xOffset, int yOffset) {
        final Toast toast = new Toast(context);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.setDuration(duration);
//        final TextView view = (TextView)View.inflate(context, R.layout.toast, null);
//        view.setText(text);
//        toast.setView(view);
//        toast.show();
    }

    public static void startActivity(Context context, String filename, String mimeType) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final Uri uri = Uri.fromFile(new File(filename));
        if (TextUtils.isEmpty(mimeType)) {
            intent.setData(uri);
        } else {
            intent.setDataAndType(uri, mimeType);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
//            showToast(context, R.string.browser_open_error, Toast.LENGTH_SHORT);
        }
    }

    public static final class AnimatorPool implements AnimatorListener {
        private final Context mContext;
        private final int mAnimatorResId;
        private final LinkedList<Animator> mAnimators;

        public AnimatorPool(Context context, int animatorResId) {
            mAnimatorResId = animatorResId;
            mAnimators = new LinkedList<Animator>();
            mContext   = context.getApplicationContext();
        }

        public Animator obtain() {
            Animator animator = mAnimators.pollFirst();
            if (animator == null) {
                animator = AnimatorInflater.loadAnimator(mContext, mAnimatorResId);
                animator.addListener(this);
            }

            return animator;
        }

        public void clear() {
            mAnimators.clear();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mAnimators.add(animator);
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mAnimators.add(animator);
        }

        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

    public static Bitmap getBitmapFromImageView(ImageView imageView) {
    	Drawable drawable = imageView.getDrawable();
    	if(drawable instanceof BitmapDrawable) {
    		return ((BitmapDrawable)drawable).getBitmap();
    	}

    	return null;
    }  

    /**
     * This utility class cannot be instantiated.
     */
    private ViewUtils() {
    }
}
