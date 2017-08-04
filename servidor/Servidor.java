import java.net.*;
import java.io.*;
import java.util.*;

class Servidor {
  public static void main(String[] args) {
    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(8080);
    } catch (IOException e) {
      System.out.println("Nao foi possivel usar o porto: " + 8080 + ", " + e);
      System.exit(1);
    }

    for (int i = 0; i >= 0; i++) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException e) {
        System.out.println("Falha na aceitacao: " + 8080 + ", " + e);
        System.exit(1);
      }
      System.out.println("Conectou!");

      new Servindo(clientSocket).start();
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


class Servindo extends Thread {
  Socket clientSocket;
  static ObjectOutputStream os[] = new ObjectOutputStream[2];
  static int cont = 0;

  Servindo(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    boolean threadAtiva = true;
    int idcliente;
    try {
      ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
      os[cont] = new ObjectOutputStream(clientSocket.getOutputStream());
      idcliente = cont++;
      Object inputLine;
      System.out.println("Conexao estabelecida com cliente " + cont);
      os[cont - 1].writeInt(cont);
      os[cont - 1].flush();
      inputLine = (Boolean) true;
      if(cont == 2) {
        os[1].writeObject(inputLine);
        os[1].flush();
        os[0].writeObject(inputLine);
        os[0].flush();
      }
      do {
        inputLine = is.readObject();
        if(inputLine instanceof Boolean) {
          threadAtiva = (Boolean) inputLine;
        }

        if (cont == 2) {
          if (idcliente == 0 && !threadAtiva)
            break;
          //if (threadAtiva){
            os[1].writeObject(inputLine);
            os[1].flush();

            os[0].writeObject(inputLine);
            os[0].flush();
          //}
        }
      } while (threadAtiva);

      os[idcliente].close();
      is.close();
      this.clientSocket.close();

      System.out.println("Conexao finalizada!" + (idcliente + 1));
      cont = 0;

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
};
