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
            dispatcher.RegisterReceiveHandler("toggleAgent", new MessageHandlerDelegateWrapper(ToggleAgent));
            dispatcher.RegisterReceiveHandler("reetiIP", new MessageHandlerDelegateWrapper(ReetiIP));
            dispatcher.RegisterReceiveHandler("page", new MessageHandlerDelegateWrapper(ShowPage));
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
			//AgentGaze dir;
            string dir = "CUSTOM";
            float horizontal = 0.0f;
            float vertical = 0.0f;

			if (args["dir"] != null)
				dir = args["dir"].Value<string>();
            else if ((args["horizontal"] != null) || (args["vertical"] != null))
            {
                float.TryParse(args["horizontal"].Value<string>(),out horizontal);
                float.TryParse(args["vertical"].Value<string>(), out vertical);
            }
            else
                dir = "TOWARDS";

            System.Diagnostics.Debug.WriteLine("horizontal = " + horizontal + "| vertical =" + vertical);

			_agent.Turn(dir,horizontal,vertical);

		}

        private void ShowPage(JObject args)
        {
            if (args["url"] != null)
            {
                _agent.ShowPage(args["url"].Value<string>());
            }
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

        private List<string> extension = new List<string>(0);

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
            if (args["extension"] != null && (bool)args["extension"])
                extension = menus;
            else
            {
              if ( !twoCol ) menus.AddRange(extension);
              _agent.ShowMenu(menus, twoCol);
            }

			SendActionDone("show_menu", new JArray(menus).ToString());
		}

		private void Say(JObject args)
		{
			if (args["text"] != null)
				_agent.Say(args["text"].Value<string>());
		}

        private void ToggleAgent(JObject args)
        {
           _agent.ToggleAgent();
        }

		public static string REETI_IP;

		private void ReetiIP(JObject args)
        {
			if (args["address"] != null)
				REETI_IP = args["address"].Value<string>();
        }
		private void StopSpeech(JObject args)
		{
			_agent.StopSpeech();
		}
	}
}
