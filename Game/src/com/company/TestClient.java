package com.company;

import com.company.CommnClasses.*;

import java.io.*;
import java.net.Socket;

public class TestClient {
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader ins;
    private static BufferedWriter outs;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", /*4004*/48651);
                //reader = new BufferedReader(new InputStreamReader(System.in));
                //System.out.println("Вы что-то хотели сказать? Введите это здесь:");
                //String word = reader.readLine();
                //out.write(word + "\n");
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                //outs = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                //outs.write("hi");
                //outs.flush();
                //
                //PlayerRegister nick = new PlayerRegister("1231", "4565", "7897");
                MessageQuery nick = new MessageQuery (322752058,"1231", "123","alex huy");
                out.writeObject(nick);

                //out.flush();
                MessageAnswer ans = (MessageAnswer) in.readObject();
                System.out.println(ans.isOk + ans.description+ ans.sender + ans.receiver+ ans.message + ans.date);
                //String serverWord = in.readLine();
                //System.out.println(serverWord);
            } finally {
                System.out.println("Клиент был закрыт...");
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }

    }
}

