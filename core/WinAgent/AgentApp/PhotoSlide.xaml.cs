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
using System.IO;
using System.Windows.Media.Animation;

namespace AgentApp
{
    /// <summary>
    /// Interaction logic for PhotoSlide.xaml
    /// </summary>
    public partial class PhotoSlide : UserControl
    {
        string[] _imageFileNames;
        int _currentIdx;

        public PhotoSlide()
        {
            InitializeComponent();

            _imageFileNames = Directory.GetFiles("SamplePhotos");

            this.Loaded += new RoutedEventHandler(PhotoSlide_Loaded);
        }

        void PhotoSlide_Loaded(object sender, RoutedEventArgs e)
        {
            RenderCurrentPhoto();
        }

        protected override Size MeasureOverride(Size constraint)
        {
            var baseMeasure = base.MeasureOverride(constraint);

            if (constraint.IsEmpty)
                return baseMeasure;

            double h = constraint.Height == double.PositiveInfinity ? Window.GetWindow(this).ActualHeight : constraint.Height;

            return new Size(baseMeasure.Width, Math.Max(h, baseMeasure.Height));
        }

        void IncrementIndex()
        {
            _currentIdx++;
            if (_currentIdx >= _imageFileNames.Length)
                _currentIdx = 0;
        }

        void DecrementIndex()
        {
            _currentIdx--;
            if (_currentIdx < 0)
                _currentIdx = _imageFileNames.Length - 1;
        }

        string GetCurrentFileName()
        {
            return _imageFileNames[_currentIdx];
        }

        ImageSource GetCurrentPhoto()
        {
            BitmapImage bi = new BitmapImage();
            bi.BeginInit();
            bi.UriSource = new Uri(GetCurrentFileName(), UriKind.Relative);
            bi.EndInit();
            return bi;
        }

        void RenderCurrentPhoto()
        {
            Storyboard storyboard = new Storyboard();
            TimeSpan duration = new TimeSpan(0, 0, 1);
            DoubleAnimation animation = new DoubleAnimation();

            animation.FillBehavior = FillBehavior.Stop;
            animation.From = 0.0;
            animation.To = 1.0;
            animation.Duration = new Duration(duration);
            // Configure the animation to target de property Opacity
            Storyboard.SetTargetName(animation, Rect.Name);
            Storyboard.SetTargetProperty(animation, new PropertyPath(Control.OpacityProperty));
            // Add the animation to the storyboard
            storyboard.Children.Add(animation);


            ((ImageBrush)Rect.Fill).ImageSource = GetCurrentPhoto();
            
            // Begin the storyboard
            storyboard.Begin(this);
        }

        internal void Next()
        {
            IncrementIndex();
            RenderCurrentPhoto();
        }

        internal void Previous()
        {
            DecrementIndex();
            RenderCurrentPhoto();
        }
    }

}
