using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Linq;
using System.Windows.Input;
using System.Windows.Documents;
using System.Collections.Generic;

namespace Rummy.UI
{
    public class FanCanvas : Panel
    {
        public static readonly DependencyProperty OrientationProperty =
          DependencyProperty.Register("Orientation", typeof(Orientation),
          typeof(FanCanvas), new FrameworkPropertyMetadata(Orientation.Horizontal,
          FrameworkPropertyMetadataOptions.AffectsArrange));

        public static readonly DependencyProperty SpacingProperty =
          DependencyProperty.Register("Spacing", typeof(double),
          typeof(FanCanvas), new FrameworkPropertyMetadata(10d,
          FrameworkPropertyMetadataOptions.AffectsArrange));

        public static readonly DependencyProperty AngleIncrementProperty =
          DependencyProperty.Register("AngleIncrement", typeof(double),
          typeof(FanCanvas), new FrameworkPropertyMetadata(10d,
          FrameworkPropertyMetadataOptions.AffectsArrange));

        public static readonly DependencyProperty SortCardsProperty =
            DependencyProperty.Register("SortCards", typeof(bool),
            typeof(FanCanvas), new FrameworkPropertyMetadata(false,
                FrameworkPropertyMetadataOptions.AffectsArrange));

        public static readonly RoutedEvent CardDropEvent = EventManager.RegisterRoutedEvent(
            "CardDrop",
            RoutingStrategy.Bubble,
            typeof(EventHandler<CardDropEventArgs>),
            typeof(FanCanvas));

        CardShape _cardBeingDragged;
        List<CardShape> _dragsInLimbo = new List<CardShape>();
        Point _dragOriginalPos;
        bool _isDrag;
        private DragAdorner _dragAdorner;
        Dictionary<CardShape, Point> cardLocations = new Dictionary<CardShape, Point>();
        CardPlaceHolder placeHolder;

        public FanCanvas()
        {
            this.ClipToBounds = false;

            placeHolder = new CardPlaceHolder() { Width = CardGroupControllerBase.CardWidth, Height = CardGroupControllerBase.CardHeight };
            this.Children.Add(placeHolder);

            this.PreviewMouseLeftButtonDown += new System.Windows.Input.MouseButtonEventHandler(FanCanvas_PreviewMouseLeftButtonDown);
            this.PreviewMouseLeftButtonUp += new System.Windows.Input.MouseButtonEventHandler(FanCanvas_PreviewMouseLeftButtonUp);
            this.PreviewMouseMove += new System.Windows.Input.MouseEventHandler(FanCanvas_MouseMove);
        }

        void FanCanvas_PreviewMouseLeftButtonUp(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            if (_cardBeingDragged != null)
            {
                _dragsInLimbo.Add(_cardBeingDragged);
                _cardBeingDragged = null;
                if (_isDrag)
                {
                    _isDrag = false;
                    AdornerLayerForDrag().Remove(_dragAdorner);
                    _dragAdorner = null;

                    this.ReleaseMouseCapture();

                    var dropEventArgs = new CardDropEventArgs(_dragsInLimbo.Last(), e, CardDropEvent, this);
                    RaiseEvent(dropEventArgs);
                }
            }
        }

        void FanCanvas_MouseMove(object sender, System.Windows.Input.MouseEventArgs e)
        {
            if (_cardBeingDragged != null)
            {
                var currentPos = GetMousePositionForDragCalculations(e);
                var dragVector = currentPos - _dragOriginalPos;

                if (_isDrag == false && (Math.Abs(dragVector.X) >= SystemParameters.MinimumHorizontalDragDistance
                        || Math.Abs(dragVector.Y) >= SystemParameters.MinimumVerticalDragDistance))
                {
                    _isDrag = true;
                    _dragAdorner = new DragAdorner(_cardBeingDragged, _cardBeingDragged, true, 1);

                    _cardBeingDragged.Visibility = System.Windows.Visibility.Hidden;
                    AdornerLayerForDrag().Add(_dragAdorner);

                    this.CaptureMouse();
                }

                if (_isDrag)
                {
                    Console.WriteLine(currentPos);

                    var p = this.TransformToVisual(AdornerLayerForDrag()).Transform(new Point(0, 0));
                    p += new Vector(cardLocations[_cardBeingDragged].X, cardLocations[_cardBeingDragged].Y);
                    //var p = cardLocations[_cardBeingDragged];
                    //p = TransformToVisual(AdornerLayerForDrag()).Transform(p);

                    //var p = _cardBeingDragged.TransformToVisual(AdornerLayerForDrag()).Transform(new Point(0, 0));

                    _dragAdorner.LeftOffset = dragVector.X;
                    _dragAdorner.TopOffset = dragVector.Y;
                }
            }
        }

        private AdornerLayer AdornerLayerForDrag()
        {
            return AdornerLayer.GetAdornerLayer((Visual)Parent);
        }

        private Point GetMousePositionForDragCalculations(System.Windows.Input.MouseEventArgs e)
        {
            return e.GetPosition(Window.GetWindow(this));
        }

        private void AddToRenderTransform(UIElement element, Transform tr)
        {
            if (element.RenderTransform == null || (element.RenderTransform is TransformGroup) == false)
            {
                var group = new TransformGroup();
                group.Children.Add(element.RenderTransform);

                element.RenderTransform = group;
            }

            ((TransformGroup)element.RenderTransform).Children.Add(tr);
        }

        void FanCanvas_PreviewMouseLeftButtonDown(object sender, System.Windows.Input.MouseButtonEventArgs e)
        {
            if (e.Source is CardShape)
            {
                var card = (CardShape)e.Source;

                _dragOriginalPos = GetMousePositionForDragCalculations(e);
                _cardBeingDragged = card;
            }
        }

