package net.lazybd.vpn.util;

public class System
{
    static {
        java.lang.System.loadLibrary("system");
    }

    public static native int sendfd(int fd, String sock);
    public static native void jniclose(int fd);
}
