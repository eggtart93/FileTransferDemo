package com.jkzhou.demo;
public class Demo
{
    private static final String HELP = "Usage:\n\t<send> <targetHost> <targetPort> <filePath>\nor\n\t<receive> <localPort> <pathToSave>";

    public static void main(String[] args)
    {
        if ((args == null) || (args.length == 0))
        {
            exitWithError();
        }

        if ("receive".equalsIgnoreCase(args[0]) && (args.length == 3))
        {
            new Receiver(Integer.valueOf(args[1]), args[2]).start();
        }
        else if ("send".equalsIgnoreCase(args[0]) && (args.length == 4))
        {
            new Sender(args[1], Integer.valueOf(args[2]), args[3]).start();
        }
        else
        {
            exitWithError();
        }
    }

    private static void exitWithError()
    {
        System.err.println("Invalid input");
        System.out.println(HELP);
        System.exit(-1);
    }
}
