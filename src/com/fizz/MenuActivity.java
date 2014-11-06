package com.fizz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class MenuActivity extends FragmentActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState) ;
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		Fragment newFragment = new StickyFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.content, newFragment).commit();
	}
}
