using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Rummy.UI;
using Rummy;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;

namespace AgentApp
{
    class SRummyPlugin : IPlugin
    {
        GameShape game;
        IMessageDispatcher _remote;

        public SRummyPlugin(bool agentStarts, IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
        {
            this._remote = remote;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
                game = new GameShape(agentStarts ? Player.Two : Player.One);
                game.AgentCardsController.AutoPlay = false;

                game.AgentCardsController.CanPlay += (s, e) =>
                {
                    var body = new JObject();
                    Move m = game.AgentCardsController.GetBestMove();
                    body["action"] = MoveNameToSend(m);
                    _remote.Send("rummy.available_action", body);
                };

                game.GameState.StateChanged += (oldState, newState) =>
                {
                    var body = new JObject();
                    body["state"] = StateToSend(newState);
					body["old_tate"] = StateToSend(oldState);
					body["user_cards"] = game.GameState.GetCards(GameShape.HumanPlayer).Count;
					body["agent_cards"] = game.GameState.GetCards(GameShape.AgentPlayer).Count;
					_remote.Send("rummy.state_changed", body);
                };

                game.GameState.MoveHappened += m =>
                {
                    var body = new JObject();
                    body["move"] = MoveNameToSend(m);
                    body["player"] = PlayerNameToSend(m.Player);
					_remote.Send("rummy.move_happened", body);
                };
            });

            _remote.RegisterReceiveHandler("rummy.best_move",
				  new MessageHandlerDelegateWrapper(x => DoBestMove()));
        }
		public void Dispose()
		{
			_remote.RemoveReceiveHandler("rummy.best_move");
		}

        private string StateToSend(State newState)
        {
            string res;
            switch (newState)
            {
                case State.Player1Draw:
                    res = PlayerNameToSend(Player.One) + "_draw";
                    break;
                case State.Player1MeldLayDiscard:
                    res = PlayerNameToSend(Player.One) + "_action";
                    break;
                case State.Player1Won:
                    res = PlayerNameToSend(Player.One) + "_won";
                    break;
                case State.Player2Draw:
                    res = PlayerNameToSend(Player.Two) + "_draw";
                    break;
                case State.Player2MeldLayDiscard:
                    res = PlayerNameToSend(Player.Two) + "_action";
                    break;
                case State.Player2Won:
                    res = PlayerNameToSend(Player.Two) + "_won";
                    break;
                default:
                    res = "";
                    break;
            }

            return res;
        }

        private string PlayerNameToSend(Player player)
        {
            return player == GameShape.HumanPlayer ? "user" : "agent";
        }

        private static string MoveNameToSend(Move m)
        {
            return m.GetType().Name.Replace("Move", "").ToLower();
        }

		private void DoBestMove()
		{
			LogUtils.LogWithTime("Doing rummy best move");
			bool done = false;
			int tries = 0;
			while (!done && tries < 5)
			{
				try
				{
					tries++;
					game.AgentCardsController.GetBestMove().Realize(game.GameState);
					done = true;
				}
				catch (Exception)
				{
				}
			}
		}

        public System.Windows.UIElement GetUIElement()
        {
            return game;
        }
    }
}
