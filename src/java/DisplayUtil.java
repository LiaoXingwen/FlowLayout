package main.com.viewdesign;

import android.content.Context;
import android.view.WindowManager;
import android.view.View.MeasureSpec;

/** 
     * dp、sp 转换为 px 的工具类 
     *  
     * @author fxsky 2012.11.12 
     * 
     */  
    public class DisplayUtil {  
        /** 
         * 将px值转换为dip或dp值，保证尺寸大小不变 
         *  
         * @param pxValue 
         * @param scale 
         *            （DisplayMetrics类中属性density） 
         * @return 
         */  
        public static int px2dip(Context context, float pxValue) {  
            final float scale = context.getResources().getDisplayMetrics().density;  
            return (int) (pxValue / scale + 0.5f);  
        }  
      
        /** 
         * 将dip或dp值转换为px值，保证尺寸大小不变 
         *  
         * @param dipValue 
         * @param scale 
         *            （DisplayMetrics类中属性density） 
         * @return 
         */  
        public static int dip2px(Context context, float dipValue) {  
            final float scale = context.getResources().getDisplayMetrics().density;  
            return (int) (dipValue * scale + 0.5f);  
        }  
      
        /** 
         * 将px值转换为sp值，保证文字大小不变 
         *  
         * @param pxValue 
         * @param fontScale 
         *            （DisplayMetrics类中属性scaledDensity） 
         * @return 
         */  
        public static int px2sp(Context context, float pxValue) {  
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
            return (int) (pxValue / fontScale + 0.5f);  
        }  
      
        /** 
         * 将sp值转换为px值，保证文字大小不变 
         *  
         * @param spValue 
         * @param fontScale 
         *            （DisplayMetrics类中属性scaledDensity） 
         * @return 
         */  
        public static int sp2px(Context context, float spValue) {  
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
            return (int) (spValue * fontScale + 0.5f);  
        }  
        
        
        /**
         * 获取屏幕宽度
        * @Title: getWidth
        * @Description: 
        * @param @param context
        * @param @return    
        * @return float   
        * @throws
        */
        public static int getWidth(Context context) {
        	WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
			return wm.getDefaultDisplay().getWidth();
		}
        
        /**
         * 获取屏幕高度
         * 
         * @Title: getWidth
         * @Description: 
         * @param @param context
         * @param @return    
         * @return float   
         * @throws
         */
         public static int getHeight(Context context) {
         	WindowManager wm = (WindowManager) context
                     .getSystemService(Context.WINDOW_SERVICE);
 			return wm.getDefaultDisplay().getHeight();
 		}
     	/**
     	 * 获取我的大小设置
     	 *
     	 * @param @param defaultSize
     	 * @param @param measureSpec
     	 * @param @return    
     	 *
     	 */
     	public static int getMySize(int defaultSize, int measureSpec) {
     		int mySize = defaultSize;

     		int mode = MeasureSpec.getMode(measureSpec);
     		int size = MeasureSpec.getSize(measureSpec);

     		switch (mode) {
     		case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
     			mySize = defaultSize;
     			break;
     		}
     		case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
     			//我们将大小取最大值,你也可以取其他值
     			mySize = size;
     			break;
     		}
     		case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
     			mySize = size;
     			break;
     		}
     		}
     		return mySize;
     	}
    }  