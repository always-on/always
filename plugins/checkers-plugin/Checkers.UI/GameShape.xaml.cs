using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.ComponentModel;
using System.Collections;
using System.Data;
using System.Resources;
using System.Windows.Media.Animation;

namespace Checkers.UI
{
	/// <summary>
	/// Interaction logic for GameShape.xaml
	/// </summary>
	public partial class GameShape : UserControl
	{
		bool okToMove = false;

        private enum Turn
        {
            Red,
            Black
        }

        #region Globals
        private bool IsDragging;
        private Point _startPoint;
        private Point _endPoint;
        private CheckerPiece currentPiece;
        private CheckerPiece capturedPiece;
        private Turn currentTurn;
		private DragAdorner _dragAdorner;
        #endregion

        void Window1_Loaded(object sender, RoutedEventArgs e)
        {
            this.grdBoard.Drop += new DragEventHandler(grdBoard_Drop);
            this.grdBoard.PreviewMouseLeftButtonUp += 
				new MouseButtonEventHandler(grdBoard_PreviewMouseLeftButtonUp);            
        }
        
        // Here, the source is the drop target which in this case is a Label. This is needed to get
        // a reference to the underlying grid cell. That way we know the cell to which to add the new 
        // image. 
        void grdBoard_Drop(object sender, DragEventArgs e)
        {
			// /red is user
			string from = "", to = ""; //hit = "";

            // use the label in the cell to get the current row and column
            EmptySpace l = e.Source as EmptySpace;
            int r = Grid.GetRow((EmptySpace)e.Source);
            int c = Grid.GetColumn((EmptySpace)e.Source);
            okToMove = false;
			from = currentPiece.row + "," + currentPiece.col;
			to = r + "," + c;

            CheckerPiece checker;
            if (currentPiece is RedChecker)
            {
                if (currentTurn != Turn.Red)
                {
                    System.Windows.Forms.MessageBox.Show("It's not your turn");
                    return; // it's not your turn.
                }

                // It's red's turn...
                checker = new RedChecker();
				if (l.row == currentPiece.row + 1 && (l.col == currentPiece.col + 1 
					|| l.col == currentPiece.col - 1))
				{
					okToMove = true;
				}
                
                //now check to see if we captured anything
                BlackChecker opponentPiece ;
                if (c == currentPiece.col + 2)
                {
                    opponentPiece = grdBoard.Children.OfType<BlackChecker>()
						.Where(p => p.row == currentPiece.row + 1 
							&& (p.col == currentPiece.col + 1)).SingleOrDefault();
					//hit = opponentPiece.row + "/" + opponentPiece.col;
                }
                else
                {
                    opponentPiece = grdBoard.Children.OfType<BlackChecker>()
						.Where(p => p.row == currentPiece.row + 1 
							&& (p.col == currentPiece.col - 1)).SingleOrDefault();
					//hit = opponentPiece.row + "/" + opponentPiece.col;
				}

                if (opponentPiece != null && l.row - currentPiece.row == 2)
                {
                    int validCol = (opponentPiece.col > currentPiece.col) ? 
						currentPiece.col + 2 : currentPiece.col - 2;
                    if (r == currentPiece.row + 2 && c == validCol)
                    {
                        Storyboard PieceCaptured = 
							opponentPiece.Resources["PieceCaptured"] as Storyboard;
                        capturedPiece = opponentPiece;

                        if (PieceCaptured != null)
                        {
                            PieceCaptured.Completed += new EventHandler(RemovePiece);
                            PieceCaptured.Begin();
                        }
                        
                        okToMove = true;
                    }
                }
                if (okToMove)
                {                    
                    //currentTurn = Turn.Black;
					this.Played(this, new cellEventArg
					{moveDesc = from+"//"+to}
					);
                }
            }
            else
            {
                if (currentTurn != Turn.Black)
                {
                    System.Windows.Forms.MessageBox.Show("It's not your turn");
                    return; // it's not your turn.
                }

                // It's black's turn...
                checker = new BlackChecker();
                if (l.row == currentPiece.row - 1 && 
					(l.col == currentPiece.col + 1 || l.col == currentPiece.col - 1))
                    okToMove = true;
				
				CaptureHumanCellInAgentMoveIfAny(r, c, l);
				
                if (okToMove)
                    currentTurn = Turn.Red;
            }

            if (okToMove)
            {
				MoveChecker(r, c, checker);
            }
        }

