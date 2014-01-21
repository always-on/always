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
using Agent.Tcp;
using System.Media;
using System.IO;
using System.Reflection;

namespace Checkers.UI
{
	/// <summary>
	/// Interaction logic for GameShape.xaml
	/// </summary>
	public partial class GameShape : UserControl
	{
		bool okToMove = false;
		int illegalTouchCounter = 0;
		private static int RED_LAST_ROW = 0;
		private static int BLACK_LAST_ROW = 7;

		private int latestC = 0, latestR = 0;
		private CheckerPiece LatestRedTryingToMove = null;
		SoundPlayer sound;

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

            if (currentPiece is RedChecker || currentPiece is RedKingChecker)
            {
                if (currentTurn != Turn.Red)
                {
					// Should never be here
                    System.Windows.Forms.MessageBox.Show("It's not your turn");
                    return; 
                }

				illegalTouchCounter = 0;

                // It's red's turn...
				if (currentPiece is RedKingChecker
					|| (currentPiece is RedChecker && r == RED_LAST_ROW))
					checker = new RedKingChecker();
				else
					checker = new RedChecker();


				if (currentPiece is RedChecker || currentPiece is RedKingChecker)
				{
					if ((l.row == currentPiece.row - 1 
						&& (l.col == currentPiece.col + 1 || l.col == currentPiece.col - 1))
						|| 
						((l.row == currentPiece.row + 1 && currentPiece is RedKingChecker) 
						&& (l.col == currentPiece.col + 1 || l.col == currentPiece.col - 1)))
					{
						okToMove = true;
					}
				}

				//now check to see if user captured anything
				//if the logic is hard to follow, 
				//it basically checks the location if the supposedly hit agent(black) checker
				//based on the fact that user (red) piece could be crown, and assign a 
				//crowned or normal hit piece, if any, to the opponentPiece variable.
				//>>
				CheckerPiece opponentPiece = null; //was black
				
				if (c == currentPiece.col + 2)
				{
					if (r == currentPiece.row - 2)
					{
						if (grdBoard.Children.OfType<BlackChecker>()
							.Where(p => p.row == currentPiece.row - 1
								&& (p.col == currentPiece.col + 1)).SingleOrDefault() != null)
							opponentPiece = grdBoard.Children.OfType<BlackChecker>()
								.Where(p => p.row == currentPiece.row - 1
									&& (p.col == currentPiece.col + 1)).SingleOrDefault();
						else
							opponentPiece = grdBoard.Children.OfType<BlackKingChecker>()
							.Where(p => p.row == currentPiece.row - 1
								&& (p.col == currentPiece.col + 1)).SingleOrDefault();
					}
					else if (r == currentPiece.row + 2 && currentPiece is RedKingChecker)
					{
						if (grdBoard.Children.OfType<BlackChecker>()
							.Where(p => p.row == currentPiece.row + 1
								&& (p.col == currentPiece.col + 1)).SingleOrDefault() != null)
							opponentPiece = grdBoard.Children.OfType<BlackChecker>()
								.Where(p => p.row == currentPiece.row + 1
									&& (p.col == currentPiece.col + 1)).SingleOrDefault();
						else
							opponentPiece = grdBoard.Children.OfType<BlackKingChecker>()
							.Where(p => p.row == currentPiece.row + 1
								&& (p.col == currentPiece.col + 1)).SingleOrDefault();
					}
				}
				else if (c == currentPiece.col - 2)
				{
					if (r == currentPiece.row - 2)
					{
						if (grdBoard.Children.OfType<BlackChecker>()
							.Where(p => p.row == currentPiece.row - 1
								&& (p.col == currentPiece.col - 1)).SingleOrDefault() != null)
							opponentPiece = grdBoard.Children.OfType<BlackChecker>()
								.Where(p => p.row == currentPiece.row - 1
									&& (p.col == currentPiece.col - 1)).SingleOrDefault();
						else
							opponentPiece = grdBoard.Children.OfType<BlackKingChecker>()
							.Where(p => p.row == currentPiece.row - 1
								&& (p.col == currentPiece.col - 1)).SingleOrDefault();
					}
					else if ((r == currentPiece.row + 2 && currentPiece is RedKingChecker))
					{
						if (grdBoard.Children.OfType<BlackChecker>()
							.Where(p => p.row == currentPiece.row + 1
								&& (p.col == currentPiece.col - 1)).SingleOrDefault() != null)
							opponentPiece = grdBoard.Children.OfType<BlackChecker>()
								.Where(p => p.row == currentPiece.row + 1
									&& (p.col == currentPiece.col - 1)).SingleOrDefault();
						else
							opponentPiece = grdBoard.Children.OfType<BlackKingChecker>()
							.Where(p => p.row == currentPiece.row + 1
								&& (p.col == currentPiece.col - 1)).SingleOrDefault();
					}
				}
				//<<

				//>> now remove if any black or black king was hit
                if (opponentPiece != null && Math.Abs(l.row - currentPiece.row) == 2)
                {
                    int validCol = (opponentPiece.col > currentPiece.col) ? 
						currentPiece.col + 2 : currentPiece.col - 2;
                    if (((r == currentPiece.row - 2) 
						|| (Math.Abs(r - currentPiece.row) == 2 
						&& currentPiece is RedKingChecker)) && c == validCol)
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
					this.UserPlayed(this, new CheckerEventArg
					{moveDesc = from+"//"+to});
                }
            }

