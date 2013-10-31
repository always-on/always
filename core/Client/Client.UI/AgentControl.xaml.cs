using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Speech.Recognition;
using System.Speech.Recognition.SrgsGrammar;
using Agent.Core;
using System.Xml;
//using RagClient.Debugger.Tests;
using System.Speech.Synthesis;
using System.Text.RegularExpressions;
using System.Timers;
using FormsControl = System.Windows.Forms.Control;
using FormsPanel = System.Windows.Forms.Panel;
using System.Threading;
//using RagClient.Agent.Flash;
using System.Diagnostics;
using UnityUserControl;


namespace Agent.UI
{
	public partial class AgentControl : UserControl, IAgentControl
	{
        public enum AgentType { Unity, Reeti, Mirror };
        
        public static AgentType agentType;

        private ReetiTranslation AgentTranslate;

        UnityUserControl.UnityUserControl agent;
		public event EventHandler<ActionDoneEventArgs> ActionDone = delegate { };
        public event EventHandler LoadComplete;
        XmlDocument xmlMessage = new XmlDocument();

        //Agent Controls
        public AgentControl()
        {
            Console.WriteLine("Starting AgentControl...");
            Buttons = new NullChoiceButtons();

            InitializeComponent();

            InitAgent();

            if(agentType == AgentType.Reeti)
                AgentTranslate = new ReetiTranslation();
        }

        private void agentCallbackListener(object sender, AgentEvent e)
        {
            if(e.value == "LoadComplete")
                LoadComplete(this, null);
        }

        private void ttsCallbackListener(object sender, UnityUserControl.TTSEvent e)
        {
            switch (e.eventType)
            {
                case "viseme":
                    // Put Reeti code here
                    break;
                case "bookmark":
                    AgentTranslate.TranslateToReetiCommand("speech", e.eventValue);
                    break;
                case "end":
                    ActionDone(this,new ActionDoneEventArgs("speech",e.sourceUtterance));
                    break;
            }
        }

        private void InitAgent()
        {
            agent = new UnityUserControl.UnityUserControl();
            agent.Dock = System.Windows.Forms.DockStyle.Fill; 
            agent.Init();
            WFHost.Child = agent;
            agent.agentEvent += agentCallbackListener;
            agent.ttsEvent += ttsCallbackListener;
        }

        public void Nod()
        {
            Express(AgentFaceExpression.Nod);
        }

        public void NormalExpression()
        {
            Express(AgentFaceExpression.Warm);
        }


        public void Smile(int mseconds)
        {
            Express(AgentFaceExpression.Smile);
            //ScheduleReturningToNormalExpression(mseconds);
        }

        public void ShowConcern(int mseconds)
        {
            Express(AgentFaceExpression.Concern);
            //ScheduleReturningToNormalExpression(mseconds);
        }

        public void Idle(bool enabled)
        {
            //NYI
        }

        /* DEPRECATED
        private void ScheduleReturningToNormalExpression(int mseconds)
        {
            var t = new System.Timers.Timer(mseconds);
            t.Elapsed += (s, e) =>
            {
                NormalExpression();
                t.Stop();
                t.Dispose();
            };
            t.Start();
        }*/

        private void Perform(string xmlCommand)
        {
            AgentTranslate.TranslateToReetiCommand("perform",xmlCommand);
            xmlMessage.LoadXml(xmlCommand);
            agent.perform(xmlMessage);
        }

        private void HideNorthEasternLogoOnAgent()
        {
            //NYI
        }

        public void Say(string text)
        {
            agent.speak(text);
        }

        public void Turn(string dir, float horizontal, float vertical)
        {
            Perform("<GAZE dir=\"" + dir + "\" horizontal=\"" + horizontal + "\" vertical=\"" + vertical + "\" />");
        }

        public void Express(AgentFaceExpression expression)
        {
            Perform(expression.getAction());
        }

        public event EventHandler<UserSelectedButtonEventArgs> UserSelectedButton = delegate { };

        public void Delay(int seconds)
        {
            Perform("<DELAY MS=\"" + seconds * 1000 + "\"/>");
        }

        public void ShowMenu(IList<string> buttons, bool twoColumn)
        {
            Buttons.RenderButtons(buttons, twoColumn);
        }

        public void StopSpeech()
        {
            agent.stopSpeak();
        }

/***********************************************UI CONTROLS*******************************/
		private IChoiceButtons _buttons;
		public IChoiceButtons Buttons
		{
			get { return _buttons; }
			set
			{
				if (_buttons != null)
				{
					_buttons.UserSelectedButton -= Buttons_UserSelectedButton;
				}

				if (value != null)
					_buttons = value;
				else
					_buttons = new NullChoiceButtons();

				_buttons.UserSelectedButton += Buttons_UserSelectedButton;
			}
		}

		void Buttons_UserSelectedButton(object sender, UserSelectedButtonEventArgs e)
		{
			Nod();
			UserSelectedButton(sender, e);
		}

		protected override Size MeasureOverride(Size constraint)
		{
			if (constraint.IsEmpty)
				return base.MeasureOverride(constraint);

			if (double.IsInfinity(constraint.Width) && double.IsInfinity(constraint.Height))
				return base.MeasureOverride(constraint);

			double w = constraint.Width;
			double h = constraint.Height;

			var minDim = Math.Min(w, h);

			return new Size(minDim, minDim);
		}

		private IEnumerable<FormsControl> GetControlsRecursively(FormsControl c)
		{
			yield return c;

			foreach (FormsControl ic in c.Controls)
				foreach (FormsControl ctrl in GetControlsRecursively(ic))
					yield return ctrl;
		}


        //Unused?
        /*
        protected override Size ArrangeOverride(Size arrangeBounds)
        {
            /*
			var p = base.ArrangeOverride(arrangeBounds);

			var faParent = fa.Parent as FormsPanel;

			fa.Size = new System.Drawing.Size((int)(faParent.ClientSize.Width * 1.08), (int)(faParent.ClientSize.Height * 1.08));
			fa.Left = -(int)(faParent.ClientSize.Width * 0.08);

			return p;*/
        /*    return base.ArrangeOverride(arrangeBounds);
        }*/
	}
}
