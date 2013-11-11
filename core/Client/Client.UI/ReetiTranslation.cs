using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.UI
{
    class ReetiTranslation
    {
        private ReetiCommunication reeti = new ReetiCommunication();
        private const String HORIZONTAL = "horizontal";
        private const String VERTICAL = "vertical";
        private String Smooth = " smooth:";
        private const String Head_Nod = "Global.HeadNod.play();";
        private const String Hor_Rotate = "Global.servo.neckRotat =";
        private const String Ver_Tilt = "Global.servo.neckTilt =";
        private String Neutral_Position = "Global.servo.neutralPosition();"; 
        private const String Smile = "Global.Happy.play();";
        private const String Concern = "Global.Sad.play();";

        private String ConstructMessage(double HorOutput, double VerOutput)
        {
            String cmd;
            cmd = Hor_Rotate + HorOutput + Smooth + "1s" + ",";
            cmd += Ver_Tilt;
            cmd += VerOutput + Smooth + "1s" + ";";
            return cmd;
        }

        private double findOutput(String HorOrVer, String cmd) // What does this method do?
        {
            double output;
            int start = 0;
            int end = 0;
            if (HorOrVer.Equals(HORIZONTAL))
            {
                start = cmd.IndexOf(HORIZONTAL) + 12;
                end = cmd.IndexOf(VERTICAL) - 2;
            }
            else if (HorOrVer.Equals(VERTICAL))
            {

                if (cmd.Contains("bookmark"))
                {
                    start = cmd.IndexOf(VERTICAL) + 10;
                    end = cmd.IndexOf("</bookmark>") - 1;
                }
                else
                {
                    start = cmd.IndexOf(VERTICAL) + 10;
                    end = cmd.IndexOf("/>") - 2;
                }

            }
            output = Convert.ToSingle(cmd.Substring(start, end - start));
            return output;
        }

        private double mapOutput(String HorOrVer, double output)
        {
            if (HorOrVer.Equals(HORIZONTAL))
            {
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
                else // DO you mean output == 0?
                    output = 50;    //Insert from JSOn File
            }
            else if (HorOrVer.Equals(VERTICAL))
            {
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
                    output = 55.56; //Insert from json File
            }
            return output;
        }

        public void TranslateToReetiCommand(String task, String Command) // How about sad face?
        {
            if (Command.Contains("NOD") && task.Equals("perform"))
            {
                SendCommand(Head_Nod);
            }
            //TODO: check if face tracking
            if (Command.Contains("GAZE")) //&& task.Equals("speech") )
            {
                double HorOutput = mapOutput(HORIZONTAL, findOutput(HORIZONTAL, Command));
                double VerOutput = mapOutput(VERTICAL, findOutput(VERTICAL, Command));

                String command = ConstructMessage(HorOutput, VerOutput);
                SendCommand(command);
            }
            if (Command.Contains("CONCERN"))
            {
                SendCommand(Concern);
            }
            if (Command.Contains("SMILE"))
            {
                SendCommand(Smile);
            }
            if (Command.Contains("WARM"))
            {
                SendCommand(Neutral_Position);
            }
        }

        private void SendCommand(String Command)
        {
            reeti.Send(Command);
        }
    }
}
