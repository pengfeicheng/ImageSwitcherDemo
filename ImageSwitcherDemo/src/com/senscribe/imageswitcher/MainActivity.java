package com.senscribe.imageswitcher;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends Activity implements ViewFactory,OnTouchListener{

	private final String TAGLOG = "MainActivity";
	private ImageSwitcher imgs_browser = null;

	private String[] imageNames;
	private final String imageRoot = "MyPicture";
	private int currentPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.imgs_browser = (ImageSwitcher)findViewById(R.id.imgsw_browser);
		
		try {
			imageNames = getAssets().list(imageRoot);
		} catch (IOException e) {
			Log.e(TAGLOG, "initial image names:"+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		this.imgs_browser.setFactory(this);
		this.imgs_browser.setOnTouchListener(this);
		this.setCurrentImage();
	}
	
	private void setCurrentImage()
	{
		if (this.currentPosition > -1 && this.currentPosition < this.imageNames.length)
		{
			String imageName = this.imageNames[currentPosition];
			String imagePath = this.imageRoot + "/" + this.imageNames[currentPosition];
			try
			{
				InputStream is = getAssets().open(imagePath);
				Drawable drawable = Drawable.createFromStream(is, imageName);
				this.imgs_browser.setImageDrawable(drawable);
				is.close();
			}
			catch(Exception e)
			{
				Log.e(TAGLOG, e.getMessage());
			}
		}
	}
	
	public void moveNext()
	{
		currentPosition++;
		if (currentPosition >= this.imageNames.length)
		{
			currentPosition %= this.imageNames.length;
		}
		this.imgs_browser.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
		this.imgs_browser.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.right_out));
		this.setCurrentImage();
	}
	
	public void moveUp()
	{
		currentPosition--;
		if (currentPosition < 0)
		{
			currentPosition += this.imageNames.length;
		}
		this.imgs_browser.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
		this.imgs_browser.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
		this.setCurrentImage();
	}
	
	@Override
	public View makeView() {
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		imageView.setPadding(5, 5, 5, 5);
		imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return imageView;
	}
	
	private float startX = 0;
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		if (view.getId() == R.id.imgsw_browser)
		{
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.startX = event.getX();
				break;

			case MotionEvent.ACTION_UP:
				float endX = event.getX();
				if (Math.abs(endX - startX) > 5)	// filter click event
				{
					if (endX > startX)
					{
						this.moveNext();
					}
					else
					{
						this.moveUp();
					}
				}
			default:
				break;
			}
		}
		return true;
	}
}
