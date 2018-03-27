import java.awt.Color;

public class Queen extends Piece
{
	public static final int value=90;		
	public Queen(Color colorParameter)
	{
		super(colorParameter,value);
		ImageNameBlack="queen_80_filter_black.png";		
		ImageNameWhite="queen_80_filter_white.png";
		pieceId=queenId;
	}
}
