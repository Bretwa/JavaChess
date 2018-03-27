import java.awt.Color;

public class Knight extends Piece
{
	public static final int value=30;	
	public Knight(Color colorParameter)
	{
		super(colorParameter,value);
		ImageNameBlack="knight_80_filter_black.png";		
		ImageNameWhite="knight_80_filter_white.png";
		pieceId=knightId;
	}
}
