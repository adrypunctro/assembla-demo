/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucenebot.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Adrian Simionescu
 * 30 black
 * 31 red
 * 32 green
 * 33 yellow
 * 34 blue
 * 35 magenta
 * 36 cyan
 * 37 white
 * 40 black background
 * 41 red background
 * 42 green background
 * 43 yellow background
 * 44 blue background
 * 45 magenta background
 * 46 cyan background
 * 47 white background
 * 1 make bright (usually just bold)
 * 21 stop bright (normalizes boldness)
 * 4 underline
 * 24 stop underline
 * 0 clear all formatting
 */
public class VA_DEBUG
{
    public static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * 1 - Console
     * 2 - File log
     */
    private static final int SOURCE = 1;
    
    /**
     *
     * @param process
     * @param msg
     */
    public static void INFO(String process, String msg)
    {
        INFO(process, msg, true);
    }
    
    public static void INFO(String process, String msg, boolean newline)
    {
        switch(SOURCE)
        {
            case 1:
                _consoleInfo(process, msg, newline);
                break;
        }
    }
    
    public static void INFO(String msg, boolean newline)
    {
        switch(SOURCE)
        {
            case 1:
                _consoleInfo(msg, newline);
                break;
        }
    }
    
    /**
     *
     * @param process
     * @param msg
     */
    public static void ERROR(String process, String msg)
    {
        switch(SOURCE)
        {
            case 1:
                _consoleError(process,msg);
                break;
        }
    }
    
    /**
     *
     * @param process
     * @param msg
     */
    public static void ERROR(String msg, boolean newline)
    {
        switch(SOURCE)
        {
            case 1:
                _consoleError(msg, newline);
                break;
        }
    }
    
    /**
     *
     * @param process
     * @param msg
     */
    public static void SUCCESS(String process, String msg)
    {
        switch(SOURCE)
        {
            case 1:
                _consoleSuccess(process,msg);
                break;
        }
    }
    
    /**
     *
     * @param process
     * @param msg
     */
    public static void SUCCESS(String msg, boolean newline)
    {
        switch(SOURCE)
        {
            case 1:
                _consoleSuccess(msg, newline);
                break;
        }
    }
    
    /**
     *
     * @param process
     * @param msg
     */
    public static void WARNING(String process, String msg)
    {
        switch(SOURCE)
        {
            case 1:
                _consoleWarning(process,msg);
                break;
        }
    }
    
    /**
     *
     * @param str
     * @param color_code
     * @return
     */
    public static String colorize(String str, int color_code) {
        return (char)27+"[0m"+(char)27+"["+color_code+"m"+str+(char)27+"[0m"+(char)27+"[1m";
    }
    
    private static void _consoleInfo(String process, String msg, boolean newline)
    {
        Date date = new Date();
        System.out.println(
                (char)27+"[36m["+dateFormat.format(date)+"] "+
                (char)27+"[0m"+process+" "+(char)27+"[0m");

        int i=0;
        int count = msg.split("\n").length;
        for(String line : msg.split("\n")) {
            System.out.print((char)27+"[1m"+line+(char)27+"[0m");
            boolean last = (i++ == count-1); 
            if (!last || newline) {
                System.out.print("\n");
            }
        }
    }
    
    private static void _consoleInfo(String msg, boolean newline)
    {
        int i=0;
        int count = msg.split("\n").length;
        for(String line : msg.split("\n")) {
            System.out.print((char)27+"[1m"+line+(char)27+"[0m");
            boolean last = (i++ == count-1); 
            if (!last || newline) {
                System.out.print("\n");
            }
        }
    }
    
    private static void _consoleWarning(String process, String msg)
    {
        Date date = new Date();
        System.out.println(
                (char)27+"[36m["+dateFormat.format(date)+"] "+
                (char)27+"[0m"+process+" "+(char)27+"[0m");

        for(String line : msg.split("\n")) {
            System.out.println((char)27+"[33m"+line+(char)27+"[0m");
        }
    }
    
    private static void _consoleError(String process, String msg)
    {
        Date date = new Date();
        System.out.println(
                (char)27+"[36m["+dateFormat.format(date)+"] "+
                (char)27+"[0m"+process+" "+(char)27+"[0m");

        for(String line : msg.split("\n")) {
            System.out.println((char)27+"[31m"+line+(char)27+"[0m");
        }
    }
    
    private static void _consoleError(String msg, boolean newline)
    {
        int i=0;
        int count = msg.split("\n").length;
        for(String line : msg.split("\n")) {
            System.out.print((char)27+"[31m"+line+(char)27+"[0m");
            boolean last = (i++ == count-1); 
            if (!last || newline) {
                System.out.print("\n");
            }
        }
    }
    
    private static void _consoleSuccess(String process, String msg)
    {
        Date date = new Date();
        System.out.println(
                (char)27+"[36m["+dateFormat.format(date)+"] "+
                (char)27+"[0m"+process+" "+(char)27+"[0m");

        for(String line : msg.split("\n")) {
            System.out.println((char)27+"[32m"+line+(char)27+"[0m");
        }
    }
    
    private static void _consoleSuccess(String msg, boolean newline)
    {
        int i=0;
        int count = msg.split("\n").length;
        for(String line : msg.split("\n")) {
            System.out.print((char)27+"[32m"+line+(char)27+"[0m");
            boolean last = (i++ == count-1); 
            if (!last || newline) {
                System.out.print("\n");
            }
        }
    }
    
}
