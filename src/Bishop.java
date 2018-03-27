import java.awt.Color;

public class Bishop extends Piece
{
	public static final int value=40;		
	public Bishop(Color colorParameter)
	{
		super(colorParameter,value);
		ImageNameBlack="bishop_80_filter_black.png";		
		ImageNameWhite="bishop_80_filter_white.png";
		pieceId=bishopId;
	}
}
