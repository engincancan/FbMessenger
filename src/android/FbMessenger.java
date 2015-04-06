package org.engin.test.fbmessenger;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import com.facebook.FacebookSdk;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;
import com.ionicframework.testmessenger191211.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class FbMessenger extends CordovaPlugin {
    public static final String TAG = "FbMessenger";
    // Are we in a PICK flow?
    private boolean mPicking;
    private static final String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
    private static final String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
    private static final int PROTOCOL_VERSION = 20150314;
    private static final String YOUR_APP_ID = "[YOUR_FACEBOOK_APP_ID]";
    private static final int SHARE_TO_MESSENGER_REQUEST_CODE = 1;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      if(!FacebookSdk.isInitialized()){
          FacebookSdk.sdkInitialize(this.cordova.getActivity());
        }
    }
    
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("shareMethod")) {
            Activity activity = this.cordova.getActivity();
            Uri uri = Uri.parse("android.resource://"+activity.getPackageName() +"/"+ R.drawable.tree);
            ShareToMessengerParams shareToMessengerParams =
            ShareToMessengerParams.newBuilder(uri, "image/jpeg")
            .setMetaData("{ \"image\" : \"trees\" }")
            .build();
            if (mPicking) {
                MessengerUtils.finishShareToMessenger(activity, shareToMessengerParams);
            } else {  
                MessengerUtils.shareToMessenger(
                        activity,
                    SHARE_TO_MESSENGER_REQUEST_CODE,
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
    
    // Taken from commons StringEscapeUtils
    private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote,
                                              boolean escapeForwardSlash) throws IOException {
      if (out == null) {
        throw new IllegalArgumentException("The Writer must not be null");
      }
      if (str == null) {
        return;
      }
      int sz;
      sz = str.length();
      for (int i = 0; i < sz; i++) {
        char ch = str.charAt(i);

        // handle unicode
        if (ch > 0xfff) {
          out.write("\\u" + hex(ch));
        } else if (ch > 0xff) {
          out.write("\\u0" + hex(ch));
        } else if (ch > 0x7f) {
          out.write("\\u00" + hex(ch));
        } else if (ch < 32) {
          switch (ch) {
            case '\b':
              out.write('\\');
              out.write('b');
              break;
            case '\n':
              out.write('\\');
              out.write('n');
              break;
            case '\t':
              out.write('\\');
              out.write('t');
              break;
            case '\f':
              out.write('\\');
              out.write('f');
              break;
            case '\r':
              out.write('\\');
              out.write('r');
              break;
            default:
              if (ch > 0xf) {
                out.write("\\u00" + hex(ch));
              } else {
                out.write("\\u000" + hex(ch));
              }
              break;
          }
        } else {
          switch (ch) {
            case '\'':
              if (escapeSingleQuote) {
                out.write('\\');
              }
              out.write('\'');
              break;
            case '"':
              out.write('\\');
              out.write('"');
              break;
            case '\\':
              out.write('\\');
              out.write('\\');
              break;
            case '/':
              if (escapeForwardSlash) {
                out.write('\\');
              }
              out.write('/');
              break;
            default:
              out.write(ch);
              break;
          }
        }
      }
    }

    private static String hex(char ch) {
      return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }



}
