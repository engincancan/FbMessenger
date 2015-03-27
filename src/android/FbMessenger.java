package org.engin.test.FbMessenger;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;


import android.provider.Settings;

public class FbMessenger extends CordovaPlugin {
    public static final String TAG = "FbMessenger";
    // Are we in a PICK flow?
    private boolean mPicking;
    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final String YOUR_APP_ID = "[YOUR_FACEBOOK_APP_ID]";
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("shareMethod")) {
            ShareToMessengerParams shareToMessengerParams =
            ShareToMessengerParams.newBuilder(contentUri, "image/jpeg")
            .setMetaData("{ \"image\" : \"trees\" }")
            .build();

            if (mPicking) {
                MessengerUtils.finishShareToMessenger(this, shareToMessengerParams);
            } else {  
                MessengerUtils.shareToMessenger(
                    this,
                    REQUEST_CODE_SHARE_TO_MESSENGER,
                    shareToMessengerParams);
            }
        }
        else {
            return false;
        }
        return true;
    }

    @Override
    public void onNewIntent(Intent intent) {

        if (Intent.ACTION_PICK.equals(intent.getAction())) {
            mPicking = true;    
            MessengerThreadParams mThreadParams = MessengerUtils.getMessengerThreadParamsForIntent(intent);

            String metadata = mThreadParams.metadata;
            List<String> participantIds = mThreadParams.participants;
            try {
                StringWriter writer = new StringWriter(metadata.length() * 2);
                escapeJavaStyleString(writer, metadata, true, false);
                webView.loadUrl("javascript:fbMessengerCallback('" + writer.toString() + "');");
            } catch (IOException ignore) {
            }


        }
    }



}