		public void MoveChecker(int r, int c, CheckerPiece checker)
		{
			checker.col = c;
			checker.row = r;

			// bind the mouse events
			checker.PreviewMouseLeftButtonDown += 
				new MouseButtonEventHandler(grdBoard_PreviewMouseLeftButtonDown);
			checker.PreviewMouseMove += 
				new MouseEventHandler(grdBoard_PreviewMouseMove);
			checker.Cursor = Cursors.Hand;
			checker.AllowDrop = false;

			// add the piece to the board
			Grid.SetRow(checker, r);
			Grid.SetColumn(checker, c);
			this.grdBoard.Children.Remove(currentPiece);
			grdBoard.Children.Add(checker);
			Storyboard DropPiece = checker.Resources["DropPiece"] as Storyboard;
			if (DropPiece != null)
			{
				DropPiece.Begin();
			}
		}

		public void CaptureHumanCellInAgentMoveIfAny(int r, int c, EmptySpace l)
		{
			RedChecker opponentPiece = null;
			if (c == currentPiece.col + 2)
			{
				opponentPiece = grdBoard.Children.OfType<RedChecker>()
					.Where(p => p.row == currentPiece.row - 1 
						&& (p.col == currentPiece.col + 1)).SingleOrDefault();
			}
			else if (c == currentPiece.col - 2)
			{
				opponentPiece = grdBoard.Children.OfType<RedChecker>()
					.Where(p => p.row == currentPiece.row - 1 
						&& (p.col == currentPiece.col - 1)).SingleOrDefault();
			}

			//FIXME: capturing a piece to the left isn't working //isn't it??
			if (opponentPiece != null && currentPiece.row - l.row == 2)
			{
				int validCol = (opponentPiece.col > currentPiece.col) ? 
					currentPiece.col + 2 : currentPiece.col - 2;
				if (r == currentPiece.row - 2 && c == validCol)
				{
					capturedPiece = opponentPiece;
					Storyboard PieceCaptured = 
						opponentPiece.Resources["PieceCaptured"] as Storyboard;
					if (PieceCaptured != null)
					{
						PieceCaptured.Completed += new EventHandler(RemovePiece);
						PieceCaptured.Begin();
					}
					okToMove = true;
				}
			}
		}

        void RemovePiece(object sender, EventArgs e)
        {
            if (capturedPiece != null)
            {
                capturedPiece.Visibility = Visibility.Hidden;

                if (capturedPiece is RedChecker)
                {
                    RedChecker deadman = new RedChecker();
                    Storyboard AddToGraveyard = 
						deadman.Resources["AddToGraveyard"] as Storyboard;
                    this.pnlBlackGraveyard.Children.Add(deadman);
                    AddToGraveyard.Begin();
                }
                else
                {
                    BlackChecker deadman = new BlackChecker();
                    Storyboard AddToGraveyard = 
						deadman.Resources["AddToGraveyard"] as Storyboard;
                    this.pnlRedGraveyard.Children.Add(deadman);
                    AddToGraveyard.Begin();
                }
                grdBoard.Children.Remove(capturedPiece);
            }
        }

		public GameShape()
		{
			this.InitializeComponent();
            this.grdBoard.AllowDrop = true;
            Reset();
		}

        /// <summary>
        /// This function loads the game pieces into the grid cells and prepares
        /// </summary>
        public void Reset()
        {
            currentTurn = Turn.Black; // Red goes first.
            this.pnlBlackGraveyard.Children.Clear();
            this.pnlRedGraveyard.Children.Clear();

            int col = 0;
            for (int row = 0; row < grdBoard.RowDefinitions.Count; row++)
            {
                for (col = 0; col < grdBoard.ColumnDefinitions.Count; col ++)
                {
                    Border b = new Border();
                    b.BorderThickness = new Thickness(1, 1, 1, 1);
                    b.BorderBrush = Brushes.Black;
                    Grid.SetColumn(b, col);
                    Grid.SetRow(b, row);
                    this.grdBoard.Children.Add(b);
                }
            }

            for (int row = 0; row < grdBoard.RowDefinitions.Count; row++)
            {
                // put a piece in every other cell
                for (col = (col % 2 != 0 ? 0 : 1); col < 
					grdBoard.ColumnDefinitions.Count; col += 2)
                {                    
                    EmptySpace l = new EmptySpace();
                    l.Margin = new Thickness(0, 0, 0, 0);
                    l.AllowDrop = true;
                    l.Background = Brushes.Black;
                    l.Name = "Label" + (row * col).ToString();
                    l.Drop += new DragEventHandler(grdBoard_Drop);
                    l.col = col;
                    l.row = row;

                    Grid.SetColumn(l, col);
                    Grid.SetRow(l, row);
                    this.grdBoard.Children.Add(l);

                    if (row < 3)
                    {
                        RedChecker redChecker = new RedChecker();
                        redChecker.PreviewMouseLeftButtonDown += 
							new MouseButtonEventHandler(grdBoard_PreviewMouseLeftButtonDown);
                        redChecker.PreviewMouseMove += 
							new MouseEventHandler(grdBoard_PreviewMouseMove);
                        redChecker.Cursor = Cursors.Hand;
                        redChecker.AllowDrop = false;
                        redChecker.col = col;
                        redChecker.row = row;

                        Grid.SetColumn(redChecker, col);
                        Grid.SetRow(redChecker, row);
                        this.grdBoard.Children.Add(redChecker);
                    }
                    if (row >= grdBoard.RowDefinitions.Count - 3)
                    {
                        BlackChecker blackChecker = new BlackChecker();
                        blackChecker.PreviewMouseLeftButtonDown += 
							new MouseButtonEventHandler(grdBoard_PreviewMouseLeftButtonDown);
                        blackChecker.PreviewMouseMove += 
							new MouseEventHandler(grdBoard_PreviewMouseMove);
                        blackChecker.Cursor = Cursors.Hand;
                        blackChecker.AllowDrop = false;
                        blackChecker.col = col;
                        blackChecker.row = row;

                        Grid.SetColumn(blackChecker, col);
                        Grid.SetRow(blackChecker, row);
                        this.grdBoard.Children.Add(blackChecker);
                    }
                }
            }
        }

