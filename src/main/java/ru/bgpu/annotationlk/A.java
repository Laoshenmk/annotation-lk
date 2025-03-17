package ru.bgpu.annotationlk;

public class A {

    @AppConfig
    static private String host;

    @AppConfig
    static public Integer port;

    @AppConfig(defValue="my-name")
    static private String serverName;

    @Override
    public String toString() {
        return "A{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", name=" + serverName +
                '}';
    }
}