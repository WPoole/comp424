package student_player;

import java.util.HashSet;
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

	// TODO: Complete this method. (This one might be hard to do / impossible since 
	// we would need to look further ahead in the tree than we currently are).
	public int isKingOneMoveFromFinish(TablutBoardState boardState) {
		return 0;
	}

	public boolean areTwoCoordsTheSame(Coord a, Coord b) {
		if((a.x == b.x) && (a.y == b.y)) {
			return true;
		}

		return false;
	}

	public int isKingAdjacentToBlack(TablutBoardState boardState, boolean hasKingBeenCaptured, int myColour) {
		if(myColour == 1) { // Only want to do this if we are WHITE.
			if(!hasKingBeenCaptured) {
				HashSet<Coord> blackCoords = boardState.getOpponentPieceCoordinates();
				for(Coord blackCoord : blackCoords) {
					List<Coord> kingNeighbours = Coordinates.getNeighbors(boardState.getKingPosition());
					for(Coord kingNeighbour : kingNeighbours) {
						// If one of the King's neighbors is a black piece, we DON'T want to move here.
						if(areTwoCoordsTheSame(blackCoord, kingNeighbour)) {
							return 1;
						}
					}
				}

				// If we get through all of the King's neighbors and none of them are black pieces, it is safe to move here.
				return -1;


			} else { // If the King HAS been captured, we don't care much so return 0.
				return 0;
			}
		} else { // If we are black it doesn't make sense to look at this, so just return 0.
			return 0;
		}
	}

	public int evaluationFunction(TablutBoardState boardState, int myColour) {
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


		// 6. Whether the king (in his current position) has potential to be captured in EXACTLY one more move from black.
		// i.e. Are we moving King directly adjacent to a black piece.
		int isKingAdjacentToBlack = isKingAdjacentToBlack(boardState, hasKingBeenCaptured, myColour);

		// 7. Whether the king (in his current position) is EXACTLY one move away from reaching a corner.
		int isKingOneMoveFromCorner = isKingOneMoveFromFinish(boardState);

		// 8. Distance of king from nearest corner.
		int distanceOfKingFromNearestCorner = getDistanceOfKingFromNearestCorner(boardState, hasKingBeenCaptured);
		int distanceOfKingFromAnyCorner = getDistanceOfKingFromAnyCorner(boardState, hasKingBeenCaptured);

		// 9. Whether the king has actually reached a corner or not.
		int isKingInCorner = isKingInCorner(boardState, hasKingBeenCaptured);

		return ((numWhitePiecesCaptured * 1) + (numBlackPiecesRemaining * 1) + (isKingCaptured * 100) 
				+ (distanceOfKingFromNearestCorner * 10) + (isKingInCorner * 100));

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

			int moveValue = miniMaxValueAB(depth, myColour, clonedBoardState, isLeafState, isMaxPlayer, a, b);
//			System.out.println("MOVE VALUE: " + moveValue);
			if(isMaxPlayer) {
				if(moveValue > a) {
					a = moveValue;
					optimalMove = move;
				}
			} else {
				if(moveValue < b) {
					b = moveValue;
					optimalMove = move;
				}
			}
		}

		if(isMaxPlayer) {
//			System.out.println("THE FINAL RETURNED VALUE FOR MOVE IS: " + a);
		} else {
//			System.out.println("THE FINAL RETURNED VALUE FOR MOVE IS: " + b);
		}

		return optimalMove;
	}

	public int miniMaxValueAB(int depth, int myColour, TablutBoardState clonedBoardState, boolean isLeafState, boolean isMaxPlayer, int a, int b) {
		if(depth == 0 || isLeafState) {
			return evaluationFunction(clonedBoardState, myColour);
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
		// You probably will make separate functions in MyTools.
		// For example, maybe you'll need to load some pre-processed best opening
		// strategies...
		MyTools.getSomething();

		//        *********************************************************************************

		List<Coord> cornerCoords = Coordinates.getCorners();
		for(Coord coord : cornerCoords) {
//			System.out.println("(x: " + coord.x + ", y: " + coord.y + ")");
		}

		// Find out who is black (0) and who is white (1), and whether I am max player (black) or not.
		int opponentColour = boardState.getOpponent();
		int myColour = 1 - opponentColour;
		boolean isMaxPlayer = isMaxPlayer(myColour);

		// Find move using minimax algorithm.
//		TablutMove myMove = miniMaxDecision(2, myColour, boardState, isMaxPlayer);
		TablutMove myMove = miniMaxDecisionAB(2, myColour, boardState, isMaxPlayer);

		// For debugging.
		TablutBoardState copy = (TablutBoardState) boardState.clone();
		copy.processMove(myMove);
//		System.out.println("IS KING ADJACENT TO BLACK: " + isKingAdjacentToBlack(copy, false, myColour));

		// Return your move to be processed by the server.
		return myMove;


	}
}