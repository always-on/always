using System;
using Agent.Core;
namespace Agent.UI
{
    public interface IChoiceButtons
    {
        void ClearAll();
        bool EnableSpeechRecognition { get; set; }
        void RenderButtons(System.Collections.Generic.IList<string> buttons, bool twoColumn);
		event EventHandler<UserSelectedButtonEventArgs> UserSelectedButton;
	}

    public class NullChoiceButtons : IChoiceButtons
    {
        public void ClearAll()
        {
        }

        public bool EnableSpeechRecognition
        {
            get
            {
                return false;
            }
            set
            {
            }
        }

		public void RenderButtons(System.Collections.Generic.IList<string> buttons, bool twoColumn)
        {
        }

        public event EventHandler<UserSelectedButtonEventArgs> UserSelectedButton = delegate { };
    }
}
