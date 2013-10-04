/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

import net.epsilony.tb.py4j.Example;
import py4j.GatewayServer;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Py4jServer {

    public static void main(String[] args) {
        GatewayServer server = new GatewayServer(new Example());
        server.start();
    }
}
