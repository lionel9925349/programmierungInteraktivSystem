/**
 * Die LaunchServer class ertellt eine Verbindung zwischen Client und Server
 * und fuehrt die Instructions von Client  aus
 *
 * @author <b>Lionel Ullrich Noumi Noumi and Ruben Chester Dieutchou</b>
 * @version 1.0
 */
package pis.hue2.server;

import pis.hue2.common.Instruction;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LaunchServer {

    private ServerSocket server;
    private Socket client;
    private static int count_of_clients = 0;
    boolean isLeben = true;

    /**
     * die runServer startet die server mit einem port
     * @param port
     */
    public void runServer(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("server connected");
            while (isLeben) {
                client = server.accept();
                ClientHandler clientThread = new ClientHandler(client);
                clientThread.start();
                count_of_clients++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  die class ClientHandler  erlaubt es uns, jeden client in einem thread zu verwalten
     */
    public static class ClientHandler extends Thread {

        private Socket client;
        DataInputStream dataInput;
        DataOutputStream dataOutput;

        /**
         * der Konstructor von ClientHandler
         * @param clientHandler
         * @throws IOException wenn eine Input/Output Exception vorkommt
         */
        public ClientHandler(Socket clientHandler) throws IOException {
            this.client = clientHandler;
            dataInput = new DataInputStream(client.getInputStream());
            dataOutput = new DataOutputStream(client.getOutputStream());
        }


        /**
         * jede Instruction , die der client gibt , wird hier ausgefuehrt
         */
        @Override
        public void run() {
            try {
                while (true) {
                    String message = dataInput.readUTF();
                    if (message == null) {
                        System.out.println("no message");
                    } else if (message.equals(Instruction.CON.toString() + "\n")) {
                        if (count_of_clients > 3) {
                            schickDnd();
                            client.close();
                        } else {
                            schickAck();
                        }
                    } else if (message.equals(Instruction.GET.toString() + "\n")) {
                        String fileName = dataInput.readUTF();
                        if (suchenFile(fileName)) {
                            schickAck();
                            String messageAck = dataInput.readUTF();
                            if (messageAck.equals(Instruction.ACK.toString() + "\n")) {
                                schickDat();
                                schickFile(fileName);
                                String lastAck = dataInput.readUTF();
                            }
                        } else {
                            schickDnd();
                        }
                    } else if (message.equals(Instruction.PUT.toString() + "\n")) {
                        schickAck();
                        String messageDat = dataInput.readUTF();
                        if (messageDat.equals(Instruction.DAT.toString() + "\n")) {
                            String fileName = dataInput.readUTF();
                            if (suchenFile(fileName)) {
                                schickDnd();
                            } else {
                                schickAck();
                                receiveFile(fileName);
                            }

                        }
                    } else if (message.equals(Instruction.LST.toString() + "\n")) {
                        schickAck();
                        String messageAck = dataInput.readUTF();
                        if (messageAck.equals(Instruction.ACK.toString() + "\n")) {
                            schickDat();
                            String messageAc = dataInput.readUTF();
                            if (messageAc.equals(Instruction.ACK.toString() + "\n")) {
                                schickList();
                            }

                        }

                    } else if (message.equals(Instruction.DEL.toString() + "\n")) {
                        String fileName = dataInput.readUTF();
                        if (suchenFile(fileName) == true) {
                            deleteFile(fileName);
                            schickAck();
                        } else
                            schickDnd();
                    } else if (message.equals(Instruction.DSC.toString() + "\n")) {
                        schickDsc();
                        client.close();
                        if (count_of_clients > 0) {
                            count_of_clients--;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        /**
         *   schickt die Instruction ACK
         */
        public void schickAck() {
            try {
                dataOutput.writeUTF(Instruction.ACK.toString() + "\n");
                dataOutput.flush();
            } catch (Exception e1) {
                System.out.println(e1);
            }
        }

        /**
         * die Methode schickFile erlaubt uns eine File to client zu schicken
         * @param filename
         * @throws Exception wenn eine Exception auftritt
         */
        public void schickFile(String filename) throws Exception {

            File file = new File("src/pis/hue2/server/fileServer/" + filename);
            int length = (int) file.length();
            dataOutput.writeInt(length);
            FileInputStream fr = new FileInputStream(file);
            byte b[] = new byte[length];
            fr.read(b, 0, b.length);
            OutputStream os = client.getOutputStream();
            os.write(b, 0, b.length);
        }

        /**
         *   schickt die Instruction DAT
         */
        public void schickDat() {
            try {
                dataOutput.writeUTF(Instruction.DAT.toString() + "\n");
                dataOutput.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * die Methode suchenFile nimmt eine fileName und sucht das in die server Folder
         * @param fileName
         * @return
         */
        public Boolean suchenFile(String fileName) {
            String folderLocal = "src/pis/hue2/server/fileServer/";
            File file = new File(folderLocal);
            File[] fileLst = file.listFiles();
            for (File f : fileLst) {
                if (f.getName().equals(fileName)) {
                    return true;
                }
            }
            return false;
        }

        /**
         *   schickt die Instruction DND
         */
        public void schickDnd() {
            try {
                dataOutput.writeUTF(Instruction.DND.toString() + "\n");
                dataOutput.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         *   schickt die Instruction DSC
         */
        public void schickDsc() {
            try {
                dataOutput.writeUTF(Instruction.DSC.toString() + "\n");
                dataOutput.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        /**
         *  die Methode receiveFile empfangt , die Datei , die von client geschickt wird
         * @param fileName
         * @throws Exception wenn eine Exception auftritt
         */
        public void receiveFile(String fileName) throws Exception {
            int length = dataInput.readInt();
            byte[] bytDatei = new byte[length];
            InputStream inputServer = client.getInputStream();
            FileOutputStream fileOutput = new FileOutputStream("src/pis/hue2/server/fileServer/" + fileName);
            inputServer.read(bytDatei, 0, bytDatei.length);
            fileOutput.write(bytDatei, 0, bytDatei.length);
        }

        /**
         * die Methode schickList  schickt die List alle Datei , die in Server liegt
         */
        public void schickList() {
            try {
                String folderLocal = "src/pis/hue2/server/fileServer/";
                File file = new File(folderLocal);
                File[] files = file.listFiles();
                String listFileConcat = "";
                for (int i = 0; i < files.length; i++) {
                    listFileConcat += files[i].getName() + "\n";
                }
                dataOutput.writeUTF(listFileConcat);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * die Methode deleteFile nimmt eine FileName als parameter und loescht sie in Ordner von Server
         * @param fileName
         */
        public void deleteFile(String fileName) {
            String folderLocal = "src/pis/hue2/server/fileServer/";
            File file = new File(folderLocal);
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.getName().equals(fileName)) {
                    f.delete();
                }
            }

        }

    }


    /**
     * das ist Main classe , um die Server zu starten
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LaunchServer server = new LaunchServer();
                    server.runServer(6501);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }
}