        public Orientation Orientation
        {
            get { return (Orientation)GetValue(OrientationProperty); }
            set { SetValue(OrientationProperty, value); }
        }

        public double Spacing
        {
            get { return (double)GetValue(SpacingProperty); }
            set { SetValue(SpacingProperty, value); }
        }

        public double AngleIncrement
        {
            get { return (double)GetValue(AngleIncrementProperty); }
            set { SetValue(AngleIncrementProperty, value); }
        }

        public bool SortCards
        {
            get { return (bool)GetValue(SortCardsProperty); }
            set { SetValue(SortCardsProperty, value); }
        }

        public event EventHandler<CardDropEventArgs> CardDrop
        {
            add { AddHandler(CardDropEvent, value); }
            remove { RemoveHandler(CardDropEvent, value); }
        }

        protected override Size MeasureOverride(Size availableSize)
        {
            foreach (UIElement child in this.Children)
            {
                // Give each child all the space it wants
                if (child != null)
                    child.Measure(new Size(Double.PositiveInfinity,
                                           Double.PositiveInfinity));
            }

            // The SimpleCanvas itself needs no space
            return new Size(0, 0);
        }

        protected override Size ArrangeOverride(Size finalSize)
        {
            Point location = new Point(0, 0);

            if (Children.OfType<CardShape>().Count() == 0)
            {
                if (Children.Contains(placeHolder) == false)
                    Children.Add(placeHolder);
                placeHolder.Visibility = System.Windows.Visibility.Visible;
            }
            else
            {
                placeHolder.Visibility = System.Windows.Visibility.Collapsed;
            }

            if (placeHolder.Visibility != System.Windows.Visibility.Collapsed)
            {
                placeHolder.Arrange(new Rect(location, placeHolder.DesiredSize));
                var a = Orientation == System.Windows.Controls.Orientation.Vertical ? 90 : 0;
                placeHolder.RenderTransform = new RotateTransform(a, placeHolder.RenderSize.Width / 2, placeHolder.RenderSize.Height);

            }

            double angle = GetStartingAngle();
            int n = 0;

            var cardShapes = Children.OfType<CardShape>();
            if(SortCards)
                cardShapes = cardShapes.OrderBy(x => GetOrderIntegerFromFaceString(x.GetCardFace()));
            
            foreach (CardShape child in cardShapes)
            {
                if (child != null)
                {
                    Panel.SetZIndex(child, n);
                    n++;

                    // Give the child its desired size
                    child.Arrange(new Rect(location, child.DesiredSize));
                    cardLocations[child] = location;

                    // WARNING: Overwrite any RenderTransform with one that
                    //          arranges children in the fan shape
                    var trans = new RotateTransform(angle, child.RenderSize.Width / 2, child.RenderSize.Height);
                    child.RenderTransform = trans;

                    // Update the offset and angle for the next child
                    if (Orientation == Orientation.Vertical)
                        location.Y += Spacing;
                    else
                        location.X += Spacing;

                    angle += AngleIncrement;
                }
            }

            // Fill all the space given
            return finalSize;
        }

        private int GetOrderIntegerFromFaceString(string p)
        {
            if (string.IsNullOrEmpty(p))
                return 0;
            int suitInt = 0;
            switch (p[0])
            {
                case 'S':
                    suitInt = 1;
                    break;
                case 'D':
                    suitInt = 2;
                    break;
                case 'C':
                    suitInt = 3;
                    break;
                case 'H':
                    suitInt = 4;
                    break;
            }

            int rankInt = 0;
            string rankStr = p.Substring(1);
            switch (rankStr)
            {
                case "A":
                    rankInt = 1;
                    break;
                case "J":
                    rankInt = 11;
                    break;
                case "Q":
                    rankInt = 12;
                    break;
                case "K":
                    rankInt = 13;
                    break;
                default:
                    rankInt = int.Parse(rankStr);
                    break;
            }

            //descending
            rankInt = 14 - rankInt;

            return suitInt + rankInt * 4;
        }

        double GetStartingAngle()
        {
            double angle;

            if (this.Children.Count % 2 != 0)
                // Odd, so the middle child will have angle == 0
                angle = -AngleIncrement * (this.Children.Count / 2);
            else
                // Even, so the middle two children will be half of the AngleIncrement on either side of 0
                angle = -AngleIncrement * (this.Children.Count / 2) + AngleIncrement / 2;

            // Rotate 90 degrees if vertical
            if (Orientation == Orientation.Vertical)
                angle += 90;

            return angle;
        }

        public void CancelDrag()
        {
            var lastDraggedCard = _dragsInLimbo.Last();
            CancelDrag(lastDraggedCard);
        }

        public void CancelDrag(CardShape draggedCard)
        {
            _dragsInLimbo.Remove(draggedCard);
            draggedCard.HighlightOnVisibility = true;
            draggedCard.Visibility = System.Windows.Visibility.Visible;
        }

        public void CancelDrag(Card draggedCard)
        {
            var shape = _dragsInLimbo.FirstOrDefault(x => x.GetCardFace() == draggedCard.ToString());

            if (shape != null)
                CancelDrag(shape);
        }

        protected override void OnVisualChildrenChanged(DependencyObject visualAdded, DependencyObject visualRemoved)
        {
            base.OnVisualChildrenChanged(visualAdded, visualRemoved);
        }

        internal bool HasVisibleCards()
        {
            return Children.OfType<CardShape>().Any(x => x.Visibility == System.Windows.Visibility.Visible);
        }   
    }
}