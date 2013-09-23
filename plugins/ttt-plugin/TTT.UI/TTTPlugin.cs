using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Core;
using Newtonsoft.Json.Linq;
using Agent.Tcp;
using TTT.UI;
using System.Windows.Controls;
using System.Media;

namespace AgentApp
{
    class TTTPlugin : IPlugin
    {
        GameShape game;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;
        Viewbox pluginContainer;

        public TTTPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
        {

            this._remote = remote;
            this._uiThreadDispatcher = uiThreadDispatcher;

            uiThreadDispatcher.BlockingInvoke(() =>
            {
              game = new GameShape();
              game.Played += (s, e) =>
              { 
                  JObject body = new JObject();
                  body["cellNum"] = ((cellEventArg)e).cellNum;
                  _remote.Send("tictactoe.human_played_cell", body);
              };
              pluginContainer = new Viewbox();
              pluginContainer.Child = game;
            });

            _remote.RegisterReceiveHandler("tictactoe.agent_cell",
                 new MessageHandlerDelegateWrapper(x => PlayAgentMove(x)));

			_remote.RegisterReceiveHandler("tictactoe.playability",
				  new MessageHandlerDelegateWrapper(x => SetPlayability(x)));
        }

		public void Dispose()
		{
            _remote.RemoveReceiveHandler("tictactoe.agent_cell");
		}

       	private void PlayAgentMove(JObject cellNumAsJObj)
		{
            
			if (cellNumAsJObj["cellNum"]
                .ToString().Trim().Equals("reset"))
            {
                game.Reset();
                return;
            }
            int cellNum = int.Parse(cellNumAsJObj["cellNum"].ToString());
            //Console.WriteLine(cellNum);
            game.PlayAgentMove(cellNum);
		}

		private void SetPlayability(JObject playabilityAsJObj)
		{
			if (playabilityAsJObj["value"].ToString().Trim().Contains("true"))
				game.MakeTheBoardPlayable();
			else if (playabilityAsJObj["value"].ToString().Trim().Contains("false"))
				game.MakeTheBoardUnplayable();
		
		}

        public System.Windows.UIElement GetUIElement()
        {
            return game;
        }

        public Viewbox GetPluginContainer()
        {
            return pluginContainer;
        }
    }
}
