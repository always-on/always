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

		public RummyPlugin(bool agentStarts, IMessageDispatcher remote
			, IUIThreadDispatcher uiThreadDispatcher)
		{
			this._remote = remote;
			uiThreadDispatcher.BlockingInvoke(() =>
			{
				game = new GameShape(agentStarts ? Player.Two : Player.One);
				game.AgentCardsController.AutoPlay = false;

				game.GameState.MoveHappened += m =>
				{
					if (m.Player == Player.One
							&& (!(m is DrawMove)))
					{
						_remote.Send("rummy.human_move"
							, getMoveAsJson(m));
					}
				};

				game.GameState.StateChanged += (oldState, newState) =>
				{
					_remote.Send("rummy.game_state", getGameStateAsJson());
				};

				game.AgentCardsController.CanPlay += (s, e) =>
				{
					//here, sending all the possible moves to Java
					_remote.Send("rummy.available_moves", getPossibleMovesAsJson());
				};
				game.GameState.GameOver += p =>
				{
					_remote.Send("rummy.gameover", getPlayerAsJson(p));
				};

				pluginContainer = new Viewbox();
				pluginContainer.Child = game;
			});

			_remote.RegisterReceiveHandler("rummy.agent_move",
				  new MessageHandlerDelegateWrapper(x => PlayAgentMove(x)));
		}

		public void Dispose()
		{
			_remote.RemoveReceiveHandler("rummy.agent_move");
		}

		private string PlayerNameToSend(Player player)
		{
			return player == GameShape.HumanPlayer ? "user" : "agent";
		}

		private static string MoveNameToSend(Move m)
		{
			return m.GetType().Name.Replace("Move", "").ToLower();
		}

		private void PlayAgentMove(JObject msg)
		{
			Move selectedMove = null;
			LogUtils.LogWithTime("Doing SGF suggested move");
			int receivedHashCode = int.Parse(msg["hashcode"].ToString());

			foreach (Move m in currentMoveSuggestions)
				Console.WriteLine(m.GetHashCode());

			foreach (Move m in game.AgentCardsController.possibleMoves.Moves())
				if (m.GetHashCode() == receivedHashCode)
					selectedMove = m;

			if (selectedMove != null)
			{
					try
					{
						selectedMove.Realize(game.GameState);
					}
					catch (Exception)
					{
							bool done = false; int tries = 0;
							while (!done && tries < 10)
							{
								try{
									tries++;
									game.AgentCardsController.GetBestMove().Realize(game.GameState);
								}
								catch (Exception){}
							}
					}
						
			}
		}

		public JObject getPossibleMovesAsJson()
		{
			List<Move> currentPossibleMoves = new List<Move>();
			currentPossibleMoves.AddRange(
				game.AgentCardsController.possibleMoves.Moves());

			currentMoveSuggestions.Clear();
			currentMoveSuggestions.AddRange(currentPossibleMoves);

			var body = new JObject();
			int numOfDiscards = 0, numOfLayoffs = 0,
				numOfMelds = 0, numOfDraws = 0;

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
						   new JProperty("meldcards", ((LayOffMove)eachMove).Meld.CardsToString()))));
					}
					else if (eachMove is MeldMove)
					{
						body.Add(new JProperty("meld" + ++numOfMelds, new JObject(
							new JProperty("meldcards", ((MeldMove)eachMove).Meld.CardsToString()))));
					}
					//draw always either from pile or stock
					else if (eachMove is DrawMove)
						body.Add(new JProperty("draw" + ++numOfDraws, new JObject(
							new JProperty("pile", ((DrawMove)eachMove).getPile().ToString()))));
				}
				catch (Exception)
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

			Card eachCard = null;
			for (int i = 0; i < game.GameState.Stock.Count; i++)
			{
				eachCard = game.GameState.Stock.PeekAt(i);
				stockCardsAsString += eachCard.ToString() + "/";
			}
			stockCardsAsString += "--" + game.GameState.Stock.Count;

			eachCard = null;
			for (int i = 0; i < game.GameState.Discard.Count; i++)
			{
				eachCard = game.GameState.Discard.PeekAt(i);
				discardCardsAsString += eachCard.ToString() + "/";
			}
			discardCardsAsString += "--" + game.GameState.Discard.Count;

			//synatx: each meld's cards by "/", melds seperated by "-"
			if (game.GameState.GetMelds(Player.Two).Count > 0)
			{
				foreach (Meld eachMeld in game.GameState.GetMelds(Player.Two))
				{
					foreach (Card eachCardOfIt in eachMeld.getCards())
						agentMeldsAsString += eachCardOfIt.ToString() + "/";
					agentMeldsAsString += "--";
				}
			}

			if (game.GameState.GetMelds(Player.One).Count > 0)
			{
				//synatx: each meld's cards by "/", melds seperated by "-"
				foreach (Meld eachMeld in game.GameState.GetMelds(Player.One))
				{
					foreach (Card eachCardOfIt in eachMeld.getCards())
						humanMeldsAsString += eachCardOfIt.ToString() + "/";
					humanMeldsAsString += "--";
				}
			}

			body.Add(new JProperty("agentCards", agentCardsAsString));
			body.Add(new JProperty("humanCards", humanCardsAsString));
			body.Add(new JProperty("stockCards", stockCardsAsString));
			body.Add(new JProperty("discardCards", discardCardsAsString));
			body.Add(new JProperty("agentMelds", agentMeldsAsString));
			body.Add(new JProperty("humanMelds", humanMeldsAsString));

			return body;

		}

		private JObject getPlayerAsJson(Player p) 
		{
			var body = new JObject();
			if (p == Player.One)
				body.Add(new JProperty("winner", "human"));
			else
				body.Add(new JProperty("winner", "agent"));
			return body;
		}

		public JObject getMoveAsJson(Move move)
		{
			var body = new JObject();
			if (move is DiscardMove)
			{
				body.Add(new JProperty("discard", new JObject(
				   new JProperty("card", ((DiscardMove)move).GetCard().ToString()))));
			}
			else if (move is LayOffMove)
			{
				body.Add(new JProperty("layoff", new JObject(
				   new JProperty("card", ((LayOffMove)move).GetCard().ToString()),
				   new JProperty("meldcards", ((LayOffMove)move).Meld.CardsToString()))));
			}
			else if (move is MeldMove)
			{
				body.Add(new JProperty("meld", new JObject(
					new JProperty("meldcards", ((MeldMove)move).Meld.CardsToString()))));
			}

			//draw is not sent as it is always performed and does not have a sgf value
			//if later cheating is added, uncomment, handle java side accordingly.
			//else if (move is DrawMove)
			//{
			//    body.Add(new JProperty("draw", new JObject(
			//        new JProperty("pile", ((DrawMove)move).))));
			//}

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
