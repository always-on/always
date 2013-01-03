using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp
{
	public interface IPluginCreator
	{
		IPlugin Create(JObject parameters);
	}

	public enum InstanceReuseMode
	{
		Remove,
		Reuse,
		Throw
	}
}
