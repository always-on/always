using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using Agent.Core;
using System.Threading;
using System.IO;

namespace Agent.Tcp
{
	public class MessageTcpListener : IRemoteConnection
	{
		public const int PORT = 11000;
		TcpListener _listener;
		TcpClient _client;

		public MessageTcpListener()
		{
#pragma warning disable 0618
            _listener = new TcpListener(PORT);
#pragma warning restore 0618
		}

		public void Start()
		{
			_listener.Start();
			_listener.BeginAcceptTcpClient(AcceptClientCallback, null);
		}

		private void AcceptClientCallback(IAsyncResult ar)
		{
			_client = _listener.EndAcceptTcpClient(ar);

			Byte[] bytes = new Byte[8196];

			_client.GetStream().BeginRead(bytes, 0, bytes.Length, ReceiveCallback, bytes);
		}

		private void ReceiveCallback(IAsyncResult ar)
		{
			try
			{
				int bytesRead = _client.GetStream().EndRead(ar);
				var bytes = (Byte[])ar.AsyncState;
                 
				if (bytesRead > 0)
				{
					var message = Encoding.ASCII.GetString(bytes, 0, bytesRead);

					FireMessageReceived(message);
					
					_client.GetStream().BeginRead(bytes, 0, bytes.Length, ReceiveCallback, bytes);
				}
			}
			catch(IOException ex)
			{
				Console.WriteLine("IOException happenend: " + ex.Message);
				Start();
			}
		}

		private void FireMessageReceived(string message)
		{
			MessageReceived(this, new MessageReceivedEventArgs(message));
		}

		public void Send(string message)
		{
			var bytes = Encoding.ASCII.GetBytes(message);

			if (_client != null && _client.Connected)
				_client.GetStream().BeginWrite(bytes, 0, bytes.Length, null, null);
		}

		public event EventHandler<MessageReceivedEventArgs> MessageReceived = delegate { };
	}
}
