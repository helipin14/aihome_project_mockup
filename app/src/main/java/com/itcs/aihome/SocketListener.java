package com.itcs.aihome;

import android.app.Application;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketListener extends Application {
    private Socket socket;
    {
        try {
            socket = IO.socket(config.server_url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
