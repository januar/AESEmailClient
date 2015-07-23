package com.aesemailclient;

import com.aesemailclient.aes.CryptoUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EncryptDialog extends DialogFragment {
	private View view;

	public EncryptDialog() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.encrypt_layout, null);
		builder.setView(view);
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return builder.create();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			Button positive = (Button)d.getButton(Dialog.BUTTON_POSITIVE);
			positive.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView txt_plaintext = (TextView) view.findViewById(R.id.txt_plaintext);
					TextView txt_secretkey = (TextView) view.findViewById(R.id.txt_secretkey);
					String key = txt_secretkey.getText().toString();
					String plaintext = txt_plaintext.getText().toString();
					String cipher = CryptoUtils.encrypt(key, plaintext);
					if (cipher == "") {
						Toast.makeText(getActivity(), CryptoUtils.LOG, Toast.LENGTH_SHORT).show();
					} else {
						EncryptDialogListener activity = (EncryptDialogListener) getActivity();
						activity.addEncrytedText(String.format("<encrypt>%s</encrypt>", cipher));
						dismiss();
					}
				}
			});
		}
	}
	
	public interface EncryptDialogListener{
		void addEncrytedText(String ciphertext);
	}
}
