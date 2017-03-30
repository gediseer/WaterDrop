package cn.dream.mao.dropwaterwallpaper;

import android.os.Bundle;
import android.renderscript.RenderScript;
import android.renderscript.RenderScriptGL;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Created by mao on 17-3-10.
 */
public class DropWaterWallPaper extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new WaterDropEngine();
    }


    class WaterDropEngine extends Engine {
        private FallRS fallRs;//rs脚本的主要工具类
        private RenderScriptGL mRs;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);


        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {//可见
                if (fallRs == null) {
                    fallRs = new FallRS(getDesiredMinimumWidth(), getDesiredMinimumHeight());
                }
                fallRs.start();
            } else {//不可见
                if (fallRs != null) {
                    fallRs.stop();

                }
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            fallRs.addDrop(event.getX(), event.getY());//只要是点击事件都添加波纹
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            //左右滑动屏幕时调用，可以不重写
        }

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            return super.onCommand(action, x, y, z, extras, resultRequested);
        }

        /**
         * 在这里初始化renderScriptGL
         *
         * @param holder
         */
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            RenderScriptGL.SurfaceConfig sc = new RenderScriptGL.SurfaceConfig();
            mRs = new RenderScriptGL(DropWaterWallPaper.this, sc);
            mRs.setPriority(RenderScript.Priority.LOW);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {//在这里能拿到surfaceView
            super.onSurfaceChanged(holder, format, width, height);
            if (fallRs == null) {
                fallRs = new FallRS(width, height);
                fallRs.init(mRs, getResources(), isPreview());
                fallRs.start();

            } else {
                fallRs.resize(width, height);
            }

            if (mRs != null) {
                mRs.setSurface(holder, width, height);
            }


        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            destroyRender();
        }

        private void destroyRender() {
            if (fallRs != null) {
                fallRs.stop();
                fallRs = null;
            }

            if (mRs != null) {
                mRs.setSurface(null, 0, 0);
                mRs.destroy();
                mRs = null;
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            destroyRender();
        }
    }


}
