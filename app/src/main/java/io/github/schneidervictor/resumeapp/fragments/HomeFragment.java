package io.github.schneidervictor.resumeapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.schneidervictor.resumeapp.R;

/**
 * Fragment for the home page
 */
public class HomeFragment extends Fragment {
	
	public HomeFragment() {
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_home, container, false);
	}
}
