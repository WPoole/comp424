package student_player;

import java.util.List;

import boardgame.Board;
import boardgame.Move;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses to
	 * associate you with your agent. The constructor should do nothing else.
	 */
	public StudentPlayer() {
		super("260508650");
	}

	public int evaluationFunction() {
		return 0;
	}
	
	public boolean isLeafState(TablutBoardState boardState) {
		// A leaf state is achieved is the game is over.
		// The game is over when one of two things happen:
		// 1. The king has been captured.
		// 2. The king has reached one of the corners.
		// Both of these things are reflected when the boardState's "winner" variable is no longer "NOBODY".
		if(boardState.getWinner() == Board.NOBODY) {
			return false;
		}
		
		return true;
	}
	
	public int evaluationFunction(TablutBoardState boardState) {
		// We will use a weighted linear function. We will create the function in such a way that
		// the MUSCOVITES (BLACK) will be the ones maximizing it.
		// The features of the function will be:
		// 1. Number of white pieces captured.
		int numWhitePieces = 9 - boardState.getNumberPlayerPieces(1); // 9 is initial number of Swedes.
		
		// 2. Number of black pieces remaining.
		int numBlackPieces = boardState.getNumberPlayerPieces(0);
		
		// 3. Distance of king from nearest corner.
		int distanceOfKingFromNearestCorner = Coordinates.distanceToClosestCorner(boardState.getKingPosition());
		
		// 4. Whether or not the king is DIRECTLY adjacent to a corner.
		// 5. Whether the king is in the center position or in one of the position that neighbors the center.
		// 6. Whether the king (in his current position) has potential to be captured in EXACTLY one more move from black.
		// 7. Whether the king (in his current position) is EXACTLY one move away from reaching a corner.
		
		// Now we compose value to return.
		return ((numWhitePieces * 1) + (numBlackPieces * 1) + (distanceOfKingFromNearestCorner * 1));
		
	}

	public TablutMove miniMaxDecision(int depth, int myColour, TablutBoardState boardState) {
		// Variables to be updated upon finding of better moves.
		int currentHighestValue = Integer.MIN_VALUE;
		TablutMove optimalMove = null;
		
		// Cycle through various subsequent states (i.e. next moves).
		List<TablutMove> legalMoves = boardState.getAllLegalMoves();
		for(TablutMove move : legalMoves) {
			// Clone boardState
			TablutBoardState clonedBoardState = (TablutBoardState) boardState.clone();
			clonedBoardState.processMove(move);
			
			// Need to check if move we performed has brought us to a leaf state.
			boolean isLeafState = isLeafState(clonedBoardState);
			
			int moveValue = miniMaxValue(depth, myColour, clonedBoardState, isLeafState);
			if(moveValue > currentHighestValue) {
				currentHighestValue = moveValue;
				optimalMove = move;
			}

		}
		
		return optimalMove;

		// We will behave as a MAX player if we are BLACK, and as a MIN player if we are WHITE.
	}
	
	public int miniMaxValue(int depth, int myColour, TablutBoardState clonedBoardState, boolean isLeafState) {
		if(depth == 0 || isLeafState) {
			return evaluationFunction(clonedBoardState);
		}
	}

	/**
	 * This is the primary method that you need to implement. The ``boardState``
	 * object contains the current state of the game, which your agent must use to
	 * make decisions.
	 */
	public Move chooseMove(TablutBoardState boardState) {
		// You probably will make separate functions in MyTools.
		// For example, maybe you'll need to load some pre-processed best opening
		// strategies...
		MyTools.getSomething();


		//        *********************************************************************************
		// Find out who is black (0) and who is white (1).
		int opponentColour = boardState.getOpponent();
		int myColour = 1 - opponentColour;

		// Is random the best you can do?
		Move myMove = clonedBoardState.getRandomMove();

		// Return your move to be processed by the server.
		return myMove;
	}
}