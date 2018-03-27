import java.awt.Color;

public class ComputerPlayer extends Player 
{
	public int depth;
	public ComputerPlayer(Color colorParameter,int depthParameter)
	{
		InstanceColor=colorParameter;
		depth=depthParameter;
	}
}
