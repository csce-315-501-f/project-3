package com.csce315501_groupf.project_3;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class MainActivity extends Activity {

	static final String GAME_MODE = "com.csce315501_groupf.project3.GAME_MODE";
	static final String GAME_DIFFICULTY = "com.csce315501_groupf.project3.GAME_DIFFICULTY";
	static final String QUESTION_CATEGORY = "com.csce315501_groupf.project3.QUESTION_CATEGORY";
	static final String PREF_FILE = "REVERSI_PREFS";
	static final String TAG = "com.reversi";
	
	private Spinner spinGameMode;
	private Spinner spinGameDifficulty;
	private Spinner spinQuestionCategory;
	
	private String gameMode;
	private List<String> gameModes;
	private String gameDifficulty;
	private List<String> gameDifficulties;
	private String questionCategory;
	private List<String> questionCategories;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(TAG,"onCreate");
        
        // load XML data
        gameModes = (List<String>) Arrays.asList(getResources().getStringArray(R.array.txtGameModes));
        gameDifficulties = (List<String>) Arrays.asList(getResources().getStringArray(R.array.txtDifficulties));
        questionCategories = (List<String>) Arrays.asList(getResources().getStringArray(R.array.txtCategories));
        
        // restore menu state
        if (savedInstanceState == null) {
        	Log.d(TAG,"creating new state");
            SharedPreferences mPrefs = getSharedPreferences(PREF_FILE, 1);
            if (mPrefs.contains(GAME_MODE)) {
            	Log.d(TAG,"loading preferences file");
	            gameMode = mPrefs.getString(GAME_MODE,"");
	            gameDifficulty = mPrefs.getString(GAME_DIFFICULTY,"");
	            questionCategory = mPrefs.getString(QUESTION_CATEGORY,"");
            }
            else {
            	Log.d(TAG,"no preferences file");
	        	gameMode = gameModes.get(0);
	        	gameDifficulty = gameDifficulties.get(0);
	        	questionCategory = questionCategories.get(0);
            }
        }
        else {
        	Log.d(TAG,"restoring previous state");
        	gameMode = savedInstanceState.getString(GAME_MODE);
        	gameDifficulty = savedInstanceState.getString(GAME_DIFFICULTY);
        	questionCategory = savedInstanceState.getString(QUESTION_CATEGORY);
        }
        
        // initialize spinner input
        spinGameMode = (Spinner) findViewById(R.id.spinGameModes);
        spinGameMode.setSelection(gameModes.indexOf(gameMode));
        spinGameDifficulty = (Spinner) findViewById(R.id.spinDifficulties);
        spinGameDifficulty.setSelection(gameDifficulties.indexOf(gameDifficulty));
        spinQuestionCategory = (Spinner) findViewById(R.id.spinCategory);
        spinQuestionCategory.setSelection(questionCategories.indexOf(questionCategory));
        
        // set spinner callbacks
        spinGameMode.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				gameMode = spinGameMode.getSelectedItem().toString();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        spinGameDifficulty.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				gameDifficulty = spinGameDifficulty.getSelectedItem().toString();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        spinQuestionCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				questionCategory = spinQuestionCategory.getSelectedItem().toString();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
    }
    
    protected void onPause() {
    	SharedPreferences prefs = getSharedPreferences(PREF_FILE, 1);
    	SharedPreferences.Editor mPrefs = prefs.edit();
    	mPrefs.putString(GAME_MODE,gameMode);
        mPrefs.putString(GAME_DIFFICULTY,gameDifficulty);
        mPrefs.putString(QUESTION_CATEGORY, questionCategory);
        mPrefs.commit();
        
        super.onPause();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putString(GAME_MODE, gameMode);
    	outState.putString(GAME_DIFFICULTY, gameDifficulty);
    	outState.putString(QUESTION_CATEGORY, questionCategory);
    	
    }
    
    public void startGame(View v) {
    	Intent intent = new Intent(this,GameBoard.class);
    	intent.putExtra(GAME_MODE, gameMode);
    	intent.putExtra(GAME_DIFFICULTY, gameDifficulty);
    	intent.putExtra(QUESTION_CATEGORY, questionCategory);
    	startActivity(intent);
    }
    
}
