package com.aesemailclient;

import java.util.List;

import com.aesemailclient.crypto.SieveofEratosthenes;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class KeyFragment extends Fragment {
	
	private View view;
	private TextView lblp;
	private TextView lblq;
	private TextView lbln;
	private Button btnGenerateKey;
	private ImageView btnCopy;

	public KeyFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_key, container, false);
		
		lblp = (TextView)findViewById(R.id.lblp);
		lblq = (TextView)findViewById(R.id.lblq);
		lbln = (TextView)findViewById(R.id.lbln);
		btnGenerateKey = (Button)findViewById(R.id.btn_generateKey);
		btnCopy = (ImageView)findViewById(R.id.btnCopy);
		
		String[] stringKey = CacheToFile.Read(getActivity(), CacheToFile.KEY_FILE).split(",");
		
		lblp.setText((stringKey[0] != null)? stringKey[0]: "");
		lblq.setText((stringKey[1] != null)? stringKey[1]: "");
		lbln.setText((stringKey[2] != null)? stringKey[2]: "");
		
		btnGenerateKey.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<Integer> keys = SieveofEratosthenes.generateRabinKey();
				int p = keys.get(0);
				int q = keys.get(1);
				int n = p * q;
				String stringKey = p + "," + q + "," + n;
				CacheToFile.Write(getActivity(), CacheToFile.KEY_FILE, stringKey);
				
				lblp.setText(Integer.toString(p));
				lblq.setText(Integer.toString(q));
				lbln.setText(Integer.toString(n));
			}
		});
		
		btnCopy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (copyToClipboard(getActivity(), lbln.getText().toString())) {
					Toast.makeText(getActivity(), "copy to clipboard", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return view;
	}
	
	public boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("key", text);
                clipboard.setPrimaryClip(clip);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
	
	protected View findViewById(int id) {
		return view.findViewById(id);
	}

}
