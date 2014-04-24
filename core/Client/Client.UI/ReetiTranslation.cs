using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Diagnostics;
using System.Threading;

namespace Agent.UI
{
    class ReetiTranslation
    {
        private static Mutex mut = new Mutex();

        private ReetiCommunication reeti = new ReetiCommunication();

        private const String headNod         = "aa"; // smallNod
        private const String moveMouth       = "bb"; // startTalking
        private const String stopMouthMove   = "cc"; // stopTalking
        private const String lookAwayThink   = "dd"; // lookAwayThink
        private const String lookAtBoard     = "ee"; // lookAtBoard
        private const String lookBack        = "ff"; // lookBack
        private const String lookAwayAtRight = "gg"; // lookAwayAtRight
        private const String expressHappy    = "hh"; // expressHappy
        private const String expressSad      = "ii"; // expressSad
        private const String bigNod          = "jj"; // bigNod
        private const String expressWarm     = "kk"; // Global.servo.neutralPosition()
        private const String delay           = "ll"; // delay

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

        private int intAccumulatedVisemeDuration = 351;

        private bool blnTalkAlreadyStarted = false;

        private double findOutput(String HorOrVer, String cmd)
        {
            double output;
            int start = 0, end = 0;

            if (HorOrVer.Equals("horizontal"))
            {
                start = cmd.IndexOf("horizontal") + 12;
                end = cmd.IndexOf("vertical") - 2;
            }
            else if (HorOrVer.Equals("vertical"))
            {
                if (cmd.Contains("bookmark"))
                {
                    start = cmd.IndexOf("vertical") + 10;
                    end = cmd.IndexOf("</bookmark>") - 1;
                }
                else
                {
                    start = cmd.IndexOf("vertical") + 10;
                    end = cmd.IndexOf("/>") - 2;
                }
            }
            output = Convert.ToSingle(cmd.Substring(start, end - start));
            return output;
        }

        private double mapOutput(String HorOrVer, double output)
        {
            if (HorOrVer.Equals("horizontal"))
            {
                output = ((output + 1) * 50);
                //if (output > 0)
                //{
                //    output = (output * 25) + 45;
                //}
                //else if (output < 0)
                //{
                //    output = (output * 25);
                //}
                //else
                //    output = 50;
            }
            else if (HorOrVer.Equals("vertical"))
            {
                output = ((output + 1) * 50);
                //if (output < 0)
                //{
                //    output *= -1;
                //    output = (output * 25);
                //}
                //else if (output > 0)
                //{
                //    output = (output * 25) + 55;
                //}
                //else
                //    output = 55.56;
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

            if (intAccumulatedVisemeDuration > 350)
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

        private Boolean isClosedViseme(String command)
        {
            int begin = 0, end = 0, number = -1;

            begin = command.IndexOf("\">") + 2;
            end = command.IndexOf("</viseme>");

            number = Convert.ToInt32(command.Substring(begin, end - begin));

            return (number == 0) ? true : false;
        }

        private void updateRobotState(String strState)
        {
            switch(strState)
            {
                case "HeadNod":
                    blnHeadNod         = false;
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
                    blnViseme          = false;
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
                    blnEndSpeech       = false;
                    break;
            }
        }

        public void TranslateToReetiCommand(String task, String Command)
        {
            mut.WaitOne();

            //if (Command.Contains("HEADNOD"))
            //{
            //    if (blnHeadNod)
            //    {
            //        SendCommand(headNod);
            //        updateRobotState("HeadNod");
            //    }
            //}
            //else 
            if (Command.Contains("DELAY"))
            {
                if (blnDelay)
                {
                    // NB: If we need to send any delay to the robot.
                    /*int delayAmount = getDelayAmount(Command)/1000;
                    SendCommand(delay + ((delayAmount < 10) ? ",0" : ",") + delayAmount);*/
                    
                    // NB: If we need to make further delay on the PC side.
                    /*Stopwatch stopwatch = new Stopwatch();
                    stopwatch = Stopwatch.StartNew();
                    while (stopwatch.ElapsedMilliseconds < (delayAmount*1000));
                    stopwatch.Stop();*/
                    
                    updateRobotState("Delay");
                }
            }

            //TODO: check if face tracking!!!
            else if (Command.Contains("GAZE"))
            {
                double HorOutput = mapOutput("horizontal", findOutput("horizontal", Command));
                double VerOutput = mapOutput("vertical", findOutput("vertical", Command));

                if( (VerOutput <= 78) && (VerOutput > 60) ) {
                    if (blnLookAwayThink)
                    {
                        SendCommand(lookAwayThink);
                        updateRobotState("LookAwayThink");
                    }
                }
                else if ( (VerOutput < 60) && (VerOutput > 30) )
                {
                    if (blnLookBack)
                    {
                        SendCommand(lookBack);
                        updateRobotState("LookBack");
                    }
                }
                else if (VerOutput < 30)
                {
                    if (blnLookAtBoard)
                    {
                        SendCommand(lookAtBoard);
                        updateRobotState("LookAtBoard");
                    }
                }
                else if (VerOutput >= 79)
                {
                    if (blnLookAwayAtRight)
                    {
                        SendCommand(lookAwayAtRight);
                        updateRobotState("LookAwayAtRight");
                    }
                }
            }
            else if (Command.Contains("CONCERN"))
            {
                if (blnConcern)
                {
                    SendCommand(expressSad);
                    updateRobotState("Concern");
                }
            }
            else if (Command.Contains("SMILE"))
            {
                if (blnSmile)
                {
                    SendCommand(expressHappy);
                    updateRobotState("Smile");
                }
            }
            else if (Command.Contains("WARM"))
            {
                if (blnWarm)
                {
                    SendCommand(expressWarm);
                    updateRobotState("Warm");
                }
            }
            else if (Command.Contains("viseme"))
            {
                if (blnViseme)
                {
                    if (!blnTalkAlreadyStarted)
                    {
                        SendCommand(moveMouth);
                        updateRobotState("Viseme");
                        blnTalkAlreadyStarted = true;
                    }
                }
                if (isClosedViseme(Command))
                {
                   SendCommand(stopMouthMove);
                   updateRobotState("EndSpeech");
                   blnTalkAlreadyStarted = false;
                }
            }
            else  if (Command.Contains("ENDSPEECH"))
            {
                if (blnEndSpeech)
                {
                    SendCommand(stopMouthMove);
                    updateRobotState("EndSpeech");
                    blnTalkAlreadyStarted = false;
                }
            }
            mut.ReleaseMutex();
        }

        private void SendCommand(String Command)
        {
            reeti.Send(Command);
        }
    }
}
