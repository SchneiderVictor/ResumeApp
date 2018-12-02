package io.github.schneidervictor.resumeapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.schneidervictor.resumeapp.R;

/**
 * Fragment for the contact information page
 */
public class ContactFragment extends Fragment {
	
	public ContactFragment() {
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}
}
