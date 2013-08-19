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
using System.Windows.Controls;

namespace AgentApp
{
    class RummyPlugin : IPlugin
    {
        GameShape game;
        IMessageDispatcher _remote;
        Viewbox pluginContainer;
        List<Move> currentMoveSuggestions = new List<Move>();

        public RummyPlugin(bool agentStarts, IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
        {
            this._remote = remote;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
                game = new GameShape(agentStarts ? Player.Two : Player.One);
                game.AgentCardsController.AutoPlay = false;

                game.AgentCardsController.CanPlay += (s, e) =>
                {
                    var allAvailableMovesBody = getPossibleMovesAsJson();
                    
                    //logging
                    //Console.WriteLine("------****-------");
                    //Console.WriteLine(allAvailableMovesBody.ToString());
                    //Console.WriteLine("------****-------");
                   
                    var body = new JObject();
                    Move m = game.AgentCardsController.GetBestMove();
                    body["action"] = MoveNameToSend(m);
                    _remote.Send("rummy.available_action", body);
                   
                    //here, sending all the possible moves to Java
                    //_remote.Send("rummy.available_moves", allAvailableMovesBody);
                };

                game.GameState.StateChanged += (oldState, newState) =>
                {
                    var body = new JObject();
                    body["state"] = StateToSend(newState);
					body["old_tate"] = StateToSend(oldState);
					body["user_cards"] = game.GameState.GetCards(GameShape.HumanPlayer).Count;
					body["agent_cards"] = game.GameState.GetCards(GameShape.AgentPlayer).Count;
					_remote.Send("rummy.state_changed", body); //delete? java side dependency?/

                    //_remote.Send("rummy.game_state", getGameStateAsJson());
                };

                game.GameState.MoveHappened += m =>
                {
                    var body = new JObject();
                    body["move"] = MoveNameToSend(m);
                    body["player"] = PlayerNameToSend(m.Player);
					_remote.Send("rummy.move_happened", body);

                    if (m.Player == Player.One) 
                    {
                     //   _remote.Send("rummy.human_move", getHumanMoveAsJson(m));
                    }
                };
                pluginContainer = new Viewbox();
                pluginContainer.Child = game;
            });

            _remote.RegisterReceiveHandler("rummy.best_move",
				  new MessageHandlerDelegateWrapper(x => DoBestMove()));

