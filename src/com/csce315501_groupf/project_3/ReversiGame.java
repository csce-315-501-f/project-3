package com.csce315501_groupf.project_3;

import java.util.*;

import android.util.Log;

public class ReversiGame {
	private static final int A = 0;
	private static final int B = 1;
	private static final int C = 2;
	private static final int D = 3;
	private static final int E = 4;
	private static final int F = 5;
	private static final int G = 6;
	private static final int H = 7;
	
	private static final int ROWS = 7;
	private static final int COLUMNS = 7;
	
	private static final int ALPHA = Integer.MIN_VALUE;
	private static final int BETA = Integer.MAX_VALUE;
	
	public static final String BLACK = "@";
	public static final String WHITE = "O";
	public static final String EMPTY = "_";
	
//	// Board weights for min-max algorithm
//	// based off of http://mnemstudio.org/ai/game/images/reversi_zones1.gif
	private static final int R1 = 5;   // center
	private static final int R2 = 3;   // center border
	private static final int R3 = 25;  // edges
	private static final int R4 = -10; // corner border
	private static final int R5 = 50;  // corner
	
	class Move {
		int row;
		int column;
		
		Move() {
			this(-1,-1);
		}
		
		Move(int c, int r) {
			row = r;
			column = c;
		}
		
		boolean isValid() {
			return row >= 0 && column >= 0;
		}
	}
	
	class WeightedMove {
		int weight;
		Move move;
		
		WeightedMove() {
			this(0,new Move());
		}
		
		WeightedMove(int w, Move m) {
			weight = w;
			move = m;
		}
	}

	String[][] board;
	Stack<String[][]> boardUndoStates;
	Stack<String[][]> boardRedoStates;
	int[][] boardWeights;
	
	char difficulty;
	
	// default constructor sets difficulty to easy
	public ReversiGame() {
		this('e');
	}
	
	public ReversiGame(char diff) {
		initBoard();
		initBoardWeights();
		boardUndoStates = new Stack<String[][]>();
		boardRedoStates = new Stack<String[][]>();
		difficulty = diff;
	}
	
	private void initBoard() {
		board = new String[8][8];
		for (int row = 0; row <= ROWS; row++) {
	        for (int column = 0; column <= COLUMNS; column++) {
	        	board[column][row] = EMPTY;
	        }
		}
	    board[D][3] = WHITE;
	    board[D][4] = BLACK;
	    board[E][3] = BLACK;
	    board[E][4] = WHITE;
	}
	
	private void initBoardWeights() {
		boardWeights = new int[8][8];
		
		boardWeights[C][2] = R1;
	    boardWeights[C][3] = R1;
	    boardWeights[C][4] = R1;
	    boardWeights[C][5] = R1;
	    boardWeights[D][2] = R1;
	    boardWeights[D][3] = R1;
	    boardWeights[D][4] = R1;
	    boardWeights[D][5] = R1;
	    boardWeights[E][2] = R1;
	    boardWeights[E][3] = R1;
	    boardWeights[E][4] = R1;
	    boardWeights[E][5] = R1;
	    boardWeights[F][2] = R1;
	    boardWeights[F][3] = R1;
	    boardWeights[F][4] = R1;
	    boardWeights[F][5] = R1;

	    /* Region 2
	     *  B3-6
	     *  C2,7
	     *  D2,7
	     *  E2,7
	     *  F2,7
	     *  G3-6
	     */
	    boardWeights[B][2] = R2;
	    boardWeights[B][3] = R2;
	    boardWeights[B][4] = R2;
	    boardWeights[B][5] = R2;
	    boardWeights[C][1] = R2;
	    boardWeights[C][6] = R2;
	    boardWeights[D][1] = R2;
	    boardWeights[D][6] = R2;
	    boardWeights[E][1] = R2;
	    boardWeights[E][6] = R2;
	    boardWeights[F][1] = R2;
	    boardWeights[F][6] = R2;
	    boardWeights[G][2] = R2;
	    boardWeights[G][3] = R2;
	    boardWeights[G][4] = R2;
	    boardWeights[G][5] = R2;

	    /* Region 3
	     *  A3-6
	     *  C0,8
	     *  D0,8
	     *  E0,8
	     *  F0,8
	     *  H3-6
	     */
	    boardWeights[A][2] = R3;
	    boardWeights[A][3] = R3;
	    boardWeights[A][4] = R3;
	    boardWeights[A][5] = R3;
	    boardWeights[C][0] = R3;
	    boardWeights[C][7] = R3;
	    boardWeights[D][0] = R3;
	    boardWeights[D][7] = R3;
	    boardWeights[E][0] = R3;
	    boardWeights[E][7] = R3;
	    boardWeights[F][0] = R3;
	    boardWeights[F][7] = R3;
	    boardWeights[H][2] = R3;
	    boardWeights[H][3] = R3;
	    boardWeights[H][4] = R3;
	    boardWeights[H][5] = R3;

	    /* Region 4
	     *  A2,7
	     *  B0,2,7,8
	     *  G0,2,7,8
	     *  H2,7
	     */
	    boardWeights[A][1] = R4;
	    boardWeights[A][6] = R4;
	    boardWeights[B][0] = R4;
	    boardWeights[B][1] = R4;
	    boardWeights[B][6] = R4;
	    boardWeights[B][7] = R4;
	    boardWeights[G][0] = R4;
	    boardWeights[G][1] = R4;
	    boardWeights[G][6] = R4;
	    boardWeights[G][7] = R4;
	    boardWeights[H][1] = R4;
	    boardWeights[H][6] = R4;

	    /* Region 5
	     *  A0,8
	     *  H0,8
	     */
	     boardWeights[A][0] = R5;
	     boardWeights[A][7] = R5;
	     boardWeights[H][0] = R5;
	     boardWeights[H][7] = R5;
	}

