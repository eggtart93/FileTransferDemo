package com.jkzhou.demo;

public final class AppUtils
{
    interface Console
    {
        void println(String format, Object... args);
    }

    public static double bytesToMB(long bytes)
    {
        return bytes / (double) (1024 * 1024);
    }

    public static Console getConsole(Class<?> callerType)
    {
        return (format, args) -> System.out.printf("[%s] : %s%n",
                callerType.getSimpleName(),
                String.format(format, args));
    }

    private AppUtils()
    {
    }
}
