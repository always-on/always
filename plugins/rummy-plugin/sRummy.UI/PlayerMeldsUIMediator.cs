using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SRummy.UI
{
    public class PlayerMeldsUIManager
    {
        List<MeldController> _controllers;
        GameState _gameState;
        private Player _player;

        public PlayerMeldsUIManager(IGameUIServices uiServices, GameState gameState, Player player, IList<MeldController> controllers)
        {
            _controllers = new List<MeldController>(controllers);
            _gameState = gameState;
            _player = player;

            _gameState.MeldHappend += GameState_MeldHappend;
            _gameState.LayoffHappened +=
                move => uiServices.GetControllerFor(move.Meld).RenderContents();
        }

        MeldController GetAFreeController()
        {
            return _controllers.First(x => x.AssociatedWithGameStateAlready == false);
        }

        void GameState_MeldHappend(MeldMove move)
        {
            if(move.Player != _player)
                return;

            var con = GetAFreeController();

            var idx = _gameState.GetMelds(_player).IndexOf(move.Meld);

            con.AssociateWithMeldInGameState(idx);
            con.RenderContents();
        }
    }
}
