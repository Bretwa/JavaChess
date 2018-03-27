/*
ChessRulesMan is a guy who perfectly know all the rules of the chess and can also manage a game
He can also play artificial moves
*/

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

// it extends thread because of use of multithreading 
public class ChessRulesMan extends Thread
{
	public static int infinite=1000000000;	// used to initialize alpha and beta values 
	public int numberOfSquarePerLine=8;	
	ArrayList<Point>[][] pieceMovesKing;
	ArrayList<Point>[][] pieceMovesKnight;
	public Piece[][] pieceMatrix;
	public int noTurn=0;
	public int currentTurn;
	public int whiteTurn=1;
	public int blackTurn=-whiteTurn;
	public int blackIsMat=123567;	// a value superior to all the others to act as an attrctive window
	public int whiteIsMat=-blackIsMat;	

	// local variables for thread computing
	ArrayList<Point> listSourceForThreadComputing;
	ArrayList<Point> listDestinationForThreadComputing;
	int beginSourceDestinationForThreadComputing;
	int endSourceDestinationForThreadComputing;
	int depthForThreadComputing;
	ArrayList<Integer> listValuesForThreadComputing;
	int numberOfEvaluationForThreadComputing=0;
	
	// initial positions of all white pieces
	public final int blackPawnsVerticalPosition=1;
	public final Point leftBlackRookInitialPosition=new Point(0,0);
	public final Point rightBlackRookInitialPosition=new Point(7,0);	
	public final Point leftBlackKnightInitialPosition=new Point(1,0);
	public final Point rightBlackKnightInitialPosition=new Point(6,0);
	public final Point leftBlackBishopInitialPosition=new Point(2,0);
	public final Point rightBlackBishopInitialPosition=new Point(5,0);	
	public final Point blackQueenInitialPosition=new Point(3,0);
	public final Point blackKingInitialPosition=new Point(4,0);
	
	// initial positions of all black pieces
	public final int whitePawnsVerticalPosition=6;
	public final Point leftWhiteRookInitialPosition=new Point(0,7);
	public final Point rightWhiteRookInitialPosition=new Point(7,7);
	public final Point leftWhiteKnightInitialPosition=new Point(1,7);
	public final Point rightWhiteKnightInitialPosition=new Point(6,7);	
	public final Point leftWhiteBishopInitialPosition=new Point(2,7);
	public final Point rightWhiteBishopInitialPosition=new Point(5,7);	
	public final Point whiteQueenInitialPosition=new Point(3,7);	
	public final Point whiteKingInitialPosition=new Point(4,7);
	
	// pieces destinations for white castling
	public final Point shortWhiteCastlingKingDestination=new Point(6,7);
	public final Point shortWhiteCastlingRookDestination=new Point(5,7);
	public final Point longWhiteCastlingKingDestination=new Point(2,7);	
	public final Point longWhiteCastlingRookDestination=new Point(3,7);

	// pieces destinations for black castling
	public final Point shortBlackCastlingKingDestination=new Point(6,0);
	public final Point shortBlackCastlingRookDestination=new Point(5,0);
	public final Point longBlackCastlingKingDestination=new Point(2,0);
	public final Point longBlackCastlingRookDestination=new Point(3,0);
	
	// run a game, initialize
	public void LaunchNewGame()
	{
		currentTurn=whiteTurn;
		InitializeMatrix();
	}
	
