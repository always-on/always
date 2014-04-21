using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Core
{
	public class UserSelectedButtonEventArgs : EventArgs
	{
		private readonly string _text;

		public UserSelectedButtonEventArgs(string text)
		{
			_text = text;
		}

		public string Text { get { return _text; } }
	}

	public class ActionDoneEventArgs : EventArgs
	{
		private readonly string _action;
		private readonly string _data;

		public ActionDoneEventArgs(string action, string data)
		{
			_action = action;
			_data = data;
		}

		public ActionDoneEventArgs(string action)
			: this(action, "")
		{
		}

		public string Action { get { return _action; } }
		public string Data { get { return _data; } }
	}

	public interface IAgentControl
	{
		void Say(string prompt);
		void ShowMenu(IList<string> buttons, bool twoColumn);
		void Nod();
		void NormalExpression();
		void ShowConcern(int mseconds);
		void Smile(int mseconds);
		void SetVisible(Boolean status);
		void Turn(string dir, float horizontal, float vertical);
		void Express(Agent.Core.AgentFaceExpression expression);
        void ShowPage(string url);
		void Idle(bool enabled);
		void StopSpeech();
		event EventHandler<UserSelectedButtonEventArgs> UserSelectedButton;
		event EventHandler<ActionDoneEventArgs> ActionDone;
	}
}