            //_remote.RegisterReceiveHandler("rummy.sgf_move",
			//	  new MessageHandlerDelegateWrapper(x => DoSGFMove(x)));

        }
		public void Dispose()
		{
			_remote.RemoveReceiveHandler("rummy.best_move");
            //_remote.RemoveReceiveHandler("rummy.sgf_move");
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
            //for draw only now, temp
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

        private void DoSGFMove(JObject msg)
        {
            Move selectedMvoe = null;
            LogUtils.LogWithTime("Doing SGF suggested move");
            int receivedHashCode = int.Parse(msg["hashcode"].ToString());

            foreach (Move m in currentMoveSuggestions)
                Console.WriteLine(m.GetHashCode());

            foreach (Move m in game.AgentCardsController.possibleMoves.Moves())
                if (m.GetHashCode() == receivedHashCode)
                    selectedMvoe = m;

            if (selectedMvoe != null){
                bool done = false;
                int tries = 0;
                while (!done && tries < 5)
                {
                    try
                    {
                        tries++;
                        selectedMvoe.Realize(game.GameState);
                        done = true;
                    }
                    catch (Exception)
                    {
                    }
                }
               
            //else
                //throw new System.InvalidOperationException(
                //    "Received hashcode not found in the initially sent set of moves.");
           
            
            Console.WriteLine("\n\n\n******received move: ");
            Console.WriteLine(selectedMvoe.ToString());
            Console.WriteLine("\n\n******received move");
            }
             else
                DoBestMove();
        }

        public JObject getPossibleMovesAsJson()
        {
            List<Move> currentPossibleMoves = new List<Move>();
            currentPossibleMoves.AddRange(
                game.AgentCardsController.possibleMoves.Moves());
            
            currentMoveSuggestions.Clear();
            currentMoveSuggestions.AddRange(currentPossibleMoves);
            
            var body = new JObject();
            int numOfDiscards = 0, numOfLayoffs = 0, numOfMelds = 0;

            foreach (Move eachMove in currentPossibleMoves)
            {
                try
                {
                    if (eachMove is DiscardMove)
                    {
                        body.Add(new JProperty("discard" + ++numOfDiscards, new JObject(
                           new JProperty("card", ((DiscardMove)eachMove).GetCard().ToString()))));
                    }
                    else if (eachMove is LayOffMove)
                    {
                        body.Add(new JProperty("layoff" + ++numOfLayoffs, new JObject(
                           new JProperty("card", ((LayOffMove)eachMove).GetCard().ToString()),
                           new JProperty("meldcards`", ((LayOffMove)eachMove).Meld.CardsToString()))));
                    }
                    else if (eachMove is MeldMove)
                    {
                        body.Add(new JProperty("meld" + ++numOfMelds, new JObject(
                            new JProperty("meldcards", ((MeldMove)eachMove).Meld.CardsToString()))));
                    }
                    //draw always either from pile or stock
                    else if (eachMove is DrawMove)
                        body.Add(new JProperty("draw"));
                }
                catch(Exception)
                {
                }
            }
            
            return body;
        }

        public JObject getGameStateAsJson()
        {
            List<Card> agentCards = new List<Card>();
            List<Card> humanCards = new List<Card>();
            var body = new JObject();
            string agentCardsAsString = "";
            string humanCardsAsString = "";
            string stockCardsAsString = "";
            string discardCardsAsString = "";
            string agentMeldsAsString = "";
            string humanMeldsAsString = "";
            
            foreach (Card card in game.GameState.GetCards(Player.Two))
                agentCardsAsString += card.ToString() + "/";
            foreach (Card card in game.GameState.GetCards(Player.One))
                humanCardsAsString += card.ToString() + "/";

            int i = 0; Card eachCard = null; 
            while ((eachCard = game.GameState.Stock.PeekAt(i++)) != null)
                stockCardsAsString += eachCard.ToString() + "/";
            stockCardsAsString += "--" + game.GameState.Stock.Count;

            i = 0; eachCard = null;
            while ((eachCard = game.GameState.Discard.PeekAt(i++)) != null)
                discardCardsAsString += eachCard.ToString() + "/";
            discardCardsAsString += "--" + game.GameState.Discard.Count;

            //synatx: each meld's cards by "/", melds seperated by "-"
            foreach (Meld eachMeld in game.GameState.GetMelds(Player.Two))
            {
                foreach (Card eachCardOfIt in eachMeld.getCards())
                    agentMeldsAsString += eachCardOfIt.ToString() + "/";
                agentMeldsAsString += "--";
            }

            //synatx: each meld's cards by "/", melds seperated by "-"
            foreach (Meld eachMeld in game.GameState.GetMelds(Player.One))
            {
                foreach (Card eachCardOfIt in eachMeld.getCards())
                    humanMeldsAsString += eachCardOfIt.ToString() + "/";
                humanMeldsAsString += "--";
            }

            body.Add(new JProperty("agentCards", agentCardsAsString));
            body.Add(new JProperty("humanCards", humanCardsAsString));
            body.Add(new JProperty("stockCards", stockCardsAsString));
            body.Add(new JProperty("discardCards", discardCardsAsString));
            body.Add(new JProperty("agentMelds", agentMeldsAsString));
            body.Add(new JProperty("humanMelds", humanMeldsAsString));

            return body;
        
        }

        public JObject getHumanMoveAsJson(Move humanMove)
        {
            var body = new JObject();
            if (humanMove is DiscardMove)
            {
                body.Add(new JProperty("discard", new JObject(
                   new JProperty("card", ((DiscardMove)humanMove).GetCard().ToString()))));
            }
            else if (humanMove is LayOffMove)
            {
                body.Add(new JProperty("layoff", new JObject(
                   new JProperty("card", ((LayOffMove)humanMove).GetCard().ToString()),
                   new JProperty("meldcards`", ((LayOffMove)humanMove).Meld.CardsToString()))));
            }
            else if (humanMove is MeldMove)
            {
                body.Add(new JProperty("meld", new JObject(
                    new JProperty("meldcards", ((MeldMove)humanMove).Meld.CardsToString()))));
            }


            return body;
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
