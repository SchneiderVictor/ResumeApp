package io.github.schneidervictor.resumeapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.schneidervictor.resumeapp.R;
import io.github.schneidervictor.resumeapp.listeners.OnTabsReadyListener;

/**
 * Fragment for the resume page information page
 *
 * thif Fragment encompasses the education, experience and skills information pages
 */
public class WorkFragment extends Fragment {
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private OnTabsReadyListener listener;
	
	public WorkFragment() {
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_work, container, false);
		
		tabLayout = view.findViewById(R.id.tab_layout);
		viewPager = view.findViewById(R.id.pager_layout);
		
		listener.onTabsReady(getChildFragmentManager());
		
		return view;
	}
	
	public TabLayout getTabLayout() {
		return tabLayout;
	}
	
	public ViewPager getViewPager() {
		return viewPager;
	}
	
	public void setListener(@NonNull OnTabsReadyListener listener) {
		this.listener = listener;
	}
}
