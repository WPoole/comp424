package student_player;

import java.util.List;

import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;

public class MyTools {
    public static double getSomething() {
        return Math.random();
    }
    
    public boolean areTwoCoordsTheSame(Coord a, Coord b) {
		if((a.x == b.x) && (a.y == b.y)) {
			return true;
		}

		return false;
	}
    
    public boolean isKingAdjacentOrDiagonallyAdjacentToCornerBoolean(TablutBoardState boardState, boolean hasKingBeenCaptured, int myColour) {
		if(myColour == 0) { // We don't really care about this if we are black.
			return false;
		}

		if(!hasKingBeenCaptured) {
			// First we check if King is adjacent to corner squares.
			Coord kingCoord = boardState.getKingPosition();
			List<Coord> cornerCoords = Coordinates.getCorners();
			for(Coord corner : cornerCoords) {
				List<Coord> coordsAdjacentToCorner = Coordinates.getNeighbors(corner);

				for(Coord cornerAdjacent : coordsAdjacentToCorner) {
					// If the king IS in a square adjacent to a corner, return 2 (good for black).
					if(areTwoCoordsTheSame(kingCoord, cornerAdjacent)) {
						return true;
					}
				}
			}

			// We also then check if the King is in any of the squares that are "diagonally adjacent" to 
			// the corners. i.e. Any of the following coordinates: (x: 1, y: 1), (x: 1, y: 7), (x: 7, y: 1), (x: 7, y: 7).
			if((kingCoord.x == 1 && kingCoord.y == 1) || (kingCoord.x == 1 && kingCoord.y == 7) || 
					(kingCoord.x == 7 && kingCoord.y == 1) || (kingCoord.x == 7 && kingCoord.y == 7)) {
				// If the king IS in a square diagonally adjacent to a corner, return 0 (indifferent for black but not good for white).
				return true;
			}

			// If the king is NOT in a square adjacent or diagonally adjacent to a corner, return -1 (good for white).
			return false;
		}

		return false;
	}

    public int isKingAdjacentOrDiagonallyAdjacentToCorner(TablutBoardState boardState, boolean hasKingBeenCaptured) {
		if(!hasKingBeenCaptured) {
			// First we check if King is adjacent to corner squares.
			Coord kingCoord = boardState.getKingPosition();
			List<Coord> cornerCoords = Coordinates.getCorners();
			for(Coord corner : cornerCoords) {
				List<Coord> coordsAdjacentToCorner = Coordinates.getNeighbors(corner);

				for(Coord cornerAdjacent : coordsAdjacentToCorner) {
					// If the king IS in a square adjacent to a corner, return 2 (good for black).
					if(areTwoCoordsTheSame(kingCoord, cornerAdjacent)) {
						return 1;
					}
				}
			}

			// We also then check if the King is in any of the squares that are "diagonally adjacent" to 
			// the corners. i.e. Any of the following coordinates: (x: 1, y: 1), (x: 1, y: 7), (x: 7, y: 1), (x: 7, y: 7).
			if((kingCoord.x == 1 && kingCoord.y == 1) || (kingCoord.x == 1 && kingCoord.y == 7) || 
					(kingCoord.x == 7 && kingCoord.y == 1) || (kingCoord.x == 7 && kingCoord.y == 7)) {
				// If the king IS in a square diagonally adjacent to a corner, return 0 (indifferent for black but not good for white).
				return 0;
			}

			// If the king is NOT in a square adjacent or diagonally adjacent to a corner, return -1 (good for white).
			return -1;
		}

		return 0;
	}
}
