/*
ChessApplication is the only no reusable class, it manages the players and it knows
 
 */

//extends JFrame

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class ChessApplication extends Applet implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static ChessBoard chessBoard;
	public static boolean isSquareSelected = false;
	public static Point oldSelectedSquare = null;
	public static ChessRulesMan chessRulesMan = null;
	public static int verticalInsertion = 10;
	public static int horizontalInsertion = 10;
	public static int maximumDepth = 5;
	public static int defaultDepth = 4;
	public static JFrame mainFrame;
	public static String newGame = "New game";
	public static String changePlayerTurn = "Change player turn";
	public static String quit = "Quit";
	public static String computerLevel = "Computer level";
	public static String computerPlaysBlack = "Computer plays black";
	public static String computerPlaysWhite = "Computer plays white";
	public static String blackPlayerLevel = "Black player level";
	public static String whitePlayerLevel = "White player level";
	public static String moves = "Moves";
	public static String redoMove = "Redo move";
	public static String undoMove = "Undo move";
	public static Player blackPlayer, whitePlayer;
	public static JCheckBoxMenuItem itemComputerPlaysBlack;
	public static JCheckBoxMenuItem itemComputerPlaysWhite;
	public static ButtonGroup groupBlack;
	public static ArrayList<JRadioButtonMenuItem> arrayListBlackLevel;
	public static ArrayList<JRadioButtonMenuItem> arrayListWhiteLevel;
	public static JCheckBoxMenuItem itemBlackComputerLevel1;
	public static ChessApplication chessApplication;
	public static ChessBoardListenerMan chessBoardMouseListener;
	public static ArrayList<Point> arrayListSourcePointHistoric;
	public static ArrayList<Point> arrayListDestinationPointHistoric;
	public static ArrayList<Boolean> arrayListIsSpecialMoveHistoric;
	public static ArrayList<Piece> arrayListPiecePotentialyDeletedHistoric;
	public static int historicIndex = 0;

	@Override
	public void init() {
		main(null);
	}

	@Override
	public void update(Graphics g) {
		mainFrame.paintAll(mainFrame.getGraphics());
		chessBoard.paint(chessBoard.getGraphics());
		paint(g);
	}

	// the main function, we create the chessboard, the players, the mouse
	// listener and the rulesman
	public static void main(String[] args) {
		chessApplication = new ChessApplication();
		chessRulesMan = new ChessRulesMan();
		chessBoard = new ChessBoard(chessRulesMan.GetPieceMatrix());
		blackPlayer = new HumanPlayer(Color.black);
		whitePlayer = new HumanPlayer(Color.white);
		chessBoardMouseListener = new ChessBoardListenerMan(chessApplication);
		chessBoard.addMouseListener(chessBoardMouseListener);
		chessRulesMan.LaunchNewGame();
		mainFrame = new JFrame("JavaChess");
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setResizable(true);
		JMenuBar menuBar = new JMenuBar();
		JMenu menuGame = new JMenu("Game");
		JMenuItem itemNewGame = new JMenuItem(newGame);
		itemNewGame.addActionListener(chessApplication);
		menuGame.add(itemNewGame);
		JMenuItem itemChangePlayerTurn = new JMenuItem(changePlayerTurn);
		itemChangePlayerTurn.addActionListener(chessApplication);
		menuGame.add(itemChangePlayerTurn);
		menuGame.addSeparator();
		menuBar.add(menuGame);
		itemComputerPlaysBlack = new JCheckBoxMenuItem(computerPlaysBlack);
		itemComputerPlaysBlack.addActionListener(chessApplication);
		menuGame.add(itemComputerPlaysBlack);
		itemComputerPlaysWhite = new JCheckBoxMenuItem(computerPlaysWhite);
		itemComputerPlaysWhite.addActionListener(chessApplication);
		menuGame.add(itemComputerPlaysWhite);
		menuGame.addSeparator();
		JMenuItem itemQuit = new JMenuItem(quit);
		itemQuit.addActionListener(chessApplication);
		menuGame.add(itemQuit);
		menuBar.add(menuGame);

		// black player plays by default
		itemComputerPlaysBlack.setSelected(true);
		blackPlayer = new ComputerPlayer(Color.black, defaultDepth);

		// now a create the menu for the computer level
		JMenu menuComputerConfiguration = new JMenu(computerLevel);

		// add black computer levels
		ButtonGroup groupBlack = new ButtonGroup();
		arrayListBlackLevel = new ArrayList<JRadioButtonMenuItem>();
		for (int counterLevel = 1; counterLevel <= maximumDepth; counterLevel++) {
			String blackPlayerLevelCounter = blackPlayerLevel + " "
					+ counterLevel;
			JRadioButtonMenuItem menuItemBlackPlayerLevel = new JRadioButtonMenuItem(
					blackPlayerLevelCounter);
			if (counterLevel == defaultDepth)
				menuItemBlackPlayerLevel.setSelected(true);
			arrayListBlackLevel.add(menuItemBlackPlayerLevel);
			menuItemBlackPlayerLevel.addActionListener(chessApplication);
			groupBlack.add(menuItemBlackPlayerLevel);
			menuComputerConfiguration.add(menuItemBlackPlayerLevel);
		}

		// put a separator between black level and white level
		menuComputerConfiguration.addSeparator();

		// add white computer levels
		ButtonGroup groupWhite = new ButtonGroup();
		arrayListWhiteLevel = new ArrayList<JRadioButtonMenuItem>();
		for (int counterLevel = 1; counterLevel <= maximumDepth; counterLevel++) {
			String whitePlayerLevelCounter = whitePlayerLevel + " "
					+ counterLevel;
			JRadioButtonMenuItem menuItemWhitePlayerLevel = new JRadioButtonMenuItem(
					whitePlayerLevelCounter);
			if (counterLevel == defaultDepth)
				menuItemWhitePlayerLevel.setSelected(true);
			arrayListWhiteLevel.add(menuItemWhitePlayerLevel);
			menuItemWhitePlayerLevel.addActionListener(chessApplication);
			groupWhite.add(menuItemWhitePlayerLevel);
			menuComputerConfiguration.add(menuItemWhitePlayerLevel);
		}
		menuComputerConfiguration.addActionListener(chessApplication);
		menuBar.add(menuComputerConfiguration);

		// we add the menu for do, undo the moves
		JMenu menuMoves = new JMenu(moves);
		JMenuItem itemUndoMove = new JMenuItem(undoMove);
		itemUndoMove.addActionListener(chessApplication);
		menuMoves.add(itemUndoMove);
		JMenuItem itemRedoMove = new JMenuItem(redoMove);
		itemRedoMove.addActionListener(chessApplication);
		menuMoves.add(itemRedoMove);
		// menuBar.add(menuMoves);

		mainFrame.getContentPane().add(chessBoard);
		mainFrame.setJMenuBar(menuBar);
		Dimension chessBoardDimension = chessBoard.GetChessBoardDimension();
		chessBoardDimension.height += horizontalInsertion * 2 ;
			chessBoardDimension.height += menuBar.getSize().height+20 ;
	//	chessBoardDimension.height += horizontalInsertion * 2 + 30;
	//	chessBoardDimension.height += menuBar.getSize().height + 20;
	//	chessBoardDimension.width += verticalInsertion * 2 + 10;
			chessBoardDimension.width += verticalInsertion * 2 ;
		mainFrame.setSize(chessBoardDimension);
		mainFrame.setLocationRelativeTo(mainFrame.getParent());
		mainFrame.setResizable(true);
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		arrayListSourcePointHistoric = new ArrayList<Point>();
		arrayListDestinationPointHistoric = new ArrayList<Point>();
		arrayListIsSpecialMoveHistoric = new ArrayList<Boolean>();
		arrayListPiecePotentialyDeletedHistoric = new ArrayList<Piece>();
	}

	// a click has been done on the board, it has to be analyzed to know what
	// happened
	public void OnMousePressedOnTheChessBoard(MouseEvent MouseEventParameter)
			throws InterruptedException {
		Piece pieceMatrix[][] = chessRulesMan.GetPieceMatrix();

		if (chessRulesMan.WhatColorHasToPlay() == null) {
			javax.swing.JOptionPane
					.showMessageDialog(null, "The game is over.");
			return;
		}

		// case of a computer vs computer game
		if (blackPlayer.getClass().getName() == "ComputerPlayer"
				&& whitePlayer.getClass().getName() == "ComputerPlayer")
			return;

		Point NewSeletectedSquare = chessBoard
				.GetCorrespondingSquare(MouseEventParameter.getPoint());
		if (NewSeletectedSquare == null)
			return;
		if (oldSelectedSquare == null) // we select a new piece
		{
			// we just can select the piece of the current player
			if (pieceMatrix[NewSeletectedSquare.y][NewSeletectedSquare.x] != null
					&& pieceMatrix[NewSeletectedSquare.y][NewSeletectedSquare.x].InstanceColor == chessRulesMan
							.WhatColorHasToPlay()) {
				chessBoard.DrawGreenASquare(NewSeletectedSquare);
				oldSelectedSquare = NewSeletectedSquare;
				chessBoard.DrawSeveralSquaresInBlue(chessRulesMan
						.GetListOfPossibleMovesForAPieceWhithChessParameter(
								NewSeletectedSquare, true,
								chessRulesMan.IsKingOnChess()));
			}
		} else // we eat another piece or select another one of current player
		{
			// we select another piece of current player
			if (pieceMatrix[NewSeletectedSquare.y][NewSeletectedSquare.x] != null
					&& pieceMatrix[NewSeletectedSquare.y][NewSeletectedSquare.x].InstanceColor == chessRulesMan
							.WhatColorHasToPlay()) {
				if (NewSeletectedSquare.x == oldSelectedSquare.x
						&& NewSeletectedSquare.y == oldSelectedSquare.y) {
					// here we unselect the current selected piece
					chessBoard
							.DrawSeveralSquares(chessRulesMan
									.GetListOfPossibleMovesForAPieceWhithChessParameter(
											oldSelectedSquare, true,
											chessRulesMan.IsKingOnChess()));
					chessBoard.PaintJustASquare(oldSelectedSquare);
					oldSelectedSquare = null;
				} else {
					// here we select a piece
					chessBoard.PaintJustASquare(oldSelectedSquare);
					chessBoard.DrawGreenASquare(NewSeletectedSquare);
					chessBoard
							.DrawSeveralSquares(chessRulesMan
									.GetListOfPossibleMovesForAPieceWhithChessParameter(
											oldSelectedSquare, true,
											chessRulesMan.IsKingOnChess()));
					chessBoard
							.DrawSeveralSquaresInBlue(chessRulesMan
									.GetListOfPossibleMovesForAPieceWhithChessParameter(
											NewSeletectedSquare, true,
											chessRulesMan.IsKingOnChess()));
					oldSelectedSquare = NewSeletectedSquare;
				}
			} else if (chessRulesMan.IsThisMovePossible(oldSelectedSquare,
					NewSeletectedSquare) == true) {
				int initialArrayListSourcePointHistoricSize = arrayListSourcePointHistoric
						.size();
				for (int counterHistoric = historicIndex; counterHistoric < initialArrayListSourcePointHistoricSize; counterHistoric++) {
					arrayListSourcePointHistoric
							.remove(arrayListSourcePointHistoric.size() - 1);
					arrayListDestinationPointHistoric
							.remove(arrayListDestinationPointHistoric.size() - 1);
					arrayListIsSpecialMoveHistoric
							.remove(arrayListIsSpecialMoveHistoric.size() - 1);
					arrayListPiecePotentialyDeletedHistoric
							.remove(arrayListPiecePotentialyDeletedHistoric
									.size() - 1);
				}
				historicIndex++;

				ArrayList<Point> listSquaresToBeRefresh = new ArrayList<Point>();
				chessRulesMan.MakeThisMoveAndGetSquaresToBeRefreshed(
						oldSelectedSquare, NewSeletectedSquare,
						listSquaresToBeRefresh, arrayListSourcePointHistoric,
						arrayListDestinationPointHistoric,
						arrayListIsSpecialMoveHistoric,
						arrayListPiecePotentialyDeletedHistoric);
				chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
				oldSelectedSquare = null;

				// at the end of the current player move, we check if the next
				// player play is mat, if this is the case, the game is over
				if (chessRulesMan.DoesCurrentPlayerIsMat() == true) {
					if (chessRulesMan.WhatColorHasToPlay() == Color.black)
						javax.swing.JOptionPane.showMessageDialog(null,
								"White wins !");
					if (chessRulesMan.WhatColorHasToPlay() == Color.white)
						javax.swing.JOptionPane.showMessageDialog(null,
								"Black wins !");
					chessRulesMan.EndTheGame();
				}

				// if the opponent is an artificial intelligence player it's its
				// time to player
				if (blackPlayer.getClass().getName() == "ComputerPlayer"
						&& chessRulesMan.WhatColorHasToPlay() == Color.black) {
					chessRulesMan.PlayComputerAndGetSquaresToBeRefreshed(
							((ComputerPlayer) blackPlayer).depth,
							listSquaresToBeRefresh,
							arrayListSourcePointHistoric,
							arrayListDestinationPointHistoric,
							arrayListIsSpecialMoveHistoric,
							arrayListPiecePotentialyDeletedHistoric);
					chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
					historicIndex++;
				}

				// if the opponent is an artificial intelligence player it's its
				// time to player
				if (whitePlayer.getClass().getName() == "ComputerPlayer"
						&& chessRulesMan.WhatColorHasToPlay() == Color.white) {
					chessRulesMan.PlayComputerAndGetSquaresToBeRefreshed(
							((ComputerPlayer) whitePlayer).depth,
							listSquaresToBeRefresh,
							arrayListSourcePointHistoric,
							arrayListDestinationPointHistoric,
							arrayListIsSpecialMoveHistoric,
							arrayListPiecePotentialyDeletedHistoric);
					chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
					historicIndex++;
				}

				// after each move we check if the game is over
				checkIfGameIsOver();
			}
		}
	}

	// a computer player vs another player computer game
	public void ComputerVsComputerGame() throws InterruptedException {
		Calendar calendar = Calendar.getInstance();
		long beginingTime = calendar.getTimeInMillis();
		Date beginDate = new Date(beginingTime);
		while (true) {
			ArrayList<Point> listSquaresToBeRefresh = new ArrayList<Point>();
			if (chessRulesMan.WhatColorHasToPlay() == Color.black)
				chessRulesMan.PlayComputerAndGetSquaresToBeRefreshed(
						((ComputerPlayer) blackPlayer).depth,
						listSquaresToBeRefresh, arrayListSourcePointHistoric,
						arrayListDestinationPointHistoric,
						arrayListIsSpecialMoveHistoric,
						arrayListPiecePotentialyDeletedHistoric);
			else if (chessRulesMan.WhatColorHasToPlay() == Color.white)
				chessRulesMan.PlayComputerAndGetSquaresToBeRefreshed(
						((ComputerPlayer) whitePlayer).depth,
						listSquaresToBeRefresh, arrayListSourcePointHistoric,
						arrayListDestinationPointHistoric,
						arrayListIsSpecialMoveHistoric,
						arrayListPiecePotentialyDeletedHistoric);
			chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
			historicIndex++;

			// after each move we check if the game is over
			if (chessRulesMan.DoesCurrentPlayerIsMat() == true)
				break;
		}
		calendar = Calendar.getInstance();
		long endTime = calendar.getTimeInMillis();
		Date endDate = new Date(endTime);
		long diff = endDate.getTime() - beginDate.getTime();
		// System.out.println("Game is over total time : "+diff/1000+" seconds");
	}

	// check if the game is over and display a message about this
	public boolean checkIfGameIsOver() {
		if (chessRulesMan.DoesCurrentPlayerIsMat() == true) {
			if (chessRulesMan.WhatColorHasToPlay() == Color.black)
				javax.swing.JOptionPane.showMessageDialog(null, "White wins !");
			if (chessRulesMan.WhatColorHasToPlay() == Color.white)
				javax.swing.JOptionPane.showMessageDialog(null, "Black wins !");
			chessRulesMan.EndTheGame();
			return true;
		}
		return false;
	}

	// get the black computer level into the menu
	public int GetBlackLevel() {
		Iterator<JRadioButtonMenuItem> iteratorBlackLevel = arrayListBlackLevel
				.iterator();
		while (iteratorBlackLevel.hasNext()) {
			JRadioButtonMenuItem currentRadioButtonMenuItem = iteratorBlackLevel
					.next();
			if (currentRadioButtonMenuItem.isSelected() == true) {
				String stringLevel = currentRadioButtonMenuItem
						.getText()
						.substring(
								currentRadioButtonMenuItem.getText().length() - 1,
								currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out
				.println("Error while getting black level, no level selected");
		return -1;
	}

	// get the white computer level into the menu
	public int GetWhiteLevel() {
		Iterator<JRadioButtonMenuItem> iteratorWhiteLevel = arrayListWhiteLevel
				.iterator();
		while (iteratorWhiteLevel.hasNext()) {
			JRadioButtonMenuItem currentRadioButtonMenuItem = iteratorWhiteLevel
					.next();
			if (currentRadioButtonMenuItem.isSelected() == true) {
				String stringLevel = currentRadioButtonMenuItem
						.getText()
						.substring(
								currentRadioButtonMenuItem.getText().length() - 1,
								currentRadioButtonMenuItem.getText().length());
				return Integer.parseInt(stringLevel);
			}
		}
		System.out
				.println("Error while getting white level, no level selected");
		return -1;
	}

	public void PlayWhiteComputerIfNeededOrComputerVsComputerIfNeeded() {
		if (itemComputerPlaysBlack.getState() == true)
			try {
				ComputerVsComputerGame();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		else if (chessRulesMan.WhatColorHasToPlay() == Color.white) {
			ArrayList<Point> listSquaresToBeRefresh = new ArrayList<Point>();
			try {
				chessRulesMan.PlayComputerAndGetSquaresToBeRefreshed(
						((ComputerPlayer) whitePlayer).depth,
						listSquaresToBeRefresh, arrayListSourcePointHistoric,
						arrayListDestinationPointHistoric,
						arrayListIsSpecialMoveHistoric,
						arrayListPiecePotentialyDeletedHistoric);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
			historicIndex++;
			checkIfGameIsOver();
		}
	}

	public void PlayBlackComputerIfNeededOrComputerVsComputerIfNeeded() {
		if (itemComputerPlaysWhite.getState() == true)
			try {
				ComputerVsComputerGame();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		else if (chessRulesMan.WhatColorHasToPlay() == Color.black) {
			ArrayList<Point> listSquaresToBeRefresh = new ArrayList<Point>();
			try {
				chessRulesMan.PlayComputerAndGetSquaresToBeRefreshed(
						((ComputerPlayer) blackPlayer).depth,
						listSquaresToBeRefresh, arrayListSourcePointHistoric,
						arrayListDestinationPointHistoric,
						arrayListIsSpecialMoveHistoric,
						arrayListPiecePotentialyDeletedHistoric);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
			historicIndex++;
			checkIfGameIsOver();
		}
	}

	// look if an event has occurred on the menu's items
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getActionCommand().equals(changePlayerTurn) == true) {
			chessRulesMan.changePlayerTurn();
		}

		// if the user changes the black or white level, nothing is done, we
		// could
		if (actionEvent.getActionCommand()
				.substring(0, actionEvent.getActionCommand().length() - 2)
				.equals(blackPlayerLevel)) {
			if (itemComputerPlaysBlack.isSelected() == true)
				blackPlayer = new ComputerPlayer(Color.black, GetBlackLevel());
		}

		if (actionEvent.getActionCommand()
				.substring(0, actionEvent.getActionCommand().length() - 2)
				.equals(whitePlayerLevel)) {
			if (itemComputerPlaysWhite.isSelected() == true)
				whitePlayer = new ComputerPlayer(Color.white, GetWhiteLevel());
		}

		if (actionEvent.getActionCommand().equals(undoMove)) {
			// computer does not play anymore in this case
			whitePlayer = new HumanPlayer(Color.black);
			itemComputerPlaysWhite.setSelected(false);
			blackPlayer = new HumanPlayer(Color.black);
			itemComputerPlaysBlack.setSelected(false);

			// we can undo only if there are at least one move to undo
			if (arrayListSourcePointHistoric.size() > 0
					&& arrayListDestinationPointHistoric.size() > 0
					&& arrayListIsSpecialMoveHistoric.size() > 0
					&& arrayListPiecePotentialyDeletedHistoric.size() > 0
					&& historicIndex > 0) {
				// we need the source, destination and if it's a special move
				Point sourcePoint = arrayListSourcePointHistoric
						.get(historicIndex - 1);
				Point destinationPoint = arrayListDestinationPointHistoric
						.get(historicIndex - 1);
				Boolean isSpecialMove = arrayListIsSpecialMoveHistoric
						.get(historicIndex - 1);
				ArrayList<Point> listSquaresToBeRefresh = new ArrayList<Point>();
				Piece piecePotentialyDeleted = arrayListPiecePotentialyDeletedHistoric
						.get(historicIndex - 1);

				// we unmake the move, no need to save anything in the history
				chessRulesMan.UnMakeThisMoveAndGetSquaresToBeRefreshed(
						destinationPoint, sourcePoint, listSquaresToBeRefresh,
						isSpecialMove, piecePotentialyDeleted);
				chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
				historicIndex--;
			}
		}

		if (actionEvent.getActionCommand().equals(redoMove)) {
			// computer does not play anymore in this case
			whitePlayer = new HumanPlayer(Color.black);
			itemComputerPlaysWhite.setSelected(false);
			blackPlayer = new HumanPlayer(Color.black);
			itemComputerPlaysBlack.setSelected(false);

			if (arrayListSourcePointHistoric.size() > 0
					&& arrayListDestinationPointHistoric.size() > 0
					&& arrayListIsSpecialMoveHistoric.size() > 0
					&& arrayListPiecePotentialyDeletedHistoric.size() > 0
					&& historicIndex < arrayListSourcePointHistoric.size()) {
				ArrayList<Point> listSquaresToBeRefresh = new ArrayList<Point>();
				Point sourcePoint = arrayListSourcePointHistoric
						.get(historicIndex);
				Point destinationPoint = arrayListDestinationPointHistoric
						.get(historicIndex);
				Boolean isSpecialMove = arrayListIsSpecialMoveHistoric
						.get(historicIndex);
				Piece piecePotentialyDeleted = arrayListPiecePotentialyDeletedHistoric
						.get(historicIndex);
				chessRulesMan
						.MakeThisMoveAndGetSquaresToBeRefreshedWithoutHistoric(
								sourcePoint, destinationPoint,
								listSquaresToBeRefresh, isSpecialMove,
								piecePotentialyDeleted);
				chessBoard.DrawSeveralSquares(listSquaresToBeRefresh);
				historicIndex++;
			}
		}

		// check if computer plays black or not
		if (actionEvent.getActionCommand().equals(computerPlaysBlack)) {
			for (int counterHistoric = historicIndex; counterHistoric < arrayListSourcePointHistoric
					.size(); counterHistoric++)
				arrayListSourcePointHistoric.remove(counterHistoric);
			if (itemComputerPlaysBlack.isSelected() == true) {
				blackPlayer = new ComputerPlayer(Color.black, GetBlackLevel());
				PlayBlackComputerIfNeededOrComputerVsComputerIfNeeded();
			} else
				blackPlayer = new HumanPlayer(Color.black);
		}

		// check if computer plays black or not
		if (actionEvent.getActionCommand().equals(computerPlaysWhite)) {
			for (int counterHistoric = historicIndex; counterHistoric < arrayListSourcePointHistoric
					.size(); counterHistoric++)
				arrayListSourcePointHistoric.remove(counterHistoric);
			if (itemComputerPlaysWhite.isSelected() == true) {
				whitePlayer = new ComputerPlayer(Color.white, GetWhiteLevel());
				PlayWhiteComputerIfNeededOrComputerVsComputerIfNeeded();
			} else
				whitePlayer = new HumanPlayer(Color.black);
		}

		// create a new game
		if (actionEvent.getActionCommand().equals(newGame)) {
			arrayListSourcePointHistoric.clear();
			arrayListDestinationPointHistoric.clear();
			arrayListIsSpecialMoveHistoric.clear();
			arrayListPiecePotentialyDeletedHistoric.clear();
			historicIndex = 0;
			whitePlayer = new HumanPlayer(Color.black);
			itemComputerPlaysWhite.setSelected(false);
			blackPlayer = new HumanPlayer(Color.black);
			itemComputerPlaysBlack.setSelected(false);
			chessRulesMan.LaunchNewGame();
		}

		// we leave the application
		if (actionEvent.getActionCommand().equals(quit)) {
			System.exit(0);
		}
	}
}
