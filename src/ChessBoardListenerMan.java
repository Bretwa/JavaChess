import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoardListenerMan extends MouseAdapter  
{
	public ChessBoard BoardInstance;
	public ChessApplication ChessApplicationInstance;
	public ChessBoardListenerMan(ChessApplication ChessApplicationParameter)
	{
		ChessApplicationInstance=ChessApplicationParameter;
	}
	public void mousePressed(MouseEvent MouseEventParameter)
	{
		try 
		{
			ChessApplicationInstance.OnMousePressedOnTheChessBoard(MouseEventParameter);
		} 
		catch(InterruptedException e) 
		{
			e.printStackTrace();
		}		
	}
}
