using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Rummy;
using sRummy.UI;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;

namespace AgentApp
{
    class sRummyPlugin : IPlugin
    {
        GameShape game;
        GameStateManager stateManager;
        IMessageDispatcher _remote;

        public sRummyPlugin(bool agentStarts, IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
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
                    _remote.Send("srummy.available_action", body);
                };

                game.GameState.StateChanged += (oldState, newState) =>
                {
                    var body = new JObject();
                    body["state"] = StateToSend(newState);
					body["old_tate"] = StateToSend(oldState);
					body["user_cards"] = game.GameState.GetCards(GameShape.HumanPlayer).Count;
					body["agent_cards"] = game.GameState.GetCards(GameShape.AgentPlayer).Count;
					_remote.Send("srummy.state_changed", body);
                };

                game.GameState.MoveHappened += m =>
                {
                    var body = new JObject();
                    body["move"] = MoveNameToSend(m);
                    body["player"] = PlayerNameToSend(m.Player);
					_remote.Send("srummy.move_happened", body);
                };
            });

            _remote.RegisterReceiveHandler("srummy.agent_move",
				  new MessageHandlerDelegateWrapper(x => VisualizeMove(x)));
        }
		public void Dispose()
		{
            _remote.RemoveReceiveHandler("srummy.agent_move");
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

        private void VisualizeMove(JObject move)
		{
            Player _player;
            if (move["type"].ToString().Equals("meld"))
            {
                if(move["pile_name"].ToString().Equals("stock"))
                    new MeldMove(_player, PileName.Stock);
                else
                    new MeldMove(_player, PileName.Discard);
            }

            if (move["type"].ToString().Equals("draw"))
            {
                if (move["pile_name"].ToString().Equals("stock"))
                    new DrawMove(_player, PileName.Stock);
                else
                    new DrawMove(_player, PileName.Discard);
            }

            if (move["type"].ToString().Equals(""))
            {

            }
                
                
                
                
                
                
                game.AgentCardsController.GetBestMove().Realize(game.GameState);
                    
					

		}

        public System.Windows.UIElement GetUIElement()
        {
            return game;
        }
    }
}
