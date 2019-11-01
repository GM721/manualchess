import java.net.*;
import java.io.*;

class Main
{
	static public void main(String[] args)
	{
		while (true)
		{
			try
			{
				ServerSocket buba = new ServerSocket(228);
				Socket server = buba.accept();
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF("buab");
				buba.close();
			}
			catch(Exception e)
			{
			}
		}
	}
}
