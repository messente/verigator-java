package com.messente.verigator;

public class BuildVersion {
    public static String getBuildVersion() {
        return BuildVersion.class.getPackage().getImplementationVersion();
    }
}
