package com.jkzhou.demo;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver
{
    private static final int DISK_BUFFER_SIZE = 64 * 1024;
    private static final int NETWORK_BUFFER_SIZE = 128 * 1024;

    private final int localPort;
    private final String savePath;

    Receiver(int port, String path)
    {
        localPort = port;
        savePath = path;
    }

    void start()
    {
        println("Start listening at port %d ...", localPort);
        try (
                ServerSocket connection = new ServerSocket(localPort);
                Socket remote = connection.accept();
                InputStream dataIn = new BufferedInputStream(remote.getInputStream(), NETWORK_BUFFER_SIZE);
                OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(savePath), DISK_BUFFER_SIZE)
        )
        {
            println("Start receiving file from <%s:%d>", remote.getInetAddress().getHostAddress(), remote.getPort());
            long start = System.currentTimeMillis();
            long totalBytesReceived = 0;

            int byteReceived = -1;
            while ((byteReceived = dataIn.read()) != -1)
            {
                fileOut.write(byteReceived);
                ++totalBytesReceived;
            }
            fileOut.flush();

            double timeUsed = (System.currentTimeMillis() - start) / 1000.;
            double avgSpeed = (totalBytesReceived / 1000.) / timeUsed;
            println("Completed, file saved to: %s.%n\tTotal bytes send: %d bytes,%n\tTime used: %.3f sec,%n\tAverage speed: %.3f KB/s", savePath, totalBytesReceived, timeUsed, avgSpeed);
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
