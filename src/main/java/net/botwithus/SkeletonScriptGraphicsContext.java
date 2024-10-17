package net.botwithus;

import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

import static net.botwithus.rs3.imgui.ImGui.ProgressBar;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {

    private SkeletonScript script;
    private long startTime;  // Time when the bot starts (in milliseconds)
    private long totalPausedTime; // Total time spent in paused state
    private boolean running; // To track whether the timer is running or paused
    private final int startingXP;
    private int currentXP;

    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SkeletonScript script) {
        super(scriptConsole);
        this.script = script;
        this.running = false; // Timer starts in a paused state
        this.startingXP = (Skills.NECROMANCY).getSkill().getExperience(); // Store the starting XP for Necromancy
        this.totalPausedTime = 0; // No time has been paused initially
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("My script", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                // Settings tab
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Welcome to my script!");
                    ImGui.Text("My script's state is: " + script.getBotState());

                    // Start/Stop buttons for controlling the bot state
                    if (ImGui.Button("Start")) {
                        script.setBotState(SkeletonScript.BotState.SKILLING);
                        startTimer(); // Start the timer when the bot starts skilling
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
                        stopTimer(); // Stop the timer when the bot goes idle
                    }

                    ImGui.Separator();

                    // Display session info (XP, Level, Timer, etc.)
                    currentXP = (Skills.NECROMANCY).getSkill().getExperience();
                         int xpGained = currentXP - startingXP;
                        int currentLevel = (Skills.NECROMANCY).getLevel(); // Replace with your desired skill

                        // Get the total elapsed time (calculate it only if the timer is running)
                        long elapsedTime = running ? System.currentTimeMillis() - startTime + totalPausedTime : totalPausedTime;

                        // Timer formatting
                        int hours = (int) (elapsedTime / 1000) / 3600;
                        int minutes = (int) ((elapsedTime / 1000) % 3600) / 60;
                        int seconds = (int) (elapsedTime / 1000) % 60;
                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);


                        // Get the total elapsed time (calculate it only if the timer is running)
                        long elapsedTimeMillis = running ? System.currentTimeMillis() - startTime + totalPausedTime : totalPausedTime;
                        long elapsedTimeSeconds = elapsedTimeMillis / 1000; // Convert milliseconds to seconds


                        // Correct XP per hour calculation
                        double xpPerHour = 0;
                        if (elapsedTimeSeconds > 0) {
                            xpPerHour = (xpGained / (double) elapsedTimeSeconds) * 3600; // XP/hr calculation
                        }
                        // Display session info
                        ImGui.Text("Current XP: " + currentXP);
                        ImGui.Text("Current Level: " + currentLevel);
                        ImGui.Text("XP Gained: " + xpGained);
                        ImGui.Text("Random Events: " + script.randomEvents);
                        ImGui.Text(String.format("XP/hr: %.2f", xpPerHour)); // Display XP/hr
                        ImGui.Separator();
                        ImGui.Text("Timer: " + timeString); // Show formatted timer


                    // Progress bar for XP to next level (corrected calculation)
                    int xpToNextLevel = Skills.NECROMANCY.getExperienceToNextLevel(); // XP needed to reach the next level
                    int xpAtStartOfLevel = (Skills.NECROMANCY).getExperienceAt(currentLevel); // XP at the start of the current level
                    float progress = (float) ((currentXP - xpAtStartOfLevel) / (xpToNextLevel - xpAtStartOfLevel) * 100); // Calculate progress as percentage


                        // Progress bar for XP to next level
                        float progressBarWidth = 300f; // Define a width for the progress bar
                        float progressBarHeight = 25f; // Define a height for the progress bar
                        ProgressBar(currentXP + "/" + xpToNextLevel + " - " + (progress), progress, progressBarWidth, progressBarHeight);


                    ImGui.EndTabItem(); // End of the "Settings" tab
                }

                // Another tab example
                if (ImGui.BeginTabItem("Other", ImGuiWindowFlag.None.getValue())) {
                    script.setSomeBool(ImGui.Checkbox("Are you cool?", script.isSomeBool()));
                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }
    }

    @Override
    public void drawOverlay() {
        // Overlay is left empty as the session info is now inside the settings tab
        super.drawOverlay();
    }

    // Start the timer (also handles resuming after being paused)
    private void startTimer() {
        if (!running) {
            startTime = System.currentTimeMillis(); // Capture the current time when the bot starts skilling
            running = true;
        }
    }

    // Pause the timer (accumulates paused time)
    private void stopTimer() {
        if (running) {
            totalPausedTime += System.currentTimeMillis() - startTime; // Accumulate the time spent before pausing
            running = false;
        }
    }

}
