/**
 * Die ClientGui class ist ein Graphical User Interface ,damit kann ein Client mehrere Actions Ausfueren
 * wie zum beispiel CONNEXION, GET, PUT, LIST, DELETE , DISCONECT
 *
 * @author <b>Lionel Ulrich Noumi Noumi and Ruben Chester Dieutchou</b>
 * @version 1.0
 */

import pis.hue2.client.LaunchClient;
import pis.hue2.common.Ausgabe;
import pis.hue2.common.Instruction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Die ClientGui class implement die Schnittstelle Ausgabe
 */
public class Gui extends JFrame implements Ausgabe {
    private JFrame frame;
    LaunchClient client;
    JTextArea anzeigeNachricht = new JTextArea();

    /**
     *Hier wir die Frame und die Verschiedene Button initialisieren
     * @throws IOException wenn eine Input/Output Exception vorkommt
     */
    public Gui() throws IOException {

        frame = new JFrame();
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setBounds(600, 750, 700, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton btnConnecion = new JButton("   Connection");
        btnConnecion.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnConnecion.setBounds(10, 26, 180, 39);
        frame.getContentPane().add(btnConnecion);

        JButton btnDisconnect = new JButton("   Disconnect");
        btnDisconnect.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnDisconnect.setBounds(287, 26, 180, 39);
        frame.getContentPane().add(btnDisconnect);

        JButton btnGet = new JButton("   GET");
        btnGet.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnGet.setBounds(10, 85, 100, 39);
        frame.getContentPane().add(btnGet);

        JTextArea textAreaGet = new JTextArea();
        textAreaGet.setLineWrap(true);
        textAreaGet.setBounds(150, 85, 155, 45);
        frame.getContentPane().add(textAreaGet);

        JButton btnPut = new JButton("   PUT");
        btnPut.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnPut.setBounds(10, 150, 100, 39);
        frame.getContentPane().add(btnPut);

        JTextArea textAreaPut = new JTextArea();
        textAreaPut.setLineWrap(true);
        textAreaPut.setBounds(150, 150, 155, 45);
        frame.getContentPane().add(textAreaPut);

        JButton btnDelete = new JButton("   DELETE");
        btnDelete.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnDelete.setBounds(10, 240, 140, 39);
        frame.getContentPane().add(btnDelete);

        JTextArea textAreaDelete = new JTextArea();
        textAreaDelete.setLineWrap(true);
        textAreaDelete.setBounds(230, 240, 140, 45);
        frame.getContentPane().add(textAreaDelete);

        JButton btnNewButton_6 = new JButton("   List");
        btnNewButton_6.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnNewButton_6.setBounds(10, 320, 140, 39);
        frame.getContentPane().add(btnNewButton_6);

        anzeigeNachricht.setLineWrap(true);
        anzeigeNachricht.setBounds(90, 380, 155, 45);
        frame.getContentPane().add(anzeigeNachricht);

        btnDisconnect.setEnabled(false);
        btnGet.setEnabled(false);
        btnPut.setEnabled(false);
        btnDelete.setEnabled(false);
        btnNewButton_6.setEnabled(false);

        btnConnecion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<String, Void>() {
                    String resultCon;

                    @Override
                    protected String doInBackground() throws Exception {
                        try {
                            client = new LaunchClient(6501);
                            client.schickCon();
                            resultCon = client.dataInput.readUTF();
                            if (resultCon.equals(Instruction.ACK.toString() + "\n")) {
                                btnDisconnect.setEnabled(true);
                                btnGet.setEnabled(true);
                                btnPut.setEnabled(true);
                                btnDelete.setEnabled(true);
                                btnNewButton_6.setEnabled(true);
                                btnConnecion.setEnabled(false);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                        return resultCon;
                    }

                    protected void done() {
                        try {
                            resultCon = get();
                            zeigeNachricht("ResultCON: " + resultCon);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }

                }.execute();
            }
        });

        btnDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<String, Void>() {
                    String resultDsc;

                    @Override
                    protected String doInBackground() throws Exception {
                        try {
                            client.schickDsc();
                            resultDsc = client.dataInput.readUTF();
                            if (resultDsc.equals(Instruction.DSC.toString() + "\n")) {
                                btnGet.setEnabled(false);
                                btnPut.setEnabled(false);
                                btnDelete.setEnabled(false);
                                btnNewButton_6.setEnabled(false);
                                btnConnecion.setEnabled(true);
                                btnDisconnect.setEnabled(false);
                            }

                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                        return resultDsc;
                    }

                    protected void done() {
                        try {
                            resultDsc = get();
                            zeigeNachricht("Client: " + resultDsc);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }

                }.execute();
            }
        });

        btnGet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<String, Void>() {
                    String resultGet;

                    @Override
                    protected String doInBackground() throws Exception {
                        try {
                            client.schickGet();
                            String filename = textAreaGet.getText();
                            client.setTex(filename);
                            client.dataOutput.writeUTF(filename);
                            String message = client.dataInput.readUTF();
                            if (message.equals(Instruction.ACK.toString() + "\n")) {
                                client.schickAck();
                                String messageDat = client.dataInput.readUTF();
                                if (messageDat.equals(Instruction.DAT.toString() + "\n")) {
                                    client.receiveFile(filename);
                                    client.schickAck();
                                    resultGet = message;
                                }
                            } else {
                                resultGet = message;
                            }

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "get Exception :  " + ex.getMessage());
                        }
                        return resultGet;
                    }

                    @Override
                    protected void done() {
                        try {
                            resultGet = get();
                            zeigeNachricht("GetResult: " + resultGet);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, ex.getMessage());
                        }
                    }
                }.execute();
            }
        });

        btnPut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new SwingWorker<String, Void>() {
                    String resultPut;

                    @Override
                    protected String doInBackground() throws Exception {
                        try {
                            client.schickPut();
                            String messageAck = client.dataInput.readUTF();
                            if (messageAck.equals(Instruction.ACK.toString() + "\n")) {
                                client.schickDat();
                                String fileName = textAreaPut.getText();
                                client.setTex(fileName);
                                client.dataOutput.writeUTF(fileName);
                                System.out.println("GO SCHICKlOCALFILE :" + fileName);
                                resultPut = client.dataInput.readUTF();
                                if (resultPut.equals(Instruction.DND.toString() + "\n")) {
                                    return resultPut;
                                } else if (resultPut.equals(Instruction.ACK.toString() + "\n")) {
                                    client.schickFile(fileName);
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Put Exception : " + ex.getMessage());
                        }
                        return resultPut;
                    }

                    @Override
                    protected void done() {
                        try {
                            resultPut = get();
                            zeigeNachricht("GetResultPut: " + resultPut);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, ex.getMessage());
                        }
                    }
                }.execute();
            }
        });


        btnNewButton_6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<String, Void>() {
                    String lstOfFile;

                    @Override
                    protected String doInBackground() throws Exception {
                        try {
                            client.schickList();
                            String messageAck = client.dataInput.readUTF();
                            if (messageAck.equals(Instruction.ACK.toString() + "\n")) {
                                client.schickAck();
                                String messageDat = client.dataInput.readUTF();
                                if (messageDat.equals(Instruction.DAT.toString() + "\n")) {
                                    client.schickAck();
                                    lstOfFile = client.dataInput.readUTF();
                                }
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "List Exception : " + ex.getMessage());
                        }

                        return lstOfFile;
                    }

                    public void done() {
                        try {
                            lstOfFile = get();
                            zeigeListe(lstOfFile);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "ListDone ex: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new SwingWorker<String, Void>() {
                    String resultDel;

                    @Override
                    protected String doInBackground() throws Exception {
                        try {
                            client.schickDel();
                            String fileName = textAreaDelete.getText();
                            client.setTex(fileName);
                            client.dataOutput.writeUTF(fileName);
                            String messageAck = client.dataInput.readUTF();
                            resultDel = messageAck;
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Del ex: " + ex.getMessage());
                        }

                        return resultDel;
                    }

                    @Override
                    protected void done() {
                        try {
                            resultDel = get();
                            zeigeNachricht("GetResultDel: " + resultDel);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, ex.getMessage());
                        }
                    }
                }.execute();
            }
        });
    }

    /**
     * die Methode zeigt eine Nachricht im GUI
     * @param nachricht wird von server gegeben
     */
    @Override
    public void zeigeNachricht(String nachricht) {
        anzeigeNachricht.setText(nachricht);
    }

    /**
     * das zeigt die LIST alle folder die im Server liegt
     * @param List ist die List von File
     */
    @Override
    public void zeigeListe(String List) {
        JOptionPane.showMessageDialog(frame, "List On Server: \n" + List);
    }

    /**
     * die Main class von Gui startet das GUI
     * @param args
     * @throws IOException wenn eine Exception auftritt
     */
    public static void main(String[] args) throws IOException {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Gui guiFenster = new Gui();
                    guiFenster.frame.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

