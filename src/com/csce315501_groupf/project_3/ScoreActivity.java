package com.csce315501_groupf.project_3;

import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoreActivity extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		SharedPreferences score = getSharedPreferences(MainActivity.SCORE_FILE,0);
		
		TableLayout scoreTable=(TableLayout)findViewById(R.id.scoreTableLayout); 
		
		Log.d(MainActivity.TAG, String.format("Getting all scores"));
		Map<String, ?> scoreMap = score.getAll();
//		Map.Entry<String, ?> smallest = scoreMap.entrySet().iterator().next();
		int rank = 1;
    	for (Map.Entry<String, ?> i: scoreMap.entrySet()) {
    		Log.d(MainActivity.TAG, String.format("High Score: %s",i.getValue()));
    		TableRow scoreRow = new TableRow(this);
    		scoreRow.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
    		TextView scoreText = new TextView(this);
    		scoreText.setText(String.format("%d: %.1f%%",rank,Double.parseDouble(i.getValue().toString())));
    		scoreRow.addView(scoreText);
    		scoreTable.addView(scoreRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    		++rank;
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score, menu);
		return true;
	}

}
