using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;
using Microsoft.Win32;
using Agent.Tcp;
using System.Windows.Forms;
using Newtonsoft.Json.Linq;
using System.Threading;
using System.Timers;
//using System.Windows.Input;

namespace Agent.UI
{
    [ComVisible(true)]
    public class VideoCaller
    {
        public VideoCaller(IMessageDispatcher remote)
        {
            this._remote = remote;
            //_remote.RegisterReceiveHandler("acceptCall", new MessageHandlerDelegateWrapper(m => acceptCall()));
            //_remote.RegisterReceiveHandler("endCall", new MessageHandlerDelegateWrapper(m => endCall()));

        }

        String communicationURL = "";

        Boolean activeCall = false;

        UnityUserControl.UnityUserControl uc;
        IMessageDispatcher _remote;
        public System.Windows.Forms.WebBrowser page;
        private System.Timers.Timer restartTimer;
        //START OF VIDEO CALLING CODE
        public void addCaller(UnityUserControl.UnityUserControl uc)
        {
            this.uc = uc;
            page = new System.Windows.Forms.WebBrowser();
            page.Dock = System.Windows.Forms.DockStyle.Fill;
            page.Location = new System.Drawing.Point(0, 0);
            page.Name = "videoCaller";
            this.uc.Controls.Add(page);
            page.BringToFront();
            //page.Visible = false;
            page.ScriptErrorsSuppressed = true;
            page.Navigate("https://ragserver.ccs.neu.edu/hangoutTest/");
            page.ObjectForScripting = this;
            page.DocumentCompleted += new System.Windows.Forms.WebBrowserDocumentCompletedEventHandler(this.onVideoCallerDocumentComplete);
            restartTimer = new System.Timers.Timer(100);
            restartTimer.Elapsed += onVideoCallerTimer;
            //page.PreviewKeyDown += new PreviewKeyDownEventHandler(page_PreviewKeyDown);
        }

        //Debug method to find mouse pos
        
        void page_PreviewKeyDown(object sender, PreviewKeyDownEventArgs e)
        {
            System.Diagnostics.Debug.WriteLine(e.KeyCode);
            if (e.KeyCode == Keys.C)
                MessageBox.Show(Cursor.Position.ToString());
        }
         

        //Nasty workaround to google hangout blocking out javascript
        [System.Runtime.InteropServices.DllImport("user32.dll")]
        static extern bool PostMessage(IntPtr hWnd, uint Msg, int wParam, int lParam);

        [DllImport("user32", SetLastError = true)]
        static extern IntPtr FindWindowEx(IntPtr parentHandle, IntPtr childAfter, string className, IntPtr windowTitle);

        private const int WM_LEFTBUTTONDOWN = 0x0201;
        private const int WM_LEFTBUTTONUP = 0x0202;
        private const int WM_MOUSEMOVE = 0x0200;

        public void SendClick(int x, int y)
        {
            //for debugging
                //get the browser pointer
            IntPtr pControl;
            pControl = FindWindowEx(page.Handle, IntPtr.Zero, "Shell Embedding", IntPtr.Zero);
            pControl = FindWindowEx(pControl, IntPtr.Zero, "Shell DocObject View", IntPtr.Zero);
            pControl = FindWindowEx(pControl, IntPtr.Zero, "Internet Explorer_Server", IntPtr.Zero);

            PostMessage(pControl,(uint)WM_LEFTBUTTONDOWN,0,MAKELPARAM(x,y));
            PostMessage(pControl, (uint)WM_LEFTBUTTONUP, 0, MAKELPARAM(x, y));
            
        }

        private int MAKELPARAM(int p, int p_2)
        {
            return ((p_2 << 16) | (p & 0xFFFF));
        }

        public void onVideoCallerDocumentComplete(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            //System.Diagnostics.Debug.WriteLine("onDocumentComplete Called");
            if (page.Url.ToString().Contains("plus.google.com/hangouts/"))
            {
                //Check for debugging computer
                if (Screen.PrimaryScreen.Bounds.Width == 1280)
                    SendClick(405, 660);
                else
                    SendClick(340,600);
            }
        }

