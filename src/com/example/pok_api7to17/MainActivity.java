package com.example.pok_api7to17;
import android.app.Activity;
import android.os.Bundle;
public class MainActivity extends Activity {
	MainView mv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mv = new MainView(this);
		setContentView(mv);
	}
}
