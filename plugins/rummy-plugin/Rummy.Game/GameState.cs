using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;

namespace Rummy
{
    public class GameState
    {
        private Deck _stock = new Deck();
        private Stack _discard = new Stack();

        private readonly Dictionary<Player, List<Meld>> _melds = new Dictionary<Player, List<Meld>>();
        private readonly Dictionary<Player, ObservableCollection<Card>> _cards = new Dictionary<Player, ObservableCollection<Card>>();

        private State _currentState;
        private Card _cardJustDrawn;

        private ErrorMessage _currentError;

        public Card CardJustDrawn
        {
            get
            {
                return _cardJustDrawn;
            }
        }

        public Deck Stock
        {
            get
            {
                return _stock;
            }
        }

        public Stack Discard
        {
            get
            {
                return _discard;
            }
        }

        public List<Meld> GetMelds(Player player)
        {
            return _melds[player];
        }

        public List<Meld> Player1Melds
        {
            get { return GetMelds(Player.One); }
            set { _melds[Player.One] = value; }
        }

        public List<Meld> Player2Melds
        {
            get { return GetMelds(Player.Two); }
            set { _melds[Player.Two] = value; }
        }

        public ObservableCollection<Card> GetCards(Player player)
        {
            return _cards[player];
        }

        public IList<Card> Player1Cards
        {
            get { return GetCards(Player.One); }
        }

        public IList<Card> Player2Cards
        {
            get { return GetCards(Player.Two); }
        }

        public State CurrentState
        {
            get
            {
                return _currentState;
            }
        }

        public bool GameIsOver()
        {
            return CurrentState == State.Player1Won || CurrentState == State.Player2Won;
        }

        public ErrorMessage CurrentError
        {
            get
            {
                return _currentError;
            }
        }

        public GameState()
        {
            GenerateInitialGameStateAndDealCards();
        }

        public GameState(State startingState)
        {
            GenerateInitialGameStateAndDealCards();

            _currentState = startingState;
        }

        public GameState(ICollection<Card> player1Cards)
        {
            GenerateInitialStateAndShuffleStock(player1Cards);

            DealCardsToPlayer(Player.Two);
            foreach (var card in player1Cards)
            {
                AddToPlayerCards(Player.One, card);
            }
        }

        public GameState(Card[] player1Cards, State state)
            : this(player1Cards)
        {
            _currentState = state;
        }

        public GameState(ICollection<Card> player1Cards, List<Meld> player1Melds)
        {
            ICollection<Card> IgnoreCard = new List<Card>(player1Cards);

            foreach (var m in player1Melds)
            {
                foreach (var c in m)
                {
                    IgnoreCard.Add(c);
                }
            }

            GenerateInitialStateAndShuffleStock(IgnoreCard);
            Player1Melds = player1Melds;

            DealCardsToPlayer(Player.Two);
            foreach (var card in player1Cards)
            {
                AddToPlayerCards(Player.One, card);
            }
        }

        public GameState(Card[] player1Cards, List<Meld> player1Melds, State state)
            : this(player1Cards, player1Melds)
        {
            _currentState = state;
        }

        private void GenerateInitialGameStateAndDealCards()
        {
            GenerateInitialStateAndShuffleStock();

            DealCardsToPlayers();
        }

        private void GenerateInitialStateAndShuffleStock()
        {
            GenerateInitialStateAndShuffleStock(new Card[0]);
        }

        private void GenerateInitialStateAndShuffleStock(ICollection<Card> except)
        {
            _stock = new Deck();
            _discard = new Stack();
            _cards[Player.One] = new ObservableCollection<Card>();
            _cards[Player.Two] = new ObservableCollection<Card>();
            _melds[Player.One] = new List<Meld>();
            _melds[Player.Two] = new List<Meld>();

            _currentState = State.Player1Draw;
            _currentError = ErrorMessage.NoError;

            Stock.Create((int)DateTime.Now.Ticks);

            _discard.Add(Stock.Pop());
        }

        private void DealCardsToPlayers()
        {
            DealCardsToPlayer(Player.One);
            DealCardsToPlayer(Player.Two);
        }

        private void DealCardsToPlayer(Player player)
        {
            for (int i = 0; i < 10; i++)
            {
                Card c = Stock.Pop();
                c.FaceUp = true;
                AddToPlayerCards(player, c);
            }
        }

