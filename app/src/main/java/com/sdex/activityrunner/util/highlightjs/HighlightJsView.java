package com.sdex.activityrunner.util.highlightjs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import timber.log.Timber;

/**
 * This Class was created by Patrick J
 * on 09.06.16. For more Details and Licensing
 * have a look at the README.md
 */

public class HighlightJsView extends WebView {

    private Theme theme = Theme.LIGHT;
    private String content;
    private boolean zoomSupport = false;
    private boolean showLineNumbers = false;
    private int scrollPosition = 0;

    //local variables to register callbacks
    private OnContentChangedListener onContentChangedListener;

    public interface OnContentChangedListener {
        void onContentChanged();
    }

    public HighlightJsView(Context context) {
        super(context);
        initView(context);
    }

    public HighlightJsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HighlightJsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public HighlightJsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView(Context context) {
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (onContentChangedListener != null) onContentChangedListener.onContentChanged();
                scrollTo(0, scrollPosition);
            }
        });
        //set the settings for the view
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(zoomSupport);
        //disable zoom controls on +Honeycomb devices
        settings.setDisplayZoomControls(false);
        //to remove padding and margin
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    private void changeZoomSettings(boolean enable) {
        this.zoomSupport = enable;
        getSettings().setSupportZoom(enable);
    }

    /**
     * Attach a callback to receive calls when the content has changed
     *
     * @param onContentChangedListener
     */
    public void setOnContentChangedListener(OnContentChangedListener onContentChangedListener) {
        this.onContentChangedListener = onContentChangedListener;
    }

    /**
     * Set the desired theme to highlight the given source.
     * Default: {@link Theme#LIGHT}
     *
     * @param theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * Receive the {@link Theme} which is currently set.
     *
     * @return The {@link Theme} which is currently set.
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Set the source to highlight from a String
     *
     * @param source - The source as String
     */
    public void setSource(String source) {
        if (source != null && !source.isEmpty()) {
            //generate and load the content
            this.content = source;
            String page = SourceUtils.generateContent(source, theme.getName(), "xml", zoomSupport, showLineNumbers);
            loadDataWithBaseURL("file:///android_asset/", page, "text/html", "utf-8", null);
        } else {
            Timber.e("Source can't be null or empty.");
        }
    }

    /**
     * Refresh the View.
     * Needs to be called when setting a new language, theme or source.
     */
    public void refresh() {
        if (content != null) {
            loadUrl("about:blank");
            setSource(content);
        }
    }

    /**
     * Enable or disable zooming functionality.
     * Zooming is disabled by default.
     *
     * @param supportZoom - true to enable, false to disable zoom
     */
    public void setZoomSupportEnabled(boolean supportZoom) {
        changeZoomSettings(supportZoom);
    }

    /**
     * Enable or disable line numbers on the left side of the code snippet.
     * Line numbers are disabled by default
     *
     * @param showLineNumbers - true to show - false to hide line numbers next to the code
     */
    public void setShowLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        this.scrollPosition = t;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.position = this.scrollPosition;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.scrollPosition = savedState.position;
    }

    private static class SavedState extends BaseSavedState {
        int position;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.position = in.readInt();
        }

        @RequiresApi(Build.VERSION_CODES.N)
        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.position = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.position);
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {

            // This was also missing
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? new SavedState(in, loader) : new SavedState(in);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? new SavedState(in, null) : new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
