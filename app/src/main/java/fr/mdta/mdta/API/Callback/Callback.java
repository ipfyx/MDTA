package fr.mdta.mdta.API.Callback;

import java.io.Serializable;

/**
 * Created by baptiste on 07/11/17.
 */

public interface Callback extends Serializable {
    void OnErrorHappended();

    void OnErrorHappended(String error);

    void OnTaskCompleted(Object object);
}
