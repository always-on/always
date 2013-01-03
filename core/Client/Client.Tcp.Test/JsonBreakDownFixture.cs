using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Agent.Tcp.Test
{
	[TestFixture]
	public class JsonBreakDownFixture
	{
		[Test]
		public void StringContainingOneObject()
		{
			var l = MessageDispatcherImpl.BreakDownToIndividualStrings("{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}")
				.ToList();

			Assert.AreEqual(1, l.Count);
			Assert.AreEqual("{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}", l[0]);
		}

		[Test]
		public void StringContainingLessThanOneObject()
		{
			//note: the missing } at the end of string
			string original = "{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}";
			var l = MessageDispatcherImpl.BreakDownToIndividualStrings(original)
				.ToList();

			Assert.AreEqual(1, l.Count);
			Assert.AreEqual(original, l[0]);
		}

		[Test]
		public void StringWithNoJson()
		{
			var l = MessageDispatcherImpl.BreakDownToIndividualStrings("Hello, how are you?")
				.ToList();

			Assert.AreEqual(1, l.Count);
			Assert.AreEqual("Hello, how are you?", l[0]);
		}

		[Test]
		public void StringContainingTwoObjects()
		{
			var thingsBetweenThem = new[] { "", "  ", "\n", "\r\n" };

			foreach (var d in thingsBetweenThem)
			{
				var l = MessageDispatcherImpl.BreakDownToIndividualStrings(
								"{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}" + d +
								"{\"msg_type\":\"speech\",\"msg_body\":{\"text\":\"Hello\"}}")
					.ToList();

				Assert.AreEqual(2, l.Count);
				Assert.AreEqual("{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}", l[0]);
				Assert.AreEqual("{\"msg_type\":\"speech\",\"msg_body\":{\"text\":\"Hello\"}}", l[1]);
			}
		}

	}
}
