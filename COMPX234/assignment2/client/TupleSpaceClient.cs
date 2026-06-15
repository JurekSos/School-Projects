using System;
using System.IO;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Collections.Generic;

public class TupleSpaceClient
{
    public static void Main(string[] args)
    {
        // 1. Check that exactly three command-line arguments were given:
        //      - server hostname
        //      - server port
        //      - input file
        if (args.Length == 3)
        {
            string serverHostname = args[0];
            // 2. Convert the port number to an integer.
            int portNum = int.Parse(args[1]);
            string inputFilename = args[2];
            // 3. Check that the input file exists.
            if (File.Exists(inputFilename))
            {
                // 4. Connect to the server using TcpClient.
                TcpClient client = new TcpClient(serverHostname, portNum);

                using (NetworkStream stream = client.GetStream())
				{
					using (StreamReader reader = new StreamReader(stream))
					{
						using (StreamWriter writer = new StreamWriter(stream) { AutoFlush = true })
						{
							// 5. Open the input file and read each request line.
							List<string> requests = ReadFile(inputFilename);
							// 6. Convert each request into the short protocol form.
							List<string> formattedRequests = formatRequests(requests);
							// 7. Send the request to the server using the framed protocol.
							foreach(string r in formattedRequests)
							{
								writer.WriteLine(r);
								// 8. Read the framed response from the server.
								string serverResponse = reader.ReadLine();
								// 9. Print the original request and the server response.
								Console.WriteLine("Request: " + r + "\nResponse: " + serverResponse + "\n");
							}
						}
					}
				}
				client.Dispose();
            }
            else
            {
                Console.WriteLine(inputFilename + " does not exist.\nTerminating.");
                System.Environment.Exit(2);
            }
        }
        else
        {
            Console.WriteLine("Argument count was not three.\nTerminating.");
			System.Environment.Exit(0);
        }
        //
        // You may write extra helper methods if you want,
        // but they are not required.
    }

    private static List<string> ReadFile(string filename)
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
			Console.WriteLine("Error while parsing file: " + ex.Message + "\nTerminating.");
			System.Environment.Exit(-1);
		}

		return ret;
    }

	private static List<string> formatRequests(List<string> requests)
	{
		List<string> ret = new List<string>();
		foreach(string request in requests)
		{
			string messageLength;
			string bodyAction;
			string bodyContent;

			string[] splitRequest = request.Split(' ');

			if(splitRequest.Length < 2)
			{
				//Invalid request
				printInvalidRequestError(request);
				continue;
			}

			//Deal with request action
			if(splitRequest[0].Length == 1)
			{
				if(splitRequest[0] == "R" || splitRequest[0] == "G" || splitRequest[0] == "P")
				{
					bodyAction = splitRequest[0];
				}
				else
				{
					//Invalid request
					printInvalidRequestError(request);
					continue;
				}
			}
			else
			{
				if(splitRequest[0].ToUpper() == "READ")
				{
					bodyAction = "R";
				}
				else if(splitRequest[0].ToUpper() == "GET")
				{
					bodyAction = "G";
				}
				else if(splitRequest[0].ToUpper() == "PUT")
				{
					bodyAction = "P";
				}
				else{
					//Invalid request
					printInvalidRequestError(request);
					continue;
				}	
			}
			
			int contentStartIndex = splitRequest[0].Length + 1;
			bodyContent = request.Substring(contentStartIndex);
			bodyContent = bodyContent.Trim();

			if(bodyContent.Length == 0)
			{
				//Invalid request
				printInvalidRequestError(request);
				continue;
			}
			else
			{
				if(bodyAction == "P")
				{
					//Check that the PUT request has exactly 3 words, and the last is an integer
					if(splitRequest.Length == 3)
					{
						try
						{
							int value = int.Parse(splitRequest[splitRequest.Length - 1]);
							if(value < 0)
							{
								//Invalid request
								printInvalidRequestError(request);
								continue;
							}
						}
						catch(FormatException)
						{
							//Invalid request
							printInvalidRequestError(request);
							continue;
						}
					}
					else
					{
						//Invalid request
						printInvalidRequestError(request);
						continue;
					}
				}
				else
				{
					//Check that the request contains 2 words
					if(splitRequest.Length != 2){
						//Invalid request
						printInvalidRequestError(request);
						continue;
					}
				}
			}

			//Find message length
			int bodyLength = bodyAction.Length + bodyContent.Length + 1;
			if(bodyLength > 995)
			{
				//Invalid request
				printInvalidRequestError(request);
				continue;
			}
			else
			{
				int totalLength = bodyLength + 3;
				messageLength = totalLength.ToString().PadLeft(3, '0');

				string message = messageLength + " " + bodyAction + " " + bodyContent;

				ret.Add(message);
			}
		}

		return ret;
	}

	private static void printInvalidRequestError(string request)
	{
		Console.WriteLine("Invalid request: \"" + request + "\"");
	}

    private static bool SendRequest(NetworkStream stream, string requestBody)
    {
        // 1. Frame the message as "NNN body".
        // 2. Convert it to ASCII bytes.
        // 3. Write it to the stream.
        // 4. Flush the stream.
        // 5. Return true if successful, otherwise false.

        return false;
    }
}
