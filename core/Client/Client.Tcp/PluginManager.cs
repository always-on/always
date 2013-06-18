using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp
{
	public class PluginManager
	{
		private ILayoutWithPluginSupport layout;
		private Dictionary<string, IPluginCreator> pluginCreators = new Dictionary<string, IPluginCreator>();
		private Dictionary<IPluginCreator, IPlugin> instances = new Dictionary<IPluginCreator, IPlugin>();

		public PluginManager(ILayoutWithPluginSupport layout)
		{
			this.layout = layout;
		}

		public void RegisterPlugin(string p, IPluginCreator plugin)
		{
			pluginCreators.Add(p, plugin);
		}

		public void Start(string pluginName, JObject parameters)
		{
			Start(pluginName, parameters, InstanceReuseMode.Throw);
		}

		public void Start(string pluginName, JObject parameters, InstanceReuseMode instanceReuseMode)
		{
			if (!pluginCreators.ContainsKey(pluginName))
				throw new PluginNotFoundException();

			var creator = pluginCreators[pluginName];

			IPlugin plugin = null;

			if (instances.ContainsKey(creator))
			{
				if(instanceReuseMode == InstanceReuseMode.Throw)
					throw new Exception("An instance of the plugin <" + pluginName + "> already exists");

				if (instanceReuseMode == InstanceReuseMode.Reuse)
					plugin = instances[creator];
				else
					instances[creator].Dispose();
				
			}

			if (plugin == null)
			{
				plugin = creator.Create(parameters);
				instances[creator] = plugin;
			}

            layout.ShowPlugin(plugin.GetPluginContainer());
        }
	}
}
