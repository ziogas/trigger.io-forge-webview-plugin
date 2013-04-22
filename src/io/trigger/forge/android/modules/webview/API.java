package io.trigger.forge.android.modules.webview;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeParam;
import io.trigger.forge.android.core.ForgeTask;
import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class API {
	
	static RelativeLayout mOverlay = null;
	static WebView mWebView = null;
	static Boolean mClearHistory = false;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void show(final ForgeTask task, @ForgeParam("url") final String url, @ForgeParam("padding_top") final int padding_top, @ForgeParam("padding_bottom") final int padding_bottom ) {
		
		if (url.length() == 0) {
			task.error("No url");
			return;
		}
		
		task.performUI(new Runnable() {
			public void run() {
				
				float scale = ForgeApp.getActivity().getResources().getDisplayMetrics().density; 
				
				if ( mOverlay == null || mWebView == null ) {
					
					//Set overlay
					mOverlay = new RelativeLayout(ForgeApp.getActivity());
					
					//Set our new webview
					mWebView = new WebView(ForgeApp.getActivity().getApplicationContext());
					mWebView.getSettings().setJavaScriptEnabled(true);
					
					mWebView.setWebViewClient(new WebViewClient() {
					    public boolean shouldOverrideUrlLoading(WebView view, String url){
					        view.loadUrl(url);
					        return false; // prevents the default action - opening in system browser
					    }
					});
					
					mWebView.setWebChromeClient(new WebChromeClient() {
						public void onProgressChanged(WebView view, int progress) {
					        if ( progress == 100 && mClearHistory ) {
					            view.clearHistory();
					            mClearHistory = false;
					        }
					    }
					});
					
					//Add webview to overlay and overlay to current app
					mOverlay.addView (mWebView);
					ForgeApp.getActivity().addModalView(mOverlay);
				}
				
				mOverlay.setPadding(0, (int) (padding_top*scale + 0.5f), 0, (int) (padding_bottom*scale + 0.5f));
				mWebView.clearAnimation();
				mWebView.clearView();
				mClearHistory = true;
				
				//Fire it
				mWebView.loadUrl(url);
				
				task.success ();
			}
		});
	}
	
	public static void goBack (final ForgeTask task) {
		
		if(mWebView.canGoBack() == true){
			
			task.performUI(new Runnable() {
				public void run() {
					mWebView.goBack();
					task.success();
				}
			});
			
        } else {
        	_close (task);
        }
	}
	
	public static void close (final ForgeTask task) {
		_close (task);
	}
	
	public static void _close (final ForgeTask task) {
		
		task.performUI(new Runnable() {
			public void run() {
				
				ForgeApp.event("webview.before_close", null);
		    	ForgeApp.getActivity().removeModalView(mOverlay, new Runnable() {
					public void run() {
						ForgeApp.event("webview.closed", null);
						
						mOverlay = null;
						mWebView = null;
						
						task.success();
					}
				});
			}
		});
	}
}
