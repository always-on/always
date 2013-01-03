using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using Moq;
using System.Windows;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp.Test
{
	[TestFixture]
	public class PluginManagerFixture
	{
		ILayoutWithPluginSupport layout;
		PluginManager subject;

		[SetUp]
		public void SetUp()
		{
			layout = Mock.Of<ILayoutWithPluginSupport>();

			subject = new PluginManager(layout);
		}

		[Test]
		public void Test()
		{
			SetUp();

			var uiElement = Mock.Of<UIElement>();

			var plugin = Mock.Of<IPlugin>();
			Mock.Get(plugin)
				.Setup(x => x.GetUIElement())
				.Returns(uiElement);

			var pluginCreator = Mock.Of<IPluginCreator>();
			Mock.Get(pluginCreator)
				.Setup(x => x.Create(null))
				.Returns(plugin);

			subject.RegisterPlugin("rummy", pluginCreator);

			subject.Start("rummy", null);

			Mock.Get(layout)
				.Verify(x => x.ShowPlugin(uiElement));
		}

		[Test]
		public void WhenThereIsNoCreatorForName_ShouldThrowException()
		{
			Assert.Throws<PluginNotFoundException>(() =>
								subject.Start("notRegistered", null));
		}

	}
}
