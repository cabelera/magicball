package com.example.guilherme.conectesp;

/**
 * Created by guilherme on 04/04/17.
 */

public interface JoystickMovedListener {

    public void OnMoved(int pan, int tilt);

    public void OnReleased();

}
