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
using RagClient.Debugger.Tests;
using System.Speech.Synthesis;
using System.Text.RegularExpressions;
using System.Timers;
using FormsControl = System.Windows.Forms.Control;
using FormsPanel = System.Windows.Forms.Panel;
using System.Threading;
using RagClient.Agent.Flash;
using System.Diagnostics;

namespace Agent.UI
{
	public partial class AgentControl : UserControl, IAgentControl
	{
		RagClient.Controls.GazeAgent _agent;
		bool _usingLoquendo;
		public event EventHandler<ActionDoneEventArgs> ActionDone = delegate { };
		private RagClient.Components.FlashAgent flashAgent;
		private readonly SpeechManagerHelper speechManager = new SpeechManagerHelper();

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

		public AgentControl()
		{
			Buttons = new NullChoiceButtons();

			InitializeComponent();

			InitAgent();

			TryUsingLoquendoTts();
		}

		private void TryUsingLoquendoTts()
		{
			SpeechSynthesizer ttsSynth = new SpeechSynthesizer();

			Console.WriteLine("Listing installed speech synthesizer voices...");
			foreach (InstalledVoice ttsVoice in ttsSynth.GetInstalledVoices())
			{
				if (ttsVoice.VoiceInfo.Name == "Susan" && ttsVoice.VoiceInfo.AdditionalInfo["Vendor"] == "Loquendo")
				{
					SwitchToSusanVoice();
				}
			}
		}

		private void SwitchToSusanVoice()
		{
			_agent.Voice = "Susan";
			_usingLoquendo = true;
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


		private RagClient.Debugger.Tests.FlashAgent_webagent fa;
		private void InitAgent()
		{
			_agent = new RagClient.Controls.GazeAgent();
			WFHost.Child = _agent;
			fa = GetControlsRecursively(_agent).OfType<RagClient.Debugger.Tests.FlashAgent_webagent>().First();
			fa.Dock = System.Windows.Forms.DockStyle.None;

			HideNorthEasternLogoOnAgent();

			flashAgent = (RagClient.Components.FlashAgent)_agent.GetType().GetField("agent", System.Reflection.BindingFlags.NonPublic | System.Reflection.BindingFlags.Instance).GetValue(_agent);
			flashAgent.ActionCompleted += flashAgent_ActionCompleted;

			SendACommandToMakeAgentAppear();

			Perform("<POSTURE />");//init agent body for idle behaviors

		}


		protected override Size ArrangeOverride(Size arrangeBounds)
		{
			var p = base.ArrangeOverride(arrangeBounds);

			var faParent = fa.Parent as FormsPanel;

			fa.Size = new System.Drawing.Size((int)(faParent.ClientSize.Width * 1.08), (int)(faParent.ClientSize.Height * 1.08));
			fa.Left = -(int)(faParent.ClientSize.Width * 0.08);

			return p;
		}

		private IEnumerable<FormsControl> GetControlsRecursively(FormsControl c)
		{
			yield return c;

			foreach (FormsControl ic in c.Controls)
				foreach (FormsControl ctrl in GetControlsRecursively(ic))
					yield return ctrl;
		}

		void flashAgent_ActionCompleted(object sender, RagClient.Agent.ActionCompletedEventArgs e)
		{
			if (e.Action is RagClient.Debugger.Tests.Speech)
			{
				var act = (RagClient.Debugger.Tests.Speech)e.Action;
				string text;
				if (_usingLoquendo)
					text = act.Text;
				else {
					var fld = act.Prompt.GetType().GetField("_text", System.Reflection.BindingFlags.Instance | System.Reflection.BindingFlags.NonPublic);
					text = fld.GetValue(act.Prompt) as string;
				}

				System.Diagnostics.Debug.Assert(text != null);

				text = Regex.Replace(text, "(<[^>]+>)", "");
				var d = DateTime.Now;
				LogUtils.LogWithTime("finished saying (" + text + ")");
				ActionDone(this, new ActionDoneEventArgs("speech", text));
			}
		}

		private void SendACommandToMakeAgentAppear()
		{
			Nod();
			
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
			ScheduleReturningToNormalExpression(mseconds);
		}

		public void ShowConcern(int mseconds)
		{
			Express(AgentFaceExpression.Concern);
			ScheduleReturningToNormalExpression(mseconds);
		}

		public void Idle(bool enabled)
		{
			DoActionOnFlashAgent(new RagClient.Agent.Actions.Idle(enabled ? "ON" : "OFF"));
		}

		

		private void DoActionOnFlashAgent(RagClient.Agent.AbstractAction action)
		{
			try
			{
				var p = (FlashPlayer)flashAgent.Context.Player;
				flashAgent.Invoke(new Action(() =>
				{
					flashAgent.Do(action);
				}));
			}
			catch (InvalidOperationException)
			{
				flashAgent.Do(action);
			}
		}

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
		}

		private void Perform(string xmlCommand)
		{
			_agent.XmlEngine.XmlText = "<perform>" + xmlCommand + "</perform>";
		}

		private void HideNorthEasternLogoOnAgent()
		{
			var northEasternLogo = _agent.Controls.OfType<System.Windows.Forms.PictureBox>()
				.First();

			northEasternLogo.Visible = false;
		}

		public void Say(string text)
		{
			LogUtils.LogWithTime("start say: " + text);
			var speechXML = "<speak  version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" " +
									"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
									"xsi:schemaLocation=\"http://www.w3.org/2001/10/synthesis.xsd\" " +
									"xml:lang=\"en-US\">"
						+ text + "</speak>";
			Speech speech = new Speech(speechXML);
			speech.Prompt = new Prompt(speechXML, SynthesisTextFormat.Ssml);
			DoActionOnFlashAgent(speech);
		}

		public void Say(string text, IList<string> buttons)
		{
			Console.WriteLine("Saying... " + (DateTime.Now - DateTime.Today).TotalMilliseconds);

			Say(text);
		}

		public void Turn(AgentGaze gaze)
		{
			Perform("<Orientation cmd=\"" + gaze + "\"/>"); ///THIS
		}

		public void Express(AgentFaceExpression expression)
		{
            Perform(expression.getAction());
		}

		public event EventHandler<UserSelectedButtonEventArgs> UserSelectedButton = delegate { };

		public void Delay(int seconds)
		{
			Perform("<Delay MS=\"" + seconds * 1000 + "\"/>");
		}

		public void ShowMenu(IList<string> buttons, bool twoColumn)
		{
			Buttons.RenderButtons(buttons, twoColumn);
		}

		public void StopSpeech()
		{
			if (flashAgent.Context.ContainsKey("SpeechManager"))
			{
				var manager = flashAgent.Context["SpeechManager"] as SpeechManager;
				if (manager != null)
					speechManager.StopCurrentSpeech(manager);
			}
		}
	}
}