        void grdBoard_PreviewMouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            _endPoint = e.GetPosition(null);
			AdornerLayerForDrag().Remove(_dragAdorner);
			_dragAdorner = null;
			
			this.ReleaseMouseCapture();
        }
        void grdBoard_PreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            _startPoint = e.GetPosition(null);
        }

        private void StartDrag(MouseEventArgs e)
        {
            IsDragging = true;
			CheckerPiece src;
			if (e.Source.GetType() == typeof(RedChecker))
				src = (RedChecker)e.Source;
			else
				src = (BlackChecker)e.Source;
			currentPiece = src;

			//>>
			_dragAdorner = new DragAdorner(currentPiece, currentPiece, true, 1);
			//currentPiece.Visibility = System.Windows.Visibility.Hidden;
			AdornerLayerForDrag().Add(_dragAdorner);
			//<<

			this.CaptureMouse();

            DataObject data = new DataObject(
				System.Windows.DataFormats.Text.ToString(), "abcd");
            DragDropEffects de = DragDrop.DoDragDrop(
				this.grdBoard, data, DragDropEffects.All);                       

            IsDragging = false;
        }

        void grdBoard_PreviewMouseMove(object sender, MouseEventArgs e)
        {
            if (e.LeftButton == MouseButtonState.Pressed && !IsDragging)
            {
                Point position = e.GetPosition(null);

                if (Math.Abs(position.X - _startPoint.X) 
					> SystemParameters.MinimumHorizontalDragDistance ||
                    Math.Abs(position.Y - _startPoint.Y) 
					> SystemParameters.MinimumVerticalDragDistance)
                {
                    StartDrag(e);
                }
            }
        }

		private AdornerLayer AdornerLayerForDrag()
		{
			return AdornerLayer.GetAdornerLayer((Visual)this.LayoutRoot);
		}

        private void btnReset_Click(object sender, RoutedEventArgs e)//rename to something with reset msg
        {
            this.grdBoard.Children.Clear();
            this.Reset();
        }

		public event EventHandler Played = delegate { };

		public void PlayAgentMove(string moveDesc)
		{
			
			this.Dispatcher.Invoke((Action)(() =>
			{
				currentTurn = Turn.Black;
				List<string> descs = moveDesc.Split("//".ToArray()).ToList();
				string from = descs[0], to = descs[2];
				
				int fromx = int.Parse(from.Split(',')[0]),
					fromy = int.Parse(from.Split(',')[1]),
					tox = int.Parse(to.Split(',')[0]),
					toy = int.Parse(to.Split(',')[1]);

				currentPiece = grdBoard.Children.OfType<BlackChecker>()
					.Where(p => (p.row == fromx) && (p.col == fromy)).SingleOrDefault();
				EmptySpace tempEmptySpace = grdBoard.Children.OfType<EmptySpace>()
					.Where(p => (p.row == tox) && (p.col == toy)).SingleOrDefault();

				CaptureHumanCellInAgentMoveIfAny(tox, toy, tempEmptySpace);
				MoveChecker(tox, toy, currentPiece);

				currentTurn = Turn.Red;

			}));
		}

    }

	class cellEventArg : EventArgs
	{
		public string moveDesc { get; set; }

	}
}