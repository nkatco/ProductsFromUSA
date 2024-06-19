package com.example.productsfromusa.DAO;

import com.example.productsfromusa.models.StateData;
import com.example.productsfromusa.models.User;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StateDataDAO {

    public final Map<String, StateData> statesData = new HashMap<>();

    public StateData getStateDataByUserId(String id) {
        return statesData.get(id);
    }
    public void setStateData(User user, String state, Object data) {
        if(statesData.get(state + "_" + user.getId()) == null) {
            StateData stateData = new StateData();
            stateData.setUser_id(user.getId());
            stateData.setState(state);
            stateData.setData(data);
            statesData.put(state + "_" + user.getId(), stateData);
        } else {
            statesData.remove(state + "_" + user.getId());
            StateData stateData = new StateData();
            stateData.setUser_id(user.getId());
            stateData.setState(state);
            stateData.setData(data);
            statesData.put(state + "_" + user.getId(), stateData);
        }
    }

    public void removeStateDataByUserId(String id) {
        List<StateData> matchingData = new ArrayList<>();

        for (StateData data : statesData.values()) {
            if (data.getUser_id().equals(id)) {
                matchingData.add(data);
            }
        }
        for(StateData data : matchingData) {
            statesData.remove(data.getUser_id());
        }
    }
}
