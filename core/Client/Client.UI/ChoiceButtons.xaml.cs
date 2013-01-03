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
using Agent.Core;
using System.Speech.Recognition.SrgsGrammar;
using System.Speech.Recognition;
using System.Windows.Media.Animation;
using SoundsLikeExtensions;

namespace Agent.UI
{
    /// <summary>
    /// Interaction logic for ChoiceButtons.xaml
    /// </summary>
    public partial class ChoiceButtons : UserControl, Agent.UI.IChoiceButtons
    {
        SpeechRecognitionEngine speechEngine = new SpeechRecognitionEngine();
        public event EventHandler<UserSelectedButtonEventArgs> UserSelectedButton = delegate { };
        private bool _micAvailable = true;

        public bool EnableSpeechRecognition
        {
            get { return (bool)GetValue(EnableSpeechRecognitionProperty); }
            set { SetValue(EnableSpeechRecognitionProperty, value); }
        }

        public static readonly DependencyProperty EnableSpeechRecognitionProperty =
            DependencyProperty.Register("EnableSpeechRecognition", typeof(bool), typeof(ChoiceButtons), new UIPropertyMetadata(false));

        public ChoiceButtons()
        {
            InitializeComponent();

            try
            {
                speechEngine.SetInputToDefaultAudioDevice();
            }
            catch (InvalidOperationException)
            {
                _micAvailable = false;
            }

            if(_micAvailable)
                speechEngine.SpeechRecognized += speechEngine_SpeechRecognized;
        }

        void speechEngine_SpeechRecognized(object sender, SpeechRecognizedEventArgs e)
        {
            if (e.Result != null)
            {
                if (EnableSpeechRecognition && e.Result.Confidence > .92)
                {
                    var button = FindButton(e.Result.Text);
                    OnUserSelectedButton(button);
                }
            }
        }

        private ContentControl FindButton(string text)
        {
            return ButtonsContainer.Children
                                    .Cast<ContentControl>()
                                    .OrderByDescending(x => text.SimilarText(GetButtonText(x)))
                                    .First();
        }

		public void RenderButtons(IList<string> buttons, bool twoColumn)
        {
			Dispatcher.BeginInvoke(new Action(() =>
			{
				ClearAll();
				SrgsRule root = new SrgsRule("command");
				SrgsOneOf commands = new SrgsOneOf();
				root.Add(new SrgsItem(commands));

				int numCols = twoColumn ? 2 : 1;
				int numRows = ((buttons.Count - 1) / numCols) + 1;
				ButtonsContainer.Rows = numRows;
				ButtonsContainer.Columns = numCols;

				for (int i = 0; i < numRows*numCols; ++i )
				{
					int index = (i%numCols)*numRows+i/numCols;
					UIElement b;
					if (index < buttons.Count)
					{
						string s = buttons[index];
						b = new Button()
						{
							Content = new TextBlock()
							{
								TextWrapping = TextWrapping.Wrap,
								Text = s,
							},
						};

						commands.Add(new SrgsItem(s));
					}
					else
					{
						b = new TextBlock();
					}

					ButtonsContainer.Children.Add(b);
				}

				if (commands.Items.Count > 0 && EnableSpeechRecognition && _micAvailable)
				{
					SrgsDocument doc = new SrgsDocument();
					doc.Rules.Add(root);
					doc.Root = root;
					speechEngine.LoadGrammar(new Grammar(doc));
					speechEngine.RecognizeAsync(RecognizeMode.Multiple);
				}
			}), null);
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            if (e.OriginalSource is ContentControl && ((ContentControl)e.OriginalSource).Content is TextBlock)
            {
                var button = (ContentControl)e.OriginalSource;
                OnUserSelectedButton(button);
            }
        }

        private void OnUserSelectedButton(ContentControl button)
        {
            string s = GetButtonText(button);

            var storyboard = (Storyboard)Resources["ButtonSelectAnimation"];
            storyboard.Begin(button);
            HideButtonsExcept(button);
            UserSelectedButton(this, new UserSelectedButtonEventArgs(s));
        }

        private static string GetButtonText(ContentControl button)
        {
            return ((TextBlock)((ContentControl)button).Content).Text;
        }

        private void HideButtonsExcept(ContentControl button)
        {
            foreach (var b in ButtonsContainer.Children
                        .Cast<UIElement>()
                        .Where(x => x != button)
                        .ToArray())
                b.Visibility = System.Windows.Visibility.Hidden;

            button.IsEnabled = false;

            ClearSpeechGrammar();
        }

        public void ClearAll()
        {
            ButtonsContainer.Children.Clear();
            ClearSpeechGrammar();
        }

        private void ClearSpeechGrammar()
        {
            speechEngine.RecognizeAsyncStop();
            speechEngine.UnloadAllGrammars();
        }
    }
}
