package com.jkzhou.demo;

import static com.jkzhou.demo.AppConfig.CMD_RECEIVE;
import static com.jkzhou.demo.AppConfig.CMD_SEND;
import static com.jkzhou.demo.AppConfig.HINT_USAGE;
import static com.jkzhou.demo.AppConfig.HINT_USER_INPUT_ERROR;

import com.jkzhou.demo.AppUtils.Console;

public class App
{
    private static final Console CONSOLE = AppUtils.getConsole(App.class);

    public static void main(String[] args)
    {
        if ((args == null) || (args.length == 0))
        {
            exitWithError();
        }

        if (CMD_RECEIVE.equalsIgnoreCase(args[0]) && (args.length > 1))
        {
            new Receiver(Integer.valueOf(args[1]), args.length > 2 ? args[2] : null).start();
        }
        else if (CMD_SEND.equalsIgnoreCase(args[0]) && (args.length == 4))
        {
            new Sender(args[1], Integer.valueOf(args[2]), args[3]).start();
        }
        else
        {
            exitWithError();
        }
        CONSOLE.println("Bye...\n");
    }

    private static void exitWithError()
    {
        CONSOLE.println(HINT_USER_INPUT_ERROR);
        CONSOLE.println(HINT_USAGE);
        System.exit(-1);
    }
}
