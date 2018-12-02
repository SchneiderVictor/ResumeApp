package io.github.schneidervictor.resumeapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.schneidervictor.resumeapp.R;

/**
 * Fragment for the skills information page
 */
public class SkillsFragment extends Fragment {
	
	public SkillsFragment() {
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_skills, container, false);
	}
}
