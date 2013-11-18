package com.csce315501_groupf.project_3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
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
	private String mode;
	
	private boolean hasSDCard;
	
	ReversiGame game;
	
	TextView temp;
	
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		Log.d(MainActivity.TAG, "create game board");
		
		setContentView(R.layout.game_board);
	
		
		Intent intent = getIntent();
		difficulty = intent.getStringExtra(MainActivity.GAME_DIFFICULTY);
		category = intent.getStringExtra(MainActivity.QUESTION_CATEGORY);
		mode = intent.getStringExtra(MainActivity.GAME_MODE);
		hasSDCard = (intent.getIntExtra(MainActivity.SD_CARD, 0) > 0)?true:false;
		
		setupButtons();
		temp = (TextView) findViewById(R.id.temp);
		
		
		Log.d(MainActivity.TAG, "buttons setup");
		
		game = new ReversiGame(difficulty.toLowerCase().charAt(0));
		
		updateButtons();
	}


	public void submitQuestion(View v) {
		setContentView(R.layout.game_board);
		setupButtons();
		updateButtons();
	}
	
	private void doMove(int r, int c) {
		Log.d(MainActivity.TAG,String.format("doing move (%d,%d)",r,c));
//		setContentView(R.layout.activity_questions);
//		temp.setText(String.format("doing move (%d,%d)",r,c));
		if(game.lightTurn(c, r+1)) {
//			temp.setText(String.format("did move (col=%d,row=%d) with result %s",c+1,r+1, success?"G":"B"));
			switch (game.hasWon(ReversiGame.WHITE)) {
	            case 'w':
	            	temp.setText("W");
	            	gameOverAlert(getResources().getString(R.string.game_over_win));
//	            	new AlertDialog.Builder(this).setTitle("Game Over!").setMessage("Congratulations! You won!").setNeutralButton("Close", null).show();  
	                return;
	            case 't':
	            	temp.setText("T");
	            	gameOverAlert(getResources().getString(R.string.game_over_tie));
//	            	new AlertDialog.Builder(this).setTitle("Game Over!").setMessage("You tied!").setNeutralButton("Close", null).show();
	                return;
	            case 'l':
	            	temp.setText("L");
	            	gameOverAlert(getResources().getString(R.string.game_over_lose));
//	            	new AlertDialog.Builder(this).setTitle("Game Over!").setMessage("You lost :(").setNeutralButton("Close", null).show();
	                return;
            }
            ReversiGame.Move m = game.blackTurn();
            temp.setText("black ("+m.column+", "+m.row+")");
            
            //skip dark turn if no moves
            if (!m.isValid()) {
            	temp.setText("black has no moves ("+m.column+", "+m.row+")");
            }
            else {
				while (!game.canMove(ReversiGame.WHITE)) {
		            m = game.blackTurn();
		            temp.setText(m.column + " " + m.row);
		            switch (game.hasWon(ReversiGame.BLACK)) {
		                case 'w':
		                	temp.setText("L");
		                	gameOverAlert(getResources().getString(R.string.game_over_lose));
//		                	new AlertDialog.Builder(this).setTitle("Game Over!").setMessage("You lost :(").setNeutralButton("Close", null).show();
		                    return;
		                case 't':
		                	temp.setText("T");
		                	gameOverAlert(getResources().getString(R.string.game_over_tie));
//		                	new AlertDialog.Builder(this).setTitle("Game Over!").setMessage("You tied!").setNeutralButton("Close", null).show();
		                	return;
		                case 'l':
		                	temp.setText("W");
		                	gameOverAlert(getResources().getString(R.string.game_over_win));
//		                	new AlertDialog.Builder(this).setTitle("Game Over!").setMessage("Congratulations! You won!").setNeutralButton("Close", null).show();
		                    return;
		            }
		        }
            }
		}
		updateButtons();
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
				// TODO Auto-generated method stub
				Intent intent = new Intent(arg0.getContext(),ScoreActivity.class);
		    	startActivity(intent);
			}
			
		});
	}
	
	public void gameUndo(View v) {
		boolean success = game.undo();
		Log.d(MainActivity.TAG, success?"Undo Success":"Undo fail");
		updateButtons();
	}
	
	public void gameRedo(View v) {
		boolean success = game.redo();
		Log.d(MainActivity.TAG, success?"Redo Success":"Redo fail");
		updateButtons();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_board, menu);
		return true;
	}
	
	
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    }
	

	private void setupButtons() {
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
	
	private void updateButtons() {
		String display = game.display();
		String[] gb = display.split("\n"); 
        for(int k = 0; k < 8; ++k) {
            for(int n = 0; n < 8; ++n) {
                switch (gb[k].charAt(n)) {
					case WHITE: 
						board[n][k].setImageResource(R.drawable.reversi_white);
						break;
					case BLACK:
						board[n][k].setImageResource(R.drawable.reversi_black);
						break;
					case MOVE:
						board[n][k].setImageResource(R.drawable.reversi_move);
						break;
					case EMPTY:
						board[n][k].setImageResource(R.drawable.reversi_empty);
						break;
                }
            }
        }
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
