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
        private String Hor_Rotate = "Global.servo.neckRotat =";
        private String Ver_Tilt = "Global.servo.neckTilt =";
        private String Neutral_Position = "Global.servo.neutralPosition();";
        private String Smile = "Global.servo.Happy.play();";
        public void TranslateToReetiCommand(String task, String Command)
        {
            if (Command.Contains("NOD") && task.Equals("perform"))
            {
                SendCommand(Head_Nod);
            }
            if (Command.Contains("GAZE") && task.Equals("speech"))
            {
                String command;
                double output;
               
                command = Hor_Rotate;
                int start = Command.IndexOf("horizontal") + 12;
                int end = Command.IndexOf("vertical") - 2;
                output = Convert.ToSingle(Command.Substring(start, end - start));
                //Mirror the Agent 
                if (output < 0)
                {
                    output *= -1;
                    output = (output * 25) + 50;
                }
                else if (output > 0)
                {
                    output = (output * 25);
                }
                else
                    output = 50;
                command += output + ",";

                command += Ver_Tilt;
                start = Command.IndexOf("vertical") + 10;
                end = Command.IndexOf("</bookmark>") - 1;
                output = Convert.ToSingle(Command.Substring(start, end - start));
                if (output < 0)
                {
                    output *= -1;
                    output = (output * 25);
                }
                else if (output > 0)
                {
                    output = (output * 25) + 50;
                }
                else
                    output = 55.56;
                command += output + ";";
                SendCommand(command);
            }
            if (Command.Contains("SMILE") && task.Equals("perform"))
            {
                SendCommand(Smile);
            }
        }
        private void SendCommand(String Command)
        {
            reeti.Send(Command);
        }
    }
}
