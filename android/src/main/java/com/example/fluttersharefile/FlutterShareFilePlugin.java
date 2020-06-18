package com.example.fluttersharefile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

import io.flutter.app.FlutterActivity;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import androidx.core.content.FileProvider;

/** FlutterShareFilePlugin */
public class FlutterShareFilePlugin extends FlutterActivity implements MethodCallHandler {
  /** Plugin registration. */
  private static Registrar instance;

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_share_file");
    channel.setMethodCallHandler(new FlutterShareFilePlugin());
    instance = registrar;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("shareimage")) {
      Object arguments = call.arguments;
      HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
      String fileName = argsMap.get("fileName");
      String message = argsMap.get("message");
      shareFile(fileName, message);
    } else {
      result.notImplemented();
    }
  }

  private void shareFile(String fileName, String message) {
    File imageFile = new File(instance.activeContext().getCacheDir(), fileName);
    String packageName = instance.activeContext().getPackageName();
    Uri contentUri = FileProvider.getUriForFile(instance.activeContext(), packageName, imageFile);
    Log.d("MIODEBUG", packageName);
    Log.d("MIODEBUG", contentUri.toString());
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    shareIntent.setDataAndType(contentUri, "image/png");

    Intent instagramIntent = new Intent("com.instagram.share.ADD_TO_STORY");
    instagramIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
    instagramIntent.putExtra("content_url", contentUri);
    instagramIntent.setType("image/png");

    Intent chooser = Intent.createChooser(shareIntent, "Share image using");

    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { instagramIntent });

    instance.activity().startActivity(chooser);
}
}