	void saveBoardState() {
		// board state is saved after every DARK turn, so that
	    // the board can be reset to the last time the player was able to play
		String[][] backupBoard = new String[COLUMNS][ROWS];
	    for (int i = 0; i < COLUMNS; ++i) {
	    	for (int j = 0; j < ROWS; ++j) {
	    		backupBoard[i][j] = board[i][j];
	    	}
	    }
		boardUndoStates.push(backupBoard);
	}
	
	boolean lightTurn(int column, int row) {
		// removing undo/redo functionality for now since it's crashing the device
		saveBoardState();
	    boardRedoStates.clear();
	    if (board[column][row-1] == BLACK || board[column][row-1] == WHITE) 
	        return false;
	    board[column][row-1] = WHITE;
	    int flip = 1;
	    if (!doFlipWrapper(column,row, flip)) {
	    	board[column][row-1] = EMPTY;
	        return false;
	    }
	    return true;
	}
	
	boolean doFlip(int x, int y, int xdir, int ydir, int flip) {
		String piece = board[x][y-1];
		String opponent = (piece == WHITE)?BLACK:WHITE;
		
		if (piece == WHITE || piece == BLACK) {
		    x += xdir;
		    y = y + ydir - 1;
		    ArrayList<Move> toFlip = new ArrayList<Move>();
		    boolean isFlip = false;
		    while (0 <= x && x <= ROWS && 0 <= y && y <= COLUMNS) {
		        if (board[x][y] == opponent) {
		            toFlip.add(new Move(x,y));
		        }
		        else if (!toFlip.isEmpty() && board[x][y] == piece) {
		            isFlip = true;
		            break;
		        }
		        else if (board[x][y] == EMPTY) {
		            break;
		        }
	            else if (board[x][y] == piece && toFlip.isEmpty()) {
		            break;
	            }
		        x += xdir;
		        y += ydir;
		    }
		    if (isFlip && flip == 1) {
		        for (int i = 0; i < toFlip.size(); ++i) {
		            board[toFlip.get(i).column][toFlip.get(i).row] = piece;
		        }
		    }
		    return isFlip;
		}
		else {
			return false;
		}
	}
	
	boolean doFlipWrapper(int x, int y, int flip) {
	    boolean[] f = new boolean[8];
	    f[0] = doFlip(x,y,-1,0,flip);
	    f[1] = doFlip(x,y,-1,1,flip);
	    f[2] = doFlip(x,y,-1,-1,flip);
	    f[3] = doFlip(x,y,0,1,flip);
	    f[4] = doFlip(x,y,0,-1,flip);
	    f[5] = doFlip(x,y,1,1,flip);
	    f[6] = doFlip(x,y,1,0,flip);
	    f[7] = doFlip(x,y,1,-1,flip);
	    return f[0] || f[1] || f[2] || f[3] || f[4] || f[5] || f[6] || f[7];
	}

