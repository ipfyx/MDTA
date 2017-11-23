package fr.mdta.mdta.API.Callback;

/**
 * Created by baptiste on 07/11/17.
 */

public interface Callback {
    void OnErrorHappended();

    void OnErrorHappended(String error);

    void OnTaskCompleted(Object object);
}
