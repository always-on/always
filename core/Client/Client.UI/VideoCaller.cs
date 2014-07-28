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

        UserControl uc;
        IMessageDispatcher _remote;
        System.Windows.Forms.WebBrowser page;
        //START OF VIDEO CALLING CODE
        public void addCaller(UserControl uc)
        {
            this.uc = uc;
            page = new System.Windows.Forms.WebBrowser();
            page.Dock = System.Windows.Forms.DockStyle.Fill;
            page.Location = new System.Drawing.Point(0, 0);
            page.Name = "videoCaller";
            this.uc.Controls.Add(page);
            page.BringToFront();
            page.Visible = false;
            page.ScriptErrorsSuppressed = true;
            page.Navigate("https://ragserver.ccs.neu.edu/hangoutTest/");
            page.ObjectForScripting = this;
            page.DocumentCompleted += new System.Windows.Forms.WebBrowserDocumentCompletedEventHandler(this.onVideoCallerDocumentComplete);
        }

        public void onVideoCallerDocumentComplete(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            Console.WriteLine("onDocumentComplete Called");
            if (page.Url.ToString().Contains("plus.google.com/hangouts/"))
            {
                clearUI();
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
                + "function acceptCall(){	$.ajax({url: 'https://ragserver.ccs.neu.edu/hangoutTest/saveURL.php',type: 'POST',data: {participantURL:\"" + communicationURL + "\",URL:\"accept\"},success: function(data) { console.log(data);}});};");
            head.AppendChild(s);

        }
        //Javascript hooks
        public void onParticipantLeave()
        {
            Console.WriteLine("participant has left");
            JObject body = new JObject();
            endCall();
            //_remote.Send("callEnded",body);
        }

        public void onParticipantRequest()
        {
            Console.WriteLine("got a participant request");
            JObject body = new JObject();
            body["id"] = "bob";
            _remote.Send("videoCall", body);
            //For accept
            //videoCaller.Document.InvokeScript("acceptCall");
            //videoCaller.Document.InvokeScript("rejectCall");
        }

        public void setCommunicationURL(Object o)
        {
            communicationURL = o.ToString();
            communicationURL = communicationURL.Trim();
            Console.WriteLine(communicationURL);
            createCommunicationFunctions();
        }

        public void log(Object o)
        {
            Console.WriteLine(o.ToString());
        }

        public void acceptCall()
        {
            page.Document.InvokeScript("acceptCall");
            page.Visible = true;
        }

        public void rejectCall()
        {
            page.Document.InvokeScript("rejectCall");
            page.Visible = true;
        }

        public void endCall()
        {
            page.Visible = false;
            page.Navigate("");
            page.Navigate("https://ragserver.ccs.neu.edu/hangoutTest/");
            JObject body = new JObject();
            _remote.Send("callEnded", body);
        }

    }


}
