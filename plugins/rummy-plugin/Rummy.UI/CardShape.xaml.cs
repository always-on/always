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
using System.Windows.Media.Animation;

namespace Rummy.UI
{
    /// <summary>
    /// Interaction logic for UserControl1.xaml
    /// </summary>
    public partial class CardShape : UserControl
    {
        public const double CardWidth = 72;
        public const double CardHeight = 97;
        public const double CardWidthRect = 73;
        public const double CardHeightRect = 98;

        public CardShape()
        {
            InitializeComponent();

            aniFlipStart = (Storyboard)Resources["aniFlipStart"];
            aniFlipEnd = (Storyboard)Resources["aniFlipEnd"];

            ShowBack(WithAnimation.No);
        }

        private ICardController _controller = new NullCardController();
        private bool isDrag;
        private Point oldMousePos;
        private double beforeDragTop;
        private double beforeDragLeft;
        private Storyboard aniFlipStart;
        private Storyboard aniFlipEnd;
        private bool _showingBack;
        private bool _dragCancelAnimationInProgress;

        public bool ShowingBack
        {
            get { return _showingBack; }
        }
        public ICardController Controller
        {
            get
            {
                return _controller;
            }
            set
            {
                if (value != null)
                    _controller = value;
                else
                    _controller = new NullCardController();
            }
        }

        public void Highlight(bool on)
        {
            rectBorder.Visibility = on ? Visibility.Visible : Visibility.Collapsed;
        }

        private void imgCard_MouseEnter(object sender, MouseEventArgs e)
        {
            Controller.MouseEnter(sender, e);
        }

        private void imgCard_MouseLeave(object sender, MouseEventArgs e)
        {
            Controller.MouseLeave(sender, e);
        }

        public void ShowBack(WithAnimation animate)
        {
            _showingBack = true;

            if (animate == WithAnimation.Yes)
            {
                StartFlipAnimation(() => ShowBack(WithAnimation.No));
            }
            else
            {
                ClipImageToShowTheRightImage(7, 5);
            }
        }

        private void StartFlipAnimation(Action toCallWhenInTheMiddleOfFlip)
        {
            var animationClone = aniFlipStart.Clone();

            animationClone.Completed += (s, o) =>
            {
                toCallWhenInTheMiddleOfFlip();
                aniFlipEnd.Begin();
            }; ;

            animationClone.Begin(this);
        }

        private void ClipImageToShowTheRightImage(int x, int y)
        {
            ((RectangleGeometry)imgCard.Clip).Rect = new Rect(x * CardWidthRect, y * CardHeightRect, CardWidth,
                                                               CardHeight);
            var tran = (TranslateTransform)imgCard.RenderTransform;
            tran.X = -x * CardWidthRect;
            tran.Y = -y * CardHeightRect;

            imgCard.RenderTransformOrigin = new Point(0.05 + (x * 0.1), 0.08 + (y * 0.166666));
        }

        public void ShowFace(Suit suit, int rank, WithAnimation animate)
        {
            _showingBack = false;

            if (animate == WithAnimation.Yes)
            {
                StartFlipAnimation(() => ShowFace(suit, rank, WithAnimation.No));
                return;
            }

            int x, y;

            if (rank <= 10)
            {
                x = (rank - 1) % 2;
                y = (rank - 1) / 2;

                switch (suit)
                {
                    case Suit.Spades:
                        x += 6;
                        break;
                    case Suit.Diamonds:
                        x += 2;
                        break;
                    case Suit.Clubs:
                        x += 4;
                        break;
                }
            }
            else
            {
                int number = (rank - 11);
                switch (suit)
                {
                    case Suit.Spades:
                        number += 6;
                        break;
                    case Suit.Hearts:
                        number += 9;
                        break;
                    case Suit.Diamonds:
                        number += 3;
                        break;
                }

                x = (number % 2) + 8;
                y = number / 2;
            }

            ClipImageToShowTheRightImage(x, y);
        }

        private void imgCard_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (_controller.Draggable && !_dragCancelAnimationInProgress)
            {
                imgCard.CaptureMouse();
                isDrag = true;
                oldMousePos = e.GetPosition(LayoutRoot);
                beforeDragLeft = Canvas.GetLeft(this);
                beforeDragTop = Canvas.GetTop(this);
            }
        }

        private void imgCard_MouseMove(object sender, MouseEventArgs e)
        {
            if (isDrag)
            {
                Point newMousePos = e.GetPosition(LayoutRoot);

                double dx = newMousePos.X - oldMousePos.X;
                double dy = newMousePos.Y - oldMousePos.Y;

                Canvas.SetLeft(this, Canvas.GetLeft(this) + dx);
                Canvas.SetTop(this, Canvas.GetTop(this) + dy);
            }
        }

        private void imgCard_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            if (isDrag)
            {
                imgCard.ReleaseMouseCapture();
                isDrag = false;

                _controller.DragFinished();
            }
        }

        internal void CancelDrag()
        {
            _dragCancelAnimationInProgress = true;
            MoveWithAnimation(beforeDragLeft, beforeDragTop);
        }

        private void MoveWithAnimation(double left, double top)
        {
            const double duration = .5;

            var leftAnimation = AnimateDouble(Canvas.LeftProperty, left, duration);
            var topAnimation = AnimateDouble(Canvas.TopProperty, top, duration);

            Storyboard sb = new Storyboard();
            sb.Children.Add(leftAnimation);
            sb.Children.Add(topAnimation);
            sb.FillBehavior = FillBehavior.Stop;

            sb.Completed += (s, o) =>
            {
                Canvas.SetLeft(this, left);
                Canvas.SetTop(this, top);
                _dragCancelAnimationInProgress = false;
            };

            sb.Begin(this);
        }

        private static DoubleAnimation AnimateDouble(DependencyProperty property, double left, double duration)
        {
            DoubleAnimation leftAnimation = new DoubleAnimation(left, new Duration(TimeSpan.FromSeconds(duration)));
            Storyboard.SetTargetProperty(leftAnimation, new PropertyPath(property));
            leftAnimation.EasingFunction = new PowerEase();
            return leftAnimation;
        }
    }
}

