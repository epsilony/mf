/* (c) Copyright by Man YUAN */
package net.epsilony.mf.util;

import net.epsilony.tb.py4j.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import py4j.GatewayServer;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class Py4jServer {

    public static final Logger logger = LoggerFactory.getLogger(Py4jServer.class);

    public static void main(String[] args) {
        GatewayServer server = new GatewayServer(new Example());
        server.start();
        logger.info("started");
    }
}
