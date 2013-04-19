package io.trigger.forge.android.modules.webview;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeEventListener;
import android.view.KeyEvent;

public class EventListener extends ForgeEventListener {
	
	@Override
	public Boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && API.mOverlay != null) {
			
			if(API.mWebView.canGoBack() == true){
				
				API.mWebView.goBack();
				
	        } else {
	        	ForgeApp.getActivity().removeModalView(API.mOverlay, new Runnable() {
					public void run() {
						ForgeApp.event("webview.closed", null);
						
						API.mOverlay = null;
						API.mWebView = null;
					}
				});
	        }
			
			return true;
		}
		return null;
	}
}