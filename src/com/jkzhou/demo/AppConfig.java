package com.jkzhou.demo;

public final class AppConfig
{
    /* Input related settings */
    public static final String HINT_USAGE = "Usage:\n\t1) <send> <targetHost> <targetPort> <filePath>\n\t2) <receive> <localPort> [dirToSave]\n";
    public static final String HINT_USER_INPUT_ERROR = "Invalid input";
    protected static final String CMD_RECEIVE = "RECEIVE";
    protected static final String CMD_SEND = "SEND";

    /* Network related settings */
    protected static final String SIGNAL_CONFIRM = "__SIGNAL_CONFIRM__";
    protected static final String SIGNAL_REJECT = "__SIGNAL_REJECT__";
    protected static final int DISK_BUFFER_SIZE = 64 * 1024;
    protected static final int NETWORK_BUFFER_SIZE = 128 * 1024;
    protected static final int SOCKET_WAIT_TIMEOUT = 60 * 1000;

    /* Output settings */
    public static final String DEFAULT_SAVE_DIR = System.getProperty("user.home");

    private AppConfig()
    {
    }
}
