package io.github.schneidervictor.resumeapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import io.github.schneidervictor.resumeapp.R;

/**
 * DialogFragment for the welcome message
 */
public class MessageDialog extends DialogFragment {
	private int layout;
	
	public MessageDialog() {
	}
	
	public void setLayout(int layout) {
		this.layout = layout;
	}
	
	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(this.layout, null);
		
		builder.setView(layout)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
		
		return builder.create();
	}
}
