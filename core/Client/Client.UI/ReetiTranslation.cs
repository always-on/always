using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.UI
{
    class ReetiTranslation
    {
        private ReetiCommunication reeti = new ReetiCommunication();
        private String Head_Nod = "Global.servo.HeadNod.play();";
        public void TranslateToReetiCommand(String Command)
        {
            if (Command.Contains("NOD"))
            {
                SendCommand(Head_Nod);
            }
        }
        private void SendCommand(String Command)
        {
            reeti.Send(Command);
        }
    }
}
