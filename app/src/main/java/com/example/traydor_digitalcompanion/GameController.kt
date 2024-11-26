package com.example.traydor_digitalcompanion

import android.util.Log

object GameController {
    private var initialPlayerCount: Int = 0
    private var currentRound: Int = 1
    private var lastRoundPlayed = false

    // Method to set the initial player count based on user input
    fun setInitialPlayerCount(playerCount: Int) {
        initialPlayerCount = playerCount
        Log.d("GameController", "Initial player count set to: $initialPlayerCount")
    }

    // Method to get the current player count
    fun getInitialPlayerCount(): Int {
        return initialPlayerCount
    }

    // Method to decrement the player count after each round when a player is eliminated
    fun playerVotedOut() {
        if (initialPlayerCount > 0) {
            initialPlayerCount--
            Log.d("GameController", "Player voted out. Remaining players: $initialPlayerCount")
        }
    }

    // Method to check if the current game is in the final round (i.e., 5 players remain)
    fun isFinalRound(): Boolean {
        val isFinal = initialPlayerCount == 5 && !lastRoundPlayed
        Log.d("GameController", "Checking if it's the final round: $isFinal")
        return isFinal
    }

    // Method to check if the game should end after the final round
    fun isGameEnd(): Boolean {
        // Game ends if there are 5 players and the final round was already played
        val isEnd = initialPlayerCount <= 5 && lastRoundPlayed
        Log.d("GameController", "Checking if the game should end: $isEnd")
        return isEnd
    }

    // Method to increment the round count
    fun incrementRound() {
        currentRound++
        Log.d("GameController", "Round incremented. Current round: $currentRound")
    }

    // Method to mark that the final round was played
    fun markFinalRoundPlayed() {
        lastRoundPlayed = true
        Log.d("GameController", "Final round marked as played.")
    }

    // Method to determine if reshuffle prompt should be shown (i.e., not the first round)
    fun shouldShowReshufflePrompt(): Boolean {
        val shouldShow = currentRound > 1
        Log.d("GameController", "Should show reshuffle prompt: $shouldShow")
        return shouldShow
    }

    // Method to reset all game values
    fun resetGameValues() {
        initialPlayerCount = 0
        currentRound = 1
        lastRoundPlayed = false
    }
}
