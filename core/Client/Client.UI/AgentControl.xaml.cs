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
using System.Windows.Forms;
using System.Threading;
//using RagClient.Agent.Flash;
using System.Diagnostics;
using UnityUserControl;
using Microsoft.Win32;
using Agent.Tcp;

namespace Agent.UI
{
    public partial class AgentControl : System.Windows.Controls.UserControl, IAgentControl
	{
        public enum AgentType { Unity, Reeti, Mirror };

        public static AgentType agentType = AgentType.Unity;

        private ReetiTranslation AgentTranslate;

        UnityUserControl.UnityUserControl agent;

        System.Windows.Forms.WebBrowser page;
        public VideoCaller videoCaller;
		public event EventHandler<ActionDoneEventArgs> ActionDone = delegate { };
        public event EventHandler LoadComplete;
        XmlDocument xmlMessage = new XmlDocument();

        //Agent Controls
        public AgentControl()
        {
            Debug.WriteLine("Starting AgentControl...");
			Debug.WriteLine("agentType = " + agentType);
            Buttons = new NullChoiceButtons();

            InitializeComponent();

            InitAgent();

            Agent.Tcp.AgentControlJsonAdapter.ReetiIPReceived += (s, e) =>
            {
				if (agentType == AgentType.Unity)
				{
					agentType = AgentType.Reeti;
					Debug.WriteLine("Received REETI_IP "+Agent.Tcp.AgentControlJsonAdapter.REETI_IP+" (agentType = Reeti)");
				}
                if (AgentTranslate == null && (agentType == AgentType.Reeti) || (agentType == AgentType.Mirror))
                    AgentTranslate = new ReetiTranslation();
            };
        }

        private void agentCallbackListener(object sender, AgentEvent e)
        {
            if (e.value == "LoadComplete")
            {
                XmlDocument doc = new XmlDocument();
                doc.LoadXml("<CAMERA ZOOM=\"1.6\"/>");
                agent.perform(doc);
                LoadComplete(this, null);
            }
        }

        private void ttsCallbackListener(object sender, UnityUserControl.TTSEvent e)
        {
            switch (e.eventType)
            {
                case "viseme":
                    if ((Agent.Tcp.AgentControlJsonAdapter.REETI_IP != null) &&
                        (agentType != AgentType.Unity) && (AgentTranslate != null))
                        AgentTranslate.TranslateToReetiCommand("speech", e.eventValue);
                    break;
                case "bookmark":
                    if ((Agent.Tcp.AgentControlJsonAdapter.REETI_IP != null) &&
                        (agentType != AgentType.Unity) && (AgentTranslate != null))
                        AgentTranslate.TranslateToReetiCommand("speech", e.eventValue);
                    break;
                case "end":
                    if ((Agent.Tcp.AgentControlJsonAdapter.REETI_IP != null) &&
                        (agentType != AgentType.Unity) && (AgentTranslate != null))
                        AgentTranslate.TranslateToReetiCommand("speech", "ENDSPEECH");

                    ActionDone(this, new ActionDoneEventArgs("speech", e.sourceUtterance));
                    break;
            }
        }

        private void InitAgent()
        {
            setWebBrowserOptions();
            agent = new UnityUserControl.UnityUserControl();
            agent.Dock = System.Windows.Forms.DockStyle.Fill;
            agent.Location = new System.Drawing.Point(0, 0);
            agent.Init();
            InitPage();
            WFHost.Child = agent;
            agent.agentEvent += agentCallbackListener;
            agent.ttsEvent += ttsCallbackListener;
        }
        private void InitPage()
        {
            page = new System.Windows.Forms.WebBrowser();
            page.Dock = System.Windows.Forms.DockStyle.Fill;
            page.Location = new System.Drawing.Point(0, 0);
            page.MinimumSize = new System.Drawing.Size(20, 20);
            page.Name = "webBrowser";
            page.Size = new System.Drawing.Size(150, 150);
            agent.Controls.Add(page);
            //            WFHost.Child = agent;
            page.BringToFront();
            page.Visible = false;
            page.DocumentCompleted += new System.Windows.Forms.WebBrowserDocumentCompletedEventHandler(this.onPageLoad);
        }

        //This code sets the webbrowser used for hangout/the agent to be set to a later version instead of the default (which is IE6)

        private void setWebBrowserOptions()
        {
            var fileName = System.IO.Path.GetFileName(Process.GetCurrentProcess().MainModule.FileName);
            SetBrowserFeatureControlKey("FEATURE_BROWSER_EMULATION", fileName, GetBrowserEmulationMode()); // Webpages containing standards-based !DOCTYPE directives are displayed in IE10 Standards mode.
            SetBrowserFeatureControlKey("FEATURE_AJAX_CONNECTIONEVENTS", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_ENABLE_CLIPCHILDREN_OPTIMIZATION", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_MANAGE_SCRIPT_CIRCULAR_REFS", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_DOMSTORAGE ", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_GPU_RENDERING ", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_IVIEWOBJECTDRAW_DMLT9_WITH_GDI  ", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_DISABLE_LEGACY_COMPRESSION", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_LOCALMACHINE_LOCKDOWN", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_BLOCK_LMZ_OBJECT", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_BLOCK_LMZ_SCRIPT", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_DISABLE_NAVIGATION_SOUNDS", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_SCRIPTURL_MITIGATION", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_SPELLCHECKING", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_STATUS_BAR_THROTTLING", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_TABBED_BROWSING", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_VALIDATE_NAVIGATE_URL", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_WEBOC_DOCUMENT_ZOOM", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_WEBOC_POPUPMANAGEMENT", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_WEBOC_MOVESIZECHILD", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_ADDON_MANAGEMENT", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_WEBSOCKET", fileName, 1);
            SetBrowserFeatureControlKey("FEATURE_WINDOW_RESTRICTIONS ", fileName, 0);
            SetBrowserFeatureControlKey("FEATURE_XMLHTTP", fileName, 1);
        }


