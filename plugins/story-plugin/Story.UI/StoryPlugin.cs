using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;
using System.Windows.Controls;
using System.Runtime.InteropServices;
using System.Threading;
using System.IO;
using Microsoft.Expression.Encoder.Devices;
using Microsoft.Expression.Encoder.Live;
using System.Windows.Forms;
using System.Diagnostics;


namespace Story.UI
{
    class StoryPlugin : IPlugin
    {
        StoryPage story;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;
        Viewbox pluginContainer;

        LiveJob job;
        LiveDeviceSource liveSource;
        EncoderDevice videoDevice;
        EncoderDevice audioDevice;
        FileArchivePublishFormat fileOut;

        public StoryPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
        {
            this._remote = remote;
            this._uiThreadDispatcher = uiThreadDispatcher;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
                story = new StoryPage();
                pluginContainer = new Viewbox();
                pluginContainer.Child = story;
            });
            _remote.RegisterReceiveHandler("story.stopRecording",
                new MessageHandlerDelegateWrapper(m => endVideoCapture()));
            _remote.RegisterReceiveHandler("story.startRecording",
                new MessageHandlerDelegateWrapper(m => startVideoCapture(m)));
            _remote.RegisterReceiveHandler("story.saveRecording",
                new MessageHandlerDelegateWrapper(m => saveRecording(m)));

            foreach (EncoderDevice edv in EncoderDevices.FindDevices(EncoderDeviceType.Video))
            {
                //Debug.WriteLine("found a video deviced named: " + edv.Name);
                videoDevice = edv;
            }
            foreach (EncoderDevice edv in EncoderDevices.FindDevices(EncoderDeviceType.Audio))
            {
                //Debug.WriteLine("found a audio deviced named: " + edv.Name);
                if (edv.Name.ToLower().Contains("microphone"))
                    audioDevice = edv;
            }

            story.SizeChanged += new System.Windows.SizeChangedEventHandler(story_SizeChanged);
            job = new LiveJob();


            if (!System.IO.Directory.Exists("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\"))
                System.IO.Directory.CreateDirectory("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\");

            fileOut = new FileArchivePublishFormat();
            fileOut.OutputFileName = "C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv";
            System.Console.WriteLine(fileOut.OutputFileName);
        }

        void story_SizeChanged(object sender, System.Windows.SizeChangedEventArgs e)
        {
            story.videoPreview.Height = int.Parse(story.Height.ToString());
            story.videoPreview.Width = int.Parse(story.Width.ToString());
        }

        public void Dispose()
        {
            _remote.RemoveReceiveHandler("story.stopRecording");
            _remote.RemoveReceiveHandler("story.startRecording");
            _remote.RemoveReceiveHandler("story.saveRecording");
            if (File.Exists("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv"))
                File.Delete("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv");
            //endCapture();
        }

        public Viewbox GetPluginContainer()
        {
            return pluginContainer;
        }

        private void startVideoCapture(JObject m)
        {
            liveSource = job.AddDeviceSource(videoDevice, audioDevice);

            while(!story.videoPreview.IsHandleCreated)
                Thread.Sleep(100);

            if (liveSource.PreviewWindow == null)
            {
                story.videoPreview.Invoke((MethodInvoker)delegate
                {
                    story.videoPreview = new System.Windows.Forms.Panel();
                    story.videoPreview.Height = int.Parse(story.Height.ToString());
                    story.videoPreview.Width = int.Parse(story.Width.ToString());
                    story.FormHost.Child = story.videoPreview;
                    liveSource.PreviewWindow = new PreviewWindow(new HandleRef(story.videoPreview, story.videoPreview.Handle));
                    //job.OutputPreviewWindow = new PreviewWindow(new HandleRef(story.videoPreview, story.videoPreview.Handle));
                    //liveSource.PreviewWindow.SetSize(new System.Drawing.Size((int)story.videoPreview.Width, (int)story.videoPreview.Height));
                });
            }
            job.ActivateSource(liveSource);

            if (File.Exists("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv"))
                File.Delete("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv");


            job.PublishFormats.Add(fileOut);

            job.StartEncoding();
        }

        private void saveRecording(JObject m)
        {
            try
            {
                string status = m["saved"].ToString();
                string storyType = m["type"].ToString();
                if (storyType == "")
                    storyType = "misc";
                storyType += "_" + DateTime.Now.ToString("M-d-yyyy-H-mm-ss");
                if (status == "SAVED")
                {
                    //rename the video
                    if (File.Exists("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv"))
                        File.Move("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv", "C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\" + storyType + ".wmv");
                }
                else
                {
                    if (File.Exists("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv"))
                        File.Delete("C:\\Dropbox\\" + System.Windows.Forms.SystemInformation.ComputerName + "\\videos\\tempVideo.wmv");
                    //delete the video
                }
            }
            catch (Exception e)
            {
                Debug.WriteLine("Exception in saving recording:" + e);
            }
        }

        public void endVideoCapture()
        {
            job.StopEncoding();
            job.RemoveDeviceSource(liveSource);
            //liveSource.Dispose();
            //liveSource = null;
            //job.Dispose();
        }
    }
}

