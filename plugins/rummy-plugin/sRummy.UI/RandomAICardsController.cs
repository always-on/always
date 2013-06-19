using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Rummy;

namespace SRummy.UI
{
    public class RandomAICardsController : PlayerCardsController
    {
        Random rnd = new Random();
        public PossibleMoves possibleMoves;
        List<Func<IEnumerable<Move>, IEnumerable<Move>>> _moveSelectorsPriorityList;
        public bool AutoPlay { get; set; }

        public event EventHandler CanPlay = delegate { };

        public RandomAICardsController(IGameUIServices uiServices, FanCanvas shape, GameState gameState, Player player)
            : base(uiServices, shape, gameState, player, false)
        {
            AutoPlay = true;

            possibleMoves = new PossibleMoves(player, gameState);
            gameState.MoveHappened += gameState_PlayerActed;

            InitMoveSelectorsPriorityList();
        }

        void gameState_PlayerActed(Move m)
        {
            CheckForActionOpportunity();
        }

        private void InitMoveSelectorsPriorityList()
        {
            Func<IEnumerable<Move>, IEnumerable<Move>> meldSelector =
                x =>
                {
                    var allMelds = x.OfType<MeldMove>().ToArray();

                    if (allMelds.Length == 0)
                        return Enumerable.Empty<Move>();

                    var maxMeldSize = allMelds.Max(y => y.Meld.Count());
                    return allMelds
                        .Where(y => y.Meld.Count() == maxMeldSize)
                        .Cast<Move>();
                };

            Func<IEnumerable<Move>, IEnumerable<Move>> layOffSelector =
                x => x.OfType<LayOffMove>().Cast<Move>();

            Func<IEnumerable<Move>, IEnumerable<Move>> everythingElse =
                x => x;

            _moveSelectorsPriorityList = new List<Func<IEnumerable<Move>, IEnumerable<Move>>>()
            {
                meldSelector,
                layOffSelector,
                everythingElse
            };
        }

        public void DoActionsPossible()
        {
            Console.WriteLine("DoActionsPossible");
            var allMoves = possibleMoves.Moves();

            if (allMoves.Count == 0)
            {
                Console.WriteLine("no move possible");
                return;
            }

            foreach (var selector in _moveSelectorsPriorityList)
            {
                if (TryRandomlyDoingOneMove(selector(allMoves)))
                    break;
            }
        }

        public Move GetBestMove()
        {
            var allMoves = possibleMoves.Moves();
            foreach (var selector in _moveSelectorsPriorityList)
            {
                var selected = PickRandomlyFrom(selector(allMoves));
                if(selected != null)
                    return selected;
            }

            return null;
        }

        private Move PickRandomlyFrom(IEnumerable<Move> moves)
        {
            if (moves == null)
                return null;

            var movesArr = moves.ToArray();

            if (movesArr.Length == 0)
                return null;

            var r = rnd.Next(movesArr.Length);

            return movesArr[r];
        }

        private bool TryRandomlyDoingOneMove(IEnumerable<Move> moves)
        {
            var n = moves.Count();
            for (int i = 0; i < n; i++)
            {
                var m = PickRandomlyFrom(moves);

                Console.WriteLine("A move selected: " + m);
                try
                {
                    m.Realize(GameState);
                }
                catch
                {
                    Console.WriteLine("move failed, trying another one");
                    continue;
                }

                break;
            }

            return true;
        }

        public bool DrawTime
        {
            get
            {
                return GameState.CanDraw(Player);
            }
        }

        public void CheckForActionOpportunity()
        {
            if (AutoPlay)
                DoActionsPossible();
            else if (possibleMoves.Moves().Count > 0)
                CanPlay(this, EventArgs.Empty);
        }
    }
}
