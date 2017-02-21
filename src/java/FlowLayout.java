package main.com.viewdesign;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * ��׿�Զ���ؼ�֮��ʽ����
 * @���� ������
 *
 * @ʱ�� 2017-2-21
 */
public class FlowLayout extends ViewGroup {

	int viewWidth,viewHeight ;//�ؼ���͸�

	ArrayList<RowViews> rows = new ArrayList<FlowLayout.RowViews>();//��¼����Ϣ��һ������һ��
	
	int viewSpace = 15 ; //���
	
	
	public FlowLayout(Context context) {
		super(context,null);

	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs,0);
	}



	//		����ģʽ��
	//		EXACTLY����ʾ�����˾�ȷ��ֵ��һ�㵱childView���������Ϊ��ȷֵ��match_parentʱ��ViewGroup�Ὣ������ΪEXACTLY��
	//		AT_MOST����ʾ�Ӳ��ֱ�������һ�����ֵ�ڣ�һ�㵱childView���������Ϊwrap_contentʱ��ViewGroup�Ὣ������ΪAT_MOST��
	//		UNSPECIFIED����ʾ�Ӳ�����Ҫ���Ͷ��һ�������AadapterView��item��heightMode�С�ScrollView��childView��heightMode�У�����ģʽ�Ƚ��ټ���

	//			onMeasure�����Լ��Ŀ�͸�
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/** 
		 * ��ô�ViewGroup�ϼ�����Ϊ���Ƽ��Ŀ�͸ߣ��Լ�����ģʽ 
		 */  
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
		viewWidth = DisplayUtil.getMySize(DisplayUtil.getWidth(getContext()), widthMeasureSpec) ;
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);  

		// ��������е�childView�Ŀ�͸�  
		measureChildren(widthMeasureSpec, heightMeasureSpec);  
		/** 
		 * ��¼��view���ø� 
		 */  
		int height = viewSpace; //ͳ�Ƹ߶� 
		int width = viewSpace ;//ͳ��һ�еĿ�ȣ������ж��Ƿ񻹻���
		int oldwidth = 0 ;//��¼��û�г��������һ���ؼ�ʱ�Ŀ��
		int maxRowHeight = 0 ; //��ǰ�е�����и�
		int oldMaxRowHeight = 0 ;//��¼��û�г��������һ���ؼ�ʱ�ĵ�ǰ�е������
		int childCount= getChildCount();
		RowViews rowViews = new RowViews() ; //һ�е�view����
		
		for(int i = 0 ; i<childCount;i++){
			oldwidth = width;
			oldMaxRowHeight =maxRowHeight ; 
			View childView = getChildAt(i);
			MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
			int h = childView.getMeasuredHeight() + params.topMargin + params.bottomMargin+viewSpace;//�ؼ���ռ�ø߶�
			maxRowHeight = Math.max(maxRowHeight, h);
			width += childView.getMeasuredWidth() + params.rightMargin + params.leftMargin+viewSpace;
			if (width>viewWidth) {
				//�ж��Ƿ�һ���ؼ��ͳ����˸��ؼ��Ĵ�С
				if (oldwidth!=viewSpace) {
					i=i-1;//����
					maxRowHeight = oldMaxRowHeight;
				}else {
					rowViews.addView(new ViewInfo(childView, viewSpace));
				}
				width = viewSpace;
				
				rowViews.setStartLocationY(height);
				height += maxRowHeight ; 
				rowViews.setMaxHeight(maxRowHeight);//�������ĸ߶�
				rows.add(rowViews);//�ӵ�1��
				rowViews = new RowViews();//����
				
				maxRowHeight = 0 ;
				//�պ������һ�����س���
				if (i==childCount-1) {
					rowViews = null ; 
					break;
				}else {
					continue;
				}
			}else {
				rowViews.addView(new ViewInfo(childView, oldwidth));
				
				//�����һ�������ˣ���û�г���һ�У����Ϊһ��
				if (i==childCount-1) {
					rowViews.setStartLocationY(height);
					height += maxRowHeight ;
					rowViews.setMaxHeight(maxRowHeight);//�������ĸ߶�
					rows.add(rowViews);//�ӵ�1��
					rowViews = null ; 
				}
			}
			
			
		}

		/** 
		 * �����wrap_content����Ϊ���Ǽ����ֵ 
		 * ����ֱ������Ϊ�����������ֵ 
		 */  
		setMeasuredDimension(viewWidth, viewHeight = (heightMode == MeasureSpec.EXACTLY) ? viewHeight  
				: height);  
	}


	@Override  
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)  
	{  
		return new MarginLayoutParams(getContext(), attrs);  
	}


	//�������λ�ý�������
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
	 * һ�е�����
	 * 
	 * @���� ������
	 *
	 * @ʱ�� 2017-2-21
	 */
	class RowViews{
		Vector<ViewInfo> views = new Vector<ViewInfo>();
		int maxHeight ; //��ǰ�е����߶�
		int startLocationY ;//��ʼ��λ��
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
