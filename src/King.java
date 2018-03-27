import java.awt.Color;
public class King extends Piece
{
	public boolean doIMoved;	
	public static final int value=10000;
	public King(Color colorParameter)
	{
		super(colorParameter,value);
		ImageNameBlack="king_80_filter_black.png";		
		ImageNameWhite="king_80_filter_white.png";
		pieceId=kingId;
		doIMoved=false;		
	}
}
