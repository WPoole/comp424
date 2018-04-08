package student_player;

import java.util.HashSet;
import java.util.List;

import javax.print.attribute.standard.RequestingUserName;

import boardgame.Board;
import boardgame.Move;
import coordinates.Coord;
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

	public boolean isKingCaptured(TablutBoardState boardState) {
		if(boardState.getKingPosition() == null) {
			return true;
		}

		return false;
	}

	public int isKingInCorner(TablutBoardState boardState, boolean hasKingBeenCaptured) {
		if(!hasKingBeenCaptured) {
			if(Coordinates.isCorner(boardState.getKingPosition())) {
				return 1;
			}
		}

		return 0;
	}

	public int getDistanceOfKingFromAnyCorner(TablutBoardState boardState, boolean hasKingBeenCaptured) {
		if(!hasKingBeenCaptured) {
			Coord kingCoord = boardState.getKingPosition();
			int minManhattanDistanceToAnyCorner = Integer.MAX_VALUE;
			List<Coord> cornerCoords = Coordinates.getCorners();

			for(Coord cornerCoord : cornerCoords) {
				int manhattanDistance = kingCoord.distance(cornerCoord);
				if(manhattanDistance <= minManhattanDistanceToAnyCorner) {
					minManhattanDistanceToAnyCorner = manhattanDistance;
				}
			}

			return minManhattanDistanceToAnyCorner;
		}

		return 0;
	}

	public boolean areTwoCoordsTheSame(Coord a, Coord b) {
		if((a.x == b.x) && (a.y == b.y)) {
			return true;
		}

		return false;
	}

	public boolean isKingAdjacentToBlack(TablutBoardState boardState) {
		HashSet<Coord> blackCoords = boardState.getPlayerPieceCoordinates();
		for(Coord blackCoord : blackCoords) {
			List<Coord> kingNeighbours = Coordinates.getNeighbors(boardState.getKingPosition());
			for(Coord kingNeighbour : kingNeighbours) {
				// If one of the King's neighbors is a black piece, we DON'T want to move here.
				if(areTwoCoordsTheSame(blackCoord, kingNeighbour)) {
					return true;
				}
			}
		}

		return false;
	}

	public int evaluationFunctionBlack(TablutBoardState boardState, int myColour) {
		// Value to return.
		int finalValue = 0;

		// 1. Number of white pieces captured.
		int numWhitePiecesCaptured = 9 - boardState.getNumberPlayerPieces(1); // 9 is initial number of Swedes.
		finalValue += numWhitePiecesCaptured * 1;

		// 2. Number of black pieces remaining.
		int numBlackPiecesRemaining = boardState.getNumberPlayerPieces(0);
		finalValue += numBlackPiecesRemaining * 2;

		// 3. Whether the king is captured or not.
		boolean isKingCaptured = isKingCaptured(boardState);
		if(isKingCaptured) {
			return Integer.MAX_VALUE;
		}
		
		// 4. Distance of king from nearest corner.
		int distanceOfKingFromAnyCorner = getDistanceOfKingFromAnyCorner(boardState, isKingCaptured);
		finalValue += distanceOfKingFromAnyCorner * 1;
		
		// 5. Whether the king has actually reached a corner or not.
		int isKingInCorner = isKingInCorner(boardState, isKingCaptured);
		if(isKingInCorner == 1) { 
			return Integer.MIN_VALUE;
		}

		return finalValue;
	}
	
	public int evaluationFunctionWhite(TablutBoardState boardState, int myColour) {
		// Value to return.
		int finalValue = 0;

		// We will use a weighted linear function.
		// The features of the function will be:
		// 1. Number of white pieces captured.
		int numWhitePiecesRemaining = boardState.getNumberPlayerPieces(1); // 9 is initial number of Swedes.
		finalValue += numWhitePiecesRemaining * 1;
		
		// 2. Number of black pieces remaining.
		int numBlackPiecesCaptured = 16 - boardState.getNumberPlayerPieces(0);
		finalValue += numBlackPiecesCaptured * 2;
		
		// 3. Whether the king is captured or not.
		boolean isKingCaptured = isKingCaptured(boardState);
		// If the King has been captured, we know that its position value will be null. Therefore, any other checks 
		// on the king following this will give null pointer exceptions. Therefore, if the King has been captured,
		// set this flag so that we don't perform the other checks.
		if(isKingCaptured) {
			return Integer.MIN_VALUE;
		}

		// 4. Are we moving King directly adjacent to a black piece.
		boolean isKingAdjacentToBlack = isKingAdjacentToBlack(boardState);
		if(!isKingAdjacentToBlack) {
			finalValue += 500;
		}

		// 5. Distance of king from nearest corner.
		int distanceOfKingFromAnyCorner = getDistanceOfKingFromAnyCorner(boardState, isKingCaptured);
		finalValue -= distanceOfKingFromAnyCorner * 1;
		
		// 6. Whether the king has actually reached a corner or not.
		int isKingInCorner = isKingInCorner(boardState, isKingCaptured);
		if(isKingInCorner == 1) { 
			return Integer.MAX_VALUE;
		}

		return finalValue;
	}

	public TablutMove miniMaxDecisionAB(int depth, int myColour, TablutBoardState boardState, boolean isMaxPlayer) {
		// Variables to be updated upon finding of better moves.
		int a = Integer.MIN_VALUE;
		int b = Integer.MAX_VALUE;
		TablutMove optimalMove = null;

		// Cycle through various subsequent states (i.e. next moves).
		List<TablutMove> legalMoves = boardState.getAllLegalMoves();
		for(TablutMove move : legalMoves) {
			// Clone boardState
			TablutBoardState clonedBoardState = (TablutBoardState) boardState.clone();
			clonedBoardState.processMove(move);

			// Need to check if move we performed has brought us to a leaf state.
			boolean isLeafState = isLeafState(clonedBoardState);

			int moveValue = miniMaxValueAB(depth, myColour, clonedBoardState, isLeafState, false, a, b);

			if(moveValue > a) {
				a = moveValue;
				optimalMove = move;
			}
		}

		return optimalMove;
	}
	

	public int miniMaxValueAB(int depth, int myColour, TablutBoardState clonedBoardState, boolean isLeafState, boolean isMaxPlayer, int a, int b) {
		if(depth == 0 || isLeafState) {
			if(myColour == 0) { // If we are black, use the black evaluation function.
				return evaluationFunctionBlack(clonedBoardState, myColour);
			} else { // If we are white, use the white evaluation function.
				return evaluationFunctionWhite(clonedBoardState, myColour);
			}
		}

		if(isMaxPlayer) { // If I am the max player.
			List<TablutMove> legalMoves = clonedBoardState.getAllLegalMoves();
			for(TablutMove move : legalMoves) {
				TablutBoardState clonedClonedBoardState = (TablutBoardState) clonedBoardState.clone();
				clonedClonedBoardState.processMove(move);

				boolean isCloneLeafState = isLeafState(clonedClonedBoardState);
				
				a = Math.max(a, miniMaxValueAB(depth-1, myColour, clonedClonedBoardState, isCloneLeafState, !isMaxPlayer, a, b));
				if(a >= b) {
					return b;
				}
			}

			return a;

		} else { // Else, I am the min player.
			List<TablutMove> legalMoves = clonedBoardState.getAllLegalMoves();
			for(TablutMove move : legalMoves) {
				TablutBoardState clonedClonedBoardState = (TablutBoardState) clonedBoardState.clone();
				clonedClonedBoardState.processMove(move);

				boolean isCloneLeafState = isLeafState(clonedClonedBoardState);
				
				b = Math.min(b, miniMaxValueAB(depth-1, myColour, clonedClonedBoardState, isCloneLeafState, !isMaxPlayer, a, b));
				if(a >= b) {
					return a;
				}
			}

			return b;
		}
	}
	/**
	 * This is the primary method that you need to implement. The ``boardState``
	 * object contains the current state of the game, which your agent must use to
	 * make decisions.
	 */
	public Move chooseMove(TablutBoardState boardState) {
		MyTools.getSomething();

		// Find out who is black (0) and who is white (1).
		int opponentColour = boardState.getOpponent();
		int myColour = 1 - opponentColour;
		boolean isMaxPlayer = true; // No matter what colour we are, we always want to be the max player.

		// Find move using minimax algorithm.
		TablutMove myMove = miniMaxDecisionAB(2, myColour, boardState, isMaxPlayer);

		// Return your move to be processed by the server.
		return myMove;
	}
}