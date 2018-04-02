package student_player;

import java.util.List;

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

	public boolean isMaxPlayer(int myColour) {
		if(myColour == 0) {
			return true;
		}

		return false;
	}

	public int isKingCaptured(TablutBoardState boardState) {
		if(boardState.getKingPosition() == null) {
			return 1;
		}

		return 0;
	}

	public int isKingInCorner(TablutBoardState boardState, boolean hasKingBeenCaptured) {
		if(!hasKingBeenCaptured) {
			if(Coordinates.isCorner(boardState.getKingPosition())) {
				return -1;
			}
		}

		return 0;
	}

	public int getDistanceOfKingFromNearestCorner(TablutBoardState boardState, boolean hasKingBeenCaptured) {
		if(!hasKingBeenCaptured) {
			return Coordinates.distanceToClosestCorner(boardState.getKingPosition());
		}

		return 0;
	}

	public int isKingAdjacentToCorner(TablutBoardState boardState, boolean hasKingBeenCaptured) {
		if(!hasKingBeenCaptured) {
			List<Coord> cornerCoords = Coordinates.getCorners();
			for(Coord corner : cornerCoords) {
				List<Coord> coordsAdjacentToCorner = Coordinates.getNeighbors(corner);

				for(Coord cornerAdjacent : coordsAdjacentToCorner) {
					// If the king is NOT in a square adjacent to a corner, return 1 (good for black).
					if(areTwoCoordsTheSame(boardState.getKingPosition(), cornerAdjacent)) {
						return 1;
					}
				}
			}
			
			// If the king is NOT in a square adjacent to a corner, return -1 (good for white).
			return -1;
		}

		return 0;
	}

	// TODO: Complete this method.
	public int isKingOneMoveFromFinish(TablutBoardState boardState) {
		return 0;
	}

	public boolean areTwoCoordsTheSame(Coord a, Coord b) {
		if((a.x == b.x) && (a.y == b.y)) {
			return true;
		}

		return false;
	}

	public int evaluationFunction(TablutBoardState boardState) {
		// Flag to know if King has been captured or not.
		boolean hasKingBeenCaptured = false;

		// We will use a weighted linear function. We will create the function in such a way that
		// the MUSCOVITES (BLACK) will be the ones maximizing it.
		// The features of the function will be:
		// 1. Number of white pieces captured.
		int numWhitePiecesCaptured = 9 - boardState.getNumberPlayerPieces(1); // 9 is initial number of Swedes.

		// 2. Number of black pieces remaining.
		int numBlackPiecesRemaining = boardState.getNumberPlayerPieces(0);

		// 3. Whether the king is captured or not.
		int isKingCaptured = isKingCaptured(boardState);
		// If the King has been captured, we know that its position value will be null. Therefore, any other checks 
		// on the king following this will give null pointer exceptions. Therefore, if the King has been captured,
		// set this flag so that we don't perform the other checks.
		if(isKingCaptured == 1) {
			hasKingBeenCaptured = true;
		}

		// 4. Whether or not the king is DIRECTLY adjacent to a corner.
		// TODO: This isn't quite right. As of right now it prevents us from reaching the corner in most cases.
		int isKingAdjacentToCorner = isKingAdjacentToCorner(boardState, hasKingBeenCaptured);

		// 5. Whether the king is in the center position or in one of the position that neighbors the center.
		// 6. Whether the king (in his current position) has potential to be captured in EXACTLY one more move from black. (Use sandwich function).
		// 7. Whether the king (in his current position) is EXACTLY one move away from reaching a corner.
		int isKingOneMoveFromCorner = isKingOneMoveFromFinish(boardState);

		// 8. Distance of king from nearest corner.
		int distanceOfKingFromNearestCorner = getDistanceOfKingFromNearestCorner(boardState, hasKingBeenCaptured);

		// 9. Whether the king has actually reached a corner or not.
		// TODO: Need to think this one through a bit more, not quite working.
		int isKingInCorner = isKingInCorner(boardState, hasKingBeenCaptured);

		// Now we compose value to return.
		return ((numWhitePiecesCaptured * 1) + (numBlackPiecesRemaining * 1) + (isKingCaptured * 100) 
				+ (distanceOfKingFromNearestCorner * 10) + isKingAdjacentToCorner * 10);

	}

	public TablutMove miniMaxDecision(int depth, int myColour, TablutBoardState boardState, boolean isMaxPlayer) {
		// Variables to be updated upon finding of better moves.
		int currentHighestValue = Integer.MIN_VALUE;
		int currentLowestValue = Integer.MAX_VALUE;
		TablutMove optimalMove = null;

		// Cycle through various subsequent states (i.e. next moves).
		List<TablutMove> legalMoves = boardState.getAllLegalMoves();
		for(TablutMove move : legalMoves) {
			// Clone boardState
			TablutBoardState clonedBoardState = (TablutBoardState) boardState.clone();
			clonedBoardState.processMove(move);

			// Need to check if move we performed has brought us to a leaf state.
			boolean isLeafState = isLeafState(clonedBoardState);

			int moveValue = miniMaxValue(depth, myColour, clonedBoardState, isLeafState, isMaxPlayer);
			System.out.println("MOVE VALUE: " + moveValue);
			if(isMaxPlayer) {
				if(moveValue > currentHighestValue) {
					currentHighestValue = moveValue;
					optimalMove = move;
				}
			} else {
				if(moveValue < currentLowestValue) {
					currentLowestValue = moveValue;
					optimalMove = move;
				}
			}
		}

		if(isMaxPlayer) {
			System.out.println("THE FINAL RETURNED VALUE FOR MOVE IS: " + currentHighestValue);
		} else {
			System.out.println("THE FINAL RETURNED VALUE FOR MOVE IS: " + currentLowestValue);
		}

		return optimalMove;

		// We will behave as a MAX player if we are BLACK, and as a MIN player if we are WHITE.
	}

	public int miniMaxValue(int depth, int myColour, TablutBoardState clonedBoardState, boolean isLeafState, boolean isMaxPlayer) {
		if(depth == 0 || isLeafState) {
			return evaluationFunction(clonedBoardState);
		}

		if(isMaxPlayer) { // If I am the max player.
			int currentHighestValue = Integer.MIN_VALUE;

			List<TablutMove> legalMoves = clonedBoardState.getAllLegalMoves();
			for(TablutMove move : legalMoves) {
				TablutBoardState clonedClonedBoardState = (TablutBoardState) clonedBoardState.clone();
				clonedClonedBoardState.processMove(move);

				boolean isCloneLeafState = isLeafState(clonedClonedBoardState);
				int moveValue = miniMaxValue(depth-1, myColour, clonedClonedBoardState, isCloneLeafState, !isMaxPlayer);

				if(moveValue > currentHighestValue) {
					currentHighestValue = moveValue;
				}
			}

			return currentHighestValue;

		} else { // Else, I am the min player.
			int currentLowestValue = Integer.MAX_VALUE;

			List<TablutMove> legalMoves = clonedBoardState.getAllLegalMoves();
			for(TablutMove move : legalMoves) {
				TablutBoardState clonedClonedBoardState = (TablutBoardState) clonedBoardState.clone();
				clonedClonedBoardState.processMove(move);

				boolean isCloneLeafState = isLeafState(clonedClonedBoardState);
				int moveValue = miniMaxValue(depth-1, myColour, clonedClonedBoardState, isCloneLeafState, !isMaxPlayer);

				if(moveValue < currentLowestValue) {
					currentLowestValue = moveValue;
				}
			}

			return currentLowestValue;
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
		// Find out who is black (0) and who is white (1), and whether I am max player (black) or not.
		int opponentColour = boardState.getOpponent();
		int myColour = 1 - opponentColour;
		boolean isMaxPlayer = isMaxPlayer(myColour);

		// Find move using minimax algorithm.
		TablutMove myMove = miniMaxDecision(2, myColour, boardState, isMaxPlayer);

		//		TablutMove myMove = (TablutMove) boardState.getRandomMove();

		// Return your move to be processed by the server.
		return myMove;


	}
}