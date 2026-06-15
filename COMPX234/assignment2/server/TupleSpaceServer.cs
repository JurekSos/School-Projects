using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.IO;

public class TupleSpaceServer
{

    #region  Global Variables

    private static readonly Dictionary<string, string> tupleSpace = new Dictionary<string, string>();
    private static readonly object stateLock = new object();

    private static int totalClients = 0;
    private static int totalOperations = 0;
    private static int readCount = 0;
    private static int getCount = 0;
    private static int putCount = 0;
    private static int errorCount = 0;

    private static List<Thread> workers = new List<Thread>();

    #endregion

    #region Getters and Setters
    #endregion

    public static void Main(string[] args)
    {
        // Check that exactly one command-line argument was given.
        if (args.Length == 1)
        {
            // Convert it to an integer port number.
            int portNum = int.Parse(args[0]);
            // Check that the port is in the range 50000 to 59999.
            if (50000 <= portNum && portNum <= 59999)
            {
                // Create and start a TCP listener on the port.
                TcpListener listener = new TcpListener(IPAddress.Any, portNum);
				listener.Start();
                // Start a background thread that runs PrintStatsLoop().
                Thread statPrinter = new Thread(PrintStatsLoop);
                statPrinter.Start();


                // STAGE 2:
                // Change the server so it accepts clients in a loop.
                // Start a new worker thread for each client.
                // Do not wait immediately for each worker thread.
                while (true)
                {
					try
					{
						// STAGE 1:
						// Accept one client connection.
						TcpClient client = listener.AcceptTcpClient();
						// Increase totalClients safely.
						lock (stateLock)
						{
							totalClients++;

							// Create a worker thread for that client.
							Thread worker = new Thread(TupleSpaceWorker.HandleClient);
							// Pass the accepted TcpClient into the worker thread.
							worker.Start(client);

							workers.Add(worker);
						}
					}
					catch (IOException ex)
					{
						Console.WriteLine("Error handling client: " + ex.Message);
					}
                }
                // Wait for the worker thread to finish.
                foreach (Thread t in workers)
                {
                    t.Join();
                }
				statPrinter.Join();
            }
            else
            {
                // If invalid, print:
                //      Usage: mono TupleSpaceServer.exe <port>
                //    and stop.
                Console.WriteLine("Usage: mono TupleSpaceServer.exe " + portNum.ToString());
                System.Environment.Exit(-1);
            }
        }
        else
        {
            Console.WriteLine("Argument count was not one.\nTerminating.");
            System.Environment.Exit(160);
        }
    }

    public static string HandleRequest(string requestBody)
    {
		totalOperations++;

		if(requestBody == null)
		{
			return null;
		}

		string ret = "";
        // Parse the request body.
		string[] splitRequest = requestBody.Split(' ');

		string requestType = splitRequest[1];
		// Work out whether the request is READ, GET, or PUT.
		if(requestType == "P")
		{
			putCount++;
			string key = splitRequest[2];
			string val = splitRequest[3];

			lock(stateLock)
			{
				if(tupleSpace.ContainsKey(key))
				{
					ret = "038 ERROR: Key already in tuple space.";
					errorCount++;
				}
				else
				{
					tupleSpace.Add(key, val);
					ret = "006 OK";
				}
			}
		}
		else if(requestType == "R")
		{
			readCount++;
			string key = splitRequest[2];

			lock(stateLock)
			{
				if(tupleSpace.ContainsKey(key))
				{
					string val = tupleSpace[key];
					int messagelength = val.Length + 3;
					
					ret = messagelength.ToString().PadLeft(3, '0') + " " + val;
				}
				else
				{
					ret = "025 error: key not found.";
					errorCount++;
				}
			}
		}
		else if(requestType == "G")
		{
			getCount++;
			string key = splitRequest[2];

			lock(stateLock)
			{
				if(tupleSpace.ContainsKey(key))
				{
					string val = tupleSpace[key];
					int messagelength = val.Length + 3;

					tupleSpace.Remove(key);
					
					ret = messagelength.ToString().PadRight(3, '0') + " " + val;
				}
				else
				{
					ret = "025 error: key not found.";
					errorCount++;
				}
			}
		}
		// Access tupleSpace.
		// Update the counters.
		// Return the correct response string.
		// When multiple worker threads are running, shared state must be protected.
        return ret;
    }

	public static void DecrementClients()
	{
		lock (stateLock)
		{
			totalClients--;
		}
	}

    private static void PrintStatsLoop()
    {
        while (true)
        {
            Thread.Sleep(10000);
            PrintStats();
        }
    }

    private static void PrintStats()
    {
        // Print the current tuple space statistics.
        // This method should read shared data safely.
		
        lock (stateLock)
        {
			int tuples = tupleSpace.Count; 
			float avgTupleSize = 0;
			float avgKeySize = 0;
			float avgValueSize = 0;

			int totalTupleSize = 0;
			int totalKeySize = 0;
			int totalValueSize = 0;

			foreach(string k in tupleSpace.Keys)
			{
				totalKeySize += k.Length;
			}
			foreach(string v in tupleSpace.Values)
			{
				totalValueSize += v.Length;
			}
			totalTupleSize = totalKeySize + totalValueSize;

			if(tuples != 0)
			{
				avgTupleSize = (float)totalTupleSize/tuples;
				avgKeySize = (float)totalKeySize/tuples;
				avgValueSize = (float)totalValueSize/tuples;
			}

			Console.WriteLine("--- Tuple Space Stats ---");
			Console.WriteLine("Tuples: " + tuples.ToString());
			Console.WriteLine("Avg Tuple Size: " + avgTupleSize.ToString("0.00"));
			Console.WriteLine("Avg Key Size: " + avgKeySize.ToString("0.00"));
			Console.WriteLine("Avg Value Size: " + avgValueSize.ToString("0.00"));
			Console.WriteLine("Clients: " + totalClients.ToString());
			Console.WriteLine("Operations: " + totalOperations.ToString());
			Console.WriteLine("READs: " + readCount.ToString());
			Console.WriteLine("GETs: " + getCount.ToString());
			Console.WriteLine("PUTs: " + putCount.ToString());
			Console.WriteLine("Errors: " + errorCount.ToString());
        }
    }
}
