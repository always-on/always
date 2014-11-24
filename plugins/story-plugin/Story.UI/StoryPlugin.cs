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


namespace Story.UI
{
    class StoryPlugin : IPlugin
    {
        StoryPage story;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;
        Viewbox pluginContainer;

        LiveJob job = new LiveJob();
        LiveDeviceSource liveSource;
        EncoderDevice videoDevice;
        EncoderDevice audioDevice;


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

            foreach (EncoderDevice edv in EncoderDevices.FindDevices(EncoderDeviceType.Video))
            {
                //Console.WriteLine("found a video deviced named: " + edv.Name);
                videoDevice = edv;
            }
            foreach (EncoderDevice edv in EncoderDevices.FindDevices(EncoderDeviceType.Audio))
            {
                //Console.WriteLine("found a audio deviced named: " + edv.Name);
                if (edv.Name.ToLower().Contains("microphone"))
                    audioDevice = edv;
            }

            story.SizeChanged += new System.Windows.SizeChangedEventHandler(story_SizeChanged);
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
            //endCapture();
        }

        public Viewbox GetPluginContainer()
        {
            return pluginContainer;
        }

        private void startVideoCapture(JObject m)
        {
            liveSource = job.AddDeviceSource(videoDevice, audioDevice);
            story.videoPreview.Invoke((MethodInvoker)delegate
            {
                Console.WriteLine(story.Height);
                story.videoPreview.Height = int.Parse(story.Height.ToString());
                story.videoPreview.Width = int.Parse(story.Width.ToString());
                liveSource.PreviewWindow = new PreviewWindow(new HandleRef(story.videoPreview, story.videoPreview.Handle));
            });
            job.ActivateSource(liveSource);

            FileArchivePublishFormat fileOut = new FileArchivePublishFormat();

            fileOut.OutputFileName = "C:\\testXAMLVideo.wmv";

            job.PublishFormats.Add(fileOut);

            job.StartEncoding();
        }

        public void endVideoCapture()
        {
            job.StopEncoding();
            job.RemoveDeviceSource(liveSource);
            liveSource = null;
        }
    }
}

