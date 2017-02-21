###安卓自定义控件-流式布局
> 学过java基础都知道，进行Swing界面设计时，有一个常用的布局就是流式布局，但在安卓中却不存在这么一种布局。当我们可以通过自定义ViewGroup来实现一个自己FlowLayout

####先上效果图
![FlowLayout icon](https://github.com/LiaoXingwen/FlowLayout/blob/master/Screenshot_2017-02-21-17-44-07-979_main.com.myspace.png)

####主要java代码

`public class FlowLayout extends ViewGroup {

	int viewWidth,viewHeight ;//控件宽和高

	ArrayList<RowViews> rows = new ArrayList<FlowLayout.RowViews>();//记录行信息，一个数据一行
	
	int viewSpace = 15 ; //间隔
	
	
	public FlowLayout(Context context) {
		super(context,null);

	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs,0);
	}



	//		测量模式：
	//		EXACTLY：表示设置了精确的值，一般当childView设置其宽、高为精确值、match_parent时，ViewGroup会将其设置为EXACTLY；
	//		AT_MOST：表示子布局被限制在一个最大值内，一般当childView设置其宽、高为wrap_content时，ViewGroup会将其设置为AT_MOST；
	//		UNSPECIFIED：表示子布局想要多大就多大，一般出现在AadapterView的item的heightMode中、ScrollView的childView的heightMode中；此种模式比较少见。

	//			onMeasure设置自己的宽和高
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/** 
		 * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式 
		 */  
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
		viewWidth = DisplayUtil.getMySize(DisplayUtil.getWidth(getContext()), widthMeasureSpec) ;
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);  

		// 计算出所有的childView的宽和高  
		measureChildren(widthMeasureSpec, heightMeasureSpec);  
		/** 
		 * 记录子view设置高 
		 */  
		int height = viewSpace; //统计高度 
		int width = viewSpace ;//统计一行的宽度，用来判断是否还换行
		int oldwidth = 0 ;//记录还没有尝试添加下一个控件时的宽度
		int maxRowHeight = 0 ; //当前行的最大行高
		int oldMaxRowHeight = 0 ;//记录还没有尝试添加下一个控件时的当前行的最大行
		int childCount= getChildCount();
		RowViews rowViews = new RowViews() ; //一行的view数据
		
		for(int i = 0 ; i<childCount;i++){
			oldwidth = width;
			oldMaxRowHeight =maxRowHeight ; 
			View childView = getChildAt(i);
			MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
			int h = childView.getMeasuredHeight() + params.topMargin + params.bottomMargin+viewSpace;//控件的占用高度
			maxRowHeight = Math.max(maxRowHeight, h);
			width += childView.getMeasuredWidth() + params.rightMargin + params.leftMargin+viewSpace;
			if (width>viewWidth) {
				//判断是否一个控件就超过了父控件的大小
				if (oldwidth!=viewSpace) {
					i=i-1;//回退
					maxRowHeight = oldMaxRowHeight;
				}else {
					rowViews.addView(new ViewInfo(childView, viewSpace));
				}
				width = viewSpace;
				
				rowViews.setStartLocationY(height);
				height += maxRowHeight ; 
				rowViews.setMaxHeight(maxRowHeight);//设置最大的高度
				rows.add(rowViews);//加到1行
				rowViews = new RowViews();//重置
				
				maxRowHeight = 0 ;
				//刚好是最后一个因素充满
				if (i==childCount-1) {
					rowViews = null ; 
					break;
				}else {
					continue;
				}
			}else {
				rowViews.addView(new ViewInfo(childView, oldwidth));
				
				//当最后一个因素了，而没有充满一行，添加为一行
				if (i==childCount-1) {
					rowViews.setStartLocationY(height);
					height += maxRowHeight ;
					rowViews.setMaxHeight(maxRowHeight);//设置最大的高度
					rows.add(rowViews);//加到1行
					rowViews = null ; 
				}
			}
			
			
		}

		/** 
		 * 如果是wrap_content设置为我们计算的值 
		 * 否则：直接设置为父容器计算的值 
		 */  
		setMeasuredDimension(viewWidth, viewHeight = (heightMode == MeasureSpec.EXACTLY) ? viewHeight  
				: height);  
	}


	@Override  
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)  
	{  
		return new MarginLayoutParams(getContext(), attrs);  
	}


	//对组件的位置进行设置
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			for (int i = 0; i < rows.size(); i++) {
				RowViews rowViews = rows.get(i);
				Vector<ViewInfo> views = rowViews.getViews();
				for (int j = 0; j < views.size(); j++) {
					ViewInfo viewInfo = views.get(j);
					viewInfo.view.layout(viewInfo.startX,
							rowViews.startLocationY, 
							viewInfo.startX+viewInfo.view.getMeasuredWidth(), 
							rowViews.startLocationY+viewInfo.view.getMeasuredHeight());
				}
				
			}
		}
	}  


	/**
	 * 一行的数据
	 * 
	 * @作者 廖兴文
	 *
	 * @时间 2017-2-21
	 */
	class RowViews{
		Vector<ViewInfo> views = new Vector<ViewInfo>();
		int maxHeight ; //当前行的最大高度
		int startLocationY ;//开始的位置
		public int getStartLocationY() {
			return startLocationY;
		}
		public void setStartLocationY(int startLocationY) {
			this.startLocationY = startLocationY;
		}
		public Vector<ViewInfo> getViews() {
			return views;
		}
		public void addView(ViewInfo view) {
			this.views.add(view);
		}
		public void removeView(View view) {
			this.views.remove(view);
		}
		public int getMaxHeight() {
			return maxHeight;
		}
		public void setMaxHeight(int maxHeight) {
			this.maxHeight = maxHeight;
		}
		
	} 
	
	
	class ViewInfo {
		View view ; 
		int startX;
		public View getView() {
			return view;
		}
		public void setView(View view) {
			this.view = view;
		}
		public int getStartX() {
			return startX;
		}
		public void setStartX(int startX) {
			this.startX = startX;
		}
		public ViewInfo() {
		}
		public ViewInfo(View view, int startX) {
			this.view = view;
			this.startX = startX;
		}
		
	}
	


}
`
####自定义view常用的自定义工具类
`	package main.com.viewdesign;

import android.content.Context;
import android.view.WindowManager;
import android.view.View.MeasureSpec;

/** 
     * dp、sp 转换为 px 的工具类 
     *  
     * 
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
    }  `

### 到此，自定义控件就已经结束了，可以在xml中或者代码中使用了

