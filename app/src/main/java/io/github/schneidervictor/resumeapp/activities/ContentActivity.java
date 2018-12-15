package io.github.schneidervictor.resumeapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import java.util.InputMismatchException;
import java.util.List;

import io.github.schneidervictor.resumeapp.R;
import io.github.schneidervictor.resumeapp.adapters.WorkPagerAdapter;
import io.github.schneidervictor.resumeapp.containers.ContactInfoContainer;
import io.github.schneidervictor.resumeapp.dialogs.WelcomeDialog;
import io.github.schneidervictor.resumeapp.fragments.ContactFragment;
import io.github.schneidervictor.resumeapp.fragments.HomeFragment;
import io.github.schneidervictor.resumeapp.fragments.ProjectsFragment;
import io.github.schneidervictor.resumeapp.fragments.WorkFragment;
import io.github.schneidervictor.resumeapp.listeners.OnTabsReadyListener;

/**
 * The Bulk of the application
 * <p>
 * The Activity where my entire resume can be explored
 */
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
	
	private ConstraintLayout selectedView;
	
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
	
	/**
	 * Returns the user to the MainActivity with the appropriate exit animation
	 */
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
	 *                         options
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
					R.animator.fall_in,
					R.animator.fall_out);
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
	
	/**
	 * Uses the url stored in the view's tag to launch the appropriate webiste
	 *
	 * @param view selected view
	 */
	public void visitTaggedSite(View view) {
		Uri uri = Uri.parse(view.getTag().toString());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		
		startActivity(intent);
	}
	
	/**
	 * Updates the Fragment's UI to keep the most recently selected View 'opened', and
	 * display the 'closing' of a View.
	 *
	 * @param view the most recently tapped View
	 */
	private void updateUI(ConstraintLayout view, int viewOpeningLayout, int selectedClosingLayout) {
		ConstraintSet openingSet = new ConstraintSet();
		openingSet.clone(this, viewOpeningLayout);
		
		// UI update to 'deselect' the previously selected View
		if (selectedView != null) {
			
			// selectedView.setBackgroundResource(R.drawable.rounded_rectangle);
			ConstraintSet closingSet = new ConstraintSet();
			closingSet.clone(this, selectedClosingLayout);
			
			TransitionManager.beginDelayedTransition(view);
			closingSet.applyTo(selectedView);
		}
		
		// UI update to 'deselect' the newView
		if (selectedView == view) {
			selectedView = null;
			return;
		}
		
		// UI update to 'select' the newView
		selectedView = view;
		// newView.setBackgroundResource(R.drawable.selected_rounded_rectangle);
		TransitionManager.beginDelayedTransition(view);
		openingSet.applyTo(view);
	}
	
	/**
	 * Toggles the selected view to display(open) or hide(the underlying information)
	 *
	 * @param view the selected project
	 */
	public void toggleProjectView(View view) {
		updateUI((ConstraintLayout) view, getOpeningLayout(view), getClosingLayout(selectedView));
	}
	
	/**
	 * A helper method to get the appropriate 'opened' and 'closed' layout resources
	 *
	 * @param view the view for which we are querying the layout resourced
	 * @return int[] with 'open' layout at index 0 and 'closed' layout at index 1
	 */
	private int[] getOpeningClosingLayouts(View view) {
		// reached in the case when selectedView variable is null.
		// no error is caused by this behaviour.
		if (view == null) {
			return new int[2];
		}
		
		switch (view.getId()) {
			case R.id.project_pcrs_jslinux:
				return new int[]{R.layout.pcrs_project_open, R.layout.pcrs_project_closed};
			case R.id.project_dots_lines:
				return new int[]{R.layout.dotsnlines_project_open, R.layout.dotsnlines_project_closed};
			case R.id.project_memories:
				return new int[]{R.layout.memories_project_open, R.layout.memories_project_closed};
			case R.id.therapy_inov_before_after:
				return new int[]{R.layout.website_project_open, R.layout.website_project_closed};
			default:
				throw new InputMismatchException("View not yet supported");
		}
	}
	
	/**
	 * Wrapper method to get a specific layout resource
	 */
	private int getOpeningLayout(View view) {
		return getOpeningClosingLayouts(view)[0];
	}
	
	
	/**
	 * Wrapper method to get a specific layout resource
	 */
	private int getClosingLayout(View view) {
		return getOpeningClosingLayouts(view)[1];
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
	
	/**
	 * Checks if the user has the associated app installed
	 *
	 * @param intent intent that would launch an associated app
	 * @return true iff the associated app is installed
	 */
	private boolean isIntentAvailable(Intent intent) {
		final PackageManager packageManager = getApplicationContext().getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
	/**
	 * Launches the default browser, directed to the input link
	 *
	 * @param link social media website link
	 */
	private void launchSocialLink(String link) {
		Uri uri = Uri.parse(link);
		Intent socialIntent = new Intent(Intent.ACTION_VIEW, uri);
		
		startActivity(socialIntent);
	}
	
	/**
	 * Launched an the associated application, indicated by pack, if installed.
	 * Otherwise, launches the browser to the backupLink
	 *
	 * @param mainLink   app-specific link
	 * @param backupLink website link
	 * @param pack       android application package
	 */
	private void launchSocialLink(String mainLink, String backupLink, String pack) {
		Uri uri = Uri.parse(mainLink);
		Intent socialIntent = new Intent(Intent.ACTION_VIEW, uri);
		socialIntent.setPackage(pack);
		
		if (isIntentAvailable(socialIntent)) {
			startActivity(socialIntent);
		} else {
			launchSocialLink(backupLink);
		}
	}
	
	public void launchGithub(View view) {
		launchSocialLink("https://github.com/" + ContactInfoContainer.GITHUB_USERNAME);
	}
	
	public void launchLinkedIn(View view) {
		launchSocialLink("https://www.linkedin.com/in/" + ContactInfoContainer.LINKEDIN_USERNAME + "/");
	}
	
	public void launchPhone(View view) {
		Intent emailIntent = new Intent(Intent.ACTION_DIAL);
		emailIntent.setData(Uri.parse("tel:" + ContactInfoContainer.PHONE_NUMBER));
		
		startActivity(emailIntent);
	}
	
	public void launchEmail(View view) {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
		emailIntent.setData(Uri.parse("mailto:" + ContactInfoContainer.EMAIL_ADDRESS));
		
		startActivity(emailIntent);
	}
	
	public void launchIG(View view) {
		launchSocialLink("http://instagram.com/_u/" + ContactInfoContainer.INSTAGRAM_USERNAME,
				"http://instagram.com/" + ContactInfoContainer.INSTAGRAM_USERNAME,
				"com.instagram.android");
	}
	
	public void launchTwitter(View view) {
		launchSocialLink("twitter://user?screen_name=" + ContactInfoContainer.TWITTER_USERNAME,
				"https://twitter.com/" + ContactInfoContainer.TWITTER_USERNAME,
				"com.twitter.android");
	}
	
	public void launchFacebook(View view) {
		launchSocialLink("fb://facewebmodal/f?href=" + ContactInfoContainer.FACEBOOK_LINK,
				ContactInfoContainer.FACEBOOK_LINK,
				"com.facebook.katana");
	}
}
