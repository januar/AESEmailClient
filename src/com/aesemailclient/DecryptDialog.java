package com.aesemailclient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aesemailclient.aes.CryptoUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DecryptDialog extends DialogFragment {
	
	private View view;
	private String cipher_text;
	private String content;
	private String secret_key;
	
	public DecryptDialog() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.decrypt_layout, null);
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
				DecryptDialogListener activity = (DecryptDialogListener) getActivity();
				activity.loadContent(content);
			}
		});
		
		Bundle bundle = getArguments();
		this.cipher_text = bundle.getString("ciphertext");
		this.content = bundle.getString("content");

		return builder.create();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			Button positive = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
			positive.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView txt_secretkey = (TextView) view
							.findViewById(R.id.txt_secretkey_dec);
					secret_key = txt_secretkey.getText().toString();
					if (secret_key.length() == 0) {
						Toast.makeText(getActivity(), "Please insert key and plaintext", Toast.LENGTH_SHORT).show();
					} else {
						//replaceEncryptText();
						String plaintext = CryptoUtils.decrypt(secret_key, cipher_text);
						if (plaintext != "") {
							DecryptDialogListener activity = (DecryptDialogListener) getActivity();
							dismiss();
							activity.loadContent(plaintext, content);
						}else{
							Toast.makeText(getActivity(), CryptoUtils.LOG,
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		}
	}
	
	private void replaceEncryptText()
	{
		String result = "";
		Pattern pattern = Pattern.compile(ReadActivity.REGEX_PATTERN);
		Matcher matcher = pattern.matcher(this.cipher_text);
		
		String cipher = CryptoUtils.decrypt(secret_key, cipher_text);
		if (cipher == "") {
			Toast.makeText(getActivity(), CryptoUtils.LOG,
					Toast.LENGTH_SHORT).show();
		}else{
			matcher.reset(content);
			int index = 0;
			while (matcher.find()) {
				cipher = CryptoUtils.decrypt(secret_key, matcher.group(2));
				if (cipher != "") {
					result += content.substring(index, index + (matcher.start() - index)) + cipher;
				}else{
					Log.i(getTag(), CryptoUtils.LOG);
					result += content.substring(matcher.start(), matcher.end());
				}
				index = matcher.end();
			}
			result += content.substring(index);
			DecryptDialogListener activity = (DecryptDialogListener) getActivity();
//			activity.loadContent(result);
			dismiss();
		}
	}

	public interface DecryptDialogListener {
		void loadContent(String plaintext, String content);
		void loadContent(String content);
	}
}
