package com.csce315501_groupf.project_3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameBoard extends Activity {
	
	private static final int ROWS = 8;
	private static final int COLUMNS = 8;
	ImageButton[][] board;;

	private String difficulty;
	private String category;
	private String mode;
	
	TextView temp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		Log.d(MainActivity.TAG, "create game board");
		
		setContentView(R.layout.game_board);
	
		setupButtons();
		temp = (TextView) findViewById(R.id.temp);
		
		
		Log.d(MainActivity.TAG, "buttons setup");
		
		
		if (savedInstanceState == null) {
			//((ImageButton)findViewById(R.id.but00)).setImageResource(R.drawable.reversi_black);
			board[3][4].setImageResource(R.drawable.reversi_black);
			board[4][3].setImageResource(R.drawable.reversi_black);
			board[3][3].setImageResource(R.drawable.reversi_white);
			board[4][4].setImageResource(R.drawable.reversi_white);
		}
		else {
			
		}
	}

	private void doMove(int x, int y) {
		Log.d(MainActivity.TAG,String.format("doing move (%d,%d)",x,y));
		temp.setText(String.format("doing move (%d,%d)",x,y));
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
