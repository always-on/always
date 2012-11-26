using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Documents;
using System.Windows;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Windows.Markup;
using System.Xml;
using System.IO;

namespace Rummy.UI 
{
    class DragAdorner : Adorner
    {
        protected UIElement _child;
        protected VisualBrush _brush;
        protected UIElement _owner;

        public DragAdorner(UIElement owner) : base(owner) { }

        public DragAdorner(UIElement owner, UIElement adornElement, bool useVisualBrush, double opacity)
            : base(owner)
        {
            System.Diagnostics.Debug.Assert(owner != null);
            System.Diagnostics.Debug.Assert(adornElement != null); 
            _owner = owner;
            _originalElement = adornElement;
            if (useVisualBrush)
            {
                var cloned = CloneElement(adornElement);
                RemoveRotations(cloned.RenderTransform);
                _brush = new VisualBrush(cloned);

                _brush.Stretch = Stretch.None;
                _brush.Opacity = opacity;
                Rectangle r = new Rectangle();
                r.RadiusX = 3;
                r.RadiusY = 3;

                //TODO: questioning DesiredSize vs. Actual 
                var widthHeight = Math.Max(adornElement.RenderSize.Width, adornElement.RenderSize.Height);
                r.Width = adornElement.RenderSize.Width;
                r.Height = adornElement.RenderSize.Height;

                //XCenter = r.Width / 2;
                //YCenter = r.Height / 2;

                r.Fill = _brush;
                _child = r;
            }
            else
                _child = adornElement;
        }


        void RemoveRotations(Transform transform)
        {
            if (transform == null)
                return;

            if (transform is RotateTransform)
            {
                ((RotateTransform)transform).Angle = 0;
                return;
            }

            if (transform is TransformGroup)
            {
                foreach (var ct in ((TransformGroup)transform).Children)
                {
                    RemoveRotations(ct);
                }
            }
        }

        private double _leftOffset;
        public double LeftOffset
        {
            get { return _leftOffset; }
            set
            {
                _leftOffset = value;
                UpdatePosition();
            }
        }

        private double _topOffset;
        public double TopOffset
        {
            get { return _topOffset; }
            set
            {
                _topOffset = value;

                UpdatePosition();
            }
        }

        private void UpdatePosition()
        {
            AdornerLayer adorner = (AdornerLayer)this.Parent;
            if (adorner != null)
            {
                adorner.Update(this.AdornedElement);
            }
        }

        public static UIElement CloneElement(UIElement orig)
        {
            if (orig == null)
                return (null);

            string s = XamlWriter.Save(orig);

            StringReader stringReader = new StringReader(s);

            XmlReader xmlReader = XmlTextReader.Create(stringReader, new XmlReaderSettings());

            return (UIElement)XamlReader.Load(xmlReader);
        }

        protected override Visual GetVisualChild(int index)
        {
            return _child;
        }

        protected override int VisualChildrenCount
        {
            get
            {
                return 1;
            }
        }


        protected override Size MeasureOverride(Size finalSize)
        {
            _child.Measure(finalSize);
            return _child.DesiredSize;
        }

        protected override Size ArrangeOverride(Size finalSize)
        {
            _child.Arrange(new Rect(_child.DesiredSize));
            return finalSize;
        }

        public double scale = 1.0;
        private UIElement _originalElement;
        public override GeneralTransform GetDesiredTransform(GeneralTransform transform)
        {
            GeneralTransformGroup result = new GeneralTransformGroup();

            result.Children.Add(base.GetDesiredTransform(transform));
            //result.Children.Add(_originalElement.RenderTransform);
            result.Children.Add(new TranslateTransform(LeftOffset, TopOffset));

            return result;
        }
    }
}
