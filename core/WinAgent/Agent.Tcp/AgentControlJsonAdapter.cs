using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Core;
using System.Text.RegularExpressions;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp
{
	public class AgentControlJsonAdapter
	{
		private IAgentControl _agent;
		private IMessageDispatcher _dispatcher;

		public AgentControlJsonAdapter(IAgentControl agent, IMessageDispatcher dispatcher)
		{
			this._agent = agent;
			this._dispatcher = dispatcher;

			_agent.ActionDone += Agent_ActionDone;
			_agent.UserSelectedButton += Agent_UserSelectedButton;

			dispatcher.RegisterReceiveHandler("speech", new MessageHandlerDelegateWrapper(Say));
			dispatcher.RegisterReceiveHandler("stop_speech", new MessageHandlerDelegateWrapper(StopSpeech));
			dispatcher.RegisterReceiveHandler("show_menu", new MessageHandlerDelegateWrapper(ShowMenu));
			dispatcher.RegisterReceiveHandler("gaze", new MessageHandlerDelegateWrapper(Gaze));
			dispatcher.RegisterReceiveHandler("express", new MessageHandlerDelegateWrapper(Express));
			dispatcher.RegisterReceiveHandler("idle", new MessageHandlerDelegateWrapper(Idle));
		}

		void Agent_UserSelectedButton(object sender, UserSelectedButtonEventArgs e)
		{
			if (e == null)
				throw new ArgumentNullException("e");


			dynamic body = new JObject();
			body.text = e.Text;

			_dispatcher.Send("menu_selected", body);
		}

		void Agent_ActionDone(object sender, ActionDoneEventArgs e)
		{
			SendActionDone(e.Action, e.Data);
		}

		private void SendActionDone(string action, string data)
		{
			dynamic body = new JObject();
			body.action = action;
			body.data = data;

			_dispatcher.Send("done", body);
		}

		private void Gaze(JObject args)
		{
			AgentGaze dir;

			if (args["dir"] != null)
				dir = (AgentGaze)Enum.Parse(typeof(AgentGaze), args["dir"].Value<string>(), true);
			else
				dir = AgentGaze.Mid;

			_agent.Turn(dir);
		}

		private void Express(JObject args)
		{
			AgentFaceExpression expression;

			if (args["expression"] != null)
				expression = AgentFaceExpression.valueOf(args["expression"].Value<string>());
			else
				expression = AgentFaceExpression.Warm;

			_agent.Express(expression);
		}
		private void Idle(JObject args)
		{
			if (args["enabled"] != null)
				_agent.Idle(args["enabled"].Value<string>().Equals("true"));
			else
				_agent.Idle(true);
		}

		private void ShowMenu(JObject args)
		{
			List<string> menus;
			if (args["menus"] != null)
				menus = args["menus"].Values<string>().ToList();
			else
				menus = new List<string>(0);
			bool twoCol = false;
			if (args["twoColumn"] != null)
				twoCol = (bool)args["twoColumn"];

			_agent.ShowMenu(menus, twoCol);

			SendActionDone("show_menu", new JArray(menus).ToString());
		}

		private void Say(JObject args)
		{
			if (args["text"] != null)
				_agent.Say(args["text"].Value<string>());
		}

		private void StopSpeech(JObject args)
		{
			_agent.StopSpeech();
		}
	}
}
