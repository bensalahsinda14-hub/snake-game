package com.devops.game;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameEngineTest {
    
    private GameEngine gameEngine;

    @Before
    public void setUp() {
        gameEngine = new GameEngine();
    }

    @Test
    public void testGetVersion() {
        assertEquals("1.0.0", gameEngine.getVersion());
    }

    @Test
    public void testInitialHighScore() {
        assertEquals(0, gameEngine.getHighScore());
    }

    @Test
    public void testSetHighScore() {
        gameEngine.setHighScore(100);
        assertEquals(100, gameEngine.getHighScore());
    }

    @Test
    public void testIsValidScore() {
        assertTrue(gameEngine.isValidScore(100));
        assertFalse(gameEngine.isValidScore(-10));
    }

    @Test
    public void testCalculateLevel() {
        assertEquals(1, gameEngine.calculateLevel(0));
        assertEquals(2, gameEngine.calculateLevel(10));
        assertEquals(0, gameEngine.calculateLevel(-10));
    }
}
