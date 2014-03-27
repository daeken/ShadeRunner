package com.shaderunner.%USER%;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    GLSurfaceView surface = new GLSurfaceView(this);
    surface.setEGLContextClientVersion(2);

    GLSurfaceView.Renderer renderer = new ShaderRenderer(getShader());
    surface.setRenderer(renderer);
    
    setContentView(surface);
  }

  public String getShader() {
    try {
      Resources r = getResources();
      InputStream is = r.openRawResource(R.raw.fs);
      String out = convertStreamToString(is);
      is.close();
      return out;
    } catch(IOException e) {
      return "";
    }
  }

  String convertStreamToString(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int i = is.read();
    while (i != -1) {
      baos.write(i);
      i = is.read();
    }
    return baos.toString();
  }
}
