/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fizz;

import java.util.Locale;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StickyFragment extends Fragment implements
		ObservableScrollView.Callbacks {
	private TextView mStickyView;
	private View mPlaceholderView;
	private ObservableScrollView mObservableScrollView;

	private int textnum = 1;
	private String name ;
	private String number ;
	
	String drink ;

	private String[] drinks = { "Pale lager", "Bock", "Pilsener",
			"Schwarzbier", "Sahti", "Small beer", "Wheat beer", "Witbier" };
	private String[] ingredients = {
			"Made with a slow acting yeast that ferments at a low temperature while being stored",
			"Strong Lager",
			"Lighter lager brewed with partially malted barley",
			"Dark lager",
			"Finnish",
			"Very low alcohol",
			"Or \"Hefeweizen\", made with wheat in addition to malted barley",
			"\"White Beer\", made with herbs or fruit instead of or in addition to hops",
			"Made from cassava or maize",
			"Made from cassava, maize root, grape, apple or other fruits" };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_content, container, false);

		mObservableScrollView = (ObservableScrollView) rootView
				.findViewById(R.id.scroll_view);
		mObservableScrollView.setCallbacks(this);

		mStickyView = (TextView) rootView.findViewById(R.id.sticky);
		mStickyView.setText(R.string.sticky_item);
		mPlaceholderView = rootView.findViewById(R.id.placeholder);

		mObservableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						onScrollChanged(mObservableScrollView.getScrollY());
					}
				});
		
		getNameNumber() ;
		return rootView;
	}
	
	private void getNameNumber() {
		TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE) ;
		number = tm.getLine1Number() ;
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getActivity()).getAccounts();
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        name = account.name;
		    }
		}
	}

	@Override
	public void onScrollChanged(int scrollY) {
		mStickyView
				.setTranslationY(Math.max(mPlaceholderView.getTop(), scrollY));
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("Fizz", "onActivityResult") ;
		if(resultCode != Activity.RESULT_OK) return ;
		Log.e("Fizz", "Succesful!") ;
		
		String[] order = {drink, number, name, "1"} ;
		new SendOrderTask().execute(order);
	}

	@Override
	public void onStart() {
		super.onStart();
		LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.ll);
		for (int i = 0; i < drinks.length; i++) {
			TextView tt = new TextView(getActivity());
			tt.setText(drinks[i].toUpperCase(Locale.US) + "\n" + ingredients[i]);
			tt.setPadding(15, 15, 15, 15);
			tt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drink = ((TextView) v).getText().toString() ;
					try {  Intent venmoIntent =
							 VenmoLibrary.openVenmoPayment("1684", "Fizz",
							 "8123744601", "0.01", drink, "pay");
							 startActivityForResult(venmoIntent, 1);
							// 1 is the requestCode we are using for Venmo. Feel
							// free to change this to another number.

						// Venmo native app not install on device, so let's
						// instead open a mobile web version of Venmo in a
						// WebView
					} catch (android.content.ActivityNotFoundException e) {
						Log.e("Fizz", "onClicked") ;
						{
							Intent venmoIntent = new Intent(getActivity(),
									VenmoWebViewActivity.class);
							String venmo_uri = VenmoLibrary
									.openVenmoPaymentInWebView("1684", "Fizz",
											"8123744601", "0.01", "Drink",
											"pay");
							venmoIntent.putExtra("url", venmo_uri);
							startActivityForResult(venmoIntent, 1);
						}
					//} catch (android.content.ActivityNotFoundException e) {
						//e.printStackTrace() ;
					}
				}
			});
			tt.setTextSize(22);
			if (textnum % 2 == 0) {
				tt.setBackgroundColor(Color.argb(0, 51, 181, 229));
				tt.setTextColor(Color.argb(255, 51, 181, 229));
			} else {
				tt.setBackgroundColor(Color.argb(240, 51, 181, 229));
				tt.setTextColor(Color.argb(255, 255, 255, 255));
			}
			tt.setTypeface(SplashActivity.tp);
			ll.addView(tt);
			textnum++;
		}
	}

	@Override
	public void onDownMotionEvent() {
	}

	@Override
	public void onUpOrCancelMotionEvent() {
	}
}