        //discard a card, change state
        public void DiscardCard(Player player, Card c)
        {
            //check state
            if (!CanDiscard(player))
            {
                _currentError = ErrorMessage.NotPlayerTurn;
                throw new ArgumentException("It is not this player's turn", "player");
            }
            //check if player has the card
            PlayerHasCard(player, c);

            //check if the card was just drawn
            if (c.Equals(_cardJustDrawn))
            {
                _currentError = ErrorMessage.CannotDiscard;
                throw new ArgumentException("Cannot discard a card that was just drawn", "player");
            }
            RemovePlayerCard(player, c);
            Discard.Add(c);

            State newState = (player == Player.One) ? State.Player2Draw : State.Player1Draw;

            AfterActStuff(new DiscardMove(player, c), newState);

            _currentError = ErrorMessage.NoError;
        }

        private void ChangeStateTo(State newState)
        {
            if (GameIsOver())
                return;

            var oldState = _currentState;

            BeforeStateChange(oldState, newState);

            _currentState = newState;

            StateChanged(oldState, newState);
        }

        private void PlayerHasCard(Player player, Card c)
        {
            if (player == Player.One && !Player1Cards.Contains(c))
            {
                _currentError = ErrorMessage.NotPlayerCard;
                throw new ArgumentException("Player one does not have this card", "player");
            }
            else if (player == Player.Two && !Player2Cards.Contains(c))
            {
                _currentError = ErrorMessage.NotPlayerCard;
                throw new ArgumentException("Player two does not have this card", "player");
            }
        }

        public void LayOff(Player player, Card c, Meld m)
        {
            //check state
            if (!CanMeldLayOff(player))
            {
                _currentError = ErrorMessage.NotPlayerTurn;
                throw new ArgumentException("It is not this player's turn to meld or lay off", "player");
            }
            //check if player has the card
            PlayerHasCard(player, c);

            //try to add, check if the condition satisfies
            if (m.CanAddACard(c))
            {
                m.AddACard(c);
            }
            else
            {
                _currentError = ErrorMessage.InvalidLayoff;
                throw new ArgumentException("Cannot add this card to existing meld", "c");
            }
            RemovePlayerCard(player, c);

            LayOffMove move = new LayOffMove(player, c, m);
            AfterActStuff(move);
            LayoffHappened(move);

            _currentError = ErrorMessage.NoError;
        }

        //meld a list of cards
        //player
        //List<Card>
        public void Meld(Player player, IList<Card> lCard)
        {
            //check state
            if (!CanMeldLayOff(player))
            {
                _currentError = ErrorMessage.NotPlayerTurn;
                throw new ArgumentException("It is not this player's turn to meld or lay off", "player");
            }
            if (player == Player.One && !HasListOfCard(Player.One, lCard))
            {
                _currentError = ErrorMessage.NotPlayerCard;
                throw new ArgumentException("Player one does not have all the card required for the meld", "player");
            }
            else if (player == Player.Two && !HasListOfCard(Player.Two, lCard))
            {
                _currentError = ErrorMessage.NotPlayerCard;
                throw new ArgumentException("Player two does not have all the card required for the meld", "player");
            }

            //check if it is a valid meld
            var m = new Meld();
            if (m.IsValid(lCard))
                m = new Meld(lCard);
            else
            {
                _currentError = ErrorMessage.InvalidMeld;
                throw new ArgumentException("Cannot form a meld from a list of cards", "lCard");
            }

            //remove lCard from player
            RemovePlayerCardList(player, lCard);
            //put the meld under this player's name
            AddToPlayerMeld(player, m);

            var meldMove = new MeldMove(player, new Meld(lCard));

            AfterActStuff(meldMove);
            MeldHappend(meldMove);

            _currentError = ErrorMessage.NoError;
        }

        //meld with an actual meld object
        public void Meld(Player player, Meld m)
        {
            Meld(player, m.ToList());
        }

        //check if a player has every card in a list
        //player
        //List<Card>
        public bool HasListOfCard(Player player, IList<Card> lCard)
        {
            bool result = true;
            //check player 1
            if (player == Player.One)
            {
                foreach (Card c in lCard)
                {
                    if (!Player1Cards.Contains(c))
                        result = false;
                }

            }
            //check player 2
            else
            {
                foreach (Card c in lCard)
                {
                    if (!Player2Cards.Contains(c))
                        result = false;
                }
            }

            return result;
        }

        //remove List of cards from Player
        public void RemovePlayerCardList(Player player, IList<Card> lCard)
        {
            foreach (Card c in lCard)
            {
                RemovePlayerCard(player, c);
            }
        }

