import java.awt.Color;
import java.awt.image.BufferedImage;

public class Piece 
{
	public Color InstanceColor;
	public String ImageNameBlack;
	public String ImageNameWhite;
	public static final int pawnId=0;
	public static final int rookId=1;
	public static final int knightId=2;
	public static final int bishopId=3;
	public static final int queenId=4;
	public static final int kingId=5;
	public BufferedImage image;
	public int pieceId;
	public int value;
	public Piece(Color colorParameter,int valueParameter)
	{
		InstanceColor=colorParameter;
		value=valueParameter;
	}
	public Piece(Piece pieceParameter)
	{
		InstanceColor=pieceParameter.InstanceColor;
		pieceId=pieceParameter.pieceId;
	}	
}