    boolean undo() {
    	if (boardUndoStates.isEmpty()) return false;
    	String[][] backupBoard = new String[COLUMNS][ROWS];
	    for (int i = 0; i < COLUMNS; ++i) {
	    	for (int j = 0; j < ROWS; ++j) {
	    		backupBoard[i][j] = board[i][j];
	    	}
	    }
    	boardRedoStates.push(backupBoard);
    	board = boardUndoStates.pop();
    	return true;
    }
    
	boolean redo() {
		if (boardRedoStates.isEmpty()) return false;
		String[][] backupBoard = new String[COLUMNS][ROWS];
	    for (int i = 0; i < COLUMNS; ++i) {
	    	for (int j = 0; j < ROWS; ++j) {
	    		backupBoard[i][j] = board[i][j];
	    	}
	    }
		boardUndoStates.push(backupBoard);
		board = boardRedoStates.pop();
		return true;
	}

	ArrayList<Move> getMoves(String turn) {
		int flip = 0;
		ArrayList<Move> moves = new ArrayList<Move>();
	    for (int row = 0; row <= ROWS; row++) {
	        for (int column = 0; column <= COLUMNS; column++) {
	            if (board[column][row] != WHITE && board[column][row] != BLACK ) {
	                board[column][row] = turn;
	                if (doFlipWrapper(column,row+1, flip))
	                    moves.add(new Move(column,row+1));
	                board[column][row] = EMPTY;
	            }
	        }
	    }
		return moves;
	}

	int piecesCount(String turn) {
	    int cnt = 0;
	    for (int row = 0; row <= ROWS; row++) {
	        for (int column = 0; column <= COLUMNS; column++) {
	            if (board[column][row] == turn) ++cnt;
	        }
	    }
	    return cnt;
	}
	
	char hasWon(String turn) {
	    // check if no moves are left on the board
		String opponent = (turn==WHITE)?BLACK:WHITE;
		ArrayList<Move> playerMoves = new ArrayList<Move>();
		ArrayList<Move> opponentMoves = new ArrayList<Move>();
	    playerMoves = getMoves(turn);
	    opponentMoves = getMoves(opponent);
	        
	    // if there are no more moves, return the piece count
	    if (playerMoves.isEmpty() && opponentMoves.isEmpty()) {
	        int black = piecesCount(BLACK);
	        int white = piecesCount(WHITE);
	        if (turn == BLACK)
	            if (black > white)
	                return 'w';
	            else if (black == white)
	                return 't';
	            else
	                return 'l';
	        else
	            if (white > black)
	                return 'w';
	            else if (black == white)
	                return 't';
	            else
	                return 'l';
	    }
	    return 'n';
	}

	boolean canMove(String turn) {
	    ArrayList<Move> moves = new ArrayList<Move>();
	    moves = getMoves(turn);
	    return !moves.isEmpty();
	}
	
	Move blackTurn() {
	    ArrayList<Move> blackMoves = getMoves(BLACK);

	    if (blackMoves.isEmpty()) return new Move();

	    Move move = new Move();
	    switch (difficulty) {
		    case 'e':
		        move = findBestMove(true, 1, ALPHA, BETA).move;
		        break;
		    case 'm':
		        move = findBestMove(true, 3, ALPHA, BETA).move;
		        break;
		    case 'h':
		        move = findBestMove(true, 5, ALPHA, BETA).move;
	    }
	    
	    if (!move.isValid()) return move;
	    board[move.column][move.row-1] = BLACK;
	    if (doFlipWrapper(move.column,move.row, 1)) {
	        return move;
	    }
	    else {
	        board[move.column][move.row-1] = EMPTY;
	    }
	    return new Move();
	}
	