        private UInt32 GetBrowserEmulationMode()
        {
            //From http://stackoverflow.com/questions/18333459/c-sharp-webbrowser-ajax-call/20848398#20848398
            int browserVersion = 11;
            using (var ieKey = Registry.LocalMachine.OpenSubKey(@"SOFTWARE\Microsoft\Internet Explorer",
                RegistryKeyPermissionCheck.ReadSubTree,
                System.Security.AccessControl.RegistryRights.QueryValues))
            {
                var version = ieKey.GetValue("svcVersion");
                if (null == version)
                {
                    version = ieKey.GetValue("Version");
                    if (null == version)
                        throw new ApplicationException("Microsoft Internet Explorer is required!");
                }
                int.TryParse(version.ToString().Split('.')[0], out browserVersion);
            }

            UInt32 mode = 10001; // Internet Explorer 10. Webpages containing standards-based !DOCTYPE directives are displayed in IE10 Standards mode. Default value for Internet Explorer 10.
            switch (browserVersion)
            {
                case 7:
                    mode = 7000; // Webpages containing standards-based !DOCTYPE directives are displayed in IE7 Standards mode. Default value for applications hosting the WebBrowser Control.
                    break;
                case 8:
                    mode = 8000; // Webpages containing standards-based !DOCTYPE directives are displayed in IE8 mode. Default value for Internet Explorer 8
                    break;
                case 9:
                    mode = 9000; // Internet Explorer 9. Webpages containing standards-based !DOCTYPE directives are displayed in IE9 mode. Default value for Internet Explorer 9.
                    break;
                default:
                    // use IE10 mode by default
                    break;
            }

            return mode;
        }

        private void SetBrowserFeatureControlKey(string feature, string appName, uint value)
        {
            using (var key = Registry.CurrentUser.CreateSubKey(
                String.Concat(@"Software\Microsoft\Internet Explorer\Main\FeatureControl\", feature),
                RegistryKeyPermissionCheck.ReadWriteSubTree))
            {
                key.SetValue(appName, (UInt32)value, RegistryValueKind.DWord);
            }
        }
        //End of browser emulation code

        private void onPageLoad(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            if (page.ScrollBarsEnabled)
            {
                page.ScrollBarsEnabled = false;
            }
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

        public void initVideoCaller(MessageDispatcherImpl dispatcher)
        {
            videoCaller = new VideoCaller(dispatcher);
            videoCaller.addCaller(agent);
            dispatcher.RegisterReceiveHandler("acceptCall", new MessageHandlerDelegateWrapper(m => acceptCall()));
            dispatcher.RegisterReceiveHandler("endCall", new MessageHandlerDelegateWrapper(m => endCall()));
            dispatcher.RegisterReceiveHandler("videoCallRejected", new MessageHandlerDelegateWrapper(m => rejectCall()));
        }

        public void acceptCall()
        {
            agent.Invoke((MethodInvoker)delegate
             {
                 videoCaller.acceptCall();
                 agent.webBrowser.Visible = false;
             });
        }

        public void endCall()
        {
            agent.Invoke((MethodInvoker)delegate
            {
                //agent.webBrowser.Visible = true;
                videoCaller.endCall();
            });
        }

        public void rejectCall()
        {
            agent.Invoke((MethodInvoker)delegate
            {
                //agent.webBrowser.Visible = true;
                videoCaller.rejectCall();
            });
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

        public void SetVisible(Boolean status)
        {
            agent.Invoke((System.Windows.Forms.MethodInvoker)delegate
            {
				agent.Visible = status;
				//agent.webBrowser.Visible = status;
            });
        }

        private void Perform(string xmlCommand)
        {
            //trick to ensure the id is getting sent to the java side if it updates while the agent is running
            if (!videoCaller.idSent)
                videoCaller.sendCommunicationURL();
            if ((Agent.Tcp.AgentControlJsonAdapter.REETI_IP != null) &&
                ((agentType == AgentType.Reeti) || (agentType == AgentType.Mirror)))
                AgentTranslate.TranslateToReetiCommand("perform", xmlCommand);
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

        public void ShowPage(string url)
        {
            agent.Invoke((MethodInvoker)delegate
            {
                //agent.Visible = false;
                if (url != "")
                {
					if (agentType == AgentType.Reeti) agent.Visible = true; 
                    page.Visible = true;
                    page.Navigate(url);
                    //                page.Navigate(url);
                    page.ScrollBarsEnabled = false;
                }
                else
                {
					if (agentType == AgentType.Reeti) agent.Visible = false; 
                    page.Visible = false;
                }
            });
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
