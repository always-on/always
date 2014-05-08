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
using System.Windows.Media.Imaging;

namespace Story.UI
{
    class StoryPlugin : IPlugin
    {
        StoryPage story;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;
        Viewbox pluginContainer;

        private byte[] imageData;
        private bool capturing = false;
        private string filename = "";


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
        }

        public void Dispose()
        {
            _remote.RemoveReceiveHandler("story.stopRecording");
            _remote.RemoveReceiveHandler("story.startRecording");
            endCapture();
        }

        public Viewbox GetPluginContainer()
        {
            return pluginContainer;
        }

        Thread captureThread;

        private void startVideoCapture(JObject m)
        {
            initCamera();
            this.filename = "testVideo";
            captureThread = new Thread(videoCaptureThread);
            captureThread.Name = "CaptureThread";
            captureThread.SetApartmentState(ApartmentState.STA);
            captureThread.Start();
        }

        private void initCamera()
        {
            if (init() != 0)
                Console.WriteLine("failed to call init");
        }

        public void videoCaptureThread()
        {
            capturing = true;
            if (startCapture(filename) != 0)
            {
                Console.WriteLine("Failed to start video capture");
            }
            imageData = new byte[921654];
            Thread.Sleep(100);
            MemoryStream ms;
            BitmapImage bitmapImage;
            while (capturing)
            {
                _uiThreadDispatcher.BlockingInvoke(() =>
                {
                    getImage(imageData);
                    ms = new MemoryStream(imageData);
                    bitmapImage = new BitmapImage();
                    bitmapImage.BeginInit();
                    bitmapImage.StreamSource = ms;
                    bitmapImage.EndInit();
                    story.captureImage.Source = bitmapImage;
                });
                Thread.Sleep(35);
            }
        }

        public void endVideoCapture()
        {
            capturing = false;
            endCapture();
            if (captureThread.IsAlive)
                captureThread.Join();
            end();
        }


        [DllImport("STORYCAPTURE.DLL", CallingConvention = CallingConvention.Cdecl)]
        public static extern int init();
        [DllImport("STORYCAPTURE.DLL", CallingConvention = CallingConvention.Cdecl)]
        public static extern int startCapture(string filename);
        [DllImport("STORYCAPTURE.DLL", CallingConvention = CallingConvention.Cdecl)]
        public static extern int endCapture();
        [DllImport("STORYCAPTURE.DLL", CallingConvention = CallingConvention.Cdecl)]
        public static extern void getImage(byte[] imageData);
        [DllImport("STORYCAPTURE.DLL", CallingConvention = CallingConvention.Cdecl)]
        public static extern void end();

    }
}

