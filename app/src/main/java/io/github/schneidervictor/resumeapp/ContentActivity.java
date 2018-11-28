package io.github.schneidervictor.resumeapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

public class ContentActivity extends AppCompatActivity {
	public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_X";
	public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_Y";
	
	private int revealX;
	private int revealY;
	
	private View contentContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		
		final Intent intent = getIntent();
		revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
		revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
		contentContainer = findViewById(R.id.content_container);
		
		contentContainer.setVisibility(View.INVISIBLE);
		
		startRevealAnimation();
	}
	
	private void startRevealAnimation() {
		ViewTreeObserver viewTreeObserver = contentContainer.getViewTreeObserver();
		
		if (viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					revealActivity();
					contentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
			});
		}
	}
	
	protected void revealActivity() {
		float finalRadius = (float) (Math.max(contentContainer.getWidth(), contentContainer.getHeight()) * 1.1);
		
		// create the animator for this view (the start radius is zero)
		Animator circularReveal = ViewAnimationUtils.createCircularReveal(contentContainer, revealX, revealY, 0, finalRadius);
		circularReveal.setDuration(1000);
		circularReveal.setInterpolator(new AccelerateInterpolator());
		
		// make the view visible and start the animation
		contentContainer.setVisibility(View.VISIBLE);
		circularReveal.start();
	}
	
	@Override
	public void onBackPressed() {
		MainActivity.resumeAnimations();
		unRevealActivity();
	}
	
	protected void unRevealActivity() {
		float finalRadius = (float) (Math.max(contentContainer.getWidth(), contentContainer.getHeight()) * 1.1);
		Animator circularReveal = ViewAnimationUtils.createCircularReveal(
				contentContainer, revealX, revealY, finalRadius, 0);

		circularReveal.setDuration(1000);
		circularReveal.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				contentContainer.setVisibility(View.INVISIBLE);
				finish();
			}
		});
		
		circularReveal.start();
	}
}
