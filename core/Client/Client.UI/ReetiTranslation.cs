using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace Agent.UI
{
    class ReetiTranslation
    {
        private ReetiCommunication reeti = new ReetiCommunication();

        private const String HORIZONTAL      = "horizontal";
        private const String VERTICAL        = "vertical";
        private const String headNod         = "Global.SmallNod.play();";
        private const String neutralPosition = "Global.servo.neutralPosition();";
        private const String smileFace       = "Global.Happy.play();";
        private const String concernFace     = "Global.Sad.play();";
        private const String beginSpeech     = "Global.Talk.play(0.3);";
        private const String endSpeech       = "Global.Talk.play(0.3);";

        private long intLookBackLastMillisecond = -1;
        private int intBeginSpeechLastSecond    = -1;

        private double findOutput(String HorOrVer, String cmd)
        {
            double output;
            int start = 0, end = 0;

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
                if (output > 0)
                {
                    //output *= -1;
                    output = (output * 25) + 45;
                }
                else if (output < 0)
                {
                    output = (output * 25);
                }
                else
                    output = 50;
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
                    output = (output * 25) + 55;
                }
                else
                    output = 55.56;
            }
            return output;
        }

        private int getDelayAmount(String command)
        {
            int output = -1, begin = 0, end = 0;

            begin = command.IndexOf("MS=\"") + 4;
            end = command.IndexOf("\"</bookmark>");

            output = Convert.ToInt32(command.Substring(begin, end - begin));

            return output;
        }

        public void TranslateToReetiCommand(String task, String Command)
        {
            if (Command.Contains("HEADNOD") && task.Equals("perform"))
            {
                SendCommand(headNod);
            }
            if (Command.Contains("DELAY"))
            {
                System.Threading.Thread.Sleep(getDelayAmount(Command));
            }

            //TODO: check if face tracking!!!
            if (Command.Contains("GAZE")) //&& task.Equals("speech") )
            {
                double HorOutput = mapOutput(HORIZONTAL, findOutput(HORIZONTAL, Command));
                double VerOutput = mapOutput(VERTICAL, findOutput(VERTICAL, Command));

                if( (VerOutput < 75) && (VerOutput > 60) ) {
                    SendCommand("Global.LookAway.lookAwayThink();");
                }
                else if ( (VerOutput < 60) && (VerOutput > 20) )
                {
                    if (((DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond) - intLookBackLastMillisecond) > 2000)
                    {
                        intLookBackLastMillisecond = DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;
                        SendCommand("Global.LookAway.lookBack();");
                    }
                }
                else if (VerOutput < 20)
                {
                    SendCommand("Global.LookAway.lookAtBoard();");
                }
                else if (VerOutput >= 75)
                {
                    SendCommand("Global.LookAway.lookAwayAtRight();");
                }
            }
            if (Command.Contains("CONCERN"))
            {
                SendCommand(concernFace);
            }
            if (Command.Contains("SMILE"))
            {
                SendCommand(smileFace);
            }
            if (Command.Contains("WARM"))
            {
                SendCommand(neutralPosition);
            }
            if (Command.Contains("BEGINSPEECH"))
            {
                if (DateTime.Now.Second != intBeginSpeechLastSecond)
                {
                    intBeginSpeechLastSecond = DateTime.Now.Second;
                    SendCommand(beginSpeech);
                }
            }
            //if (Command.Contains("ENDSPEECH"))
            //{                
            //    SendCommand(endSpeech);
            //}
        }

        private void SendCommand(String Command)
        {
            reeti.Send(Command);
        }
    }
}
