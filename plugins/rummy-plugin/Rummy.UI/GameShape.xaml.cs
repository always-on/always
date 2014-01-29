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
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Diagnostics;
using Agent.Tcp;

namespace Rummy.UI
{
    /// <summary>
    /// Interaction logic for GameShape.xaml
    /// </summary>
    public partial class GameShape : UserControl, IGameUIServices
    {
        public const Player HumanPlayer = Player.One;
        public const Player AgentPlayer = Player.Two;

        public PlayerCardsController HumanCardsController { get; private set; }
        public RandomAICardsController AgentCardsController { get; private set; }
        public DiscardPileController DiscardController { get; private set; }
        public StockPileController StockController { get; private set; }
        Dictionary<FanCanvas, ICardGroupController> FanCanvasControllers = new Dictionary<FanCanvas, ICardGroupController>();
        Dictionary<Player, List<FanCanvas>> MeldShapes = new Dictionary<Player, List<FanCanvas>>();

        public GameShape()
        {
            InitializeComponent();
			//SetItUp(Player.One);//ONLY for debug
        }

		public void SetItUp()
		{
            GameState = new GameState();

            HumanCardsController = new PlayerCardsController(this, humanCards, GameState, HumanPlayer);
            AgentCardsController = new RandomAICardsController(this, agentCards, GameState, AgentPlayer);
            DiscardController = new DiscardPileController(this, Discard, GameState);
            StockController = new StockPileController(this, Stock, GameState);

            FanCanvasControllers = new Dictionary<FanCanvas, ICardGroupController>()
            {
                {humanCards, HumanCardsController},
                {agentCards, AgentCardsController},
                {Discard, DiscardController},
                {Stock, StockController}
            };

			MeldShapes = new Dictionary<Player, List<FanCanvas>>();

            CreateMeldShapesFor(HumanPlayer, Canvas.GetTop(humanCards) - 150, 80);
            CreateMeldShapesFor(AgentPlayer, 180, -80);

            var agentMeldsControllers = MeldShapes[AgentPlayer]
                .Select(x => FanCanvasControllers[x])
                .Cast<MeldController>()
                .ToList();
            new PlayerMeldsUIManager(this, GameState, AgentPlayer, agentMeldsControllers);

            SubscribeToFanCanvasDropEvents();

            this.Loaded += (s, e) => 
            {
                 AgentCardsController.CheckForActionOpportunity();
            };
		}

		public void SetStartingPlayer(Player startingPlayer)
		{
			State startingState = startingPlayer == Player.One ? State.Player1Draw : State.Player2Draw;
			GameState.SetState(startingState);
		}

        private void SubscribeToFanCanvasDropEvents()
        {
            foreach (var fc in FanCanvasControllers.Keys)
            {
                fc.CardDrop += FanCanvas_CardDrop;
            }
        }

        private void CreateMeldShapesFor(Player player, double startTop, double topIncrement)
        {
            var shapes = new List<FanCanvas>();
            var top = startTop;
            for (int i = 0; i < 3; i++)
            {
                var fc = new FanCanvas() { Orientation = Orientation.Horizontal, Spacing = 15, AngleIncrement = 5, SortCards = true };
                shapes.Add(fc);
                MainCanvas.Children.Add(fc);
                Canvas.SetTop(fc, top);
                Canvas.SetLeft(fc, 640 + (CardGroupControllerBase.CardHeight) * i);

                var controller = new MeldController(this, fc, GameState, player);
                FanCanvasControllers.Add(fc, controller);

                top += topIncrement; 
            }

            MeldShapes[player] = shapes;
        }

        protected override Size MeasureOverride(Size constraint)
        {
            var baseMeasure = base.MeasureOverride(constraint);

            if (constraint.IsEmpty)
                return baseMeasure;

            double h = constraint.Height == double.PositiveInfinity ? Window.GetWindow(this).ActualHeight : constraint.Height;

            return new Size(baseMeasure.Width, Math.Max(h, baseMeasure.Height));
        }


        private GameState _gameState;
        public GameState GameState
        {
            get { return _gameState; }
            set
            {
                _gameState = value;
                InitilizeShapes();
            }
        }

        private void InitilizeShapes()
        {
        }

        private void FanCanvas_CardDrop(object sender, CardDropEventArgs e)
        {
            var hitTestResult = VisualTreeHelper.HitTest(this, e.MouseEventArgs.GetPosition(this));

            FanCanvas fan = null;
            if (hitTestResult != null)
                fan = FindFanCanvasAncestor(hitTestResult.VisualHit);

			if (fan == null)
			{
				((FanCanvas)sender).CancelDrag();
			}
			else
			{
				var sourceController = FanCanvasControllers[(FanCanvas)sender];
				var card = sourceController.CardFromShape(e.CardShape);
				//Debug.Assert(card != null);//***
				if (card == null)
				{
					((FanCanvas)sender).CancelDrag();
				}
				else
				{
					var targetController = FanCanvasControllers[fan];

					if (targetController.AcceptDrop(card))
					{
						sourceController.DropAcceptedNotification(card);
						if (sourceController == HumanCardsController && humanCards.HasVisibleCards() == false)
						{
							foreach (var mshape in MeldShapes[HumanPlayer])
							{
								((MeldController)FanCanvasControllers[mshape]).SubmitToGameState();
							}
						}
					}
					else
					{
						((FanCanvas)sender).CancelDrag();
					}
				}
			}
        }

        private FanCanvas FindFanCanvasAncestor(DependencyObject dependencyObject)
        {
            if (dependencyObject == null)
                return null;

            while (dependencyObject != null && !(dependencyObject is FanCanvas))
            {
                dependencyObject = VisualTreeHelper.GetParent(dependencyObject);
            }

            return dependencyObject as FanCanvas;
        }

        public FanCanvas GetPlayerCardsShape(Player player)
        {
            if (player == Player.One)
                return humanCards;

            return agentCards;
        }


        public MeldController GetControllerFor(Meld m)
        {
            foreach (var player in Enum.GetValues(typeof(Player)).Cast<Player>())
            {
                var idx = GameState.GetMelds(player).IndexOf(m);
                if (idx != -1)
                {
                    return MeldShapes[player]
                        .Select(x => FanCanvasControllers[x])
                        .Cast<MeldController>()
                        .FirstOrDefault(x => x.AssociatedMeldIndex == idx);
                }
            }

            return null;
        }

		public void MakeTheBoardPlayable()
		{
			this.Dispatcher.Invoke((Action)(() =>
			{
				MainCanvas.IsEnabled = true;
			}));
		}

		public void MakeTheBoardUnplayable()
		{
			this.Dispatcher.Invoke((Action)(() =>
			{
				MainCanvas.IsEnabled = false;
			}));
		}
    }
}