            else // that is, if touched checker is black or black king <><><><><><><><><<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>
            {
				//when user touches agent stuff...
                if (currentTurn != Turn.Black)
                {
					this.UserTouchedAgentChecker(this,
						new UserTouchedAgentStuffEventArg 
						{howManyTimes = ++illegalTouchCounter});
                    //System.Windows.Forms.MessageBox.Show("Wait, that's mine!");
                    return;
                }

				//should never be here
				//>>Few lines below only for debugging (until "<<")
				checker = new BlackChecker();
				if (l.row == currentPiece.row + 1 && 
				    (l.col == currentPiece.col + 1 || l.col == currentPiece.col - 1))
				    okToMove = true;
				CaptureHumanCellInAgentMoveIfAny(r, c, l);
				if (okToMove)	currentTurn = Turn.Red;
				//<<
            }

			//if (okToMove
			//    && (checker is BlackChecker || checker is BlackKingChecker))
			//{
			//    MoveChecker(r, c, checker);
			//}

			//not doing the move until confirmed by Java side
			if (okToMove && 
				(checker is RedChecker || checker is RedKingChecker))
			{
				latestR = r; latestC = c;
				LatestRedTryingToMove = checker;
			}
        }

		public void MoveChecker(int r, int c, CheckerPiece checker)
		{
			this.Dispatcher.Invoke((Action)(() =>
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
			}));
		}

		public void CaptureHumanCellInAgentMoveIfAny(int r, int c, EmptySpace l)
		{
			//If the logic is hard to follow, read below:
			//it basically checks the location of the supposedly hit user (red) checker
			//based on the fact that agent's (black) piece could be crown, and so assigns  
			//crowned or normal hit piece, if any, to the opponentPiece variable.
			CheckerPiece opponentPiece = null;
			//>>
			if (c == currentPiece.col + 2)
			{
				if (r == currentPiece.row + 2)
				{
					if (grdBoard.Children.OfType<RedChecker>()
						.Where(p => p.row == currentPiece.row + 1
							&& (p.col == currentPiece.col + 1)).SingleOrDefault() != null)
						opponentPiece = grdBoard.Children.OfType<RedChecker>()
							.Where(p => p.row == currentPiece.row + 1
								&& (p.col == currentPiece.col + 1)).SingleOrDefault();
					else
						opponentPiece = grdBoard.Children.OfType<RedKingChecker>()
						.Where(p => p.row == currentPiece.row + 1
							&& (p.col == currentPiece.col + 1)).SingleOrDefault();
				}
				else if (r == currentPiece.row - 2 && currentPiece is BlackKingChecker)
				{
					if (grdBoard.Children.OfType<RedChecker>()
					.Where(p => p.row == currentPiece.row - 1
					&& (p.col == currentPiece.col + 1)).SingleOrDefault() != null)
						opponentPiece = grdBoard.Children.OfType<RedChecker>()
						.Where(p => p.row == currentPiece.row - 1
						&& (p.col == currentPiece.col + 1)).SingleOrDefault();
					else
						opponentPiece = grdBoard.Children.OfType<RedKingChecker>()
					.Where(p => p.row == currentPiece.row - 1
					&& (p.col == currentPiece.col + 1)).SingleOrDefault();
				}
			}
			else if (c == currentPiece.col - 2)
			{
				if (r == currentPiece.row + 2)
				{
					if (grdBoard.Children.OfType<RedChecker>()
						.Where(p => p.row == currentPiece.row + 1
							&& (p.col == currentPiece.col - 1)).SingleOrDefault() != null)
						opponentPiece = grdBoard.Children.OfType<RedChecker>()
							.Where(p => p.row == currentPiece.row + 1
								&& (p.col == currentPiece.col - 1)).SingleOrDefault();
					else
						opponentPiece = grdBoard.Children.OfType<RedKingChecker>()
						.Where(p => p.row == currentPiece.row + 1
							&& (p.col == currentPiece.col - 1)).SingleOrDefault();
				}
				else if (r == currentPiece.row - 2 && currentPiece is BlackKingChecker)
				{
					if (grdBoard.Children.OfType<RedChecker>()
					.Where(p => p.row == currentPiece.row - 1
						&& (p.col == currentPiece.col - 1)).SingleOrDefault() != null)
						opponentPiece = grdBoard.Children.OfType<RedChecker>()
						.Where(p => p.row == currentPiece.row - 1
							&& (p.col == currentPiece.col - 1)).SingleOrDefault();
					else
						opponentPiece = grdBoard.Children.OfType<RedKingChecker>()
					.Where(p => p.row == currentPiece.row - 1
						&& (p.col == currentPiece.col - 1)).SingleOrDefault();
				}
			}
			//<<

			//>> now remove if any red or red king was hit
			if (opponentPiece != null && Math.Abs(currentPiece.row - l.row) == 2)
			{
				int validCol = (opponentPiece.col > currentPiece.col) ? 
					currentPiece.col + 2 : currentPiece.col - 2;
				if (((r == currentPiece.row + 2) 
					|| (Math.Abs(r - currentPiece.row) == 2 
					&& currentPiece is BlackKingChecker)) && c == validCol)
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
				else if (capturedPiece is BlackChecker)
                {
                    BlackChecker deadman = new BlackChecker();
                    Storyboard AddToGraveyard = 
						deadman.Resources["AddToGraveyard"] as Storyboard;
                    this.pnlRedGraveyard.Children.Add(deadman);
                    AddToGraveyard.Begin();
                }
				else if (capturedPiece is RedKingChecker)
				{
					RedKingChecker deadman = new RedKingChecker();
					Storyboard AddToGraveyard =
						deadman.Resources["AddToGraveyard"] as Storyboard;
					this.pnlBlackGraveyard.Children.Add(deadman);
					AddToGraveyard.Begin();
				}
				else if (capturedPiece is BlackKingChecker)
				{
					BlackKingChecker deadman = new BlackKingChecker();
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
			sound = new SoundPlayer(GetResourceStream("Assets/playingSound.wav"));
			this.InitializeComponent();
            this.grdBoard.AllowDrop = true;
            Reset();
		}

		public void ResetGame(IUIThreadDispatcher uiThreadDispatcher)
		{
			uiThreadDispatcher.BlockingInvoke(() =>
			{
				this.grdBoard.Children.Clear();
				this.Reset();
			});
		}

        /// <summary>
        /// This function loads the game pieces into the grid cells and prepares everything
        /// </summary>
        public void Reset()
        {
            currentTurn = Turn.Black;
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

                    if (row >= grdBoard.RowDefinitions.Count - 3)
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
					if (row < 3)
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
			else if (e.Source.GetType() == typeof(BlackChecker))
				src = (BlackChecker)e.Source;
			else if(e.Source.GetType() == typeof(RedKingChecker))
				src = (RedKingChecker)e.Source;
			else
				src = (BlackKingChecker)e.Source;
			
			currentPiece = src;

			//drag adroner (Not currently used)>>
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

		public event EventHandler UserPlayed = delegate { };
		public event EventHandler UserTouchedAgentChecker = delegate { };

		public void PlayAgentMove(string moveDesc)
		{
			this.Dispatcher.Invoke((Action)(() =>
			{
				CheckerPiece checker = null;
				currentTurn = Turn.Black;
				List<string> descs = moveDesc.Split("//".ToArray()).ToList();
				string from = descs[0], to = descs[2];
				
				int fromx = int.Parse(from.Split(',')[0]),
					fromy = int.Parse(from.Split(',')[1]),
					tox = int.Parse(to.Split(',')[0]),
					toy = int.Parse(to.Split(',')[1]);

				//try black and black king, to find out which one it is
				if (grdBoard.Children.OfType<BlackChecker>()
					.Where(p => (p.row == fromx) && (p.col == fromy)).SingleOrDefault() != null)
				{
					currentPiece = grdBoard.Children.OfType<BlackChecker>()
					.Where(p => (p.row == fromx) && (p.col == fromy)).SingleOrDefault();
					
					if (tox == BLACK_LAST_ROW)
						checker = new BlackKingChecker();
					else
						checker = new BlackChecker();
				}
				else
				{
					currentPiece = grdBoard.Children.OfType<BlackKingChecker>()
					.Where(p => (p.row == fromx) && (p.col == fromy)).SingleOrDefault();
					
					checker = new BlackKingChecker();
				}
				
				EmptySpace tempEmptySpace = grdBoard.Children.OfType<EmptySpace>()
					.Where(p => (p.row == tox) && (p.col == toy)).SingleOrDefault();

				CaptureHumanCellInAgentMoveIfAny(tox, toy, tempEmptySpace);
				
				sound.Play();
				MoveChecker(tox, toy, checker);
				currentTurn = Turn.Red;

			}));
		}


		public void ReceivedConfirmation()
		{
			sound.Play();
			MoveChecker(latestR, latestC, LatestRedTryingToMove);
		}

		public void MakeTheBoardPlayable()
		{
			this.Dispatcher.Invoke((Action)(() =>
			{
				grdBoard.IsEnabled = true;
			}));
		}

		public void MakeTheBoardUnplayable()
		{
			this.Dispatcher.Invoke((Action)(() =>
			{
				grdBoard.IsEnabled = false;
			}));
		}

		private static UnmanagedMemoryStream GetResourceStream(string resName)
		{
			var assembly = Assembly.GetExecutingAssembly();
			var strResources = assembly.GetName().Name + ".g.resources";
			var rStream = assembly.GetManifestResourceStream(strResources);
			var resourceReader = new ResourceReader(rStream);
			var items = resourceReader.OfType<DictionaryEntry>();
			var stream = items.First(x => (x.Key as string) == resName.ToLower()).Value;
			return (UnmanagedMemoryStream)stream;
		}
	}

	class CheckerEventArg : EventArgs
	{
		public string moveDesc { get; set; }
	}

	class UserTouchedAgentStuffEventArg : EventArgs
	{
		public int howManyTimes { get; set; }
	}

}