        //remove a card from Player
        public void RemovePlayerCard(Player player, Card card)
        {
            GetCards(player).Remove(card);
        }

        //add a card to Player
        private void AddToPlayerCards(Player player, Card card)
        {
            GetCards(player).Add(card);
        }

        //add a meld to Player
        private void AddToPlayerMeld(Player player, Meld m)
        {
            GetMelds(player).Add(m);
        }

        //pop a card from a Pile
        Card PopFromPile(PileName p)
        {
            if (p == PileName.Stock)
                return Stock.Pop();

            return Discard.Pop();
        }

        //draw a card from stock or discard
        //player
        //pile
        public void DrawCard(Player player, PileName pn)
        {
            if (!CanDraw(player))
            {
                _currentError = ErrorMessage.NotPlayerTurn;
                throw new ArgumentException("It is not this player's turn", "player");
            }

            if (IsPileEmpty(pn))
                throw new ArgumentException("The pile is empty", "pn");

            Card c = PopFromPile(pn);
            AddToPlayerCards(player, c);
            //if drawn from discard pile, remeber the card
            if (pn == PileName.Discard)
                _cardJustDrawn = c;
            else
                _cardJustDrawn = null;

            if (pn == PileName.Stock && IsPileEmpty(PileName.Stock))
            {
                var topOnDiscard = Discard.Pop();
                while (Discard.Count > 0)
                {
                    Stock.Add(Discard.Pop());
                    Stock.Shuffle(null);
                }

                Discard.Add(topOnDiscard);
            }


            _currentError = ErrorMessage.NoError;

            var newState = (player == Player.One) ?State.Player1MeldLayDiscard : State.Player2MeldLayDiscard;

            AfterActStuff(new DrawMove(player, pn), newState);
        }

        void AfterActStuff(Move move)
        {
            AfterActStuff(move, null);
        }

        void AfterActStuff(Move move, State? newState)
        {
            if (Player1Cards.Count == 0)
            {
                ChangeStateTo(State.Player1Won);
                GameOver(Player.One);
            }
            else if (Player2Cards.Count == 0)
            {
                ChangeStateTo(State.Player2Won);
                GameOver(Player.Two);
            }
            if (!GameIsOver() && newState != null)
            {
                ChangeStateTo((State)newState);
            }

            MoveHappened(move);
        }

        //Is it player's turn to draw a card
        public bool CanDraw(Player player)
        {
            if (player == Player.One)
                return CurrentState == State.Player1Draw;

            return CurrentState == State.Player2Draw;
        }
        //is it player's turn to discard a card
        public bool CanDiscard(Player player)
        {
            if (player == Player.One)
                return CurrentState == State.Player1MeldLayDiscard;

            return CurrentState == State.Player2MeldLayDiscard;
        }

        //Is it player's turn to lay off or meld
        public bool CanMeldLayOff(Player player)
        {
            if (player == Player.One)
                return CurrentState == State.Player1MeldLayDiscard;

            return CurrentState == State.Player2MeldLayDiscard;
        }

        //check if it is this player's turn
        public bool IsPlayersTurn(Player p)
        {
            switch (p)
            {
                case Player.One:
                    {
                        return (CurrentState == State.Player1Draw || CurrentState == State.Player1MeldLayDiscard);
                    }
                case Player.Two:
                    {
                        return (CurrentState == State.Player2Draw || CurrentState == State.Player2MeldLayDiscard);
                    }
                default:
                    return false;

            }

        }

        //check the target pile is empty or not
        public bool IsPileEmpty(PileName p)
        {
            switch (p)
            {
                case PileName.Stock:
                    return Stock.Empty;
                case PileName.Discard:
                    return Discard.Empty;
                default:
                    return true;

            }

        }

        public delegate void StateChangedEventHandler(State oldState, State newState);
        public event StateChangedEventHandler BeforeStateChange = delegate { };
		public event StateChangedEventHandler StateChanged = delegate { };

        public delegate void MeldHappenedEventHandler(MeldMove move);
        public delegate void LayoffHappendEventHandler(LayOffMove move);
        public delegate void MoveHappenedEventHandler(Move m);
        public delegate void GameOverEventHandler(Player theWinner);
        public event MeldHappenedEventHandler MeldHappend = delegate { };
        public event LayoffHappendEventHandler LayoffHappened = delegate { };
        public event MoveHappenedEventHandler MoveHappened = delegate { };
        public event GameOverEventHandler GameOver = delegate { };
    }
}
