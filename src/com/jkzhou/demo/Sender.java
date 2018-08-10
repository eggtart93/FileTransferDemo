package com.jkzhou.demo;

import static com.jkzhou.demo.AppConfig.DISK_BUFFER_SIZE;
import static com.jkzhou.demo.AppConfig.NETWORK_BUFFER_SIZE;
import static com.jkzhou.demo.AppConfig.SIGNAL_CONFIRM;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jkzhou.demo.AppUtils.Console;

public class Sender
{
    private static final Console CONSOLE = AppUtils.getConsole(Sender.class);
    private final String destHost;
    private final int destPort;
    private final String filePath;

    Sender(String host, int port, String filePath)
    {
        this.destHost = host;
        this.destPort = port;
        this.filePath = filePath;
    }

    void start()
    {
        CONSOLE.println("Attempting to connect to <%s:%d>", destHost, destPort);
        try (
                InputStream fileIn = new BufferedInputStream(new FileInputStream(filePath), DISK_BUFFER_SIZE);
                Socket connection = new Socket(destHost, destPort);
                OutputStream dataOut = new BufferedOutputStream(connection.getOutputStream(), NETWORK_BUFFER_SIZE);
                PrintWriter msgWriter = new PrintWriter(connection.getOutputStream());
                BufferedReader msgReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));)
        {
            /* Connected to the remote end successfully */
            CONSOLE.println("Connection established.");

            /* Send a request with file information to the remote and wait for its confirmation */
            CONSOLE.println("Requesting confirmation of receiving the file from <%s:%d>", destHost, destPort);
            if (!requestConfirmation(msgWriter, msgReader))
            {
                CONSOLE.println("File transfer request is rejected by <%s:%d>", destHost, destPort);
                return;
            }

            /* Transfer the file over the TCP connection */
            CONSOLE.println("Start sending file to <%s:%d>", destHost, destPort);
            long timeStart = System.currentTimeMillis();
            long totalBytesSend = sendFile(fileIn, dataOut);
            double timeUsed = (System.currentTimeMillis() - timeStart) / 1000.;

            CONSOLE.println("Completed.%n\tTotal bytes send: %d bytes,%n\tTime used: %.3f sec,%n\tAverage speed: %.3f KB/s", totalBytesSend, timeUsed, (totalBytesSend / 1000.) / timeUsed);
        }
        catch (IOException e)
        {
            // TODO need a meaningful message
            e.printStackTrace();
        }
    }

    private boolean requestConfirmation(PrintWriter msgWriter, BufferedReader msgReader) throws IOException
    {
        Path fullPath = Paths.get(filePath);
        msgWriter.println(fullPath.getFileName());
        msgWriter.println(Files.size(fullPath));
        msgWriter.flush();
        return SIGNAL_CONFIRM.equals(msgReader.readLine());
    }

    private long sendFile(InputStream fileIn, OutputStream dataOut) throws IOException
    {
        long totalBytesSend = 0;
        int byteRead = -1;
        while ((byteRead = fileIn.read()) != -1)
        {
            dataOut.write(byteRead);
            ++totalBytesSend;
        }
        dataOut.flush();
        return totalBytesSend;
    }
}
