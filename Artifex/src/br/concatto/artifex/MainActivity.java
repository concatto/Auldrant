package br.concatto.artifex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity {
	static class Handles {
		public int mvp;
		public int color;
		public int position;

		public Handles(int position, int color, int mvp) {
			this.position = position;
			this.color = color;
			this.mvp = mvp;
		}
	}
	
	static class Utils {
		public static int loadShader(int type, String code) {
			int shader = GLES20.glCreateShader(type);
		    GLES20.glShaderSource(shader, code);
		    GLES20.glCompileShader(shader);
			
		    return shader;
		}
		
		public static void incrementWrap(float[] valueArray, boolean[] incrementArray, int index, float delta, boolean includeNegative) {
			int limit = includeNegative ? -1 : 0;
	    	valueArray[index] = incrementArray[index] ? valueArray[index] + delta : valueArray[index] - delta; 
	    	if (valueArray[index] > 1) {
				valueArray[index] = 1;
				incrementArray[index] = false;
			} else if (valueArray[index] < limit) {
				valueArray[index] = limit;
				incrementArray[index] = true;
			}
	    }
	}
	
	static class Triangle {
		private float angle = 0;
		private int vertices = 3;
		private int positionCount = 3;
		private float[] triangleArray = {
				//x, y, z. 0 is center, 1 is edge.
				0.0f, -0.5f, 0.0f,
	            -0.75f, 0.33f, 0.0f,
	            0.75f, 0.33f, 0.0f
		};
		private int colorCount = 4;
		private float[] colorArray = {
				0.25f, 0.75f, 1f, 1f,
				0.75f, 1f, 0.25f, 1f,
				1f, 0.25f, 0.75f, 1f
		};
		private boolean[] incrementArray = {
				false, false, true, true,
				false, true, false, true,
				true, false, false, true
		};
		private FloatBuffer positionbuffer;
		private FloatBuffer colorBuffer;
		private Handles handles;
		private final float[] modelMatrix = new float[16];
		private final float[] mvpMatrix = new float[16];
		private float previousX;
		private float x;
		
	    public Triangle(Handles handles) {
	    	this.handles = handles;
			positionbuffer = ByteBuffer.allocateDirect(triangleArray.length * 4)
	    			.order(ByteOrder.nativeOrder())
	    			.asFloatBuffer();
	    	
	    	positionbuffer.put(triangleArray).position(0);
	    	
	    	colorBuffer = ByteBuffer.allocateDirect(colorArray.length * 4)
	    			.order(ByteOrder.nativeOrder())
	    			.asFloatBuffer();
	    	
	    	colorBuffer.put(colorArray).position(0);
	    	
	    	GLES20.glEnableVertexAttribArray(handles.position);
	    	GLES20.glEnableVertexAttribArray(handles.color);
	    	Matrix.setIdentityM(modelMatrix, 0);
		}
	    
	    public void draw(final float[] projectionMatrix, final float[] viewMatrix) {
	    	float duration = 2 * 1000;
			float time = SystemClock.uptimeMillis() % duration;
	        float angle = (360.0f / duration) * time;
	    	for (int i = 0; i < colorArray.length - 1; i++) {
	    		if (i + 1 % 4 == 0) i++;
	    		Utils.incrementWrap(colorArray, incrementArray, i, 0.02f, false);
	    	}
	    	((FloatBuffer) colorBuffer.clear()).put(colorArray).position(0);
//	    	Matrix.setIdentityM(modelMatrix, 0);
//	        Matrix.setRotateM(modelMatrix, 0, angle, 0, 1, 0);
	    	increaseX();
	    	Matrix.setRotateM(modelMatrix, 0, this.angle, 0, 1, 0);
			
			Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
			Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
	    	
	    	GLES20.glVertexAttribPointer(handles.position, positionCount, GLES20.GL_FLOAT, false, positionCount * 4, positionbuffer);
	    	GLES20.glVertexAttribPointer(handles.color, positionCount, GLES20.GL_FLOAT, false, colorCount * 4, colorBuffer);
	    	
	    	GLES20.glUniformMatrix4fv(handles.mvp, 1, false, mvpMatrix, 0);
	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices);
	    }
	    
	    public void setX(float x) {
	    	this.x = x;
	    }
	    
	    public void increaseX() {
	    	float delta = 1;
	    	angle += x;
	    	if (x == 0 && previousX > delta) {
	    		angle += (previousX - delta); 
	    	} else if (x != 0) {
	    		previousX = x;
	    	}
	    }
	}
	
	static class TheRenderer implements Renderer {
		private final float[] projectionMatrix = new float[16];
		private final float[] viewMatrix = new float[16];
		private int program;
		private Triangle triangle;
		private final String vertexShaderCode =
				"uniform mat4 vMVPMatrix;" +
		        "attribute vec4 vPosition;" +
				"attribute vec4 vColor;" +
		        "varying vec4 varColor;" +
				
		        "void main() {" +
		        "  varColor = vColor;" + 
		        "  gl_Position = vMVPMatrix * vPosition;" +
		        "}";

	    private final String fragmentShaderCode =
		        "precision mediump float;" +
		        "varying vec4 varColor;" +
		        		
		        "void main() {" +
		        "  gl_FragColor = varColor;" +
		        "}";
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glClearColor(0f, 0f, 0f, 1);
			int vertex = Utils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	    	int fragment = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
	    	
	    	program = GLES20.glCreateProgram();
	    	GLES20.glAttachShader(program, vertex);
	    	GLES20.glAttachShader(program, fragment);
	    	GLES20.glBindAttribLocation(program, 0, "vPosition");
	        GLES20.glBindAttribLocation(program, 1, "vColor");
	    	GLES20.glLinkProgram(program);
	    	GLES20.glUseProgram(program);
	    	
	    	int position = GLES20.glGetAttribLocation(program, "vPosition");
	    	int color = GLES20.glGetAttribLocation(program, "vColor");
	    	int mvp = GLES20.glGetUniformLocation(program, "vMVPMatrix");
	    	Handles handles = new Handles(position, color, mvp);
			triangle = new Triangle(handles);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);
			float ratio = (float) width / height;
			Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
				        
	        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1.9f, 0, 0, -5, 0, 1, 0);
			triangle.draw(projectionMatrix, viewMatrix);
		}
		
		public Triangle getTriangle() {
			return triangle;
		}
	}
	
	private GLSurfaceView glsv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final TheRenderer renderer = new TheRenderer();
		glsv = new GLSurfaceView(this);
		glsv.setEGLContextClientVersion(2);
		glsv.setRenderer(renderer);
		glsv.setOnTouchListener(new OnTouchListener() {
			private float previousX = 0;
			private float previousY = 0;
			
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					float deltaX = previousX - x;
					float deltaY = previousY - y;
					renderer.getTriangle().setX(deltaX);
					break;
				case MotionEvent.ACTION_UP:
					renderer.getTriangle().setX(0);
					break;
				}
				
				previousX = x;
				previousY = y;
				return true;
			}
		});
		setContentView(glsv);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (glsv != null) glsv.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (glsv != null) glsv.onResume();
	}
}