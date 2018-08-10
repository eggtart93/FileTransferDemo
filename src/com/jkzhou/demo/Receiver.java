package com.jkzhou.demo;

import static com.jkzhou.demo.AppConfig.DEFAULT_SAVE_DIR;
import static com.jkzhou.demo.AppConfig.NETWORK_BUFFER_SIZE;
import static com.jkzhou.demo.AppConfig.SIGNAL_CONFIRM;
import static com.jkzhou.demo.AppConfig.SIGNAL_REJECT;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

import com.jkzhou.demo.AppUtils.Console;

public class Receiver
{
    private class FileInfo
    {
        String name;
        long size;
        Path fullPath;
    }

    private static final Console CONSOLE = AppUtils.getConsole(Receiver.class);
    private final int localPort;
    private final Path saveLocation;

    Receiver(int port, String saveDirPath)
    {
        this.localPort = port;
        this.saveLocation = Paths.get(Optional.ofNullable(saveDirPath).orElse(DEFAULT_SAVE_DIR));
    }

    void start()
    {
        CONSOLE.println("Save location is set to: <%s>", saveLocation);
        try (
                ServerSocket connection = createServerSocket();
                Socket remote = connection.accept();
                InputStream dataIn = new BufferedInputStream(remote.getInputStream(), NETWORK_BUFFER_SIZE);
                BufferedReader msgReader = new BufferedReader(new InputStreamReader(remote.getInputStream()));
                PrintWriter msgWriter = new PrintWriter(remote.getOutputStream())
        )
        {
            /* Connected to the remote end successfully */
            String remoteHost = remote.getInetAddress().getHostAddress();
            int remotePort = remote.getPort();
            CONSOLE.println("Connection established: <%s:%d>", remoteHost, remotePort);

            /* Wait for the remote end to send file information */
            FileInfo fileInfo = receiveFileInfo(msgReader);

            /* Get confirmation from user and reply back */
            if (!confirm(msgWriter, fileInfo, remoteHost, remotePort))
            {
                return;
            }

            CONSOLE.println("The file will be saved to: %s", fileInfo.fullPath);
            CONSOLE.println("Start receiving file from <%s:%d>", remoteHost, remotePort);
            long timeStart = System.currentTimeMillis();
            long totalBytesReceived = receiveFile(dataIn, fileInfo);
            double timeUsed = (System.currentTimeMillis() - timeStart) / 1000.;
            CONSOLE.println(
                    "Completed, file saved to: %s.%n\t\tTotal bytes send: %d bytes,%n\t\tTime used: %.3f sec,%n\t\tAverage speed: %.3f KB/s",
                    fileInfo.fullPath,
                    totalBytesReceived,
                    timeUsed,
                    (totalBytesReceived / 1000.) / timeUsed);
        }
        catch (IOException e)
        {
            // TODO need a meaningful message
            e.printStackTrace();
        }
    }

    private boolean confirm(PrintWriter msgWriter, FileInfo fileInfo, String remoteHost, int remotePort)
    {
        CONSOLE.println("Received a file transfer request from <%s:%d>: <%s> <%.3f MB>",
                remoteHost,
                remotePort,
                fileInfo.name,
                AppUtils.bytesToMB(fileInfo.size));

        /* Get confirmation from user */
        CONSOLE.println("Enter 'y' or 'Y' to accept, or any other keys to reject:");
        boolean isAccepted;
        try (Scanner consoleIn = new Scanner(System.in))
        {
            isAccepted = "y".equalsIgnoreCase(consoleIn.nextLine());
        }

        /* Respond to the request */
        msgWriter.println(isAccepted ? SIGNAL_CONFIRM : SIGNAL_REJECT);
        msgWriter.flush();

        CONSOLE.println("Request %s", isAccepted ? "accpeted" : "rejected");
        return isAccepted;
    }

    private ServerSocket createServerSocket() throws IOException
    {
        InetAddress localHost = InetAddress.getLocalHost();
        CONSOLE.println("Start listening at <%s:%d> ...", localHost.getHostAddress(), localPort);
        return new ServerSocket(localPort, 0, localHost);
    }

    private long receiveFile(InputStream dataIn, FileInfo fileInfo) throws IOException
    {
        long totalBytesReceived = 0;
        int byteReceived = -1;
        try (OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(fileInfo.fullPath.toString())))
        {
            while ((byteReceived = dataIn.read()) != -1)
            {
                fileOut.write(byteReceived);
                ++totalBytesReceived;
            }
            fileOut.flush();
        }
        if (fileInfo.size != totalBytesReceived)
        {
            CONSOLE.println("WARNING! The size of the file was expected to be <%d> bytes, but received <%d> bytes",
                    fileInfo.size,
                    totalBytesReceived);
        }
        return totalBytesReceived;
    }

    private FileInfo receiveFileInfo(BufferedReader msgReader) throws IOException
    {
        FileInfo fileInfo = new FileInfo();
        fileInfo.name = Optional.ofNullable(msgReader.readLine()).orElseThrow(() -> new IOException(
                "End of stream has been reached!"));
        fileInfo.size = Optional.ofNullable(msgReader.readLine()).map(Long::valueOf).orElseThrow(() -> new IOException(
                "End of stream has been reached!"));
        File saveDir = saveLocation.toFile();
        if (!saveDir.exists() || !saveDir.isDirectory())
        {
            throw new FileNotFoundException("'" + saveLocation + "' is not a directory or it does not exist!");
        }
        fileInfo.fullPath = saveLocation.resolve(fileInfo.name);
        return fileInfo;
    }
}