	WeightedMove findBestMove(boolean maximizingPlayer, int depth, int alpha, int beta) {
	    String turn = (maximizingPlayer)?BLACK:WHITE;
	    String weightPlayer = (!maximizingPlayer)?BLACK:WHITE;
	    if (depth == 0) return new WeightedMove(getBoardWeight(weightPlayer),new Move());

	    ArrayList<Move> moves = getMoves(turn);
	    String[][] backupBoard = new String[COLUMNS][ROWS];
	    for (int i = 0; i < COLUMNS; ++i) {
	    	for (int j = 0; j < ROWS; ++j) {
	    		backupBoard[i][j] = board[i][j];
	    	}
	    }
//	    System.arraycopy(board, 0, backupBoard, 0, board.length);

	    if (maximizingPlayer) { // BLACK's turn
	        WeightedMove bestAlpha = new WeightedMove();
	        for (int i = 0; i < moves.size(); ++i) {
	            board[moves.get(i).column][moves.get(i).row-1] = BLACK;
	            doFlipWrapper(moves.get(i).column,moves.get(i).row, 1);
	            WeightedMove tempAlpha = findBestMove(false, depth-1, alpha, beta);
	            if (alpha < tempAlpha.weight) {
	                alpha = tempAlpha.weight;
	                tempAlpha.move = moves.get(i);
	                bestAlpha = tempAlpha;
	            }
//	            board = backupBoard;
	    	    for (int k = 0; k < COLUMNS; ++k) {
	    	    	for (int j = 0; j < ROWS; ++j) {
	    	    		board[k][j] = backupBoard[k][j];
	    	    	}
	    	    }
	            if (beta <= alpha) {
	                break; // beta cut-off
	            }
	        }
	        return bestAlpha;
	    }
	    else { // WHITE's turn
	        WeightedMove bestBeta = new WeightedMove();
	        for (int j = 0; j < moves.size(); ++j) {
	            board[moves.get(j).column][moves.get(j).row-1] = WHITE;
	            doFlipWrapper(moves.get(j).column,moves.get(j).row, 1);
	            WeightedMove tempBeta = findBestMove(true, depth-1, alpha, beta);
	            tempBeta.weight = -1 * tempBeta.weight;
	            if (beta > tempBeta.weight) {
	                beta = tempBeta.weight;
	                tempBeta.move = moves.get(j);
	                bestBeta = tempBeta;
	            }
//	            board = backupBoard;
	            for (int i = 0; i < COLUMNS; ++i) {
	    	    	for (int k = 0; k < ROWS; ++k) {
	    	    		board[i][k] = backupBoard[i][k];
	    	    	}
	    	    }
	            if (beta <= alpha) {
	                break;
	            }
	        }
	        return bestBeta;
	    }
	}
	
	int getBoardWeight(String turn) {
	    int weight = 0;
	    for (int i = 0; i <= COLUMNS; ++i){
	        for (int j = 0; j <= ROWS; ++j) {
	            if (board[i][j] == turn) {
	                weight += boardWeights[i][j];
	            }
	        }
	    }
	    return weight;
	}
	
	String display()
	{
	    // initialize the string
	    String ss = "";

//	    vector<vector<char> > boardString(ROWS+1,vector<char>(COLUMNS+1,'.'));
//	    ArrayList< ArrayList<char> > boardString = new ArrayList< ArrayList<char> >();
	    char[][] boardString = new char[8][8];
	    for (int row = 0; row <= ROWS; row++) {
	        for (int column = 0; column <= COLUMNS; column++) {
	            boardString[column][row] = '.';
	        }
	    }
	    
	    // place pieces
		for (int i = 0; i <= ROWS; i++) {
	        for (int j = 0; j <= COLUMNS; j++) {
	            if (board[i][j] == WHITE)
	                boardString[i][j] = 'W';
	            else if (board[i][j] == BLACK)
	                boardString[i][j]= 'B';
	        }
		}
	    
	    // set available moves
	    ArrayList<Move> moves = getMoves(WHITE);
	    for(int i = 0; i < moves.size(); ++i) 
	        boardString[moves.get(i).column][moves.get(i).row-1] = 'M';
	    

	    for(int i = 0; i <= ROWS; i++) {
	        for(int j = 0; j <= COLUMNS; j++) {
	            ss += boardString[i][j];
	        }
	        ss += '\n';
	    }
		return ss;
	}
	
}