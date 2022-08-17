/**
 * die LaunchClient Class hat alle Methode , die die Client braucht
 */

package pis.hue2.client;

import pis.hue2.common.*;

import java.net.*;
import java.io.*;

public class LaunchClient {

    private Socket client;
    private int port;
    public DataOutputStream dataOutput;
    public DataInputStream dataInput;

    /**
     * der konstrukton von LaunchClient nimmt ein port an , um die verbindung mit server zu erstellen
     *
     * @param port
     */
    public LaunchClient(int port) {
        this.port = port;
        try {
            client = new Socket("127.0.0.1", this.port);
            dataInput = new DataInputStream(client.getInputStream());
            dataOutput = new DataOutputStream((client.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * schickt die Instruction CON
     */
    public void schickCon() {
        try {
            dataOutput.writeUTF(Instruction.CON.toString() + "\n");
            dataOutput.flush();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * die methode receiveFile hilft uns die Datei zu empfangen
     *
     * @param fileName
     * @throws Exception wenn eine Exception auftritt
     */
    public void receiveFile(String fileName) throws Exception {
        int length = dataInput.readInt();
        byte[] bytDatei = new byte[length];
        InputStream inputClient = client.getInputStream();
        FileOutputStream fileOutput = new FileOutputStream("src/pis/hue2/client/fileClient/" + fileName);
        inputClient.read(bytDatei, 0, bytDatei.length);
        fileOutput.write(bytDatei, 0, bytDatei.length);
    }

    /**
     * die Methode setText prueft die Eingabe von User und werf eine Exception , falls
     * die Eingabe leer ist
     *
     * @param mesage
     * @throws IllegalArgumentException wenn das argument empty ist
     */
    public void setTex(String mesage) throws IllegalArgumentException {
        if (mesage.isEmpty()) {
            throw new IllegalArgumentException("der text soll nicht leer sein");
        }
    }

    /**
     * schickt die Instruction GET
     */
    public void schickGet() {
        try {
            dataOutput.writeUTF(Instruction.GET.toString() + "\n");
            dataOutput.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * schickt die Instruction ACK
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
     * schickt die Instruction LST
     */
    public void schickList() {
        try {
            dataOutput.writeUTF(Instruction.LST.toString() + "\n");
            dataOutput.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * schickt die Instruction DEL
     */
    public void schickDel() {
        try {
            dataOutput.writeUTF(Instruction.DEL.toString() + "\n");
            dataOutput.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * schickt die Instruction PUT
     */
    public void schickPut() {
        try {
            dataOutput.writeUTF(Instruction.PUT.toString() + "\n");
            dataOutput.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * schickt die Instruction DAT
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
     * schickt die Instruction DSC
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
     * die Methode schickFile erlaubt uns eine File to server zu schicken
     *
     * @param filename
     * @throws Exception wenn eine Exception auftritt
     */
    public void schickFile(String filename) throws Exception {
        File file = new File("src/pis/hue2/client/fileClient/" + filename);
        int length = (int) file.length();
        dataOutput.writeInt(length);
        FileInputStream fileInput = new FileInputStream(file);
        byte[] bytDatei = new byte[length];
        fileInput.read(bytDatei, 0, bytDatei.length);
        OutputStream os = client.getOutputStream();
        os.write(bytDatei, 0, bytDatei.length);
    }
}
