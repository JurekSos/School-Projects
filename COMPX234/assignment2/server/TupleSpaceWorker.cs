using System;
using System.Net.Sockets;
using System.Text;
using System.IO;

using System.Text.RegularExpressions;

public class TupleSpaceWorker
{
    public static void HandleClient(object clientObject)
    {
        try
        {
            // 1. Cast the object to TcpClient.
            TcpClient client = (TcpClient)clientObject;
            // 2. Get the NetworkStream from the client.
            using (NetworkStream stream = client.GetStream())
			{
				using (StreamReader reader = new StreamReader(stream))
				{
					using (StreamWriter writer = new StreamWriter(stream) { AutoFlush = true })
					{
						while (client.Connected)
						{
							// 3. Repeatedly read one request from the client using the framed protocol.
							string request = reader.ReadLine();
							if(request != null && !request.Equals("")){
								Console.WriteLine("Echo: " + request);

								// 4. Call TupleSpaceServer.HandleRequest(...) to process that request.
								string response = TupleSpaceServer.HandleRequest(request);
								// 5. Send the response back to the client using the framed protocol.
								writer.WriteLine(response);
							}
						}
					}
				}
			}
			// 6. Stop when the client disconnects.
			client.Dispose();
			TupleSpaceServer.DecrementClients();
        }
        catch (IOException ex)
        {
            Console.WriteLine("Error handling client: " + ex.Message);
        }
        //
        // You may create extra helper methods if you want,
        // but they are not required.
    }
}
