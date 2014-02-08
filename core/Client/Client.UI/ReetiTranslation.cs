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

        private const String HORIZONTAL        = "horizontal";
        private const String VERTICAL          = "vertical";
        private const String headNod           = "Global.SmallNod.play();";
        private const String neutralPosition   = "Global.servo.neutralPosition();";
        private const String smileFace         = "Global.Happy.play();";
        private const String concernFace       = "Global.Sad.play();";
        private const String moveMouth         = "Global.Talk.play();";
        private const String mouthOpenDuration = "Global.Talk.setMouthOpenDuration(";
        private const String letMouthMove      = "Global.Talk.changeMouthPermission(true);";
        private const String stopMouthMove     = "Global.Talk.changeMouthPermission(false);";

        private bool blnHeadNod         = true;
        private bool blnDelay           = true;
        private bool blnLookAwayThink   = true;
        private bool blnLookBack        = true;
        private bool blnLookAtBoard     = true;
        private bool blnLookAwayAtRight = true;
        private bool blnConcern         = true;
        private bool blnSmile           = true;
        private bool blnWarm            = true;
        private bool blnViseme          = true;
        private bool blnEndSpeech       = true;

        private int intAccumulatedVisemeDuration = 701;

        private bool blnTalkAlreadyStarted = false;

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
                if (output > 0)
                {
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

        private int getSpeechPermission(String command)
        {
            int begin = 0, end = 0;
            int intDuration = 0;

            begin = command.IndexOf("duration=\"") + 10;
            end = command.IndexOf("\">");

            intAccumulatedVisemeDuration += Convert.ToInt32(command.Substring(begin, end - begin));

            if (intAccumulatedVisemeDuration > 700)
            {
                intDuration = intAccumulatedVisemeDuration;
                intAccumulatedVisemeDuration = 0;
                return intDuration;
            }
            else
                return -1;
        }

        private int getDelayAmount(String command)
        {
            int output = -1, begin = 0, end = 0;

            begin = command.IndexOf("MS=\"") + 4;
            end = command.IndexOf("\"</bookmark>");

            output = Convert.ToInt32(command.Substring(begin, end - begin));

            return output;
        }

        private void updateRobotState(String strState)
        {
            switch(strState)
            {
                case "HeadNod":
                    blnHeadNod         = false;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "Delay":
                    blnHeadNod         = true;
                    blnDelay           = false;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "LookAwayThink":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = false;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "LookBack":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = false;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "LookAtBoard":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = false;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "LookAwayAtRight":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = false;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "Concern":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = false;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "Smile":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = false;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "Warm":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = false;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "Viseme":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = false;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
                case "EndSpeech":
                    blnHeadNod         = true;
                    blnDelay           = true;
                    blnLookAwayThink   = true;
                    blnLookBack        = true;
                    blnLookAtBoard     = true;
                    blnLookAwayAtRight = true;
                    blnConcern         = true;
                    blnSmile           = true;
                    blnWarm            = true;
                    blnViseme          = true;
                    blnEndSpeech       = true;
                    break;
            }
        }

        public void TranslateToReetiCommand(String task, String Command)
        {
            int intDuration = 0;

            if (Command.Contains("HEADNOD") && task.Equals("perform"))
            {
                if (blnHeadNod) SendCommand(headNod);
                updateRobotState("HeadNod");
            }
            else if (Command.Contains("DELAY"))
            {
                if (blnDelay) System.Threading.Thread.Sleep(getDelayAmount(Command));
                updateRobotState("Delay");
            }

            //TODO: check if face tracking!!!
            else if (Command.Contains("GAZE")) //&& task.Equals("speech") )
            {
                double HorOutput = mapOutput(HORIZONTAL, findOutput(HORIZONTAL, Command));
                double VerOutput = mapOutput(VERTICAL, findOutput(VERTICAL, Command));

                if( (VerOutput <= 78) && (VerOutput > 60) ) {
                    if (blnLookAwayThink) SendCommand("Global.LookAway.lookAwayThink();");
                    updateRobotState("LookAwayThink");
                }
                else if ( (VerOutput < 60) && (VerOutput > 30) )
                {
                    if (blnLookBack) SendCommand("Global.LookAway.lookBack();");
                    updateRobotState("LookBack");
                }
                else if (VerOutput < 30)
                {
                    if (blnLookAtBoard) SendCommand("Global.LookAway.lookAtBoard();");
                    updateRobotState("LookAtBoard");
                }
                else if (VerOutput >= 79)
                {
                    if (blnLookAwayAtRight) SendCommand("Global.LookAway.lookAwayAtRight();");
                    updateRobotState("LookAwayAtRight");
                }
            }
            else if (Command.Contains("CONCERN"))
            {
                if (blnConcern) SendCommand(concernFace);
                updateRobotState("Concern");
            }
            else if (Command.Contains("SMILE"))
            {
                if (blnSmile) SendCommand(smileFace);
                updateRobotState("Smile");
            }
            else if (Command.Contains("WARM"))
            {
                if (blnWarm) SendCommand(neutralPosition);
                updateRobotState("Warm");
            }
            else if (Command.Contains("viseme"))
            {
                if (blnViseme)
                {
                    if (!blnTalkAlreadyStarted)
                    {
                        SendCommand(moveMouth);
                        blnTalkAlreadyStarted = true;
                    }
                }
                updateRobotState("Viseme");
            }
            if (Command.Contains("ENDSPEECH"))
            {
                if (blnEndSpeech)
                {
                    SendCommand(stopMouthMove);
                    //intAccumulatedVisemeDuration = 701;
                    blnTalkAlreadyStarted = false;
                }
                updateRobotState("EndSpeech");
            }
        }

        private void SendCommand(String Command)
        {
            reeti.Send(Command);
        }
    }
}
