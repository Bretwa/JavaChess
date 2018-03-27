/*
The ChessBoard is the board where the piece will be placed 
*/

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;

public class ChessBoard extends Canvas
{
	private static final long serialVersionUID=1L;
	public static Piece[][]MatrixPiece;
	public static int VerticalInsertion=10;
	public static int HorizontalInsertion=10;
	public static int squareSize=80;
	public static int NumberOfSquarePerLine=8;
	public static int rectangleSelectionWidth=5;
	public BufferedImage blackPawnImage=null;
	public BufferedImage blackRookImage=null;
	public BufferedImage blackKnightImage=null;
	public BufferedImage blackBishopImage=null;
	public BufferedImage blackQueenImage=null;
	public BufferedImage blackKingImage=null;
	public BufferedImage whitePawnImage=null;
	public BufferedImage whiteRookImage=null;
	public BufferedImage whiteKnightImage=null;
	public BufferedImage whiteBishopImage=null;
	public BufferedImage whiteQueenImage=null;
	public BufferedImage whiteKingImage=null;
	
	// allow to put an image under another one considering a transparent color 
	public BufferedImage makeColorTransparent(BufferedImage imageParameter,Color color) 
    {  
        BufferedImage resultImage=new BufferedImage(imageParameter.getWidth(),imageParameter.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics=resultImage.createGraphics();  
        graphics.setComposite(AlphaComposite.Src);  
        graphics.drawImage(imageParameter,null,0,0);  
        graphics.dispose();  
        for(int counterVertical=0;counterVertical<resultImage.getHeight();counterVertical++) 
            for(int counterHorizontal= 0;counterHorizontal<resultImage.getWidth();counterHorizontal++) 
                 if(resultImage.getRGB(counterHorizontal,counterVertical)==color.getRGB()) 
                	 resultImage.setRGB(counterHorizontal,counterVertical,0x8F1C1C);  
        return resultImage;  
    }   	
    
    // we repaint just a square, useful to avoid clipping on the whole chess board
    public void PaintJustASquare(Point PointParameter)
    {
    	// first of all we repaint the square itself
    	Graphics graphics=this.getGraphics();
		if(PointParameter.y%2==0)
		{
    		if(PointParameter.x%2==0)
    			graphics.setColor(Color.white);
    		else
    			graphics.setColor(Color.black);
		}
		else
		{
    		if(PointParameter.x%2==0)
    			graphics.setColor(Color.black);
    		else
    			graphics.setColor(Color.white);
		}    	
        graphics.fillRect(HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize, squareSize-1,squareSize-1);
        graphics.drawRect(HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize, squareSize-1,squareSize-1);
        
        // now we paint the piece on it if there is one
        if(MatrixPiece[PointParameter.y][PointParameter.x]!=null)
        {
        	try 
        	{
        		if(MatrixPiece[PointParameter.y][PointParameter.x].InstanceColor==Color.white)
        		{
        			switch(MatrixPiece[PointParameter.y][PointParameter.x].pieceId)
        			{
        			case Piece.pawnId:
	    				if(whitePawnImage==null)
	    					whitePawnImage=ImageIO.read(getClass().getResourceAsStream(new Pawn(Color.white).ImageNameWhite));
        				graphics.drawImage(makeColorTransparent(whitePawnImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;
        			case Piece.rookId:
	    				if(whiteRookImage==null)
	    					whiteRookImage=ImageIO.read(getClass().getResourceAsStream(new Rook(Color.white).ImageNameWhite));
        				graphics.drawImage(makeColorTransparent(whiteRookImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;      
        			case Piece.knightId:
	    				if(whiteKnightImage==null)
	    					whiteKnightImage=ImageIO.read(getClass().getResourceAsStream(new Knight(Color.white).ImageNameWhite));
        				graphics.drawImage(makeColorTransparent(whiteKnightImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;
        			case Piece.bishopId:
	    				if(whiteBishopImage==null)
	    					whiteBishopImage=ImageIO.read(getClass().getResourceAsStream(new Bishop(Color.white).ImageNameWhite));
        				graphics.drawImage(makeColorTransparent(whiteBishopImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;     
        			case Piece.queenId:
	    				if(whiteQueenImage==null)
	    					whiteQueenImage=ImageIO.read(getClass().getResourceAsStream(new Queen(Color.white).ImageNameWhite));
        				graphics.drawImage(makeColorTransparent(whiteQueenImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;
        			case Piece.kingId:
	    				if(whiteKingImage==null)
	    					whiteKingImage=ImageIO.read(getClass().getResourceAsStream(new King(Color.white).ImageNameWhite));
        				graphics.drawImage(makeColorTransparent(whiteKingImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;            				
        			default:
        				javax.swing.JOptionPane.showMessageDialog(null,"Error piece id unknow : "+
            			MatrixPiece[PointParameter.y][PointParameter.x].pieceId+" at "+PointParameter);
        			}
        		}
        		else
        		{
        			switch(MatrixPiece[PointParameter.y][PointParameter.x].pieceId)
        			{
        			case Piece.pawnId:
	    				if(blackPawnImage==null)
	    					blackPawnImage=ImageIO.read(getClass().getResourceAsStream(new Pawn(Color.black).ImageNameBlack));
        				graphics.drawImage(makeColorTransparent(blackPawnImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;
        			case Piece.rookId:
        				if(blackRookImage==null)
        					blackRookImage=ImageIO.read(getClass().getResourceAsStream(new Rook(Color.black).ImageNameBlack));
        				graphics.drawImage(makeColorTransparent(blackRookImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;        	
        			case Piece.knightId:
           				if(blackKnightImage==null)
           					blackKnightImage=ImageIO.read(getClass().getResourceAsStream(new Knight(Color.black).ImageNameBlack));
        				graphics.drawImage(makeColorTransparent(blackKnightImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;
        			case Piece.bishopId:
          				if(blackBishopImage==null)
          					blackBishopImage=ImageIO.read(getClass().getResourceAsStream(new Bishop(Color.black).ImageNameBlack));
        				graphics.drawImage(makeColorTransparent(blackBishopImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;     
        			case Piece.queenId:
         				if(blackQueenImage==null)
         					blackQueenImage=ImageIO.read(getClass().getResourceAsStream(new Queen(Color.black).ImageNameBlack));
        				graphics.drawImage(makeColorTransparent(blackQueenImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;
        			case Piece.kingId:
        				if(blackKingImage==null)
        					blackKingImage=ImageIO.read(getClass().getResourceAsStream(new King(Color.black).ImageNameBlack));
        				graphics.drawImage(makeColorTransparent(blackKingImage,Color.white),HorizontalInsertion+PointParameter.x*squareSize,VerticalInsertion+PointParameter.y*squareSize,null);
        				break;             				
        			default:
        				javax.swing.JOptionPane.showMessageDialog(null,"Error piece id unknow : "+
        				MatrixPiece[PointParameter.y][PointParameter.x].pieceId+" at "+PointParameter);
        			}
        		}
        	}
        	catch(IOException exception) 
        	{
        		javax.swing.JOptionPane.showMessageDialog(null,"Error read image : "+exception);
        		exception.printStackTrace();
        	}    
       }
    }
    
    // repaint the whole chess board, each square actually
    public void paint(Graphics graphics)
    {
    	for(int CounterVertical=0;CounterVertical<NumberOfSquarePerLine;CounterVertical++)
        	for(int CounterHorizontal=0;CounterHorizontal<NumberOfSquarePerLine;CounterHorizontal++)
        		PaintJustASquare(new Point(CounterHorizontal,CounterVertical));
    }	
    
    public Dimension GetChessBoardDimension()
    {
    	return new Dimension(NumberOfSquarePerLine*squareSize,NumberOfSquarePerLine*squareSize);
    }
    
    // the constructor, create the window with right dimensions
    public ChessBoard(Piece[][] MatrixPieceParameter)
    {
    	MatrixPiece=MatrixPieceParameter;
    }
    
    // simply draw a square in blue
    public void DrawGreenASquare(Point PointParameter)
    {
    	DrawSquare(PointParameter,Color.green);
    }
    
    // draw a square according to a specific color
    public void DrawSquare(Point PointParameter,Color colorParameter)
    {
    	int HorizontalSquareSelected=PointParameter.x;
    	int VerticalSquareSelected=PointParameter.y;
    	Graphics graphics=this.getGraphics();
    	graphics.setColor(colorParameter);
    	for(int RectangleWidth=0;RectangleWidth<rectangleSelectionWidth;RectangleWidth++)
    	{
    		// left
	    	graphics.drawLine(HorizontalInsertion+HorizontalSquareSelected*squareSize+RectangleWidth,
	    			VerticalInsertion+VerticalSquareSelected*squareSize,
	    			HorizontalInsertion+HorizontalSquareSelected*squareSize+RectangleWidth,
	    			VerticalInsertion+VerticalSquareSelected*squareSize+squareSize-1);
	    	
	    	// top
	    	graphics.drawLine(HorizontalInsertion+HorizontalSquareSelected*squareSize,
	    			VerticalInsertion+VerticalSquareSelected*squareSize+RectangleWidth,
	    			HorizontalInsertion+HorizontalSquareSelected*squareSize+squareSize-1,
	    			VerticalInsertion+VerticalSquareSelected*squareSize+RectangleWidth);	    	
	    	
	    	// right
	    	graphics.drawLine(HorizontalInsertion+HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,
	    			VerticalInsertion+VerticalSquareSelected*squareSize,
	    			HorizontalInsertion+HorizontalSquareSelected*squareSize+squareSize-RectangleWidth-1,
	    			VerticalInsertion+VerticalSquareSelected*squareSize+squareSize-1);	    	

	    	// bottom
	    	graphics.drawLine(HorizontalInsertion+HorizontalSquareSelected*squareSize,
	    			VerticalInsertion+VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1,
	    			HorizontalInsertion+HorizontalSquareSelected*squareSize+squareSize-1,
	    			VerticalInsertion+VerticalSquareSelected*squareSize+squareSize-RectangleWidth-1);		    	
    	}
    }
    
    // retrieve a point in ChessBoard coordinates with pixels coordinates as input parameter
    public Point GetCorrespondingSquare(Point PointParameter)
    {
    	int HorizontalSquareSelected=(PointParameter.x-HorizontalInsertion)/squareSize;
    	int VerticalSquareSelected=(PointParameter.y-VerticalInsertion)/squareSize;
    	if(HorizontalSquareSelected<0||HorizontalSquareSelected>=NumberOfSquarePerLine||
    		VerticalSquareSelected<0||VerticalSquareSelected>=NumberOfSquarePerLine)
    		return null;
    	return new Point(HorizontalSquareSelected,VerticalSquareSelected);
    }
    
    // draw several squares in blue given in a list 
    public void DrawSeveralSquaresInBlue(ArrayList<Point> possibleMoves)
    {
		Iterator<Point> PointIterator=possibleMoves.iterator();
		while(PointIterator.hasNext())
		{
			Point currentPoint=PointIterator.next();
			DrawSquare(currentPoint,Color.blue);
		}
    }
    
    // draw several squares from given in a list 
    public void DrawSeveralSquares(ArrayList<Point> possibleMoves)
    {
		Iterator<Point> PointIterator=possibleMoves.iterator();
		while(PointIterator.hasNext())
		{
			Point currentPoint=PointIterator.next();
			PaintJustASquare(currentPoint);
		}
    }    
}
