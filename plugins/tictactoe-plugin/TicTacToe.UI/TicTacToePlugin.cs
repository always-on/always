using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Core;
using Newtonsoft.Json.Linq;
using Agent.Tcp;
using TicTacToe.UI;

namespace AgentApp
{
    class TicTacToePlugin : IPlugin
    {
        GameShape game;
        IMessageDispatcher _remote;

        
        public TicTacToePlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
        {

            this._remote = remote;
                 
            uiThreadDispatcher.BlockingInvoke(() =>
            {
              game = new GameShape();
              game.Played += (s, e) =>
              { 
                  JObject body = new JObject();
                  body["cellNum"] = ((cellEventArg)e).cellNum;
                  _remote.Send("tictactoe.human_played_cell", body);
              };
            });

            _remote.RegisterReceiveHandler("tictactoe.agent_cell",
                 new MessageHandlerDelegateWrapper(x => playAgentMove(x)));
        }

		public void Dispose()
		{
            _remote.RemoveReceiveHandler("tictactoe.agent_cell");
		}

       	public void playAgentMove(JObject cellNumAsJObj)
		{
            if (cellNumAsJObj["cellNum"]
                .ToString().Trim().Equals("reset"))
            {
                game.reset();
                return;
            }
            int cellNum = int.Parse(cellNumAsJObj["cellNum"].ToString());
            Console.WriteLine(cellNum);
            
            game.playAgentMove(cellNum);
		}

        public System.Windows.UIElement GetUIElement()
        {
            return game;
        }
    }
}
