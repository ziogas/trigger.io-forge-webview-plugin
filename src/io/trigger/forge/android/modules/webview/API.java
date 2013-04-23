package io.trigger.forge.android.modules.webview;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeParam;
import io.trigger.forge.android.core.ForgeTask;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class API {
	
	static RelativeLayout mOverlay = null;
	static WebView mWebView = null;
	static Boolean mFirstRun = false;
	static ProgressDialog mProgressDialog = null;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void show(final ForgeTask task, @ForgeParam("url") final String url, @ForgeParam("padding_top") final int padding_top, @ForgeParam("padding_bottom") final int padding_bottom ) {
		
		if (url.length() == 0) {
			task.error("No url");
			return;
		}
		
		task.performUI(new Runnable() {
			public void run() {
			
				//Loading dialog
				mProgressDialog = ProgressDialog.show(ForgeApp.getActivity(), "", "Loading...");
				
				//Scale for correct padding
				float scale = ForgeApp.getActivity().getResources().getDisplayMetrics().density; 
				
				//If we don't have running webview
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
					
					//onPageFinished not always works as expected
					mWebView.setWebChromeClient(new WebChromeClient() {
						public void onProgressChanged(WebView view, int progress) {
							
					        if ( progress == 100 && mFirstRun ) {
					            view.clearHistory();
						    	mProgressDialog.dismiss();
						    	
						    	view.setVisibility(View.VISIBLE);
						    	
						    	//Animation stuff
					            TranslateAnimation slide = new TranslateAnimation(0, 0, ForgeApp.getActivity().getResources().getDisplayMetrics().heightPixels, 0 );   
					            slide.setDuration(300);   
								slide.setInterpolator(new AccelerateInterpolator());
					            slide.setFillAfter(true);
					            
					            view.startAnimation(slide);
						    	
						    	mFirstRun = false;
					        }
					    }
					});
					
					//Fill parent
					mWebView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

					//Replace default animation to this for instant show up
					Animation anim = AnimationUtils.loadAnimation(ForgeApp.getActivity().getBaseContext(),android.R.anim.fade_in);
					anim.setDuration (1);
					
					mOverlay.setBackgroundColor(Color.TRANSPARENT);
					mWebView.setBackgroundColor(Color.TRANSPARENT);
					
					mOverlay.addView (mWebView);
					ForgeApp.getActivity().addModalView(mOverlay);
					
					
					
					mOverlay.startAnimation(anim);
				}
				
				mOverlay.setPadding(0, (int) (padding_top*scale + 0.5f), 0, (int) (padding_bottom*scale + 0.5f));
				
				mWebView.clearAnimation();
				mWebView.clearView();
				mFirstRun = true;
				
				mWebView.setVisibility(View.INVISIBLE);
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
						mProgressDialog = null;
						
						task.success();
					}
				});
			}
		});
	}
}
