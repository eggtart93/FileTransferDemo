package com.jkzhou.demo;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Sender
{
    private static final int DISK_BUFFER_SIZE = 64 * 1024;
    private static final int NETWORK_BUFFER_SIZE = 128 * 1024;

    private final String destHost;
    private final int destPort;
    private final String filePaths;

    Sender(String host, int port, String filePath)
    {
        destHost = host;
        destPort = port;
        filePaths = filePath;
    }

    void start()
    {
        println("Attempting to connect to <%s:%d>", destHost, destPort);
        try (
                InputStream fileIn = new BufferedInputStream(new FileInputStream(filePaths), DISK_BUFFER_SIZE);
                Socket connection = new Socket(destHost, destPort);
                OutputStream dataOut = new BufferedOutputStream(connection.getOutputStream(), NETWORK_BUFFER_SIZE);
        )
        {
            println("Start sending file to <%s:%d>", destHost, destPort);
            long start = System.currentTimeMillis();
            long totalBytesSend = 0;

            int byteRead = -1;
            while ((byteRead = fileIn.read()) != -1)
            {
                dataOut.write(byteRead);
                ++totalBytesSend;
            }
            dataOut.flush();

            double timeUsed = (System.currentTimeMillis() - start) / 1000.;
            double avgSpeed = (totalBytesSend / 1000.) / timeUsed;
            println("Completed.%n\tTotal bytes send: %d bytes,%n\tTime used: %.3f sec,%n\tAverage speed: %.3f KB/s", totalBytesSend, timeUsed, avgSpeed);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void println(String format, Object... args)
    {
        System.out.format("[%s] : %s%n", this.getClass().getSimpleName(), String.format(format, args));
    }
}
