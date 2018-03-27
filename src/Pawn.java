import java.awt.Color;

public class Pawn extends Piece
{
	public static final int value=10;		
	public Pawn(Color colorParameter)
	{
		super(colorParameter,value);
		ImageNameBlack="pawn_80_filter_black.png";		
		ImageNameWhite="pawn_80_filter_white.png";
		getClass().getResourceAsStream(ImageNameBlack);

	//	if(colorParameter==Color.black)
	//	InputStream imageURL=(InputStream) getClass().getResourceAsStream(ImageNameBlack);
	//	instanceBufferedImage=ImageIO.read(imageURL);
		
		pieceId=pawnId;
	}
	public void test()
	{
//		InputStream imageURL=null;
//		imageURL=getClass().getResourceAsStream(ImageNameBlack);	
	//	BufferedImage instanceBufferedImage=null;
//		instanceBufferedImage=ImageIO.read(imageURL);
	}
}
