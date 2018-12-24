package io.github.schneidervictor.resumeapp.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import io.github.schneidervictor.resumeapp.R;

/**
 * The Main Activity
 * A simple screen with a pulsing profile picture.
 */
public class MainActivity extends AppCompatActivity {
	private static ObjectAnimator pulseAnimator;
	private static ObjectAnimator growthAnimator;
	private final int PULSE_DURATION = 1000;
	
	public static void resumeAnimations() {
		pulseAnimator.start();
		growthAnimator.start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ImageView profilePic = findViewById(R.id.app_logo);
		ImageView ring = findViewById(R.id.ring);
		
		startPulsingAnimation(profilePic, 1.2f);
		startGrowingAnimation(ring, 1.75f);
	}
	
	/**
	 * starts the pulsing animation of the given ImageView (the profile picture)
	 *
	 * @param imageView the image that must pulse
	 * @param newScale  the new scale of the image at the end of its animation
	 */
	private void startPulsingAnimation(ImageView imageView, float newScale) {
		ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(
				imageView,
				PropertyValuesHolder.ofFloat("scaleX", newScale),
				PropertyValuesHolder.ofFloat("scaleY", newScale));
		
		animation.setDuration(PULSE_DURATION / 2);
		animation.setRepeatCount(ObjectAnimator.INFINITE);
		animation.setRepeatMode(ObjectAnimator.REVERSE);
		
		pulseAnimator = animation;
		
		animation.start();
	}
	
	/**
	 * starts the the growing/fading animation of the given ImageView (the white ring)
	 *
	 * @param imageView the image that must grow and fade
	 * @param newScale  the new scale of the image at the end of its animation
	 */
	private void startGrowingAnimation(ImageView imageView, float newScale) {
		ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(
				imageView,
				PropertyValuesHolder.ofFloat("alpha", 0),
				PropertyValuesHolder.ofFloat("scaleX", newScale),
				PropertyValuesHolder.ofFloat("scaleY", newScale));
		
		animation.setDuration(PULSE_DURATION);
		animation.setRepeatCount(ObjectAnimator.INFINITE);
		
		growthAnimator = animation;
		
		animation.start();
	}
	
	/**
	 * Finds the coordinates of the center of the given view and sets them to the intent
	 *
	 * @param intent the intent that will start the ContentActivity
	 * @param view   the view from which the animation will "originate from"
	 */
	private void setAnimationCenter(Intent intent, View view) {
		int revealX = (int) (view.getX() + view.getWidth() / 2);
		int revealY = (int) (view.getY() + view.getHeight() / 2);
		
		intent.putExtra(ContentActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
		intent.putExtra(ContentActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);
	}
	
	/**
	 * Starts the Content Activity with a circle reveal animation
	 *
	 * @param view the view that triggers the Activity change
	 */
	public void startContentActivity(View view) {
		ActivityOptionsCompat options = ActivityOptionsCompat.
				makeSceneTransitionAnimation(this, view, "transition");
		Intent intent = new Intent(this, ContentActivity.class);
		
		setAnimationCenter(intent, view);
		
		pulseAnimator.end();
		growthAnimator.end();
		
		ActivityCompat.startActivity(this, intent, options.toBundle());
	}
}
