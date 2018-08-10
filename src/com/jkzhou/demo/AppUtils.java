// **********************************************************************
// Copyright (c) 2018 Telefonaktiebolaget LM Ericsson, Sweden.
// All rights reserved.
// The Copyright to the computer program(s) herein is the property of
// Telefonaktiebolaget LM Ericsson, Sweden.
// The program(s) may be used and/or copied with the written permission
// from Telefonaktiebolaget LM Ericsson or in accordance with the terms
// and conditions stipulated in the agreement/contract under which the
// program(s) have been supplied.
// **********************************************************************
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
        return (format, args) -> System.out.printf("[%s] : %s%n", callerType.getSimpleName(), String.format(format, args));
    }

    private AppUtils()
    {
    }
}