        public void clearUI()
        {
            HtmlDocument doc = page.Document;
            HtmlElement head = doc.GetElementsByTagName("head")[0];
            HtmlElement s = doc.CreateElement("script");
            //Import Jquery Functions
            s = doc.CreateElement("script");
            s.SetAttribute("type", "text/javascript");
            s.SetAttribute("async", "true");
            s.SetAttribute("src", "https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js");
            head.AppendChild(s);
            //Create Clear Functions
            s = doc.CreateElement("script");
            //s.SetAttribute("text", "function simulateClick(x, y) {jQuery(document.elementFromPoint(x, y)).click();}");
            s.SetAttribute("text", "function clearUI(){Element.prototype.remove = function() {this.parentElement.removeChild(this);}; NodeList.prototype.remove = HTMLCollection.prototype.remove = function() {for(var i = 0, len = this.length; i < len; i++) {if(this[i] && this[i].parentElement) {this[i].parentElement.removeChild(this[i]);}}}; console.log(\"removing stuff\"); parent.document.getElementsByClassName(\"d-ah-Ig d-ah-Ia-Ig\").remove(); parent.document.getElementsByClassName(\"j-Ba j-Ba-td-Ua d-ah-By\").remove(); parent.document.getElementsByClassName(\"g-gb-lE \").remove(); parent.document.getElementsByClassName(\"Za-ma-R Za-R\").remove(); parent.document.getElementsByClassName(\"Ha-ya FR P-Se j-Lb-Qc Ha-ya-Vv\").remove(); document.getElementsByClassName(\"Za-J Za-Wa-m\").remove(); document.getElementsByClassName(\"xe-i-b d-ah-nB\").remove();}");
            head.AppendChild(s);
            page.Document.InvokeScript("clearUI");
        }

        public void createCommunicationFunctions()
        {
            HtmlDocument doc = page.Document;
            HtmlElement head = doc.GetElementsByTagName("head")[0];
            HtmlElement s = doc.CreateElement("script");
            //Create Communication Functions
            s = doc.CreateElement("script");
            s.SetAttribute("text",
                   "function rejectCall(){	$.ajax({url: 'https://ragserver.ccs.neu.edu/hangoutTest/saveURL.php',type: 'POST',data: {participantURL:\"" + communicationURL + "\",URL:\"reject\"},success: function(data) { console.log(data);}});};"
                + " function acceptCall(){	$.ajax({url: 'https://ragserver.ccs.neu.edu/hangoutTest/saveURL.php',type: 'POST',data: {participantURL:\"" + communicationURL + "\",URL:\"accept\"},success: function(data) { console.log(data);}});};");
            head.AppendChild(s);
        }
        //Javascript hooks
        public void onParticipantLeave()
        {
            System.Diagnostics.Debug.WriteLine("participant has left");
            //JObject body = new JObject();
            endCall();
            //_remote.Send("callEnded",body);
        }

        public void onParticipantRequest()
        {
            System.Diagnostics.Debug.WriteLine("got a participant request");
            JObject body = new JObject();
            body["id"] = "bob";
            _remote.Send("videoCall", body);
            //For accept
            //videoCaller.Document.InvokeScript("acceptCall");
            //videoCaller.Document.InvokeScript("rejectCall");
        }

        public string videoID = "";
        public bool idSent = false;

        public void setCommunicationURL(Object o)
        {
            idSent = false;
            communicationURL = o.ToString();
            communicationURL = communicationURL.Trim();
            System.Diagnostics.Debug.WriteLine("CommunicationURL: " + communicationURL);
            createCommunicationFunctions();
            //CleanUI
            //Send id to server side
            //JObject body = new JObject();
            //body["id"] = o.ToString().Replace(".txt","");
            //_remote.Send("videoId", body);
            videoID = o.ToString().Replace(".txt", "");
            //if (!idSent)
           //     sendCommunicationURL();
            clearUI();
        }

        public void sendCommunicationURL()
        {
            if (videoID == "")
            {
                System.Diagnostics.Debug.WriteLine("VideoID not set yet CommunicationURL");
                return;
            }
            idSent = true;

            JObject body = new JObject();
            body["id"] = videoID;
            _remote.Send("videoId", body);
        }

        public void log(Object o)
        {
            System.Diagnostics.Debug.WriteLine(o.ToString());
        }

        public void acceptCall()
        {
            activeCall = true;
            page.Document.InvokeScript("acceptCall");
            page.Visible = true;
        }

        public void rejectCall()
        {
            page.Document.InvokeScript("rejectCall");
            uc.webBrowser.Visible = true;
            activeCall = false;
            page.Visible = false;
            page.Navigate("");
            restartTimer.Enabled = true; // ensure we wait for camera release
        }

        public void endCall()
        {
            uc.webBrowser.Visible = true;
            activeCall = false;
            page.Visible = false;
            page.Navigate("");
            restartTimer.Enabled = true; // ensure we wait for camera release
            JObject body = new JObject();
            _remote.Send("callEnded", body);
        }

        //Put in a delay to ensure camera is released from the webpage.
        private void onVideoCallerTimer(Object source, ElapsedEventArgs e)
        {
            restartTimer.Enabled = false;
            page.Navigate("https://ragserver.ccs.neu.edu/hangoutTest/");
        }

        public void hideCall()
        {
            page.Visible = false;
            if(activeCall){
                endCall();
            }
        }

    }


}
