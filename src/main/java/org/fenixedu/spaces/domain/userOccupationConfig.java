package org.fenixedu.spaces.domain;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.occupation.config.OccupationConfig;

import com.google.gson.JsonElement;

public class userOccupationConfig extends userOccupationConfig_Base {

    public userOccupationConfig() {
        super();
    }

    public userOccupationConfig(User user, OccupationConfig occupationConfig) {
        super();
        setUser(user);
        setConfig(occupationConfig);
    }

    public JsonElement jsonIntervals;

    public JsonElement getJsonIntervals() {
        return jsonIntervals;
    }

    public void updateConfig(OccupationConfig config) {

    }

}
