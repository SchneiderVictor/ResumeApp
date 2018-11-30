package io.github.schneidervictor.resumeapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.github.schneidervictor.resumeapp.R;
import io.github.schneidervictor.resumeapp.fragments.EducationFragment;
import io.github.schneidervictor.resumeapp.fragments.ExperienceFragment;
import io.github.schneidervictor.resumeapp.fragments.SkillsFragment;

public class WorkPagerAdapter extends FragmentPagerAdapter {
	
	private Context mainContext;
	
	public WorkPagerAdapter(FragmentManager fragmentManager, Context context) {
		super(fragmentManager);
		mainContext = context;
	}
	
	// This determines the fragment for each tab
	@Override
	public Fragment getItem(int i) {
		switch (i) {
			case 0:
				return new EducationFragment();
			case 1:
				return new ExperienceFragment();
			case 2:
				return new SkillsFragment();
			default:
				throw new IndexOutOfBoundsException();
		}
	}
	
	@Override
	public int getCount() {
		return 3;
	}
	
	// This determines the title for each tab
	@Override
	public CharSequence getPageTitle(int position) {
		// Generate title based on item position
		switch (position) {
			case 0:
				return mainContext.getString(R.string.tab_education);
			case 1:
				return mainContext.getString(R.string.tab_experience);
			case 2:
				return mainContext.getString(R.string.tab_skills);
			default:
				throw new IndexOutOfBoundsException();
		}
	}
}
