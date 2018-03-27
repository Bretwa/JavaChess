import java.awt.Color;

public class Rook extends Piece 
{
	public static final int value=50;		
	public boolean doIMoved;
	public Rook(Color colorParameter)
	{
		super(colorParameter,value);
		ImageNameBlack="rook_80_filter_black.png";	
		ImageNameWhite="rook_80_filter_white.png";		
		pieceId=rookId;
		doIMoved=false;
	}
}
