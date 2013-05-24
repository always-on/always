using System.Windows;
using System.Windows.Media;
using System.Windows.Controls.Primitives;
using System.ComponentModel;
using System;
using System.Windows.Controls;
using System.Windows.Media.Animation;

namespace sRummy.UI
{
    //From Adam Nathan's WPF 4 Unleashed
    public class CardShape : ToggleButton
    {
        string _faceValue;

        static CardShape()
        {
            // Override style
            DefaultStyleKeyProperty.OverrideMetadata(typeof(CardShape),
                new FrameworkPropertyMetadata(typeof(CardShape)));

            // Register Face dependency property
            FaceProperty = DependencyProperty.Register("Face",
                typeof(string), typeof(CardShape));

            HighlightOnVisibilityProperty = DependencyProperty.Register("HighlightOnVisibility",
                typeof(bool), typeof(CardShape), new PropertyMetadata(true));

            HideFaceProperty = DependencyProperty.Register("HideFace", typeof(bool), typeof(CardShape));
        }

        public CardShape()
        {
            DependencyPropertyDescriptor.FromProperty(FaceProperty, typeof(CardShape)).AddValueChanged(this, OnFaceChanged);
            DependencyPropertyDescriptor.FromProperty(HideFaceProperty, typeof(CardShape)).AddValueChanged(this, (s, o) =>
            {
                if (HideFace)
                    Face = null;
                else
                    Face = _faceValue;
            });
        }

        private void OnFaceChanged(object sender, EventArgs e)
        {
            if(Face != null || !HideFace)
                _faceValue = Face;
            if (HideFace)
                Face = null;
        }

        public string Face
        {
            get { return (string)GetValue(FaceProperty); }
            set { SetValue(FaceProperty, value); }
        }

        public bool HideFace
        {
            get { return (bool)GetValue(HideFaceProperty); }
            set { SetValue(HideFaceProperty, value); }
        }

        public static DependencyProperty FaceProperty, HideFaceProperty, HighlightOnVisibilityProperty;
        private bool isDrag;

        public bool IsDrag
        {
            get { return isDrag; }
            set { isDrag = value; }
        }

        public bool HighlightOnVisibility
        {
            get { return (bool)GetValue(HighlightOnVisibilityProperty); }
            set { SetValue(HighlightOnVisibilityProperty, value); }
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

        public string GetCardFace()
        {
            return _faceValue;
        }
    }
}