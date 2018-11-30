package io.github.schneidervictor.resumeapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import io.github.schneidervictor.resumeapp.R;
import io.github.schneidervictor.resumeapp.adapters.WorkPagerAdapter;
import io.github.schneidervictor.resumeapp.dialogs.WelcomeDialog;
import io.github.schneidervictor.resumeapp.fragments.ContactFragment;
import io.github.schneidervictor.resumeapp.fragments.HomeFragment;
import io.github.schneidervictor.resumeapp.fragments.ProjectsFragment;
import io.github.schneidervictor.resumeapp.fragments.WorkFragment;
import io.github.schneidervictor.resumeapp.listeners.OnTabsReadyListener;

public class ContentActivity extends AppCompatActivity implements OnTabsReadyListener {
	// center coordinates for circle animation
	public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_X";
	public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_Y";
	private int revealX;
	private int revealY;
	
	private View contentContainer;
	
	// UI fragment containers
	private HomeFragment homeFragment = new HomeFragment();
	private ProjectsFragment projectsFragment = new ProjectsFragment();
	private WorkFragment workFragment = new WorkFragment();
	private ContactFragment contactFragment = new ContactFragment();
	
	// Event listener for the BottomNavigationBar
	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {
		
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch (item.getItemId()) {
				case R.id.navigation_home:
					setFragment(homeFragment, true);
					return true;
				case R.id.navigation_projects:
					setFragment(projectsFragment, true);
					return true;
				case R.id.navigation_work:
					setFragment(workFragment, true);
					return true;
				case R.id.navigation_contact:
					setFragment(contactFragment, true);
					return true;
				default:
					return false;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		
		Intent intent = getIntent();
		BottomNavigationView navBar = findViewById(R.id.bottom_navigation_bar);
		
		revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
		revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
		contentContainer = findViewById(R.id.content_container);
		
		contentContainer.setVisibility(View.INVISIBLE);
		
		startRevealAnimation();
		
		navBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
		
		workFragment.setListener(this);
		
		// set the HomeFragment as default
		setFragment(homeFragment, true);
	}
	
	/**
	 * Checks if this is the first time the app is being run.
	 *
	 * Shows the welcome dialog iff !isFirstRun
	 */
	private void checkIsFirstRun() {
		boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
				.getBoolean("isFirstRun", true);
		
		if (isFirstRun) {
			new WelcomeDialog().show(getSupportFragmentManager(), null);
			
			getSharedPreferences("PREFERENCE", MODE_PRIVATE)
					.edit()
					.putBoolean("isFirstRun", false)
					.apply();
		}
	}
	
	/**
	 * Starts the animation that reveals this Activity
	 */
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
	
	/**
	 * the revelation animation played when the user starts *this* Activity
	 */
	protected void revealActivity() {
		float finalRadius = (float) (Math.max(contentContainer.getWidth(), contentContainer.getHeight()) * 1.1);
		
		// create the animator for this view (the start radius is zero)
		Animator circularReveal = ViewAnimationUtils.createCircularReveal(contentContainer, revealX, revealY, 0, finalRadius);
		circularReveal.setDuration(1000);
		circularReveal.setInterpolator(new AccelerateInterpolator());
		
		// make the view visible and start the animation
		contentContainer.setVisibility(View.VISIBLE);
		
		// set the listener to show the dialog at the right timing
		circularReveal.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {
			
			}
			
			@Override
			public void onAnimationEnd(Animator animator) {
				checkIsFirstRun();
			}
			
			@Override
			public void onAnimationCancel(Animator animator) {
			
			}
			
			@Override
			public void onAnimationRepeat(Animator animator) {
			
			}
		});
		
		circularReveal.start();
	}
	
	@Override
	public void onBackPressed() {
		MainActivity.resumeAnimations();
		unRevealActivity();
	}
	
	/**
	 * The hiding animation played when the user returns to the MainActivity
	 */
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
	
	/**
	 * Updates the UI with the specified Fragment
	 *
	 * @param fragment         the new Fragment object to update the UI with
	 * @param isNavBarFragment true iff fragment is associated to one of the BottomNavigationBar
	 *                            options
	 */
	private void setFragment(Fragment fragment, boolean isNavBarFragment) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		
		// set fragment animations
		if (isNavBarFragment) {
			fragmentTransaction.setCustomAnimations(
					R.animator.cross_fade_in,
					R.animator.cross_fade_out,
					R.animator.float_in,
					R.animator.float_out);
		} else {
			fragmentTransaction.setCustomAnimations(
					R.animator.float_in,
					R.animator.float_out,
					R.animator.float_out,
					R.animator.float_in);
		}
		
		fragmentTransaction.replace(R.id.content_frame, fragment);
		
		// manage Back Stack after replace() call to prevent unwanted animations and display
		if (!isNavBarFragment) {
			fragmentTransaction.addToBackStack(null);
		} else {
			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		
		fragmentTransaction.commit();
	}
	
	public void visitGithubProfile(View view) {
		Uri uri = Uri.parse("https://github.com/SchneiderVictor");
		Intent socialIntent = new Intent(Intent.ACTION_VIEW, uri);
		
		startActivity(socialIntent);
	}
	
	public void downloadResume(View view) {
		Uri uri = Uri.parse("https://schneidervictor.github.io/res/resume.docx");
		Intent socialIntent = new Intent(Intent.ACTION_VIEW, uri);
		
		startActivity(socialIntent);
	}
	
	/**
	 * called by the WorkFragment once the ViewPager and TabLayout are ready
	 */
	@Override
	public void onTabsReady(FragmentManager manager) {
		WorkPagerAdapter adapter = new WorkPagerAdapter(manager, this);
		ViewPager pager = workFragment.getViewPager();
		TabLayout tabs = workFragment.getTabLayout();
		
		pager.setAdapter(adapter);
		tabs.setupWithViewPager(pager);
	}
}
