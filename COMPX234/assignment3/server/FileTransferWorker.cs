using System;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Text;

public class FileTransferWorker
{
    public class Job
    {
        public string Filename;
        public IPEndPoint ClientEndpoint;
        public UdpClient TransferSocket;

        public Job(string filename, IPEndPoint clientEndpoint, UdpClient transferSocket)
        {
            Filename = filename;
            ClientEndpoint = clientEndpoint;
            TransferSocket = transferSocket;
        }
    }

    // Implement Run — see assignment specification
    // (Job: Filename, ClientEndpoint, TransferSocket).
    public static void Run(object jobObject)
    {
		Job job = (Job)jobObject;
		//Check file exists
		string requestedFilepath = UdpFileServer.FilePath(job.Filename);

		if(File.Exists(requestedFilepath))
		{
			//if yes send back control ok and prepare for download
			byte[] fileBytes = File.ReadAllBytes(requestedFilepath);

			int fileSize = fileBytes.Length;
			string portNum = UdpFileServer.PublicPort(job.TransferSocket).ToString();

			string okMessage = "OK " + job.Filename + " SIZE " + fileSize.ToString() + " PORT " + portNum;
			UdpFileServer.SendControlReply(job.ClientEndpoint, okMessage);

			while(true)
			{
				//Wait for message from client
				job.ClientEndpoint = new IPEndPoint(IPAddress.Any, 0);
				string command = Encoding.ASCII.GetString(job.TransferSocket.Receive(ref job.ClientEndpoint)).Trim();
				string message = "FILE " + job.Filename + " ";
				//Check request type
				if(command.StartsWith("FILE " + job.Filename + " GET START "))
				{
					/*Send requested bytes*/
					string[] splitCommand = command.Split(' ');
					string start = splitCommand[4];
					string end = splitCommand[6];
					int startByte, endByte;
					
					if(Int32.TryParse(start, out startByte))
					{
						if(Int32.TryParse(end, out endByte))
						{
							//Format the data to be sent
							int numRequestedBytes = (endByte - startByte) + 1;

							if(numRequestedBytes > 1000)
							{
								break;
							}
							
							byte[] requestedBytes = new byte[numRequestedBytes];
							Array.Copy(fileBytes, startByte, requestedBytes, 0,	numRequestedBytes);

							string requestedData = Convert.ToBase64String(requestedBytes);

							//Create message
							message += "OK START " + start + " END " + end + " DATA " + requestedData;
							
							//Send message
							byte[] bytes = Encoding.ASCII.GetBytes(message);
							job.TransferSocket.Send(bytes, bytes.Length, job.ClientEndpoint);
						}
					}
				}
				else if(command.CompareTo("FILE " + job.Filename + " CLOSE") == 0)
				{
					/*End download and transmission*/
					message += "CLOSE_OK";
							
					byte[] bytes = Encoding.ASCII.GetBytes(message);
					job.TransferSocket.Send(bytes, bytes.Length, job.ClientEndpoint);
					job.TransferSocket.Close();

					break;
				}
			}
		}
		else
		{
			//if no, send back control error
			string errMessage = "ERR " + job.Filename + " NOT_FOUND";
			UdpFileServer.SendControlReply(job.ClientEndpoint, errMessage);
			job.TransferSocket.Close();
		}
    }
}
