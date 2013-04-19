package io.trigger.forge.android.modules.webview;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeParam;
import io.trigger.forge.android.core.ForgeTask;
import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class API {
	
	static RelativeLayout mOverlay;
	static WebView mWebView;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void show(final ForgeTask task, @ForgeParam("url") final String url, @ForgeParam("padding_top") final int padding_top, @ForgeParam("padding_bottom") final int padding_bottom ) {
		
		if (url.length() == 0) {
			task.error("No url");
			return;
		}
		
		task.performUI(new Runnable() {
			public void run() {
				
				//Set overlay
				mOverlay = new RelativeLayout(ForgeApp.getActivity());
				mOverlay.setPadding(0, padding_top, 0, padding_bottom);
				
				//Set our new webview
				mWebView = new WebView(ForgeApp.getActivity().getApplicationContext());
				mWebView.getSettings().setJavaScriptEnabled(true);
				
				//Fix some things
				mWebView.setWebViewClient(new WebViewClient() {
				    public boolean shouldOverrideUrlLoading(WebView view, String url){
				        view.loadUrl(url);
				        return false; // prevents the default action - opening in system browser
				    }
				});
				
				//Add webview to overlay and overlay to current app
				mOverlay.addView (mWebView);
				ForgeApp.getActivity().addModalView(mOverlay);
				
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
