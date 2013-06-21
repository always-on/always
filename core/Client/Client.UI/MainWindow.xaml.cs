using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Diagnostics;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

using Agent.Core;
using System.Windows.Forms;
using System.Windows.Threading;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using SoftKeyboard.UI;

namespace AgentApp
{
	/// <summary>
	/// Interaction logic for MainWindow.xaml
	/// </summary>
	public partial class MainWindow : Window, ILayoutWithPluginSupport
	{
		Random rnd = new Random();
		public int _agentTurns;

        // static registration framework

        private class Plugin
        {
            public String Name;
            public Type Creator;
            public Plugin(String name, Type creator) { Name = name; Creator = creator; }
        }
        private static List<Plugin> plugins = new List<Plugin>();

        public static void RegisterPlugin (String name, String creator) {
            Type type = Type.GetType(creator);
            if (type == null) throw new PluginNotFoundException(creator);
            plugins.Add(new Plugin(name, type));
        }

		public MainWindow()
		{
			InitializeComponent();

			Agent.Buttons = Buttons;

			this.ResizeMode = ResizeMode.NoResize;

			this.WindowStyle = WindowStyle.None;
			//this.Topmost = true;
			this.WindowState = WindowState.Maximized;
		}

		private void Window_Loaded(object sender, RoutedEventArgs e)
		{
			//Brightness.SetLowestBrightness();
			LayoutPageWithoutPlugin();
            Agent.LoadComplete += onAgentLoad;
		}

        private void onAgentLoad(object sender, EventArgs e)
        {
            InitTcpListener();
        }

		private void InitTcpListener()
		{
			var listener = new MessageTcpListener();

			var dispatcher = new MessageDispatcherImpl(listener);

			var jsonAdapter = new AgentControlJsonAdapter(Agent, dispatcher);

			InitPluginManager(dispatcher);

			listener.Start();
		}

		private void InitPluginManager(MessageDispatcherImpl dispatcher)
		{
            var pluginManager = new PluginManager(this);

            foreach (Plugin plugin in plugins) {
                pluginManager.RegisterPlugin(plugin.Name, (IPluginCreator)
                    Activator.CreateInstance(plugin.Creator, new UIThreadDispatcher(this.Dispatcher), dispatcher));
                Debug.WriteLine("Plugin "+plugin.Name+" registered");
            }
                             
			dispatcher.RegisterReceiveHandler("start_plugin", new MessageHandlerDelegateWrapper(x =>
			{
				var v = x["instance_reuse_mode"] as JValue;

				InstanceReuseMode? mode = null;

				if (v != null)
				{
					InstanceReuseMode m;
					if (Enum.TryParse<InstanceReuseMode>(v.ToString(), true, out m))
						mode = m;
				}

				if (mode == null)
					pluginManager.Start(x["name"].ToString(), x["params"] as JObject);
				else
					pluginManager.Start(x["name"].ToString(), x["params"] as JObject, (InstanceReuseMode)mode);
			}));
			dispatcher.RegisterReceiveHandler("close_plugin", new MessageHandlerDelegateWrapper(x =>
			{
				Dispatcher.BeginInvoke(new Action(() =>
				{
					LayoutPageWithoutPlugin();
				}));
			}));
		}

		private void StartTimer(int interval, Action<Timer> actionOnTick)
		{
			Timer t = new Timer();
			t.Interval = interval;
			t.Tick += (s, e2) => actionOnTick((Timer)s);
			t.Start();
		}

		private void LayoutPageWithPlugin()
		{
			SetGridPos(PluginContainer, 0, 0, 2);
			SetGridPos(Agent, 0, 1, 1);
			SetGridPos(Buttons, 1, 1, 1);

			Agent.VerticalAlignment = System.Windows.VerticalAlignment.Top;
			Buttons.VerticalAlignment = System.Windows.VerticalAlignment.Top;
			PluginContainer.HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch;

			MainGrid.RowDefinitions[0].Height = GridLength.Auto;
			MainGrid.RowDefinitions[1].Height = new GridLength(1, GridUnitType.Star);

			MainGrid.ColumnDefinitions[0].Width = new GridLength(1200, GridUnitType.Star);
			MainGrid.ColumnDefinitions[1].Width = new GridLength(400, GridUnitType.Star);
			MainGrid.ColumnDefinitions[1].MinWidth = 350;
			MainGrid.ColumnDefinitions[1].MaxWidth = 400;

			UpdateLayout();
		}

		private void LayoutPageWithoutPlugin()
		{
            PluginContainer = new Viewbox();
            MainGrid.Children.Add(PluginContainer);
			SetGridPos(PluginContainer, 1, 0, 1);
			SetGridPos(Agent, 0, 0, 1);
			SetGridPos(Buttons, 0, 2, 2);
			Agent.HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch;
			Agent.VerticalAlignment = System.Windows.VerticalAlignment.Stretch;

			MainGrid.RowDefinitions[0].ClearValue(RowDefinition.HeightProperty);
			MainGrid.RowDefinitions[1].Height = GridLength.Auto;

			MainGrid.ColumnDefinitions[0].Width = GridLength.Auto;
			MainGrid.ColumnDefinitions[1].ClearValue(ColumnDefinition.WidthProperty);
			MainGrid.ColumnDefinitions[1].ClearValue(ColumnDefinition.MaxWidthProperty);
		}

		private void SetGridPos(UIElement element, int row, int col, int rowSpan)
		{
			Grid.SetRow(element, row);
			Grid.SetColumn(element, col);
			Grid.SetRowSpan(element, rowSpan);
		}

		public void ShowPlugin(Viewbox pluginContainer)
        {
			Dispatcher.BeginInvoke(new Action(() =>
			{
                //LayoutPageWithoutPlugin();
                MainGrid.Children.Remove(PluginContainer);
                PluginContainer = pluginContainer;
                MainGrid.Children.Add(PluginContainer);
                LayoutPageWithPlugin();
			}), null);
		}
	}
}
