using System;
using System.Text;
using System.IO;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;

public class UdpFileClient
{

    public static void Main(string[] args)
    {
		//Check argument length
        if (args.Length != 3)
        {
            Console.Error.WriteLine("Usage: mono UdpFileClient.exe <hostname> <port> <file-list>");
            return;
        }

		//set variables from args
		string hostname = args[0];
        int port;
        if (!Int32.TryParse(args[1], out port))
        {
            Console.Error.WriteLine("Invalid port: " + args[1]);
            return;
        }
		string requestsFileName = args[2];

		UdpClient client = new UdpClient();
		List<string> requests = getRequestsFromFile(requestsFileName);

		//request each file in the file list
		foreach(string request in requests){
			requestFile(client, hostname, port, request);
		}
    }

	private static List<string> getRequestsFromFile(string filename)
	{
		List<string> ret = new List<string>();
		try
		{
			using(StreamReader fileReader = File.OpenText(filename))
			{
				string line = fileReader.ReadLine();
				while(line != null){
					ret.Add(line);
					line = fileReader.ReadLine();
				}
			}	
		}
		catch(Exception ex){
			Console.WriteLine("Error while reading from file: " + ex.Message + "\nTerminating.");
			System.Environment.Exit(-1);
		}

		return ret;
	}

	private static void requestFile(UdpClient client, string hostname, int port, string filename)
	{
		string message = "DOWNLOAD " + filename;
		byte[] data = Encoding.UTF8.GetBytes(message);

		string response;

		client.Client.ReceiveTimeout = 1000;

		while(true)
		{
			try
			{
				//Send DOWNLOAD request
				client.Send(data, data.Length, hostname, port);
				IPEndPoint server = new IPEndPoint(IPAddress.Any, 0);
				//Wait for response
				response = Encoding.ASCII.GetString(client.Receive(ref server)).Trim();
				break;
			}
			catch(Exception ex)
			{
				if(ex.Message.CompareTo("Connection timed out") == 0)
				{
					Console.WriteLine("TIMEOUT");
					if(client.Client.ReceiveTimeout < 5000)
					{
						client.Client.ReceiveTimeout += 1000;
						continue;
					}
					else
					{
						return;
					}
				}
				else
				{
					throw ex;
				}
			}
		}

		//Process response
		if(response.StartsWith("OK " + filename + " SIZE "))
		{
			//If OK received, proceed to downloading the file
			string[] splitResponse = response.Split(' ');
			int fileSize;
			int transferPort;

			if (Int32.TryParse(splitResponse[3], out fileSize))
			{
				if(Int32.TryParse(splitResponse[5], out transferPort))
				{
					downloadFile(client, hostname, transferPort, filename, fileSize);	
				}
			}
		}
	}

	private static void downloadFile(UdpClient client, string hostname, int port, string filename, int fileSize)
	{
		Console.WriteLine(filename);

		int startByte = 0;
		int endByte = Math.Min(999, fileSize-1);

		byte[] fileBytes = new byte[fileSize];

		IPAddress serverAddress = Dns.GetHostAddresses(hostname)[0];
		IPEndPoint server = new IPEndPoint(serverAddress, port);
		IPEndPoint serverBackup = server;

		client.Client.ReceiveTimeout = 1000;

		while(startByte < fileSize)
		{
			try
			{
				//Request file chunk
				string request = "FILE " + filename + " GET START " + startByte.ToString() + " END " + endByte.ToString();
				byte[] data = Encoding.UTF8.GetBytes(request);
				Console.WriteLine("ECHO SND: \"" + request + "\""); //TEST
				client.Send(data, data.Length, server);
				//Receive and process file chunk
				string response;
				while(true)
				{
					// loop until correct message received
					server = new IPEndPoint(IPAddress.Any, 0);
					response = Encoding.ASCII.GetString(client.Receive(ref server)).Trim();
					Console.WriteLine("ECHO RCV: \"" + response + "\""); //TEST

					serverBackup = server;

					if(response.StartsWith("FILE " + filename + " OK START " + startByte.ToString() + " END " + endByte.ToString())) break;
				}
				//Reset receive timeout
				client.Client.ReceiveTimeout = 1000;

				//Translate Base 64 to bytes
				string base64Data = response.Split(' ')[8];
				byte[] receivedData = Convert.FromBase64String(base64Data);
				//Add new bytes to byte array
				Array.Copy(receivedData, 0, fileBytes, startByte, receivedData.Length);

				startByte = endByte + 1;
				endByte = Math.Min(endByte + 1000, fileSize-1);

				//Calculate progress
				int prog = (startByte*100)/fileSize;
				//Print progress
				Console.WriteLine(filename + " " + prog.ToString() + "%");
			}
			catch(Exception ex)
			{
				if(ex.Message.CompareTo("Connection timed out") == 0)
				{
					//Deal specifically with timeouts
					Console.WriteLine("TIMEOUT");
					if(client.Client.ReceiveTimeout < 5000)
					{
						client.Client.ReceiveTimeout += 1000;
						server = serverBackup;
						continue;
					}
					else
					{
						return;
					}
				}
				else
				{
					throw ex;
				}
			}
		}

		File.WriteAllBytes(filename, fileBytes);

		Console.WriteLine("OK " + filename);

		//Send CLOSE message
		byte[] closeMessageData = Encoding.UTF8.GetBytes("FILE " + filename + " CLOSE");
		client.Send(closeMessageData, closeMessageData.Length, server);


		client.Client.ReceiveTimeout = 1000;
		try
		{
			server = new IPEndPoint(IPAddress.Any, 0);
			client.Receive(ref server);
		}
		catch(Exception ex)
		{
			//protocol does not recover if CLOSE_OK is lost
			if(ex.Message.CompareTo("Connection timed out") == 0)
			{
				return;
			}
			else
			{
				throw ex;
			}
		}
	}
}
