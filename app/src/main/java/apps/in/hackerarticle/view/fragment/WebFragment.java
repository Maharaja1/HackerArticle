package apps.in.hackerarticle.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import apps.in.hackerarticle.R;
import apps.in.hackerarticle.data.AppUtils;
import apps.in.hackerarticle.data.entity.Story;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WebFragment extends Fragment {

    private static final String TAG = WebFragment.class.getSimpleName();

    @BindView(R.id.webview)
    protected WebView webview;

    @BindView(R.id.progress_web)
    protected ProgressBar progressBar;

    private Story story;
    private Unbinder unbinder;
    private Context context;

    public static WebFragment newInstance(Story story) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putInt(AppUtils.ACTION, AppUtils.ACTION_VIEW);
        args.putParcelable("data", story);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            getBundleData();
        }
    }

    private void getBundleData() {
        try {

            Bundle bundle = getArguments();
            if (bundle.get(AppUtils.ACTION) != null) {
                if (bundle.getInt(AppUtils.ACTION) == (AppUtils.ACTION_VIEW)) {
                    story = bundle.getParcelable("data");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // dispose subscriptions

        unbinder.unbind();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        try {
            // Initializing view
            context = view.getContext();
            unbinder = ButterKnife.bind(this, view);

            if (story != null && !story.getUrl().isEmpty()) {
                getStory(story.getUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void getStory(String url) {
        try {
            webview.loadUrl(url);

            // Enable Javascript
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Force links and redirects to open in the WebView instead of in a browser
            webview.setWebViewClient(new WebViewClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_refresh).setVisible(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
