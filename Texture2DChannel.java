package com.shaderunner.%USER%;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Channel%CHANNEL% {
  int program;
  int texture;
  public Channel%CHANNEL%(Context context, int _program) {
    program = _program;
    int[] textures = new int[1];
    GLES20.glGenTextures(1, textures, 0);
    texture = textures[0];
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;
    final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.channel%CHANNEL%, options);
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
  }

  public void Update() {
    while(GLES20.glGetError() != 0);

    GLES20.glActiveTexture(GLES20.GL_TEXTURE%CHANNEL%);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

    int handle = GLES20.glGetUniformLocation(program, "iChannel%CHANNEL%");
    GLES20.glUniform1i(handle, %CHANNEL%);
  }
}