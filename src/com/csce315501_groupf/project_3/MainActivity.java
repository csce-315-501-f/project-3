package com.csce315501_groupf.project_3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	static final String GAME_MODE = "com.csce315501_groupf.project3.GAME_MODE";
	static final String GAME_DIFFICULTY = "com.csce315501_groupf.project3.GAME_DIFFICULTY";
	static final String QUESTION_CATEGORY = "com.csce315501_groupf.project3.QUESTION_CATEGORY";
	static final String SD_CARD = "com.csce315501_groupf.project3.SD_CARD";
	static final String PREF_FILE = "REVERSI_PREFS";
	static final String SCORE_FILE = "REVERSI_SCORE";
	
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
	File sddir;
	File[] xmls = null;
	ArrayList<String> xmlNames = new ArrayList<String>();
	
	private int hasSDCard;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(TAG,"onCreate");
        
        Log.d(TAG, "onCreate: setting up xml database on external media");
        ArrayList<Integer> qCategoryIds = new ArrayList<Integer>();
        Field[] fields = R.raw.class.getFields();
        for(Field f : fields) {
	        try {
	        	qCategoryIds.add(f.getInt(null));
	        } catch (IllegalArgumentException e) {
	        } catch (IllegalAccessException e) { }
        } 
        for (int i: qCategoryIds) {
        	String resName = getResources().getResourceName(i);
        	String fileName = resName.replaceAll(".*/(.*)$","$1");
	        File file = new File(getExternalFilesDir(null), fileName);
	        Log.d(TAG, String.format("Checking file: %s",fileName));
	        if (!file.exists())
		        try {
		        	InputStream is = getResources().openRawResource(i);
		        	OutputStream os = new FileOutputStream(file);
		        	byte[] data = new byte[is.available()];
		        	is.read(data);
		        	os.write(data);
		        	is.close();
		        	os.close();
		        	hasSDCard = 1;
		        	Log.d(TAG, "Found sdcard");
		        }
		        catch(Exception e) {
		        	hasSDCard = 0;
		        	Log.d(TAG, "Error writing to external storage, or no external storage available");
		        }
	        else {
	        	hasSDCard = 1;
	        }
        }
        Log.d(TAG, "Transfered file if necessary");
        if (hasSDCard > 0) {
        	sddir = getExternalFilesDir(null);
        	Log.d(TAG, "setup xml list");
			xmls = sddir.listFiles();
        }
        
        // load XML data
        gameModes = (List<String>) Arrays.asList(getResources().getStringArray(R.array.txtGameModes));
        gameDifficulties = (List<String>) Arrays.asList(getResources().getStringArray(R.array.txtDifficulties));
        if (hasSDCard > 0) {
        	for (File f : xmls) {
        		xmlNames.add(f.getName());
        	}
        	questionCategories = (List<String>) xmlNames;
        }
        else 
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.textview, questionCategories);
        spinQuestionCategory.setAdapter(adapter);
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
    	intent.putExtra(SD_CARD, hasSDCard);
    	startActivity(intent);
    }
    
    public void showScores(View v) {
    	Intent intent = new Intent(this,ScoreActivity.class);
    	startActivity(intent);
    }
    
}
