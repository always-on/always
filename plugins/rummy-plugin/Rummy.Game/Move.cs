using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public abstract class Move
    {
        private readonly Player _player;

        public Player Player
        {
            get { return _player; }
        } 
        
        public abstract void Realize(GameState gameState);

        public Move(Player player)
        {
            _player = player;
        }
    }
}
