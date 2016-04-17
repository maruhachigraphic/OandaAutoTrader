/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class DefaultMessageStream implements MessageStream {

	protected Connection connection = null;
	private  PrintWriter out = null;
	private  BufferedReader in = null;

	public DefaultMessageStream(Connection connection) {
        this.connection = connection;
        try {
            out = connection.makeWriter();
            in  = connection.makeReader();
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
	}

	public void close() {
		try {
			connection.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readNext() throws IOException {
        StringBuilder request = new StringBuilder();
        String buffer  = "";
        while(true) {
       		buffer = in.readLine();
            if((buffer != null) && (!buffer.isEmpty())) {
                request.append(buffer);
            }
            else {
                break;
            }
        }

		return request.toString();
	}

	public void write(String response) {
        if(out != null) {
       		out.println(response);
        }
	}
}
