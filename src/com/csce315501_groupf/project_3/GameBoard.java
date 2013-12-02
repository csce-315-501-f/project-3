package com.csce315501_groupf.project_3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GameBoard extends Activity {
	
	private static final int ROWS = 8;
	private static final int COLUMNS = 8;
	ImageButton[][] board;;

	private static final char BLACK = 'B';
	private static final char WHITE = 'W';
	private static final char EMPTY = '.';
	private static final char MOVE = 'M';
	
	private String difficulty;
	private String category;
	
	private int blackScore;
	private int whiteScore;
	private ArrayList<ReversiGame.Move> availableMoves;
	
	private boolean hasSDCard;
	
	private List<QuestionsPullParser.Question> questions;
	private List<QuestionsPullParser.Question> usedQuestions;
	private int correctId;
	private boolean wasCorrect;
	private ReversiGame.Move currentMove;
	
	private int correctCount;
	private int totalCount;
	SharedPreferences score;
	SharedPreferences.Editor scoreEditor;
	
	ReversiGame game;
	
	TextView temp;
	
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		score = getSharedPreferences(MainActivity.SCORE_FILE,1);
		scoreEditor = score.edit();
	
		Log.d(MainActivity.TAG, "create game board");
		
		setContentView(R.layout.game_board);
	
		blackScore = 0;
		whiteScore = 0;
		availableMoves = new ArrayList<ReversiGame.Move>();
		
		correctCount = 0;
		totalCount = 0;
		
		questions = new ArrayList<QuestionsPullParser.Question>();
		usedQuestions = new ArrayList<QuestionsPullParser.Question>();
		
		Intent intent = getIntent();
		difficulty = intent.getStringExtra(MainActivity.GAME_DIFFICULTY);
		category = intent.getStringExtra(MainActivity.QUESTION_CATEGORY);
		hasSDCard = (intent.getIntExtra(MainActivity.SD_CARD, 0) > 0)?true:false;
		
		setupButtons();
		temp = (TextView) findViewById(R.id.temp);
		
		
		Log.d(MainActivity.TAG, "buttons setup");
		
		game = new ReversiGame(difficulty.toLowerCase().charAt(0));
			
		populateQuestions();
		updateButtons();
	}


	public void submitQuestion(View v) {
		++totalCount;
		RadioGroup aRadioGroup = (RadioGroup) findViewById(R.id.answersRadioGroup);
		int id = aRadioGroup.getCheckedRadioButtonId();
		Log.d(MainActivity.TAG,String.format("Correct id was %d, your choice was %d",correctId,id));
		wasCorrect = (correctId == id)?true:false;
		if (wasCorrect) ++correctCount;
		questionAlert();
	}
	
	private void doMove(int r, int c) {
		if (!moveAvailable(r,c)) {
			Log.d(MainActivity.TAG,String.format("move (%d,%d) is invalid",c,r));
			for(ReversiGame.Move a: availableMoves) {
				Log.d(MainActivity.TAG,String.format("available: (%d,%d)",a.column,a.row));
			}
			return;
		}
		setContentView(R.layout.activity_questions);
		setupQuestion();
		currentMove = new ReversiGame.Move(c,r);
	}
	
	private void makeMove() {
		int r = currentMove.row;
		int c = currentMove.column;
//		Log.d(MainActivity.TAG,String.format("doing move (%d,%d)",r,c));
		Log.d(MainActivity.TAG,String.format("DOMOVE: Question answer was (before): %s",wasCorrect));
		if (!wasCorrect) { // use random move
			Log.d(MainActivity.TAG,String.format("DOMOVE: Question answer was (after): %s",wasCorrect));
			Random rand = new Random();
			int n = rand.nextInt(availableMoves.size());
			ReversiGame.Move m = availableMoves.get(n);
			c = m.column;
			r = m.row;
		}
		if(game.lightTurn(c, r+1)) {
			switch (game.hasWon(ReversiGame.WHITE)) {
	            case 'w':
	            	updateHighScore();
	            	gameOverAlert(getResources().getString(R.string.game_over_win));
	            	updateButtons();
	                return;
	            case 't':
	            	gameOverAlert(getResources().getString(R.string.game_over_tie));
	            	updateButtons();
	                return;
	            case 'l':
	            	gameOverAlert(getResources().getString(R.string.game_over_lose));
	            	updateButtons();
	                return;
            }
            ReversiGame.Move m = game.blackTurn();
//            temp.setText("black ("+m.column+", "+m.row+")");
            
            //skip dark turn if no moves
            if (!m.isValid()) {
            	Log.d(MainActivity.TAG, String.format("Move Invalid"));
            }
            else {
				while (!game.canMove(ReversiGame.WHITE)) {
		            m = game.blackTurn();
		            switch (game.hasWon(ReversiGame.BLACK)) {
		                case 'w':
		                	gameOverAlert(getResources().getString(R.string.game_over_lose));
		                	updateButtons();
		                	return;
		                case 't':
		                	gameOverAlert(getResources().getString(R.string.game_over_tie));
		                	updateButtons();
		                	return;
		                case 'l':
		                	updateHighScore();
		                	gameOverAlert(getResources().getString(R.string.game_over_win));
		                	updateButtons();
		                    return;
		            }
		        }
            }
		}
		updateButtons();
	}
	
	public void updateHighScore() {
		Log.d(MainActivity.TAG, String.format("Updating High Scores"));
    	Map<String, ?> scoreMap = score.getAll();
    	scoreEditor.putFloat(new Date().toString(), (float) ((double)correctCount/(double)totalCount*100.0));
    	if (scoreMap.size() > 10) {
        	Map.Entry<String, ?> smallest = scoreMap.entrySet().iterator().next();
        	for (Map.Entry<String, ?> i: scoreMap.entrySet()) {
        		if (Float.parseFloat(i.getValue().toString()) < Float.parseFloat(smallest.getValue().toString())) {
        			smallest = i;
        		}
        	}
        	scoreEditor.remove(smallest.getKey());
    	}
    	scoreEditor.commit();
	}
	
	public void questionAlert() {
		RadioButton rbtn = (RadioButton) findViewById(correctId);
		String answer = rbtn.getText().toString();
		String title = (wasCorrect)?"Correct!":"Incorrect. :(";
		String message = (wasCorrect)?"Good Job!":"The correct answer was: "+answer;
		final AlertDialog alert = new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("Continue", null).show();
		Button p = alert.getButton(AlertDialog.BUTTON_POSITIVE);
		p.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// go to settings
				setContentView(R.layout.game_board);
				setupButtons();
				makeMove();
				updateButtons();
				alert.dismiss();
			}
			
		});
	}
	
	public void gameOverAlert(String text) {
		AlertDialog alert = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.game_over_title)).setMessage(text).setNegativeButton("View Scores", null).setPositiveButton("Start New Game", null).show();
		Button p = alert.getButton(AlertDialog.BUTTON_POSITIVE);
		Button n = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
		p.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// go to settings
				Intent intent = new Intent(arg0.getContext(),MainActivity.class);
		    	startActivity(intent);
			}
			
		});
		n.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(arg0.getContext(),ScoreActivity.class);
		    	startActivity(intent);
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.game_board, menu);
		return true;
	}
	
	
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    }
	

	private void setupButtons() {
		temp = (TextView) findViewById(R.id.temp);
		board = new ImageButton[ROWS][COLUMNS];
		
		board[0][0] = (ImageButton)findViewById(R.id.but00);
		board[0][1] = (ImageButton)findViewById(R.id.but01);
		board[0][2] = (ImageButton)findViewById(R.id.but02);
		board[0][3] = (ImageButton)findViewById(R.id.but03);
		board[0][4] = (ImageButton)findViewById(R.id.but04);
		board[0][5] = (ImageButton)findViewById(R.id.but05);
		board[0][6] = (ImageButton)findViewById(R.id.but06);
		board[0][7] = (ImageButton)findViewById(R.id.but07);
		
		board[1][0] = (ImageButton)findViewById(R.id.but10);
		board[1][1] = (ImageButton)findViewById(R.id.but11);
		board[1][2] = (ImageButton)findViewById(R.id.but12);
		board[1][3] = (ImageButton)findViewById(R.id.but13);
		board[1][4] = (ImageButton)findViewById(R.id.but14);
		board[1][5] = (ImageButton)findViewById(R.id.but15);
		board[1][6] = (ImageButton)findViewById(R.id.but16);
		board[1][7] = (ImageButton)findViewById(R.id.but17);
		
		board[2][0] = (ImageButton)findViewById(R.id.but20);
		board[2][1] = (ImageButton)findViewById(R.id.but21);
		board[2][2] = (ImageButton)findViewById(R.id.but22);
		board[2][3] = (ImageButton)findViewById(R.id.but23);
		board[2][4] = (ImageButton)findViewById(R.id.but24);
		board[2][5] = (ImageButton)findViewById(R.id.but25);
		board[2][6] = (ImageButton)findViewById(R.id.but26);
		board[2][7] = (ImageButton)findViewById(R.id.but27);
		
		board[3][0] = (ImageButton)findViewById(R.id.but30);
		board[3][1] = (ImageButton)findViewById(R.id.but31);
		board[3][2] = (ImageButton)findViewById(R.id.but32);
		board[3][3] = (ImageButton)findViewById(R.id.but33);
		board[3][4] = (ImageButton)findViewById(R.id.but34);
		board[3][5] = (ImageButton)findViewById(R.id.but35);
		board[3][6] = (ImageButton)findViewById(R.id.but36);
		board[3][7] = (ImageButton)findViewById(R.id.but37);
		
		board[4][0] = (ImageButton)findViewById(R.id.but40);
		board[4][1] = (ImageButton)findViewById(R.id.but41);
		board[4][2] = (ImageButton)findViewById(R.id.but42);
		board[4][3] = (ImageButton)findViewById(R.id.but43);
		board[4][4] = (ImageButton)findViewById(R.id.but44);
		board[4][5] = (ImageButton)findViewById(R.id.but45);
		board[4][6] = (ImageButton)findViewById(R.id.but46);
		board[4][7] = (ImageButton)findViewById(R.id.but47);
	
		board[5][0] = (ImageButton)findViewById(R.id.but50);
		board[5][1] = (ImageButton)findViewById(R.id.but51);
		board[5][2] = (ImageButton)findViewById(R.id.but52);
		board[5][3] = (ImageButton)findViewById(R.id.but53);
		board[5][4] = (ImageButton)findViewById(R.id.but54);
		board[5][5] = (ImageButton)findViewById(R.id.but55);
		board[5][6] = (ImageButton)findViewById(R.id.but56);
		board[5][7] = (ImageButton)findViewById(R.id.but57);
	
		board[6][0] = (ImageButton)findViewById(R.id.but60);
		board[6][1] = (ImageButton)findViewById(R.id.but61);
		board[6][2] = (ImageButton)findViewById(R.id.but62);
		board[6][3] = (ImageButton)findViewById(R.id.but63);
		board[6][4] = (ImageButton)findViewById(R.id.but64);
		board[6][5] = (ImageButton)findViewById(R.id.but65);
		board[6][6] = (ImageButton)findViewById(R.id.but66);
		board[6][7] = (ImageButton)findViewById(R.id.but67);
		
		board[7][0] = (ImageButton)findViewById(R.id.but70);
		board[7][1] = (ImageButton)findViewById(R.id.but71);
		board[7][2] = (ImageButton)findViewById(R.id.but72);
		board[7][3] = (ImageButton)findViewById(R.id.but73);
		board[7][4] = (ImageButton)findViewById(R.id.but74);
		board[7][5] = (ImageButton)findViewById(R.id.but75);
		board[7][6] = (ImageButton)findViewById(R.id.but76);
		board[7][7] = (ImageButton)findViewById(R.id.but77);
		
		for (int i = 0; i < COLUMNS; ++i) {
			for (int j = 0; j < ROWS; ++j) {
				board[i][j].setImageResource(R.drawable.reversi_empty);
			}
		}
		
	}
	
	boolean moveAvailable(int r, int c) {
		for(ReversiGame.Move a: availableMoves) {
			if (a.equals(new ReversiGame.Move(c,r))) return true;
		}
		return false;
	}
	
	private void updateButtons() {
		String display = game.display();
		String[] gb = display.split("\n");
		blackScore = 0;
		whiteScore = 0;
		availableMoves.clear();
        for(int k = 0; k < 8; ++k) {
            for(int n = 0; n < 8; ++n) {
                switch (gb[k].charAt(n)) {
					case WHITE:
						whiteScore++;
						board[n][k].setImageResource(R.drawable.reversi_white);
						break;
					case BLACK:
						blackScore++;
						board[n][k].setImageResource(R.drawable.reversi_black);
						break;
					case MOVE:
						availableMoves.add(new ReversiGame.Move(k,n));
						board[n][k].setImageResource(R.drawable.reversi_move);
						break;
					case EMPTY:
						board[n][k].setImageResource(R.drawable.reversi_empty);
						break;
                }
            }
        }
        temp.setText("White: "+whiteScore+", Black: "+blackScore);
	}
	
	private void setupQuestion() {
		Log.d(MainActivity.TAG,String.format("Setting up question"));
		Random rand = new Random();
		RadioGroup aRadioGroup = (RadioGroup) findViewById(R.id.answersRadioGroup);
		TextView aTextView = (TextView) findViewById(R.id.question);
		aRadioGroup.clearCheck();
		aRadioGroup.removeAllViews();
		Log.d(MainActivity.TAG,String.format("Removed all views from question"));
		QuestionsPullParser.Question q;
		if (questions.size() > 0) { // available question
			Log.d(MainActivity.TAG,String.format("Getting question"));
			int n = rand.nextInt(questions.size());
//			q = questions.get(0); // for debugging
			q = questions.get(n); // picks random question
			Log.d(MainActivity.TAG,String.format("Got question"));
			usedQuestions.add(q);
			Log.d(MainActivity.TAG,String.format("Added question to used List"));
			questions.remove(0);
			Log.d(MainActivity.TAG,String.format("Removed question from new list"));
		}
		else { // no questions, put used list back into question pool
			Log.d(MainActivity.TAG,String.format("questions.size <= 0"));
			questions = usedQuestions;
			usedQuestions.clear();
			q = questions.get(0);
			usedQuestions.add(questions.get(0));
			questions.remove(0);
		}
		Log.d(MainActivity.TAG,String.format("Setting up question view"));
		aTextView.setText(q.q);
		ArrayList<RadioButton> rbtns = new ArrayList<RadioButton>();
		for (int i = 0; i < q.a.size(); ++i) {
			rbtns.add(new RadioButton(this));
			rbtns.get(rbtns.size()-1).setId(i);
			rbtns.get(rbtns.size()-1).setText(q.a.get(i));
			Log.d(MainActivity.TAG,String.format("Adding button with a: %s", q.a.get(i)));
		}
		rbtns.add(new RadioButton(this));
		correctId = q.a.size();
		rbtns.get(rbtns.size()-1).setId(correctId);
		rbtns.get(rbtns.size()-1).setText(q.c);
		
		while (!rbtns.isEmpty()) {
			int n = rand.nextInt(rbtns.size());
			aRadioGroup.addView(rbtns.get(n));
			rbtns.remove(n);
		}
		((RadioButton)aRadioGroup.getChildAt(0)).setChecked(true);
	}
	
	private void populateQuestions() {
		InputStream stream = null;
	    // Instantiate the parser
	    QuestionsPullParser qpp = new QuestionsPullParser();
	    try {
	    	Log.d(MainActivity.TAG,String.format("Beginning parsing questions"));
	        stream = getQuestionsXmlByCategory();
	        questions = qpp.parse(stream);
	        Log.d(MainActivity.TAG,String.format("Finished parsing questions"));
	    } 
	    catch (Exception e) {
	    	Log.d(MainActivity.TAG,String.format("Failed parsing: %s",e.getMessage()));
	    }
	    finally {
	        if (stream != null) {
	            try {
	            	stream.close();
	            }
	            catch (Exception e) {}
	        } 
	    }
	}
	
	@SuppressWarnings("resource")
	private InputStream getQuestionsXmlByCategory() throws IOException {
		File sddir;
		File[] xmls = null;
		if (hasSDCard) {
			sddir = getExternalFilesDir(null);
			xmls = sddir.listFiles();
		}
		InputStream inputStream = null;
		try {
			boolean found = false;
			if (hasSDCard)
				for(File f : xmls) {
					Log.d(MainActivity.TAG,"Found file: " + f.getName());
					if (f.getName().equalsIgnoreCase(category)) {
						inputStream = new FileInputStream(f);
						found = true;
					}
				}
			if (!found) {
				Log.d(MainActivity.TAG,"Using default astronomy file");
				inputStream = this.getResources().openRawResource(R.raw.astronomy);
			}
			else {
				Log.d(MainActivity.TAG,"Found file on sdcard");
			}
				
		} catch (Exception e) {
			Log.d(MainActivity.TAG,"Failed parsing: " + e.getMessage());
		}
	    return inputStream;	
	}
	
	
	public void but00(View v) {
		doMove(0,0);
	}
	
	public void but01(View v) {
		doMove(0,1);
	}
	public void but02(View v) {
		doMove(0,2);
	}
	public void but03(View v) {
		doMove(0,3);
	}
	public void but04(View v) {
		doMove(0,4);
	}
	public void but05(View v) {
		doMove(0,5);
	}
	public void but06(View v) {
		doMove(0,6);
	}
	public void but07(View v) {
		doMove(0,7);
	}
	
	public void but10(View v) {
		doMove(1,0);
	}
	public void but11(View v) {
		doMove(1,1);
	}
	public void but12(View v) {
		doMove(1,2);
	}
	public void but13(View v) {
		doMove(1,3);
	}
	public void but14(View v) {
		doMove(1,4);
	}
	public void but15(View v) {
		doMove(1,5);
	}
	public void but16(View v) {
		doMove(1,6);
	}
	public void but17(View v) {
		doMove(1,7);
	}
	
	public void but20(View v) {
		doMove(2,0);
	}
	public void but21(View v) {
		doMove(2,1);
	}
	public void but22(View v) {
		doMove(2,2);
	}
	public void but23(View v) {
		doMove(2,3);
	}
	public void but24(View v) {
		doMove(2,4);
	}
	public void but25(View v) {
		doMove(2,5);
	}
	public void but26(View v) {
		doMove(2,6);
	}
	public void but27(View v) {
		doMove(2,7);
	}
	
	public void but30(View v) {
		doMove(3,0);
	}
	public void but31(View v) {
		doMove(3,1);
	}
	public void but32(View v) {
		doMove(3,2);
	}
	public void but33(View v) {
		doMove(3,3);
	}
	public void but34(View v) {
		doMove(3,4);
	}
	public void but35(View v) {
		doMove(3,5);
	}
	public void but36(View v) {
		doMove(3,6);
	}
	public void but37(View v) {
		doMove(3,7);
	}
	
	public void but40(View v) {
		doMove(4,0);
	}
	public void but41(View v) {
		doMove(4,1);
	}
	public void but42(View v) {
		doMove(4,2);
	}
	public void but43(View v) {
		doMove(4,3);
	}
	public void but44(View v) {
		doMove(4,4);
	}
	public void but45(View v) {
		doMove(4,5);
	}
	public void but46(View v) {
		doMove(4,6);
	}
	public void but47(View v) {
		doMove(4,7);
	}
	
	public void but50(View v) {
		doMove(5,0);
	}
	public void but51(View v) {
		doMove(5,1);
	}
	public void but52(View v) {
		doMove(5,2);
	}
	public void but53(View v) {
		doMove(5,3);
	}
	public void but54(View v) {
		doMove(5,4);
	}
	public void but55(View v) {
		doMove(5,5);
	}
	public void but56(View v) {
		doMove(5,6);
	}
	public void but57(View v) {
		doMove(5,7);
	}
	
	public void but60(View v) {
		doMove(6,0);
	}
	public void but61(View v) {
		doMove(6,1);
	}
	public void but62(View v) {
		doMove(6,2);
	}
	public void but63(View v) {
		doMove(6,3);
	}
	public void but64(View v) {
		doMove(6,4);
	}
	public void but65(View v) {
		doMove(6,5);
	}
	public void but66(View v) {
		doMove(6,6);
	}
	public void but67(View v) {
		doMove(6,7);
	}
	
	public void but70(View v) {
		doMove(7,0);
	}
	public void but71(View v) {
		doMove(7,1);
	}
	public void but72(View v) {
		doMove(7,2);
	}
	public void but73(View v) {
		doMove(7,3);
	}
	public void but74(View v) {
		doMove(7,4);
	}
	public void but75(View v) {
		doMove(7,5);
	}
	public void but76(View v) {
		doMove(7,6);
	}
	public void but77(View v) {
		doMove(7,7);
	}

}
