

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;

public class DragLayout extends FrameLayout {

	private GestureDetectorCompat gestureDetector;
	private ViewDragHelper dragHelper;
	private OnDragStateListener dragListener;
	private int range;// 可滑动范围

	private int width;// menu的宽度
	private int height;// menu的高度
	private int mainDistanceToLeft;
	private View leftLayout;
	private TouchDisableView mainLayout;
	private Status status = Status.Close;

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		gestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
		dragHelper = ViewDragHelper.create(this, dragHelperCallback);
		range = (int) (getWindowDisplayMetrics().widthPixels * 0.5f);
	}

	public void updateRangeRatio(float ratiod) {
		range = (int) (getWindowDisplayMetrics().widthPixels * ratiod);
		if (leftLayout != null) {
			leftLayout.requestLayout();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		leftLayout = (ViewGroup) getChildAt(0);
		leftLayout.setClickable(true);

		View mainView = getChildAt(1);
		removeView(mainView);

		mainLayout = new TouchDisableView(getContext());
		mainLayout.addView(mainView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mainLayout.setClickable(true);

		addView(mainLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public void attachToActivity(Activity activity, ViewGroup slideMenu) {
		ViewGroup viewDecor = (ViewGroup) activity.getWindow().getDecorView();
		View mainView = viewDecor.getChildAt(0);
		viewDecor.removeViewAt(0);

		setupViews(slideMenu, mainView);

		viewDecor.addView(this, 0);
	}

	private void setupViews(View slideMenu, View mainView) {
		leftLayout = slideMenu;
		leftLayout.setClickable(true);
		addView(leftLayout, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

		mainLayout = new TouchDisableView(getContext());
		mainLayout.addView(mainView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mainLayout.setClickable(true);
		addView(mainLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public View getSlideMenu() {
		return leftLayout;
	}

	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
			return Math.abs(dy) <= Math.abs(dx);
		}
	}

	private ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (mainDistanceToLeft + dx < 0) {
				return 0;
			} else if (mainDistanceToLeft + dx > range) {
				return range;
			} else {
				return left;
			}

		}

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return range;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if (xvel > 0) {
				open();
			} else if (xvel < 0) {
				close();
			} else if (releasedChild == mainLayout && mainDistanceToLeft > range * 0.3) {
				open();
			} else if (releasedChild == leftLayout && mainDistanceToLeft > range * 0.7) {
				open();
			} else {
				close();
			}
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			// 不是实时回调
			if (changedView == mainLayout) {
				mainDistanceToLeft = left;
			} else {
				mainDistanceToLeft = mainDistanceToLeft + left;
			}

			if (mainDistanceToLeft < 0) {
				mainDistanceToLeft = 0;
			} else if (mainDistanceToLeft > range) {
				mainDistanceToLeft = range;
			}

			if (changedView == leftLayout) {
				leftLayout.layout(0, 0, width, height);
				mainLayout.layout(mainDistanceToLeft, 0, mainDistanceToLeft + mainLayout.getMeasuredWidth(), height);
			}
			dispatchDragEvent(mainDistanceToLeft);
		}
	};

	public interface OnDragStateListener {
		public void onOpen();

		public void onClose();

		public void onDrag(float percent);
	}

	public void setOnDragStateListener(OnDragStateListener dragListener) {
		this.dragListener = dragListener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = leftLayout.getMeasuredWidth();
		height = leftLayout.getMeasuredHeight();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		leftLayout.layout(0, 0, width, height);
		mainLayout.layout(mainDistanceToLeft, 0, mainDistanceToLeft + mainLayout.getMeasuredWidth(), height);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return dragHelper.shouldInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		try {
			dragHelper.processTouchEvent(e);
		} catch (Exception ex) {
			return super.onTouchEvent(e);
		}
		return false;
	}

	private void dispatchDragEvent(int mainLeft) {
		float percent = mainLeft / (float) range;

		animateView(percent);
		Status lastStatus = status;
		if (dragListener == null) {
			return;
		}
		dragListener.onDrag(percent);
		if (lastStatus != getStatus() && status == Status.Close) {
			dragListener.onClose();
		} else if (lastStatus != getStatus() && status == Status.Open) {
			dragListener.onOpen();
		}
	}

	private void animateView(float percent) {
		float f1 = 1 - percent * 0.3f;
		ViewHelper.setScaleX(mainLayout, f1);
		ViewHelper.setScaleY(mainLayout, f1);

		ViewHelper.setTranslationX(leftLayout, -leftLayout.getWidth() / 2.3f + leftLayout.getWidth() / 2.3f * percent);
		ViewHelper.setScaleX(leftLayout, 0.5f + 0.5f * percent);
		ViewHelper.setScaleY(leftLayout, 0.5f + 0.5f * percent);
		ViewHelper.setAlpha(leftLayout, percent);

		Drawable background = getBackground();
		if (background != null) {
			background.setColorFilter(evaluate(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
		}
	}

	private Integer evaluate(float fraction, Object startValue, Integer endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;
		return (int) ((startA + (int) (fraction * (endA - startA))) << 24) | (int) ((startR + (int) (fraction * (endR - startR))) << 16) | (int) ((startG + (int) (fraction * (endG - startG))) << 8) | (int) ((startB + (int) (fraction * (endB - startB))));
	}

	@Override
	public void computeScroll() {
		if (dragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	public enum Status {
		Drag, Open, Close
	}

	public Status getStatus() {
		if (mainDistanceToLeft == 0) {
			status = Status.Close;
		} else if (mainDistanceToLeft == range) {
			status = Status.Open;
		} else {
			status = Status.Drag;
		}
		return status;
	}

	public void open() {
		open(true);
	}

	/**
	 * @param animate
	 *            是否需要动画
	 */
	public void open(boolean animate) {
		if (animate) {
			if (dragHelper.smoothSlideViewTo(mainLayout, range, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mainLayout.layout(range, 0, range * 2, height);
			dispatchDragEvent(range);
		}
	}

	public void close() {
		close(true);
	}

	/**
	 * @param animate
	 *            是否需要动画
	 */
	public void close(boolean animate) {
		if (animate) {
			if (dragHelper.smoothSlideViewTo(mainLayout, 0, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mainLayout.layout(0, 0, mainLayout.getMeasuredWidth(), height);
			dispatchDragEvent(0);
		}
	}

	public DisplayMetrics getWindowDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	class TouchDisableView extends RelativeLayout {

		public TouchDisableView(Context context) {
			this(context, null);
		}

		public TouchDisableView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent event) {
			if (getStatus() != Status.Close) {
				return true;
			}
			return super.onInterceptTouchEvent(event);
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (getStatus() != Status.Close) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					close();
				}
				return true;
			}
			return super.onTouchEvent(event);
		}

	}

}
