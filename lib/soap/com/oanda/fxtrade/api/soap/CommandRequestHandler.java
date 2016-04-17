/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;



/**
 * @author andry
 *
 */
public class CommandRequestHandler implements ProtocolRequestHandler {

	private ProtocolCommandsFactory commandsFactory = null;

	public CommandRequestHandler(Session session) {
		commandsFactory = new ProtocolCommandsFactory(session);
	}

    public String[] handle(String command, String[] arguments) {

        ProtocolCommandExecutor commandExecutor = commandsFactory.create(command);
        String[] executionResults = commandExecutor.execute(arguments);

        return executionResults;
    }

	public void shutDown() {
	}
}
