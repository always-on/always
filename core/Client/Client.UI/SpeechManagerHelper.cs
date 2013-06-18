using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
//using RagClient.Debugger.Tests;
using System.Speech.Synthesis;
using System.Reflection;

namespace Agent.UI
{
    /*
	public class SpeechManagerHelper
	{
		FieldInfo sythesizerField = typeof(SpeechManager).GetField("speechSynthesizer", System.Reflection.BindingFlags.Instance | System.Reflection.BindingFlags.NonPublic);

		SpeechSynthesizer GetSpeechSynthesizer(SpeechManager manager)
		{
			return (SpeechSynthesizer)sythesizerField.GetValue(manager);
		}

		public void StopCurrentSpeech(SpeechManager manager)
		{
			GetSpeechSynthesizer(manager).SpeakAsyncCancelAll();
		}
	}*/
}
