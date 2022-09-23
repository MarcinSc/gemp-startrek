package com.gempukku.startrek.common;

import com.artemis.BaseSystem;
import com.gempukku.libgdx.lib.artemis.property.PropertySystem;

public class ConnectionParamSystem extends BaseSystem {
    private PropertySystem propertySystem;

    public String getClientVersion() {
        return propertySystem.getProperty("client.version");
    }

    public String getServerHost() {
        return propertySystem.getProperty("server.host");
    }

    public int getServerPort() {
        return Integer.parseInt(propertySystem.getProperty("server.port"));
    }

    public String getHallUrl() {
        return propertySystem.getProperty("server.hall.url");
    }

    @Override
    protected void processSystem() {

    }

    public String getLoginUrl() {
        return "http://" + getServerHost() + ":" + getServerPort() + propertySystem.getProperty("server.login.url");
    }

    public String getRegisterUrl() {
        return "http://" + getServerHost() + ":" + getServerPort() + propertySystem.getProperty("server.register.url");
    }

    public String getFileListUrl() {
        return "http://" + getServerHost() + ":" + getServerPort() + propertySystem.getProperty("server.file.list.url");
    }

    public String getHallContentUrl() {
        return "http://" + getServerHost() + ":" + getServerPort() + propertySystem.getProperty("server.hall.content.url");
    }
}
