import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TimeTracker {

    private static String currentTask = null;
    private static Instant startTime = null;
    private static final Map<String, Duration> taskLog = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Simple Java Time Tracker ===");
        System.out.println("Commands: start <task_name> | stop | report | exit");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                stopCurrentTask();
                printReport();
                System.out.println("Exiting Time Tracker. Goodbye!");
                break;
            } else if (input.equalsIgnoreCase("stop")) {
                stopCurrentTask();
            } else if (input.equalsIgnoreCase("report")) {
                printReport();
            } else if (input.toLowerCase().startsWith("start ")) {
                String taskName = input.substring(6).trim();
                if (taskName.isEmpty()) {
                    System.out.println("Please specify a task name (e.g., 'start Coding').");
                } else {
                    startTask(taskName);
                }
            } else {
                System.out.println("Unknown command. Use: start <task>, stop, report, or exit.");
            }
        }
        scanner.close();
    }

    private static void startTask(String taskName) {
        if (currentTask != null) {
            stopCurrentTask(); // Auto-switch: stop the active task first
        }
        currentTask = taskName;
        startTime = Instant.now();
        System.out.println("Started tracking: '" + currentTask + "'");
    }

    private static void stopCurrentTask() {
        if (currentTask == null || startTime == null) {
            System.out.println("No task is currently running.");
            return;
        }

        Duration elapsed = Duration.between(startTime, Instant.now());
        taskLog.put(currentTask, taskLog.getOrDefault(currentTask, Duration.ZERO).plus(elapsed));

        System.out.printf("Stopped '%s' (+%s)%n", currentTask, formatDuration(elapsed));
        currentTask = null;
        startTime = null;
    }

    private static void printReport() {
        System.out.println("\n--- Time Tracking Report ---");
        if (taskLog.isEmpty() && currentTask == null) {
            System.out.println("No time logged yet.");
            return;
        }

        for (Map.Entry<String, Duration> entry : taskLog.entrySet()) {
            Duration total = entry.getValue();
            // Include live time if the task is currently active
            if (entry.getKey().equals(currentTask) && startTime != null) {
                total = total.plus(Duration.between(startTime, Instant.now()));
            }
            System.out.printf("%-20s : %s%n", entry.getKey(), formatDuration(total));
        }

        // Handle case where current task hasn't been logged to the map yet
        if (currentTask != null && !taskLog.containsKey(currentTask)) {
            Duration live = Duration.between(startTime, Instant.now());
            System.out.printf("%-20s : %s (active)%n", currentTask, formatDuration(live));
        }
        System.out.println("----------------------------");
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}