	// Evaluate a static position 
	public int EvaluateSituationPositiveForWhite()
	{
		numberOfEvaluationForThreadComputing++;
		int finalResult=0;
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null)
    			{
    				int pieceValue=0;
    				// we have a special treatment for the pawn, the closer it is to the border, the higher its score is
    				if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.pawnId)
    				{
    					if(pieceMatrix[counterVertical][counterHorizontal].InstanceColor==Color.black)
    						pieceValue=Pawn.value+counterVertical;
    					else
    						pieceValue=Pawn.value-counterVertical+numberOfSquarePerLine-1;
    				}
    				else pieceValue=pieceMatrix[counterVertical][counterHorizontal].value;
    				if(pieceMatrix[counterVertical][counterHorizontal].InstanceColor==Color.white)
    					finalResult+=pieceValue;
    				else
    					finalResult-=pieceValue;
    			}

		return finalResult;
	}

	public Piece[][] GetPieceMatrix()
	{
		return pieceMatrix;
	}

	// give the list of piece that can't move without putting the king in chess
	ArrayList<Point> GiveMeListOfNailedPieces()
	{
		ArrayList<Point> listOfCoordinatesForNailedPieces=new ArrayList<Point>();
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
    				pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay()&&
    				pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.kingId)	
    			{
    				Point coordinatesOfPotentialPieceNailed=new Point(-1,-1);
  
    				// top left
      				int counterTop=counterVertical-1;    				
    				for(int counterLeft=counterHorizontal-1;counterLeft>=0&&counterTop>=0;counterLeft--,counterTop--)
    				{
    					if(counterLeft<0||counterTop<0)
    						break;
    					if(pieceMatrix[counterTop][counterLeft]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterTop][counterLeft].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						
    						if(pieceMatrix[counterTop][counterLeft].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterLeft;
	    						coordinatesOfPotentialPieceNailed.y=counterTop;
	    					}

	    					// we add the nailed piece
	    					if(pieceMatrix[counterTop][counterLeft].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterTop][counterLeft].pieceId==Piece.queenId||
		    					pieceMatrix[counterTop][counterLeft].pieceId==Piece.bishopId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}
    				}
    				
    				// top
    				coordinatesOfPotentialPieceNailed.x=-1;
    				for(counterTop=counterVertical-1;counterTop>=0;counterTop--)
    				{
    					if(counterTop<0)
    						break;    					
    					if(pieceMatrix[counterTop][counterHorizontal]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterTop][counterHorizontal].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						if(pieceMatrix[counterTop][counterHorizontal].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterHorizontal;
	    						coordinatesOfPotentialPieceNailed.y=counterTop;
	    					}
	    					// we add the nailed piece
	    					if(pieceMatrix[counterTop][counterHorizontal].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterTop][counterHorizontal].pieceId==Piece.queenId||
		    					pieceMatrix[counterTop][counterHorizontal].pieceId==Piece.rookId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}
    				}    				
    				
       				// top right
    				coordinatesOfPotentialPieceNailed.x=-1;
    				counterTop=counterVertical-1;
    				for(int counterRight=counterHorizontal+1;counterRight<numberOfSquarePerLine&&counterTop>=0;counterRight++,counterTop--)
    				{
    					if(counterRight>=numberOfSquarePerLine||counterTop<0)
    						break;    					
    					if(pieceMatrix[counterTop][counterRight]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterTop][counterRight].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						if(pieceMatrix[counterTop][counterRight].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterRight;
	    						coordinatesOfPotentialPieceNailed.y=counterTop;
	    					}
	    					// we add the nailed piece
	    					if(pieceMatrix[counterTop][counterRight].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterTop][counterRight].pieceId==Piece.queenId||
		    					pieceMatrix[counterTop][counterRight].pieceId==Piece.bishopId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}
    				}    				
    				
       				// right
    				coordinatesOfPotentialPieceNailed.x=-1;
    				for(int counterRight=counterHorizontal+1;counterRight<numberOfSquarePerLine;counterRight++)
    				{
    					if(counterRight>=numberOfSquarePerLine)
    						break;    					
    					if(pieceMatrix[counterVertical][counterRight]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterVertical][counterRight].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						if(pieceMatrix[counterVertical][counterRight].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterRight;
	    						coordinatesOfPotentialPieceNailed.y=counterVertical;
	    					}
	    					// we add the nailed piece
	    					if(pieceMatrix[counterVertical][counterRight].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterVertical][counterRight].pieceId==Piece.queenId||
		    					pieceMatrix[counterVertical][counterRight].pieceId==Piece.rookId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}    	
    				}
    				
       				// bottom right
    				coordinatesOfPotentialPieceNailed.x=-1;
    				int counterBottom=counterVertical+1;
    				for(int counterRight=counterHorizontal+1;counterRight<numberOfSquarePerLine&&counterBottom<numberOfSquarePerLine;counterRight++,counterBottom++)
    				{
    					if(counterRight>=numberOfSquarePerLine||counterBottom>=numberOfSquarePerLine)
    						break;    					
    					if(pieceMatrix[counterBottom][counterRight]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterBottom][counterRight].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						if(pieceMatrix[counterBottom][counterRight].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterRight;
	    						coordinatesOfPotentialPieceNailed.y=counterBottom;
	    					}
  					
	    					// we add the nailed piece
	    					if(pieceMatrix[counterBottom][counterRight].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterBottom][counterRight].pieceId==Piece.queenId||
		    					pieceMatrix[counterBottom][counterRight].pieceId==Piece.bishopId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}
    				}  
    				
    				// bottom
    				coordinatesOfPotentialPieceNailed.x=-1;
    				for(counterBottom=counterVertical+1;counterBottom<numberOfSquarePerLine;counterBottom++)
    				{
    					if(counterBottom>=numberOfSquarePerLine)
    						break;    					
    					if(pieceMatrix[counterBottom][counterHorizontal]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterBottom][counterHorizontal].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						if(pieceMatrix[counterBottom][counterHorizontal].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterHorizontal;
	    						coordinatesOfPotentialPieceNailed.y=counterBottom;
	    					}

	    					// we add the nailed piece
	    					if(pieceMatrix[counterBottom][counterHorizontal].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterBottom][counterHorizontal].pieceId==Piece.queenId||
		    					pieceMatrix[counterBottom][counterHorizontal].pieceId==Piece.rookId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}
    				} 
    				
       				// bottom left
    				coordinatesOfPotentialPieceNailed.x=-1;
    				counterBottom=counterVertical+1;
    				for(int counterLeft=counterHorizontal-1;counterLeft>=0&&counterBottom<numberOfSquarePerLine;counterLeft--,counterBottom++)
    				{
    					if(counterLeft<0||counterBottom>=numberOfSquarePerLine)
    						break;    					
    					if(pieceMatrix[counterBottom][counterLeft]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterBottom][counterLeft].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						if(pieceMatrix[counterBottom][counterLeft].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterLeft;
	    						coordinatesOfPotentialPieceNailed.y=counterBottom;
	    					}

	    					// we add the nailed piece
	    					if(pieceMatrix[counterBottom][counterLeft].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterBottom][counterLeft].pieceId==Piece.queenId||
		    					pieceMatrix[counterBottom][counterLeft].pieceId==Piece.bishopId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}
    				}      	
    				
       				// left
    				coordinatesOfPotentialPieceNailed.x=-1;
    				for(int counterLeft=counterHorizontal-1;counterLeft>=0;counterLeft--)
    				{
    					if(counterLeft<0)
    						break;    					
    					if(pieceMatrix[counterVertical][counterLeft]!=null)
    					{
    						// there are two pieces of same color, break the buckle
	    					if(pieceMatrix[counterVertical][counterLeft].InstanceColor==WhatColorHasToPlay()&&
	    						coordinatesOfPotentialPieceNailed.x!=-1)
	    						break;    						
    						if(pieceMatrix[counterVertical][counterLeft].InstanceColor==WhatColorHasToPlay())
	    					{
	    						coordinatesOfPotentialPieceNailed.x=counterLeft;
	    						coordinatesOfPotentialPieceNailed.y=counterVertical;
	    					}
	    					// we add the nailed piece
	    					if(pieceMatrix[counterVertical][counterLeft].InstanceColor!=WhatColorHasToPlay()&&
		    					coordinatesOfPotentialPieceNailed.x!=-1&&
		    					(pieceMatrix[counterVertical][counterLeft].pieceId==Piece.queenId||
		    					pieceMatrix[counterVertical][counterLeft].pieceId==Piece.rookId))
	    						listOfCoordinatesForNailedPieces.add(new Point(coordinatesOfPotentialPieceNailed.x,coordinatesOfPotentialPieceNailed.y));
    					}    	
    				}
    			}
		
		return listOfCoordinatesForNailedPieces;
	}
	
	// list possible moves for a piece, with nailed pieces encapsulated
	ArrayList<Point> GetListOfPossibleMovesForAPieceWhithChessParameter(Point piecePosition,
			boolean deleteMovesThatWillPutKingInChessNotForKing,boolean isOnChess)
	{
		ArrayList<Point> listOfCoordinatesForNailedPieces=GiveMeListOfNailedPieces();
		Iterator<Point> pointIteratorPiecesNailed=listOfCoordinatesForNailedPieces.iterator();
		while(pointIteratorPiecesNailed.hasNext())
		{
			Point currentPoint=pointIteratorPiecesNailed.next();
			if(currentPoint.x==piecePosition.x&&currentPoint.y==piecePosition.y)
				return GetListOfPossibleMovesForAPiece(piecePosition,deleteMovesThatWillPutKingInChessNotForKing,true,true);
		}		
		// if we are here it means that the piece is not nailed
		return GetListOfPossibleMovesForAPiece(piecePosition,deleteMovesThatWillPutKingInChessNotForKing,isOnChess,true);
	}

	// find the king and analyzed if he is under chess
	public boolean IsKingOnChess()
	{
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
    				pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay()&&
    				pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.kingId)		
    					return IsThisSquareUnderAttack(new Point(counterHorizontal,counterVertical));
		return false;
	}
	
	public void DrawChessBoard()
	{
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    	{
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    		{
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null)
    			{
    				if(pieceMatrix[counterVertical][counterHorizontal].InstanceColor==Color.white)
    				{
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.rookId)
    						System.out.print("wr ");
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.kingId)
    						System.out.print("wK ");    	
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.bishopId)
    						System.out.print("wb ");
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.pawnId)
    						System.out.print("wp ");
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.queenId)
    						System.out.print("wq "); 	
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.knightId)
    						System.out.print("wk "); 	    					
    				}
    				if(pieceMatrix[counterVertical][counterHorizontal].InstanceColor==Color.black)
    				{
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.rookId)
    						System.out.print("br ");
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.kingId)
    						System.out.print("bK ");    	
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.bishopId)
    						System.out.print("bb ");
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.pawnId)
    						System.out.print("bp ");
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.queenId)
    						System.out.print("bq "); 	
    					if(pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.knightId)
    						System.out.print("bk "); 	   					
    				}    				
    			}
    			else
    				System.out.print("XX ");
    			
    		}
    		System.out.println("");
    	}
	}
		
	// alpha beta is the recursive function that compute all the moves
	public int AlphaBeta(int alpha,int beta,int maximumDepth,int currentDepth,int currentTurnParameter,
			ArrayList<Point> sourcePoint,ArrayList<Point> sourceDestination)
	{
		Point bestSource=new Point(-1,-1);
		Point bestDestination=new Point(-1,-1);				
		int val=0;
		int totalPossibleMoves=0;
		currentTurn=currentTurnParameter;
		if(currentDepth==0)
			return EvaluateSituationPositiveForWhite();
		boolean isKingOnChess=IsKingOnChess();
		if(currentTurn==blackTurn)
		{
			int extremum=infinite;
			val=infinite;
	    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
	    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
	    		{
	    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
	    			pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay())
	    			{
	    				ArrayList<Point> listPossibleMoves=GetListOfPossibleMovesForAPieceWhithChessParameter(new Point(counterHorizontal,counterVertical),true,isKingOnChess);
	    				int currentNumberOfPossibleMoves=listPossibleMoves.size();
	    				totalPossibleMoves+=currentNumberOfPossibleMoves;
	    				Iterator<Point> pointIteratorPossibleMoves=listPossibleMoves.iterator();
	    				while(pointIteratorPossibleMoves.hasNext())
	    				{
	    					Point currentDestination=pointIteratorPossibleMoves.next();
	    					ArrayList<Boolean> arrayListIsThisSpecialMove=new ArrayList<Boolean>();
	    					Piece piecePotentialyDeleted=MakeThisMove(new Point(counterHorizontal,counterVertical),currentDestination,arrayListIsThisSpecialMove);
	    					currentTurn=currentTurnParameter;
	    					val=Math.min(val,AlphaBeta(alpha,beta,maximumDepth,currentDepth-1,-currentTurn,sourcePoint,sourceDestination));
	    					UnMakeThisMove(currentDestination,new Point(counterHorizontal,counterVertical),
	    						piecePotentialyDeleted,arrayListIsThisSpecialMove.get(0));
	    					currentTurn=currentTurnParameter;
	    					if((val<extremum)&&currentDepth==maximumDepth)
	    					{
	    						bestSource.x=counterHorizontal;
	    						bestSource.y=counterVertical;
	    						bestDestination.x=currentDestination.x;
	    						bestDestination.y=currentDestination.y;		
	    						extremum=val;
	    					}	    				
	    					if(alpha>=val)
	    						return val;
	    					beta=Math.min(beta,val);
	    				}
	    			}
	    		}
	    	if(totalPossibleMoves==0)
	    	{
	    		if(IsKingOnChess()==false)
	    			return 0;
	      		return blackIsMat;
	    	}
		}
		else if(currentTurn==whiteTurn)
		{
			int extremum=-infinite;			
			val=-infinite;		
	    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
	    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
	    		{
	    			currentTurn=currentTurnParameter;
	    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
	    			pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay())
	    			{
	    				ArrayList<Point> listPossibleMoves=GetListOfPossibleMovesForAPieceWhithChessParameter(new Point(counterHorizontal,counterVertical),true,isKingOnChess);
	    				totalPossibleMoves+=listPossibleMoves.size();
	    				Iterator<Point> pointIteratorPossibleMoves=listPossibleMoves.iterator();
	    				while(pointIteratorPossibleMoves.hasNext())
	    				{
	    					Point currentDestination=pointIteratorPossibleMoves.next();
	    					ArrayList<Boolean> arrayListIsThisSpecialMove=new ArrayList<Boolean>();
	    					Piece piecePotentialyDeleted=MakeThisMove(new Point(counterHorizontal,counterVertical),currentDestination,arrayListIsThisSpecialMove);
	    					currentTurn=currentTurnParameter;
	    					val=Math.max(val,AlphaBeta(alpha,beta,maximumDepth,currentDepth-1,-currentTurn,sourcePoint,sourceDestination));
	    					UnMakeThisMove(currentDestination,new Point(counterHorizontal,counterVertical),
	    						piecePotentialyDeleted,arrayListIsThisSpecialMove.get(0));
	    					currentTurn=currentTurnParameter;	
	    					if((val>extremum)&&currentDepth==maximumDepth)
	    					{
	    						bestSource.x=counterHorizontal;
	    						bestSource.y=counterVertical;
	    						bestDestination.x=currentDestination.x;
	    						bestDestination.y=currentDestination.y;	
	    						extremum=val;
	    					}  		
	    					if(val>=beta)
	    						return val;
	    					alpha=Math.max(alpha,val);
	    				}
	    			}
	    		}	
	    	if(totalPossibleMoves==0)
	    	{
	    		if(IsKingOnChess()==false)
	    			return 0; 
	       		return whiteIsMat;   		
	    	}	    	
		}
		if(currentDepth==maximumDepth)
		{
			sourcePoint.clear();
			sourceDestination.clear();
			sourcePoint.add(bestSource);
			sourceDestination.add(bestDestination);
		}
		return val;
	}
	
	public void listAllPossibleMovesForCurrentPlayer(ArrayList<Point> arrayListSource,ArrayList<Point> arrayListDestination)
	{
		ArrayList<Point> listOfCoordinatesForNailedPieces=GiveMeListOfNailedPieces();
		boolean isKingOnChess=IsKingOnChess();
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    		{
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
    			pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay())
    			{
    				ArrayList<Point> listOfMoveForCurrentPiece=new ArrayList<Point>();
    				Iterator<Point> pointIteratorPiecesNailed=listOfCoordinatesForNailedPieces.iterator();
    				while(pointIteratorPiecesNailed.hasNext())
    				{
    					Point currentPoint=pointIteratorPiecesNailed.next();
    					if(currentPoint.x==counterHorizontal&&currentPoint.y==counterVertical) // the piece is nailed
    						listOfMoveForCurrentPiece=GetListOfPossibleMovesForAPiece(new Point(counterHorizontal,counterVertical),true,isKingOnChess,true);
    				}
    				if(listOfMoveForCurrentPiece.size()==0) // piece is not nailed
    					listOfMoveForCurrentPiece=GetListOfPossibleMovesForAPiece(new Point(counterHorizontal,counterVertical),false,isKingOnChess,true);
    				
    				for(int counterCurrentPossibleMoves=0;counterCurrentPossibleMoves<listOfMoveForCurrentPiece.size();counterCurrentPossibleMoves++)
    					arrayListSource.add(new Point(counterHorizontal,counterVertical));
    				
    				arrayListDestination.addAll(listOfMoveForCurrentPiece);
    			}
    		}
	}

	// the main computation function : each thread will compute  
	public void run()
	{
		int initialCurrentTurn=currentTurn;
		for(int counterMoves=beginSourceDestinationForThreadComputing;counterMoves<endSourceDestinationForThreadComputing;counterMoves++)
		{
			ArrayList<Point> bestSource=new ArrayList<Point>();
			ArrayList<Point> bestDestination=new ArrayList<Point>();
			ArrayList<Boolean> arrayListIsThisSpecialMove=new ArrayList<Boolean>();
			currentTurn=initialCurrentTurn;
			Piece piecePotentialyDeleted=MakeThisMove(listSourceForThreadComputing.get(counterMoves),
					listDestinationForThreadComputing.get(counterMoves),
					arrayListIsThisSpecialMove);
			currentTurn=-initialCurrentTurn;
			int alpha=-infinite;
			int beta=-alpha;
			int returnValue=AlphaBeta(alpha,beta,depthForThreadComputing,depthForThreadComputing,currentTurn,bestSource,bestDestination);
			listValuesForThreadComputing.set(counterMoves,returnValue);
			currentTurn=initialCurrentTurn;
			UnMakeThisMove(listDestinationForThreadComputing.get(counterMoves),
					listSourceForThreadComputing.get(counterMoves),
					piecePotentialyDeleted,arrayListIsThisSpecialMove.get(0));
			currentTurn=initialCurrentTurn;
		}
	}

	// artificial intelligence function : call the recursive function and do the move
	public void PlayComputerAndGetSquaresToBeRefreshed(int depth,ArrayList<Point> squaresToBeRefreshed,
			ArrayList<Point> arrayListSourcePointHistoric,
			ArrayList<Point> arrayListDestinationPointHistoric,
			ArrayList<Boolean> arrayListIsSpecialMoveHistoric,
			ArrayList<Piece> arrayListPiecePotentialyDeletedHistoric) throws InterruptedException
	{
	    Calendar calendar=Calendar.getInstance();
	    long beginingTime=calendar.getTimeInMillis();
	    
	    Point bestSource=new Point(-1,-1);
	    Point bestDestination=new Point(-1,-1);
	    int bestExtremum=0;
	    int extremum=0;
	    
	    ArrayList<Integer> listValues=new ArrayList<Integer>();
	    ArrayList<Point> arrayListSource=new ArrayList<Point>();
	    ArrayList<Point> arrayListDestination=new ArrayList<Point>();	    
	    
	    int numberOfEvaluation=0;
	    int counterExtremum=0;
	    for(int currentDepth=depth;currentDepth>0;currentDepth--)
	    {
	    	if(currentDepth==depth)
	    		listAllPossibleMovesForCurrentPlayer(arrayListSource,arrayListDestination);
	    	else
	    	{
	    	    ArrayList<Point> arrayListSourceTemp=new ArrayList<Point>();
	    	    ArrayList<Point> arrayListDestinationTemp=new ArrayList<Point>();	 
	    		for(int counterSourceDestination=0;counterSourceDestination<arrayListSource.size();counterSourceDestination++)
	    		{
	    			if(listValues.get(counterSourceDestination)==bestExtremum)
	    			{
	    				arrayListSourceTemp.add(new Point(arrayListSource.get(counterSourceDestination).x,
	    					arrayListSource.get(counterSourceDestination).y));
	    				arrayListDestinationTemp.add(new Point(arrayListDestination.get(counterSourceDestination).x,
	    					arrayListDestination.get(counterSourceDestination).y));	    				
	    			}
	    		}
	    		listValues.clear();
	    		arrayListSource.clear();
	    		arrayListDestination.clear();
	    		for(int counterSourceDestinationTemp=0;counterSourceDestinationTemp<arrayListSourceTemp.size();counterSourceDestinationTemp++)
	    		{
	    			arrayListSource.add(new Point(arrayListSourceTemp.get(counterSourceDestinationTemp).x,
	    					arrayListSourceTemp.get(counterSourceDestinationTemp).y));
	    			arrayListDestination.add(new Point(arrayListDestinationTemp.get(counterSourceDestinationTemp).x,
	    					arrayListDestinationTemp.get(counterSourceDestinationTemp).y));	    			
	    		}
	    	}	    	
		    if(arrayListDestination.size()==0)
		    {
		    	System.out.println("Error, no moves possible");
		    	return;
		    }
		    int numberOfCores=Runtime.getRuntime().availableProcessors();
		    if(arrayListDestination.size()<numberOfCores)
		    	numberOfCores=arrayListDestination.size();
		    int remainPossibleMoves=arrayListDestination.size();
		    ArrayList<ChessRulesMan> listChessRulesMan=new  ArrayList<ChessRulesMan>();
		    ArrayList<Thread> listThread=new  ArrayList<Thread>();
		    for(int counterMoves=0;counterMoves<arrayListDestination.size();counterMoves++)
		    	listValues.add(new Integer(0));

		    // for each core available, we create a new thread unless there are less moves than core
		    for(int counterCore=numberOfCores;counterCore>0;counterCore--)
		    {
		    	float currentMovesFloat=remainPossibleMoves/counterCore;
		    	int currentMoves=(int)currentMovesFloat;
		    	if(currentMovesFloat>0)
		    	{
		    		if(currentMoves==0)
		    			currentMoves=remainPossibleMoves;
		    	}
		    	else
		    		break;
		    	remainPossibleMoves=remainPossibleMoves-currentMoves;
		    	ChessRulesMan instanceChessRulesMan=new ChessRulesMan(
		    			currentTurn,pieceMatrix,arrayListSource,arrayListDestination,
		    			remainPossibleMoves,remainPossibleMoves+currentMoves,listValues,currentDepth-1);
		    	listChessRulesMan.add(instanceChessRulesMan);
			    Thread thread=new Thread(instanceChessRulesMan);
			    listThread.add(thread);
			    thread.start();
		    }
		    
		    // we wait all threads done their work and count the number of evaluations
		    for(int counterThread=0;counterThread<numberOfCores;counterThread++)
		    {
		    	 listThread.get(counterThread).join();
		    	 numberOfEvaluation+=listChessRulesMan.get(counterThread).numberOfEvaluationForThreadComputing;
		    }
		    
		    // the work is done now we have to get the best move found 
		    if(currentTurn==blackTurn)
		    	extremum=infinite;
		    else if(currentTurn==whiteTurn)
		    	extremum=-infinite;		    
		    int index=0;    
		    for(int counterValues=0;counterValues<listValues.size();counterValues++)
		    {
		    	if(currentTurn==blackTurn&&listValues.get(counterValues)<extremum)
		    	{
		    		index=counterValues;
		    		extremum=listValues.get(counterValues);
		    	}
		    	if(currentTurn==whiteTurn&&listValues.get(counterValues)>extremum)
		    	{
		    		index=counterValues;
		    		extremum=listValues.get(counterValues);
		    	}
		    }		    

		    // we now have the best extreme index we have to know if there are others
		    for(int counterValues=0;counterValues<listValues.size();counterValues++)
		    	if(listValues.get(counterValues)==extremum)
		    		counterExtremum++;
		    if(currentDepth==depth)
		    {
		    	// we save the index of the best move found, it can't be a bad move
		    	bestExtremum=extremum;
		    	bestSource.x=arrayListSource.get(index).x;
		    	bestSource.y=arrayListSource.get(index).y;
		    	bestDestination.x=arrayListDestination.get(index).x;
		    	bestDestination.y=arrayListDestination.get(index).y;
		    }
		    else
		    {
		    	if(bestExtremum==extremum)
		    	{
			    	bestSource.x=arrayListSource.get(index).x;
			    	bestSource.y=arrayListSource.get(index).y;
			    	bestDestination.x=arrayListDestination.get(index).x;
			    	bestDestination.y=arrayListDestination.get(index).y;
		    	}
		    }
		    
		    if(counterExtremum<2||bestExtremum!=extremum||currentDepth==1)		    
		    {
		    	// here we have found the best move, the we do it and leave
		    	MakeThisMoveAndGetSquaresToBeRefreshed(bestSource,
		    		bestDestination,
					squaresToBeRefreshed,
					arrayListSourcePointHistoric,
					arrayListDestinationPointHistoric,
					arrayListIsSpecialMoveHistoric,
					arrayListPiecePotentialyDeletedHistoric);
		    	break;
		    }
	    }
		calendar=Calendar.getInstance();
		long endTime=calendar.getTimeInMillis();
		/*
		if(currentTurn==blackTurn)
			System.out.println("White player end depth : "+depth+" time : "+
			(endTime-beginingTime)+" evaluations : "+numberOfEvaluation+" eval/s : "+
			numberOfEvaluation/(endTime-beginingTime)*1000+" extremum : "+extremum);
		else
			System.out.println("Black player end depth : "+depth+" time : "+
			(endTime-beginingTime)+" evaluations : "+numberOfEvaluation+" eval/s : "+
			numberOfEvaluation/(endTime-beginingTime)*1000+" extremum : "+extremum);
			*/
	}
	
	// we create 
	public ChessRulesMan(int currentTurnParameter,Piece [][]pieceMatrixParameter,
			ArrayList <Point>listSourceParameter,ArrayList <Point>listDestinationParameter,
			int beginSourceDestinationParameter,int endSourceDestinationParameter,
			ArrayList <Integer>listValuesParameter,int depthParameter)
	{
		InitializeANewInstance();
		beginSourceDestinationForThreadComputing=beginSourceDestinationParameter;
		endSourceDestinationForThreadComputing=endSourceDestinationParameter;
		listValuesForThreadComputing=listValuesParameter;
		currentTurn=currentTurnParameter;
		depthForThreadComputing=depthParameter;
		pieceMatrix=new Piece[numberOfSquarePerLine][numberOfSquarePerLine];
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)			
    			pieceMatrix[counterVertical][counterHorizontal]=pieceMatrixParameter[counterVertical][counterHorizontal];
    	
    	listSourceForThreadComputing=new ArrayList<Point>();
    	listDestinationForThreadComputing=new ArrayList<Point>();
    	for(int counterSourceDestination=0;counterSourceDestination<listSourceParameter.size();counterSourceDestination++)
    	{
    		listSourceForThreadComputing.add(new Point(
    				listSourceParameter.get(counterSourceDestination).x,
    				listSourceParameter.get(counterSourceDestination).y));
    		listDestinationForThreadComputing.add(new Point(
    				listDestinationParameter.get(counterSourceDestination).x,
    				listDestinationParameter.get(counterSourceDestination).y));
    	}
	}
	
	public ChessRulesMan()
	{
		InitializeANewInstance();
	}

	// constructor : we analyze possible move for king and knight, post computing is interesting in theses two cases
	@SuppressWarnings("unchecked")
	public void InitializeANewInstance()
	{
		currentTurn=noTurn;	// at the very beginning, we don't have to know who begins
		pieceMatrix=new Piece[numberOfSquarePerLine][numberOfSquarePerLine];
		
		// we give memory for the king array
		pieceMovesKing=new ArrayList[numberOfSquarePerLine][numberOfSquarePerLine];
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)	
    			pieceMovesKing[counterVertical][counterHorizontal]=new ArrayList<Point>();
    	
    	// we give memory for the knight array
    	pieceMovesKnight=new ArrayList[numberOfSquarePerLine][numberOfSquarePerLine];
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)	
    			pieceMovesKnight[counterVertical][counterHorizontal]=new ArrayList<Point>();
		
		// now we compute the moves for every pieces except paw because it's too much specific
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    		{
   				// knight, 8 moves in total
       			if(counterVertical>1)
       			{
       				if(counterHorizontal>0)
       					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal-1,counterVertical-2));
       				if(counterHorizontal<numberOfSquarePerLine-1)
       					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal+1,counterVertical-2));
       			}
       			if(counterVertical>0)
       			{
       				if(counterHorizontal<numberOfSquarePerLine-2)
       					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal+2,counterVertical-1));
       				if(counterHorizontal>1)
       					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal-2,counterVertical-1));       				
       			}
       			if(counterVertical<numberOfSquarePerLine-1)
       			{
       				if(counterHorizontal<numberOfSquarePerLine-2)
       					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal+2,counterVertical+1));
      				if(counterHorizontal>1)
      					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal-2,counterVertical+1));
       			}
       			if(counterVertical<numberOfSquarePerLine-2)
       			{
       				if(counterHorizontal<numberOfSquarePerLine-1)
       					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal+1,counterVertical+2));
       				if(counterHorizontal>0)
       					pieceMovesKnight[counterVertical][counterHorizontal].add(new Point(counterHorizontal-1,counterVertical+2));
       			}
       			
       			// king, 8 moves in total
       			if(counterHorizontal>0)
       				pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal-1,counterVertical));
       			if(counterVertical>0)
       			{
       				pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal,counterVertical-1));
     				if(counterHorizontal>0)
     					pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal-1,counterVertical-1));
     				if(counterHorizontal<numberOfSquarePerLine-1)
     					pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal+1,counterVertical-1));
       			}
       			if(counterHorizontal<numberOfSquarePerLine-1)
       				pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal+1,counterVertical));
       			if(counterVertical<numberOfSquarePerLine-1)
       			{
       				pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal,counterVertical+1));
       				if(counterHorizontal>0)
       					pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal-1,counterVertical+1));
       				if(counterHorizontal<numberOfSquarePerLine-1)
       					pieceMovesKing[counterVertical][counterHorizontal].add(new Point(counterHorizontal+1,counterVertical+1));
       			}
    		}
	}
	
	// we put the pieces at the right places 
	public void InitializeMatrix()
	{	
		// first of all the pawns
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)		
    			pieceMatrix[counterVertical][counterHorizontal]=null;

		// pawns
		for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    			if(counterVertical==whitePawnsVerticalPosition)
    				pieceMatrix[counterVertical][counterHorizontal]=new Pawn(Color.white);
    			else if(counterVertical==blackPawnsVerticalPosition)
    				pieceMatrix[counterVertical][counterHorizontal]=new Pawn(Color.black);

		// the other pieces with their specific location 
    	pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x]=new Rook(Color.black);
    	pieceMatrix[leftBlackKnightInitialPosition.y][leftBlackKnightInitialPosition.x]=new Knight(Color.black);
    	pieceMatrix[leftBlackBishopInitialPosition.y][leftBlackBishopInitialPosition.x]=new Bishop(Color.black);
    	pieceMatrix[blackQueenInitialPosition.y][blackQueenInitialPosition.x]=new Queen(Color.black);
    	pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x]=new King(Color.black);
    	pieceMatrix[rightBlackBishopInitialPosition.y][rightBlackBishopInitialPosition.x]=new Bishop(Color.black);
    	pieceMatrix[rightBlackKnightInitialPosition.y][rightBlackKnightInitialPosition.x]=new Knight(Color.black);
    	pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x]=new Rook(Color.black);
    	pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x]=new Rook(Color.white);
    	pieceMatrix[leftWhiteKnightInitialPosition.y][leftWhiteKnightInitialPosition.x]=new Knight(Color.white);
   		pieceMatrix[leftWhiteBishopInitialPosition.y][leftWhiteBishopInitialPosition.x]=new Bishop(Color.white);
    	pieceMatrix[whiteQueenInitialPosition.y][whiteQueenInitialPosition.x]=new Queen(Color.white);
    	pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x]=new King(Color.white);
    	pieceMatrix[rightWhiteBishopInitialPosition.y][rightWhiteBishopInitialPosition.x]=new Bishop(Color.white);
    	pieceMatrix[rightWhiteKnightInitialPosition.y][rightWhiteKnightInitialPosition.x]=new Knight(Color.white);
    	pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x]=new Rook(Color.white); 		
	/*
		// castling test
    	pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x]=new Rook(Color.black);
    	pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x]=new King(Color.black);
    	pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x]=new Rook(Color.black);
    	pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x]=new Rook(Color.white);
    	pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x]=new King(Color.white);
    	pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x]=new Rook(Color.white); 	
    	
    /*	// problem with black knight in level 4
    	pieceMatrix[4][0]=pieceMatrix[6][0];
    	pieceMatrix[6][0]=null;
    	pieceMatrix[3][1]=pieceMatrix[6][1];
    	pieceMatrix[6][1]=null;    	
    	pieceMatrix[5][2]=pieceMatrix[6][2];
    	pieceMatrix[6][2]=null;   
    	pieceMatrix[4][3]=pieceMatrix[6][3];
    	pieceMatrix[6][3]=null;      	
    	pieceMatrix[5][4]=pieceMatrix[6][4];
    	pieceMatrix[6][4]=null;     	
    	pieceMatrix[3][4]=pieceMatrix[0][1];
    	pieceMatrix[0][1]=null;        	

	// test pour les mouvements du roi en échec avec des tours black 
    	pieceMatrix[0][6]=new King(Color.white);
    	pieceMatrix[3][4]=new King(Color.black);
    	pieceMatrix[4][7]=new Rook(Color.white); 	
    	pieceMatrix[2][0]=new Rook(Color.white);
    	pieceMatrix[4][0]=new Rook(Color.white);
      	pieceMatrix[0][1]=new Rook(Color.white);
    	pieceMatrix[5][3]=new Pawn(Color.black);
    	
    	// test pour les mouvements du roi en échec avec des tours white
    	pieceMatrix[0][6]=new King(Color.black);
    	pieceMatrix[3][4]=new King(Color.white);
    	pieceMatrix[4][7]=new Rook(Color.black); 	
    	pieceMatrix[2][0]=new Rook(Color.black);
    	pieceMatrix[4][0]=new Rook(Color.black);
    	pieceMatrix[5][3]=new Pawn(Color.white);    	
    	
/*
    	//   basic chess with rook
    	pieceMatrix[1][1]=new King(Color.white);
    	pieceMatrix[4][4]=new King(Color.black);
    	pieceMatrix[3][7]=new Rook(Color.white); 
    	pieceMatrix[5][7]=new Rook(Color.white); 
    	pieceMatrix[3][0]=new Rook(Color.white); 
    	pieceMatrix[5][0]=new Rook(Color.white); 
    	
    	// strong pawn transformation
    	pieceMatrix[1][1]=new King(Color.white);
    	pieceMatrix[4][3]=new King(Color.black);
    	pieceMatrix[3][7]=new Rook(Color.white); 
    	pieceMatrix[5][7]=new Rook(Color.white); 
    	pieceMatrix[2][4]=new Pawn(Color.white);
    	pieceMatrix[1][5]=new Pawn(Color.white);
		
		//   advanced chess with rook
    	pieceMatrix[1][1]=new King(Color.white);
    	pieceMatrix[4][4]=new King(Color.black);
    	pieceMatrix[3][7]=new Rook(Color.white); 
    	pieceMatrix[5][7]=new Rook(Color.white); 
    	pieceMatrix[6][7]=new Rook(Color.white); 

    	// weak pawn transformation
    	pieceMatrix[0][1]=new King(Color.black);
    	pieceMatrix[3][3]=new King(Color.white);
    	pieceMatrix[2][7]=new Rook(Color.black); 
    	pieceMatrix[4][7]=new Rook(Color.black); 
    	pieceMatrix[5][4]=new Pawn(Color.black);	

	/*	
    	pieceMatrix[0][1]=new King(Color.black);
    	pieceMatrix[4][4]=new King(Color.white);  		
    	pieceMatrix[3][3]=new Bishop(Color.white);
    	pieceMatrix[3][4]=new Bishop(Color.white);
    	pieceMatrix[3][5]=new Bishop(Color.white);
    	pieceMatrix[4][5]=new Bishop(Color.white);
    	pieceMatrix[5][5]=new Bishop(Color.white);
    	pieceMatrix[5][4]=new Bishop(Color.white);
    	pieceMatrix[5][3]=new Bishop(Color.white);    	
    	pieceMatrix[4][3]=new Bishop(Color.white);		
/*
    	pieceMatrix[2][2]=new Bishop(Color.white);
    	pieceMatrix[3][2]=new Bishop(Color.white);
    	pieceMatrix[4][2]=new Bishop(Color.white);
    	pieceMatrix[5][2]=new Bishop(Color.white);
    	pieceMatrix[6][2]=new Bishop(Color.white);
    	pieceMatrix[2][6]=new Bishop(Color.white);    	
    	pieceMatrix[3][6]=new Bishop(Color.white);
    	pieceMatrix[4][6]=new Bishop(Color.white);    	
    	pieceMatrix[5][6]=new Bishop(Color.white);
    	pieceMatrix[6][6]=new Bishop(Color.white);    	
    	pieceMatrix[6][5]=new Bishop(Color.white);    	
    	pieceMatrix[6][4]=new Bishop(Color.white);
    	pieceMatrix[6][3]=new Bishop(Color.white);    	
    	pieceMatrix[6][2]=new Bishop(Color.white);
    	pieceMatrix[2][3]=new Bishop(Color.white);    	
    	pieceMatrix[2][4]=new Bishop(Color.white);
    	pieceMatrix[2][5]=new Bishop(Color.white);    	
*/
    	/*
    	pieceMatrix[1][1]=new Queen(Color.black);
    	pieceMatrix[1][2]=new Queen(Color.black);    	
    	pieceMatrix[1][3]=new Queen(Color.black);
    	pieceMatrix[1][4]=new Queen(Color.black);   
    	pieceMatrix[1][5]=new Queen(Color.black);
    	pieceMatrix[1][6]=new Queen(Color.black);       	
    	pieceMatrix[1][7]=new Queen(Color.black);
    	pieceMatrix[2][7]=new Queen(Color.black);    	
    	pieceMatrix[2][7]=new Queen(Color.black);
    	pieceMatrix[3][7]=new Queen(Color.black);   
    	pieceMatrix[4][7]=new Queen(Color.black);
    	pieceMatrix[5][7]=new Queen(Color.black);
    	pieceMatrix[6][7]=new Queen(Color.black);
    	pieceMatrix[7][7]=new Queen(Color.black);
    	pieceMatrix[7][6]=new Queen(Color.black);
    	pieceMatrix[7][5]=new Queen(Color.black);   
    	pieceMatrix[7][4]=new Queen(Color.black);
    	pieceMatrix[7][3]=new Queen(Color.black);       	
    	pieceMatrix[7][2]=new Queen(Color.black);
    	pieceMatrix[7][1]=new Queen(Color.black);    	
    	pieceMatrix[6][1]=new Queen(Color.black);
    	pieceMatrix[5][1]=new Queen(Color.black);   
    	pieceMatrix[4][1]=new Queen(Color.black);
    	pieceMatrix[3][1]=new Queen(Color.black);
    	pieceMatrix[2][1]=new Queen(Color.black);    
  	
    	pieceMatrix[1][1]=new Rook(Color.black);
    	pieceMatrix[1][2]=new Rook(Color.black);    	
    	pieceMatrix[1][3]=new Rook(Color.black);
    	pieceMatrix[1][4]=new Rook(Color.black);   
    	pieceMatrix[1][5]=new Rook(Color.black);
    	pieceMatrix[1][6]=new Rook(Color.black);       	
    	pieceMatrix[1][7]=new Rook(Color.black);
    	pieceMatrix[2][7]=new Rook(Color.black);    	
    	pieceMatrix[2][7]=new Rook(Color.black);
    	pieceMatrix[3][7]=new Rook(Color.black);   
    	pieceMatrix[4][7]=new Rook(Color.black);
    	pieceMatrix[5][7]=new Rook(Color.black);
    	pieceMatrix[6][7]=new Rook(Color.black);
    	pieceMatrix[7][7]=new Rook(Color.black);
    	pieceMatrix[7][6]=new Rook(Color.black);
    	pieceMatrix[7][5]=new Rook(Color.black);   
    	pieceMatrix[7][4]=new Rook(Color.black);
    	pieceMatrix[7][3]=new Rook(Color.black);       	
    	pieceMatrix[7][2]=new Rook(Color.black);
    	pieceMatrix[7][1]=new Rook(Color.black);    	
    	pieceMatrix[6][1]=new Rook(Color.black);
    	pieceMatrix[5][1]=new Rook(Color.black);   
    	pieceMatrix[4][1]=new Rook(Color.black);
    	pieceMatrix[3][1]=new Rook(Color.black);
    	pieceMatrix[2][1]=new Rook(Color.black);   
    	*/
	}
	
	// retrieve possible moves for a specific piece into a specific pieces matrix
	public ArrayList<Point> GetListOfPossibleMovesForAPiece(
			Point pointSource,boolean deleteMovesThatWillPutKingInChessNotForKing,
			boolean isKingOnChess,boolean deleteMovesThatWillPutKingInChessForKing)
	{
		ArrayList<Point> currentPossibleMoves=new ArrayList<Point>();
		
		// particular case of pawn
		if(pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.pawnId)
		{
			if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==Color.black&&pointSource.y<numberOfSquarePerLine-1)	// black
			{
				if(pieceMatrix[pointSource.y+1][pointSource.x]==null)
				{
					currentPossibleMoves.add(new Point(pointSource.x,pointSource.y+1));	// move one square beyond 
					if(pointSource.y==1&&pieceMatrix[pointSource.y+2][pointSource.x]==null)
						currentPossibleMoves.add(new Point(pointSource.x,pointSource.y+2));	// move two squares beyond
				}
				if(pointSource.x>0&&pieceMatrix[pointSource.y+1][pointSource.x-1]!=null&&
						pieceMatrix[pointSource.y+1][pointSource.x-1].InstanceColor==Color.white)
					currentPossibleMoves.add(new Point(pointSource.x-1,pointSource.y+1));	// eat left
				if(pointSource.x<numberOfSquarePerLine-1&&pieceMatrix[pointSource.y+1][pointSource.x+1]!=null&&
						pieceMatrix[pointSource.y+1][pointSource.x+1].InstanceColor==Color.white)
					currentPossibleMoves.add(new Point(pointSource.x+1,pointSource.y+1)); // eat right
			}
			if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==Color.white&&pointSource.y>0)	// white
			{
				if(pieceMatrix[pointSource.y-1][pointSource.x]==null)
				{
					currentPossibleMoves.add(new Point(pointSource.x,pointSource.y-1));	// move one square beyond 
					if(pointSource.y==6&&pieceMatrix[pointSource.y-2][pointSource.x]==null)
						currentPossibleMoves.add(new Point(pointSource.x,pointSource.y-2));	// move two squares beyond
				}
				if(pointSource.x>0&&pieceMatrix[pointSource.y-1][pointSource.x-1]!=null&&
						pieceMatrix[pointSource.y-1][pointSource.x-1].InstanceColor==Color.black)
					currentPossibleMoves.add(new Point(pointSource.x-1,pointSource.y-1));	// eat left
				if(pointSource.x<numberOfSquarePerLine-1&&pieceMatrix[pointSource.y-1][pointSource.x+1]!=null&&
						pieceMatrix[pointSource.y-1][pointSource.x+1].InstanceColor==Color.black)
					currentPossibleMoves.add(new Point(pointSource.x+1,pointSource.y-1)); // eat right
			}
		}
		
		// case of knight
		if(pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.knightId)
		{
			Iterator<Point> pointIterator=pieceMovesKnight[pointSource.y][pointSource.x].iterator();
			while(pointIterator.hasNext())
			{
				Point currentPoint=pointIterator.next();
				if((pieceMatrix[currentPoint.y][currentPoint.x]==null||
					((pieceMatrix[currentPoint.y][currentPoint.x]!=null)&&
					pieceMatrix[currentPoint.y][currentPoint.x].InstanceColor!=
					pieceMatrix[pointSource.y][pointSource.x].InstanceColor)))
					currentPossibleMoves.add(new Point(currentPoint.x,currentPoint.y));	// simple move or eat
			}
		}
		
		// case of king
		if(pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.kingId)
		{
			Iterator<Point> pointIterator=pieceMovesKing[pointSource.y][pointSource.x].iterator();
			while(pointIterator.hasNext())
			{
				Point currentPoint=pointIterator.next();
				if((pieceMatrix[currentPoint.y][currentPoint.x]==null||
					((pieceMatrix[currentPoint.y][currentPoint.x]!=null)&&
					pieceMatrix[currentPoint.y][currentPoint.x].InstanceColor!=
					pieceMatrix[pointSource.y][pointSource.x].InstanceColor)))
						currentPossibleMoves.add(new Point(currentPoint.x,currentPoint.y));	// simple move or eat
			}
			
			if(currentTurn==whiteTurn&&deleteMovesThatWillPutKingInChessNotForKing==true&&isKingOnChess==false)
			{
				// castling is potentially possible here, we have to check if tower and king are in good position
				if(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x]!=null&&
					pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].pieceId==Piece.kingId&&
					pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].InstanceColor==Color.white&&
					pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x]!=null&&
					pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x].pieceId==Piece.rookId&&
					pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x].InstanceColor==Color.white&&
					pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x]==null&&
					pieceMatrix[shortWhiteCastlingKingDestination.y][shortWhiteCastlingKingDestination.x]==null&&
					((Rook)(pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x])).doIMoved==false&&
					((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved==false)
				{
					// we verify that all squares are not in check
					boolean isInitialPositionUnderAttack=IsThisSquareUnderAttack(new Point(whiteKingInitialPosition.x,whiteKingInitialPosition.y));
					boolean isFirstRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(whiteKingInitialPosition.x+1,whiteKingInitialPosition.y));
					boolean isDestinationRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(shortWhiteCastlingKingDestination.x,shortWhiteCastlingKingDestination.y));		
					if(isInitialPositionUnderAttack==false&&
					isFirstRightPositionUnderAttak==false&&
					isDestinationRightPositionUnderAttak==false)
						currentPossibleMoves.add(new Point(shortWhiteCastlingKingDestination.x,shortWhiteCastlingKingDestination.y));
				}
			}
			else if(currentTurn==blackTurn&&deleteMovesThatWillPutKingInChessNotForKing==true&&isKingOnChess==false)
			{
				// castling is potentially possible here, we have to check if tower and king are in good position
				if(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x]!=null&&
					pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].pieceId==Piece.kingId&&
					pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].InstanceColor==Color.black&&
					pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x]!=null&&
					pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x].pieceId==Piece.rookId&&
					pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x].InstanceColor==Color.black&&
					pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x]==null&&
					pieceMatrix[shortBlackCastlingKingDestination.y][shortBlackCastlingKingDestination.x]==null&&
					((Rook)(pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x])).doIMoved==false&&
					((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved==false)
				{
					// we verify that all squares are not in check
					boolean isInitialPositionUnderAttack=IsThisSquareUnderAttack(new Point(blackKingInitialPosition.x,blackKingInitialPosition.y));
					boolean isFirstRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(blackKingInitialPosition.x+1,blackKingInitialPosition.y));
					boolean isDestinationRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(shortBlackCastlingKingDestination.x,shortBlackCastlingKingDestination.y));		
					if(isInitialPositionUnderAttack==false&&
					isFirstRightPositionUnderAttak==false&&
					isDestinationRightPositionUnderAttak==false)
						currentPossibleMoves.add(new Point(shortBlackCastlingKingDestination.x,shortBlackCastlingKingDestination.y));
				}
			}
			if(currentTurn==whiteTurn&&deleteMovesThatWillPutKingInChessNotForKing==true&&isKingOnChess==false)
			{
				// castling is potentially possible here, we have to check if tower and king are in good position
				if(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x]!=null&&
					pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].pieceId==Piece.kingId&&
					pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].InstanceColor==Color.white&&
					pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x]!=null&&
					pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x].pieceId==Piece.rookId&&
					pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x].InstanceColor==Color.white&&
					pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x]==null&&
					pieceMatrix[longWhiteCastlingKingDestination.y][longWhiteCastlingKingDestination.x]==null&&
					((Rook)(pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x])).doIMoved==false&&
					((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved==false)
				{
					// we verify that all squares are not in check
					boolean isInitialPositionUnderAttack=IsThisSquareUnderAttack(new Point(whiteKingInitialPosition.x,whiteKingInitialPosition.y));
					boolean isFirstRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(whiteKingInitialPosition.x-1,whiteKingInitialPosition.y));
					boolean isDestinationRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(longWhiteCastlingKingDestination.x,longWhiteCastlingKingDestination.y));		
					if(isInitialPositionUnderAttack==false&&
					isFirstRightPositionUnderAttak==false&&
					isDestinationRightPositionUnderAttak==false)
						currentPossibleMoves.add(new Point(longWhiteCastlingKingDestination.x,longWhiteCastlingKingDestination.y));
				}
			}
			else if(currentTurn==blackTurn&&deleteMovesThatWillPutKingInChessNotForKing==true&&isKingOnChess==false)
			{
				// castling is potentially possible here, we have to check if tower and king are in good position
				if(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x]!=null&&
					pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].pieceId==Piece.kingId&&
					pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].InstanceColor==Color.black&&
					pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x]!=null&&
					pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x].pieceId==Piece.rookId&&
					pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x].InstanceColor==Color.black&&
					pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x]==null&&
					pieceMatrix[longBlackCastlingKingDestination.y][longBlackCastlingKingDestination.x]==null&&
					((Rook)(pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x])).doIMoved==false&&
					((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved==false)
				{
					// we verify that all squares are not in check
					boolean isInitialPositionUnderAttack=IsThisSquareUnderAttack(new Point(blackKingInitialPosition.x,blackKingInitialPosition.y));
					boolean isFirstRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(blackKingInitialPosition.x-1,blackKingInitialPosition.y));
					boolean isDestinationRightPositionUnderAttak=IsThisSquareUnderAttack(new Point(longBlackCastlingKingDestination.x,longBlackCastlingKingDestination.y));		
					if(isInitialPositionUnderAttack==false&&
					isFirstRightPositionUnderAttak==false&&
					isDestinationRightPositionUnderAttak==false)
						currentPossibleMoves.add(new Point(longBlackCastlingKingDestination.x,longBlackCastlingKingDestination.y));
				}
			}			
			if(deleteMovesThatWillPutKingInChessForKing==true)
				DeleteKingMovesThatWillPutIntoChess(pointSource,currentPossibleMoves);
			return currentPossibleMoves;
		}		
		
		// rook and queen
		if(pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.rookId||
			pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.queenId)
		{
			// bottom
			for(int counterBottom=pointSource.y+1;counterBottom<numberOfSquarePerLine;counterBottom++)
			{
				if(pieceMatrix[counterBottom][pointSource.x]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[counterBottom][pointSource.x].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(pointSource.x,counterBottom));
						break;
					}
				currentPossibleMoves.add(new Point(pointSource.x,counterBottom));
			}
			
			// left
			for(int counterLeft=pointSource.x-1;counterLeft>=0;counterLeft--)
			{
				if(pieceMatrix[pointSource.y][counterLeft]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[pointSource.y][counterLeft].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(counterLeft,pointSource.y));
						break;
					}
				currentPossibleMoves.add(new Point(counterLeft,pointSource.y));
			}
				
			// top
			for(int counterTop=pointSource.y-1;counterTop>=0;counterTop--)
			{
				if(pieceMatrix[counterTop][pointSource.x]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[counterTop][pointSource.x].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(pointSource.x,counterTop));
						break;
					}
				currentPossibleMoves.add(new Point(pointSource.x,counterTop));
			}		
			
			// right
			for(int counterRight=pointSource.x+1;counterRight<numberOfSquarePerLine;counterRight++)
			{
				if(pieceMatrix[pointSource.y][counterRight]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[pointSource.y][counterRight].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(counterRight,pointSource.y));
						break;
					}
				currentPossibleMoves.add(new Point(counterRight,pointSource.y));
			}
		}
		
		if(pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.bishopId||
				pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.queenId)
		{
			// left top
			int counterLeft=pointSource.x-1;
			for(int counterTop=pointSource.y-1;counterTop>=0&&counterLeft>=0;counterTop--,counterLeft--)
			{
				if(pieceMatrix[counterTop][counterLeft]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[counterTop][counterLeft].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(counterLeft,counterTop));
						break;
					}
				currentPossibleMoves.add(new Point(counterLeft,counterTop));
			}
			
			// right top
			int counterRight=pointSource.x+1;
			for(int counterTop=pointSource.y-1;counterTop>=0&&counterRight<numberOfSquarePerLine;counterTop--,counterRight++)
			{
				if(pieceMatrix[counterTop][counterRight]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[counterTop][counterRight].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(counterRight,counterTop));
						break;
					}
				currentPossibleMoves.add(new Point(counterRight,counterTop));
			}						
			
			// right bottom
			counterRight=pointSource.x+1;
			for(int counterBottom=pointSource.y+1;counterBottom<numberOfSquarePerLine&&counterRight<numberOfSquarePerLine;counterBottom++,counterRight++)
			{
				if(pieceMatrix[counterBottom][counterRight]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[counterBottom][counterRight].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(counterRight,counterBottom));
						break;
					}
				currentPossibleMoves.add(new Point(counterRight,counterBottom));
			}	
			
			// left bottom
			counterLeft=pointSource.x-1;
			for(int counterBottom=pointSource.y+1;counterBottom<numberOfSquarePerLine&&counterLeft>=0;counterBottom++,counterLeft--)
			{
				if(pieceMatrix[counterBottom][counterLeft]!=null)
					if(pieceMatrix[pointSource.y][pointSource.x].InstanceColor==
						pieceMatrix[counterBottom][counterLeft].InstanceColor)
						break;
					else
					{
						currentPossibleMoves.add(new Point(counterLeft,counterBottom));
						break;
					}
				currentPossibleMoves.add(new Point(counterLeft,counterBottom));
			}		
		}
	
		// if needed we delete the moves that put king in check
		if(isKingOnChess==true&&pieceMatrix[pointSource.y][pointSource.x].pieceId!=Piece.kingId)
			DeleteMovesThatWillPutIntoChess(pointSource,currentPossibleMoves);
		
		return currentPossibleMoves;
	}

	public void DeleteKingMovesThatWillPutIntoChess(Point pointSource,ArrayList<Point> currentPossibleMoves)
	{
		ArrayList<Point> currentMovesThatHaveToBeDeleted=new ArrayList<Point>();
		Iterator<Point> pointIteratorCurrentPossibleMoves=currentPossibleMoves.iterator();
		
		// in the case of king move, we have to check if this move will still put in check
		if(pieceMatrix[pointSource.y][pointSource.x].pieceId==Piece.kingId)
		{
			while(pointIteratorCurrentPossibleMoves.hasNext())
			{
				// we simulate each move and look if king is still in check, if case we delete this move as possible
				Point currentPoint=pointIteratorCurrentPossibleMoves.next();
				ArrayList<Boolean> arrayListIsThisSpecialMove=new ArrayList<Boolean>();
				Piece piecePotentialyDeleted=MakeThisMove(pointSource,currentPoint,arrayListIsThisSpecialMove);
				ArrayList<Point> listAllThePossibleMoves=ListAllThePossibleMovesForCurrentPlayerWithoutCheckChecking();
				Iterator<Point> pointIteratorNextPossibleMoves=listAllThePossibleMoves.iterator();
				while(pointIteratorNextPossibleMoves.hasNext())
				{
					Point nextPoint=pointIteratorNextPossibleMoves.next();
					if(nextPoint.x==currentPoint.x&&nextPoint.y==currentPoint.y)	
						currentMovesThatHaveToBeDeleted.add(currentPoint);
				}
				// after moving the piece we put it into last position
				UnMakeThisMove(currentPoint,pointSource,piecePotentialyDeleted,arrayListIsThisSpecialMove.get(0));
			}
		}
		else
		{
			// here we move another piece that the king, we have to verify if the king is still under check
			Point kingPosition=new Point(); // first of all we get the king coordinates
	    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
	    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
	    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
	    				pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.kingId&&
	    				pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay())
	    			{
	    				kingPosition.x=counterHorizontal;
	    				kingPosition.y=counterVertical;
	    			}
			
	    	// we look at every possible moves for the current piece
			while(pointIteratorCurrentPossibleMoves.hasNext())
			{
				Point currentPoint=pointIteratorCurrentPossibleMoves.next();
				
				 // we simulate the move
				ArrayList<Boolean> arrayListIsThisSpecialMove=new ArrayList<Boolean>();
				Piece piecePotentialyDeleted=MakeThisMove(pointSource,currentPoint,arrayListIsThisSpecialMove);
				
				// we retrieve all moves possible for the opponent and look if he can still put the current king in check 
				ArrayList<Point> listAllThePossibleMoves=ListAllThePossibleMovesForCurrentPlayerWithoutCheckChecking();
				Iterator<Point> pointIteratorNextPossibleMoves=listAllThePossibleMoves.iterator();
				while(pointIteratorNextPossibleMoves.hasNext())
				{
					Point nextPoint=pointIteratorNextPossibleMoves.next();
					if(nextPoint.x==kingPosition.x&&nextPoint.y==kingPosition.y)	// king still in check, delete the move
						currentMovesThatHaveToBeDeleted.add(currentPoint);
				}
				
				// undo the move to test others potential 
				UnMakeThisMove(currentPoint,pointSource,piecePotentialyDeleted,arrayListIsThisSpecialMove.get(0));
			}
		}
		
		// now we delete the move in the arraylist
		Iterator<Point> pointIteratorCurrentMovesThatHaveToBeDeleted=currentMovesThatHaveToBeDeleted.iterator();
		while(pointIteratorCurrentMovesThatHaveToBeDeleted.hasNext())
		{
			Point currentPointToBeDelete=pointIteratorCurrentMovesThatHaveToBeDeleted.next();
			pointIteratorCurrentPossibleMoves=currentPossibleMoves.iterator();
			while(pointIteratorCurrentPossibleMoves.hasNext())
			{
				Point currentPointPossibleMove=pointIteratorCurrentPossibleMoves.next();
				if(currentPointToBeDelete.x==currentPointPossibleMove.x&&
					currentPointToBeDelete.y==currentPointPossibleMove.y)
				{
					currentPossibleMoves.remove(currentPointPossibleMove);
					break;
				}
			}
		}
	}

	// we list every moves of the current player, with a chess checking, useful to know if a player is mat
	public ArrayList<Point> ListAllThePossibleMovesForCurrentPlayerWithCheckCheking()
	{
		ArrayList<Point> listAllThePossibleMoves=new ArrayList<Point>();
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
    			pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay())	
    				listAllThePossibleMoves.addAll(GetListOfPossibleMovesForAPiece(new Point(counterHorizontal,counterVertical),true,IsKingOnChess(),true));
		return listAllThePossibleMoves;
	}

	// we list every moves of the current player, without a chess checking, useful to project if the opponent's king is still mate, there is no need to considerate its own king check 
	public ArrayList<Point> ListAllThePossibleMovesForCurrentPlayerWithoutCheckChecking()
	{
		ArrayList<Point> listAllThePossibleMoves=new ArrayList<Point>();
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
    			pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay())	
    				listAllThePossibleMoves.addAll(GetListOfPossibleMovesForAPiece(new Point(counterHorizontal,counterVertical),false,false,false));
    	return listAllThePossibleMoves;
	}
	
	public boolean IsThisSquareUnderAttack(Point pointPotentialTarget)
	{
		int currentTurnSav=currentTurn;
		currentTurn=-currentTurn;
		ArrayList<Point> listAllThePossibleMoves=ListAllThePossibleMovesForCurrentPlayerWithoutCheckChecking();
		currentTurn=currentTurnSav;
		Iterator<Point> pointIteratorPossibleMoves=listAllThePossibleMoves.iterator();
		while(pointIteratorPossibleMoves.hasNext())
		{
			Point currentPossibleMove=pointIteratorPossibleMoves.next();
			if(currentPossibleMove.x==pointPotentialTarget.x&&
			currentPossibleMove.y==pointPotentialTarget.y)
				return true;
		}
		return false;
	}
	
	// delete moves that will put into chess todo : optimize
	public void DeleteMovesThatWillPutIntoChess(Point pointSource,ArrayList<Point> currentPossibleMoves)
	{
		ArrayList<Point> currentMovesThatHaveToBeDeleted=new ArrayList<Point>();
		Iterator<Point> pointIteratorCurrentPossibleMoves=currentPossibleMoves.iterator();

		// here we move another piece that the king, we have to verify if the king is still under check
		Point kingPosition=new Point(); // first of all we get the king coordinates
    	for(int counterVertical=0;counterVertical<numberOfSquarePerLine;counterVertical++)
    		for(int counterHorizontal=0;counterHorizontal<numberOfSquarePerLine;counterHorizontal++)
    			if(pieceMatrix[counterVertical][counterHorizontal]!=null&&
    				pieceMatrix[counterVertical][counterHorizontal].pieceId==Piece.kingId&&
    				pieceMatrix[counterVertical][counterHorizontal].InstanceColor==WhatColorHasToPlay())
    			{
    				kingPosition.x=counterHorizontal;
    				kingPosition.y=counterVertical;
    			}
		
    	// we look at every possible moves for the current piece
		while(pointIteratorCurrentPossibleMoves.hasNext())
		{
			Point currentPoint=pointIteratorCurrentPossibleMoves.next();
			
			 // we simulate the move
			ArrayList<Boolean> arrayListIsThisSpecialMove=new ArrayList<Boolean>();
			Piece piecePotentialyDeleted=MakeThisMove(pointSource,currentPoint,arrayListIsThisSpecialMove);
			
			// we retrieve all moves possible for the opponent and look if he can still put the current king in check 
			ArrayList<Point> listAllThePossibleMoves=ListAllThePossibleMovesForCurrentPlayerWithoutCheckChecking();
			Iterator<Point> pointIteratorNextPossibleMoves=listAllThePossibleMoves.iterator();
			while(pointIteratorNextPossibleMoves.hasNext())
			{
				Point nextPoint=pointIteratorNextPossibleMoves.next();
				if(nextPoint.x==kingPosition.x&&nextPoint.y==kingPosition.y)	// king still in check, delete the move
					currentMovesThatHaveToBeDeleted.add(currentPoint);
			}
			
			// undo the move to test others potential 
			UnMakeThisMove(currentPoint,pointSource,piecePotentialyDeleted,arrayListIsThisSpecialMove.get(0));
		}

		// now we delete the moves in the arraylist
		Iterator<Point> pointIteratorCurrentMovesThatHaveToBeDeleted=currentMovesThatHaveToBeDeleted.iterator();
		while(pointIteratorCurrentMovesThatHaveToBeDeleted.hasNext())
		{
			Point currentPointToBeDelete=pointIteratorCurrentMovesThatHaveToBeDeleted.next();
			pointIteratorCurrentPossibleMoves=currentPossibleMoves.iterator();
			while(pointIteratorCurrentPossibleMoves.hasNext())
			{
				Point currentPointPossibleMove=pointIteratorCurrentPossibleMoves.next();
				if(currentPointToBeDelete.x==currentPointPossibleMove.x&&
					currentPointToBeDelete.y==currentPointPossibleMove.y)
				{
					currentPossibleMoves.remove(currentPointPossibleMove);
					break;
				}
			}
		}
	}

	// called when the human plays
	public boolean IsThisMovePossible(Point pointSource,Point pointDestination)
	{
		ArrayList<Point> currentPossibleMoves=GetListOfPossibleMovesForAPieceWhithChessParameter(pointSource,true,IsKingOnChess());
		Iterator<Point> pointIterator=currentPossibleMoves.iterator();
		while(pointIterator.hasNext())
		{
			Point currentPoint=pointIterator.next();
			if(currentPoint.x==pointDestination.x&&currentPoint.y==pointDestination.y) 
				return true;
		}
		return false;
	}
	
	// get the color of the current player
	public Color WhatColorHasToPlay()
	{
		if(currentTurn==whiteTurn)
			return Color.white;
		if(currentTurn==blackTurn)
			return Color.black;
		return null;
	}

	// make a move on the matrix and for the piece, after change the current player
	public void UnMakeThisMove(Point oldSelectedSquare,Point newSeletectedSquare,
			Piece piecePotentialyDeleted,Boolean isItSpecialMove)
	{
		// special move : castling, pawn promotion, first king and rook moves
		if(isItSpecialMove==true)
		{
			if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.kingId)
				((King)(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x])).doIMoved=false;
			else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.rookId)
				((Rook)(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x])).doIMoved=false;	
			else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.queenId)
				pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=new Pawn(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].InstanceColor);

			// maybe the move it a castling, we have to check it
			if(oldSelectedSquare.x==shortWhiteCastlingKingDestination.x&&
				oldSelectedSquare.y==shortWhiteCastlingKingDestination.y&&
				newSeletectedSquare.x==whiteKingInitialPosition.x&&
				newSeletectedSquare.y==whiteKingInitialPosition.y&&
				pieceMatrix[shortWhiteCastlingKingDestination.y][shortWhiteCastlingKingDestination.x].InstanceColor==Color.white&&
				pieceMatrix[shortWhiteCastlingKingDestination.y][shortWhiteCastlingKingDestination.x].pieceId==Piece.kingId&&
				pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x]!=null&&
				pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x].pieceId==Piece.rookId&&
				pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x].InstanceColor==Color.white)
			{
				pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x]=pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x];
				pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x]=null;
				((Rook)(pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x])).doIMoved=false;
				((King)(pieceMatrix[shortWhiteCastlingKingDestination.y][shortWhiteCastlingKingDestination.x])).doIMoved=false;
			}
			else if(oldSelectedSquare.x==shortBlackCastlingKingDestination.x&&
				oldSelectedSquare.y==shortBlackCastlingKingDestination.y&&
				newSeletectedSquare.x==blackKingInitialPosition.x&&
				newSeletectedSquare.y==blackKingInitialPosition.y&&
				pieceMatrix[shortBlackCastlingKingDestination.y][shortBlackCastlingKingDestination.x].InstanceColor==Color.black&&
				pieceMatrix[shortBlackCastlingKingDestination.y][shortBlackCastlingKingDestination.x].pieceId==Piece.kingId&&
				pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x]!=null&&
				pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x].pieceId==Piece.rookId&&
				pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x].InstanceColor==Color.black)
			{
				pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x]=pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x];
				pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x]=null;
				((Rook)(pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x])).doIMoved=false;
				((King)(pieceMatrix[shortBlackCastlingKingDestination.y][shortBlackCastlingKingDestination.x])).doIMoved=false;
			}	
			else if(oldSelectedSquare.x==longWhiteCastlingKingDestination.x&&
					oldSelectedSquare.y==longWhiteCastlingKingDestination.y&&
					newSeletectedSquare.x==whiteKingInitialPosition.x&&
					newSeletectedSquare.y==whiteKingInitialPosition.y&&
					pieceMatrix[longWhiteCastlingKingDestination.y][longWhiteCastlingKingDestination.x].InstanceColor==Color.white&&
					pieceMatrix[longWhiteCastlingKingDestination.y][longWhiteCastlingKingDestination.x].pieceId==Piece.kingId&&
					pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x]!=null&&
					pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x].pieceId==Piece.rookId&&
					pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x].InstanceColor==Color.white)
				{
					pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x]=pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x];
					pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x]=null;
					((Rook)(pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x])).doIMoved=false;
					((King)(pieceMatrix[longWhiteCastlingKingDestination.y][longWhiteCastlingKingDestination.x])).doIMoved=false;
				}
			else if(oldSelectedSquare.x==longBlackCastlingKingDestination.x&&
				oldSelectedSquare.y==longBlackCastlingKingDestination.y&&
				newSeletectedSquare.x==blackKingInitialPosition.x&&
				newSeletectedSquare.y==blackKingInitialPosition.y&&
				pieceMatrix[longBlackCastlingKingDestination.y][longBlackCastlingKingDestination.x].InstanceColor==Color.black&&
				pieceMatrix[longBlackCastlingKingDestination.y][longBlackCastlingKingDestination.x].pieceId==Piece.kingId&&
				pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x]!=null&&
				pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x].pieceId==Piece.rookId&&
				pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x].InstanceColor==Color.black)
			{
				pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x]=pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x];
				pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x]=null;
				((Rook)(pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x])).doIMoved=false;
				((King)(pieceMatrix[longBlackCastlingKingDestination.y][longBlackCastlingKingDestination.x])).doIMoved=false;
			}					
		}	
		
		pieceMatrix[newSeletectedSquare.y][newSeletectedSquare.x]=pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x];
		pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=piecePotentialyDeleted;
		currentTurn=-currentTurn;
	}	
	
	
	// allow to know if the move in parameter is a castling or not
	public ArrayList<Point> IsThatMoveACastlingIfYesGiveSquares(Point oldSelectedSquare,Point newSeletectedSquare)
	{
		// maybe the move it a castling, we have to check it
		if(currentTurn==whiteTurn&&
			oldSelectedSquare.x==whiteKingInitialPosition.x&&
			oldSelectedSquare.y==whiteKingInitialPosition.y&&
			newSeletectedSquare.x==shortWhiteCastlingKingDestination.x&&
			newSeletectedSquare.y==shortWhiteCastlingKingDestination.y&&
			pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x]!=null&&
			pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].InstanceColor==Color.white&&
			pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].pieceId==Piece.kingId&&
			pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x]!=null&&
			pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x].pieceId==Piece.rookId&&
			pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x].InstanceColor==Color.white&&
			((Rook)(pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x])).doIMoved==false&&
			((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved==false)
			{
				ArrayList<Point> squaresConcerned=new ArrayList<Point>();
				squaresConcerned.add(new Point(whiteKingInitialPosition.x,whiteKingInitialPosition.y));
				squaresConcerned.add(new Point(shortWhiteCastlingRookDestination.x,shortWhiteCastlingRookDestination.y));
				squaresConcerned.add(new Point(shortWhiteCastlingKingDestination.x,shortWhiteCastlingKingDestination.y));
				squaresConcerned.add(new Point(rightWhiteRookInitialPosition.x,rightWhiteRookInitialPosition.y));
				return squaresConcerned;
			}
		else if(currentTurn==blackTurn&&
			oldSelectedSquare.x==blackKingInitialPosition.x&&
			oldSelectedSquare.y==blackKingInitialPosition.y&&
			newSeletectedSquare.x==shortBlackCastlingKingDestination.x&&
			newSeletectedSquare.y==shortBlackCastlingKingDestination.y&&
			pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x]!=null&&
			pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].InstanceColor==Color.black&&
			pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].pieceId==Piece.kingId&&
			pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x]!=null&&
			pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x].pieceId==Piece.rookId&&
			pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x].InstanceColor==Color.black&&
			((Rook)(pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x])).doIMoved==false&&
			((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved==false)
			{
				ArrayList<Point> squaresConcerned=new ArrayList<Point>();
				squaresConcerned.add(new Point(blackKingInitialPosition.x,blackKingInitialPosition.y));
				squaresConcerned.add(new Point(shortBlackCastlingRookDestination.x,shortBlackCastlingRookDestination.y));
				squaresConcerned.add(new Point(shortBlackCastlingKingDestination.x,shortBlackCastlingKingDestination.y));
				squaresConcerned.add(new Point(rightBlackRookInitialPosition.x,rightBlackRookInitialPosition.y));
				return squaresConcerned;
			}
		else if(currentTurn==whiteTurn&&
			oldSelectedSquare.x==whiteKingInitialPosition.x&&
			oldSelectedSquare.y==whiteKingInitialPosition.y&&
			newSeletectedSquare.x==longWhiteCastlingKingDestination.x&&
			newSeletectedSquare.y==longWhiteCastlingKingDestination.y&&
			pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x]!=null&&
			pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].InstanceColor==Color.white&&
			pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x].pieceId==Piece.kingId&&
			pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x]!=null&&
			pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x].pieceId==Piece.rookId&&
			pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x].InstanceColor==Color.white&&
			((Rook)(pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x])).doIMoved==false&&
			((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved==false)
			{
				ArrayList<Point> squaresConcerned=new ArrayList<Point>();
				squaresConcerned.add(new Point(whiteKingInitialPosition.x,whiteKingInitialPosition.y));
				squaresConcerned.add(new Point(longWhiteCastlingRookDestination.x,longWhiteCastlingRookDestination.y));
				squaresConcerned.add(new Point(longWhiteCastlingKingDestination.x,longWhiteCastlingKingDestination.y));
				squaresConcerned.add(new Point(leftWhiteRookInitialPosition.x,leftWhiteRookInitialPosition.y));
				return squaresConcerned;
			}
		else if(currentTurn==blackTurn&&
			oldSelectedSquare.x==blackKingInitialPosition.x&&
			oldSelectedSquare.y==blackKingInitialPosition.y&&
			newSeletectedSquare.x==longBlackCastlingKingDestination.x&&
			newSeletectedSquare.y==longBlackCastlingKingDestination.y&&
			pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x]!=null&&
			pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].InstanceColor==Color.black&&
			pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x].pieceId==Piece.kingId&&
			pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x]!=null&&
			pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x].pieceId==Piece.rookId&&
			pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x].InstanceColor==Color.black&&
			((Rook)(pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x])).doIMoved==false&&
			((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved==false)
			{
				ArrayList<Point> squaresConcerned=new ArrayList<Point>();
				squaresConcerned.add(new Point(blackKingInitialPosition.x,blackKingInitialPosition.y));
				squaresConcerned.add(new Point(longBlackCastlingRookDestination.x,longBlackCastlingRookDestination.y));
				squaresConcerned.add(new Point(longBlackCastlingKingDestination.x,longBlackCastlingKingDestination.y));
				squaresConcerned.add(new Point(leftBlackRookInitialPosition.x,leftBlackRookInitialPosition.y));
				return squaresConcerned;
			}		
		return null;
	}
	
	// move get the squares concerned by this move, useful for the real move on the chessboard
	public Piece UnMakeThisMoveAndGetSquaresToBeRefreshed(Point oldSelectedSquare,Point newSeletectedSquare,
			ArrayList<Point> squaresToBeRefreshed,Boolean isItSpecialMove,Piece piecePotentialyDeleted)
	{
		squaresToBeRefreshed.clear();
		squaresToBeRefreshed.addAll(0,GetListOfPossibleMovesForAPieceWhithChessParameter(oldSelectedSquare,true,IsKingOnChess()));
		UnMakeThisMove(oldSelectedSquare,newSeletectedSquare,piecePotentialyDeleted,isItSpecialMove);
		ArrayList<Point> squaresFromCastling=IsThatMoveACastlingIfYesGiveSquares(oldSelectedSquare,newSeletectedSquare);
		if(squaresFromCastling!=null)
			squaresToBeRefreshed.addAll(0,squaresFromCastling);
		else
			squaresToBeRefreshed.add(oldSelectedSquare);
		return null;
	}	
	
	// useful for the redo feature : we don't need to save the historic
	public Piece MakeThisMoveAndGetSquaresToBeRefreshedWithoutHistoric(Point oldSelectedSquare,
			Point newSeletectedSquare,
			ArrayList<Point> squaresToBeRefreshed,
			Boolean isItSpecialMove,
			Piece piecePotentialyDeleted)
	{
		squaresToBeRefreshed.clear();
		squaresToBeRefreshed.addAll(0,GetListOfPossibleMovesForAPieceWhithChessParameter(oldSelectedSquare,true,IsKingOnChess()));
		ArrayList<Point> squaresFromCastling=IsThatMoveACastlingIfYesGiveSquares(oldSelectedSquare,newSeletectedSquare);
		MakeThisMoveWithoutHistoric(oldSelectedSquare,newSeletectedSquare,isItSpecialMove);
		if(squaresFromCastling!=null)
			squaresToBeRefreshed.addAll(0,squaresFromCastling);
		else
			squaresToBeRefreshed.add(oldSelectedSquare);
		return null;
	}		
	
	public Piece MakeThisMoveAndGetSquaresToBeRefreshed(Point oldSelectedSquare,
			Point newSeletectedSquare,
			ArrayList<Point> squaresToBeRefreshed,
			ArrayList<Point> arrayListSourcePointHistoric,
			ArrayList<Point> arrayListDestinationPointHistoric,
			ArrayList<Boolean> arrayListIsSpecialMoveHistoric,
			ArrayList<Piece> arrayListPiecePotentialyDeletedHistoric)
	{
		squaresToBeRefreshed.clear();
		squaresToBeRefreshed.addAll(0,GetListOfPossibleMovesForAPieceWhithChessParameter(oldSelectedSquare,true,IsKingOnChess()));
		ArrayList<Point> squaresFromCastling=IsThatMoveACastlingIfYesGiveSquares(oldSelectedSquare,newSeletectedSquare);
		arrayListSourcePointHistoric.add(new Point(oldSelectedSquare.x,oldSelectedSquare.y));
		arrayListDestinationPointHistoric.add(new Point(newSeletectedSquare.x,newSeletectedSquare.y));
		arrayListPiecePotentialyDeletedHistoric.add(MakeThisMove(oldSelectedSquare,newSeletectedSquare,arrayListIsSpecialMoveHistoric));
		if(squaresFromCastling!=null)
			squaresToBeRefreshed.addAll(0,squaresFromCastling);
		else
			squaresToBeRefreshed.add(oldSelectedSquare);
		return null;
	}	
	
	// make a move on the matrix and for the piece, after change the current player
	public void MakeThisMoveWithoutHistoric(Point oldSelectedSquare,Point newSeletectedSquare,Boolean isSpecialMoveHistoric)
	{
		if(IsThatMoveACastlingIfYesGiveSquares(oldSelectedSquare,newSeletectedSquare)!=null)
		{
			// this is a castling so we move the rook and mark it as a special move
			if(newSeletectedSquare.x==shortWhiteCastlingKingDestination.x&&newSeletectedSquare.y==shortWhiteCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x]=pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x];
				pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x]=null;
			}
			else if(newSeletectedSquare.x==shortBlackCastlingKingDestination.x&&newSeletectedSquare.y==shortBlackCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x]=pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x];
				pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x]=null;
			}
			else if(newSeletectedSquare.x==longWhiteCastlingKingDestination.x&&newSeletectedSquare.y==longWhiteCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x]=pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x];
				pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x]=null;
			}
			else if(newSeletectedSquare.x==longBlackCastlingKingDestination.x&&newSeletectedSquare.y==longBlackCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x]=pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x];
				pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x]=null;
			}				
		}
		else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.rookId)
			((Rook)pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]).doIMoved=true; // the rook has moved, we have to save this information to avoid future castling
		else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.kingId)
			((King)pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]).doIMoved=true;	// the king has moved, we have to save this information to avoid future castling
		else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.pawnId)
		{
			if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].InstanceColor==Color.white&&oldSelectedSquare.y==blackPawnsVerticalPosition)
				pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=new Queen(Color.white);
			if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].InstanceColor==Color.black&&oldSelectedSquare.y==whitePawnsVerticalPosition)
				pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=new Queen(Color.black);			
		}
		
		pieceMatrix[newSeletectedSquare.y][newSeletectedSquare.x]=pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x];
		pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=null;
		currentTurn=-currentTurn;
	}
	
	// make a move on the matrix and for the piece, after change the current player
	public Piece MakeThisMove(Point oldSelectedSquare,Point newSeletectedSquare,
			ArrayList<Boolean> arrayListIsSpecialMoveHistoric)
	{
		if(IsThatMoveACastlingIfYesGiveSquares(oldSelectedSquare,newSeletectedSquare)!=null)
		{
			// this is a castling so we move the rook and mark it as a special move
			if(newSeletectedSquare.x==shortWhiteCastlingKingDestination.x&&newSeletectedSquare.y==shortWhiteCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[shortWhiteCastlingRookDestination.y][shortWhiteCastlingRookDestination.x]=pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x];
				pieceMatrix[rightWhiteRookInitialPosition.y][rightWhiteRookInitialPosition.x]=null;
			}
			else if(newSeletectedSquare.x==shortBlackCastlingKingDestination.x&&newSeletectedSquare.y==shortBlackCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[shortBlackCastlingRookDestination.y][shortBlackCastlingRookDestination.x]=pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x];
				pieceMatrix[rightBlackRookInitialPosition.y][rightBlackRookInitialPosition.x]=null;
			}	
			else if(newSeletectedSquare.x==longWhiteCastlingKingDestination.x&&newSeletectedSquare.y==longWhiteCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[whiteKingInitialPosition.y][whiteKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[longWhiteCastlingRookDestination.y][longWhiteCastlingRookDestination.x]=pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x];
				pieceMatrix[leftWhiteRookInitialPosition.y][leftWhiteRookInitialPosition.x]=null;
			}
			else if(newSeletectedSquare.x==longBlackCastlingKingDestination.x&&newSeletectedSquare.y==longBlackCastlingKingDestination.y)
			{
				((Rook)(pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x])).doIMoved=true;
				((King)(pieceMatrix[blackKingInitialPosition.y][blackKingInitialPosition.x])).doIMoved=true;
				pieceMatrix[longBlackCastlingRookDestination.y][longBlackCastlingRookDestination.x]=pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x];
				pieceMatrix[leftBlackRookInitialPosition.y][leftBlackRookInitialPosition.x]=null;
			}				
			arrayListIsSpecialMoveHistoric.add(new Boolean(true));
		}
		else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.rookId)
		{
			// the rook has moved, we have to save this information to avoid future castling
			if(((Rook)pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]).doIMoved==false)
			{
				arrayListIsSpecialMoveHistoric.add(new Boolean(true));
				((Rook)pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]).doIMoved=true;
			}
			else
				arrayListIsSpecialMoveHistoric.add(new Boolean(false));
		}
		else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.kingId)
		{
			// the king has moved, we have to save this information to avoid future castling
			if(((King)pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]).doIMoved==false)
			{
				arrayListIsSpecialMoveHistoric.add(new Boolean(true));
				((King)pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]).doIMoved=true;
			}
			else
				arrayListIsSpecialMoveHistoric.add(new Boolean(false));
		}
		else if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].pieceId==Piece.pawnId)
		{
			if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].InstanceColor==Color.white&&oldSelectedSquare.y==blackPawnsVerticalPosition)
			{
				pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=new Queen(Color.white);
				arrayListIsSpecialMoveHistoric.add(new Boolean(true));
			}
			if(pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x].InstanceColor==Color.black&&oldSelectedSquare.y==whitePawnsVerticalPosition)
			{
				pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=new Queen(Color.black);
				arrayListIsSpecialMoveHistoric.add(new Boolean(true));
			}			
			else
				arrayListIsSpecialMoveHistoric.add(new Boolean(false));
		}	
		else
			arrayListIsSpecialMoveHistoric.add(new Boolean(false));
		
		// we really make the move, saving the piece which has maybe been deleted
		Piece eventualOldPiece=pieceMatrix[newSeletectedSquare.y][newSeletectedSquare.x];
		pieceMatrix[newSeletectedSquare.y][newSeletectedSquare.x]=pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x];
		pieceMatrix[oldSelectedSquare.y][oldSelectedSquare.x]=null;
		currentTurn=-currentTurn;
		return eventualOldPiece;
	}	
	
	// The game is over, no more player has to play
	public void EndTheGame()
	{
		currentTurn=noTurn;
	}

	public void changePlayerTurn()
	{
		currentTurn=-currentTurn;
	}
	
	// allow to know if the game has ended 
	public boolean DoesCurrentPlayerIsMat()
	{
		ArrayList<Point> possibleMoves=ListAllThePossibleMovesForCurrentPlayerWithCheckCheking();
		if(possibleMoves.size()==0)
			return true;
		return false;
	}
}

	