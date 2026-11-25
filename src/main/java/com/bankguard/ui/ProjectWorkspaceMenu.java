
package com.bankguard.ui;

// ============================================================
// BANKGUARD v1.0 - GRC PROJECT WORKSPACE
// ============================================================

// I use this menu to manage practical GRC projects.
// Each project can contain tasks and track my progress.

import com.bankguard.model.GrcProject;
import com.bankguard.model.ProjectTask;
import com.bankguard.service.GrcProjectService;
import com.bankguard.service.ProjectTaskService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProjectWorkspaceMenu {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final Scanner input;
    private final GrcProjectService projectService;
    private final ProjectTaskService taskService;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ProjectWorkspaceMenu(Scanner input) {

        // I reuse the main menu's Scanner.
        // This prevents multiple Scanners from competing
        // for input from the same keyboard.

        this.input = input;

        // I initialise my project management services.
        projectService = new GrcProjectService();
        taskService = new ProjectTaskService();
    }

    // ========================================================
    // 3. STARTING MY PROJECT WORKSPACE
    // ========================================================

    public void start() {

        // ========== DECLARATIONS ==========

        boolean running;
        String choice;

        // ========== INITIALISATION ==========

        running = true;

        // ========== PROCESSING ==========

        while (running) {

            System.out.println(
                    "\n========== GRC PROJECT WORKSPACE ==========");

            System.out.println("1. Create a new GRC project");
            System.out.println("2. View all GRC projects");
            System.out.println("3. Add a task to a project");
            System.out.println("4. View project tasks");
            System.out.println("5. Update task status");
            System.out.println("6. View project progress");
            System.out.println("7. Return to main menu");

            System.out.print("\nChoose an option: ");
            choice = input.nextLine().trim();

            // ========== MENU SELECTION ==========

            switch (choice) {

                case "1" -> createProject();

                case "2" -> viewProjects();

                case "3" -> addTask();

                case "4" -> viewTasks();

                case "5" -> updateTaskStatus();

                case "6" -> viewProgress();

                case "7" -> {

                    running = false;

                    System.out.println(
                            "Returning to BankGuard main menu...");
                }

                default -> System.out.println(
                        "Invalid option. Please try again.");
            }
        }

        // I do not close Scanner here.
        // The main BankGuard menu still needs it.
    }

    // ========================================================
    // 4. CREATING A NEW GRC PROJECT
    // ========================================================

    private void createProject() {

        // ========== DECLARATIONS ==========

        String name;
        String description;
        String category;
        String result;

        // ========== PROCESSING ==========

        System.out.println(
                "\n========== CREATE GRC PROJECT ==========");

        System.out.print("Project name: ");
        name = input.nextLine();

        System.out.print("Project description: ");
        description = input.nextLine();

        System.out.println("\nExamples of project categories:");
        System.out.println("- IT Risk Assessment");
        System.out.println("- PCI DSS Compliance");
        System.out.println("- Control Testing");
        System.out.println("- Third-Party Risk");
        System.out.println("- AI Governance");

        System.out.print("\nProject category: ");
        category = input.nextLine();

        // I ask my service to validate the information
        // before saving the project.
        result = projectService.createProject(
                name,
                description,
                category
        );

        // ========== OUTPUT ==========

        System.out.println(result);
    }

    // ========================================================
    // 5. VIEWING ALL MY GRC PROJECTS
    // ========================================================

    private void viewProjects() {

        // ========== DECLARATIONS ==========

        List<GrcProject> projects;

        // ========== PROCESSING ==========

        try {

            projects = projectService.getAllProjects();

            System.out.println(
                    "\n========== MY GRC PROJECTS ==========");

            if (projects.isEmpty()) {

                System.out.println(
                        "No GRC projects registered yet.");

                return;
            }

            // ========== OUTPUT ==========

            for (GrcProject project : projects) {

                System.out.println(
                        "\n--------------------------------------");

                System.out.println(
                        "Project ID: P-" + project.getProjectId());

                System.out.println(
                        "Project: " + project.getProjectName());

                System.out.println(
                        "Description: " + project.getDescription());

                System.out.println(
                        "Category: " + project.getCategory());

                System.out.println(
                        "Status: " + project.getStatus());

                System.out.println(
                        "Created (UTC): " + project.getCreatedAt());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Error viewing projects: " + e.getMessage());
        }
    }

    // ========================================================
    // 6. ADDING A TASK TO AN EXISTING PROJECT
    // ========================================================

    private void addTask() {

        // ========== DECLARATIONS ==========

        int projectId;
        String taskName;
        String description;
        String result;

        // ========== PROCESSING ==========

        viewProjects();

        try {

            System.out.print(
                    "\nEnter the project ID number: ");

            projectId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.print("Task name: ");
            taskName = input.nextLine();

            System.out.print("Task description: ");
            description = input.nextLine();

            // I assign the task to the selected project.
            // My service checks whether that project exists.
            result = taskService.createTask(
                    projectId,
                    taskName,
                    description
            );

            // ========== OUTPUT ==========

            System.out.println(result);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid project ID.");
        }
    }

    // ========================================================
    // 7. VIEWING TASKS INSIDE A PROJECT
    // ========================================================

    private void viewTasks() {

        // ========== DECLARATIONS ==========

        int projectId;

        // ========== PROCESSING ==========

        viewProjects();

        try {

            System.out.print(
                    "\nEnter the project ID number: ");

            projectId = Integer.parseInt(
                    input.nextLine().trim());

            // I display the work assigned to this project.
            displayTasks(projectId);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid project ID.");

        } catch (SQLException | IOException
                 | IllegalArgumentException e) {

            System.out.println(
                    "Error viewing tasks: " + e.getMessage());
        }
    }

    // ========================================================
    // 8. DISPLAYING TASKS FOR A SELECTED PROJECT
    // ========================================================

    private void displayTasks(int projectId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        List<ProjectTask> tasks;

        // ========== PROCESSING ==========

        // I check that the project exists before
        // displaying its tasks.
        if (!projectService.projectExists(projectId)) {

            System.out.println("Project not found.");
            return;
        }

        tasks = taskService.getProjectTasks(projectId);

        System.out.println(
                "\n========== PROJECT TASKS ==========");

        System.out.println("Project ID: P-" + projectId);

        if (tasks.isEmpty()) {

            System.out.println(
                    "No tasks have been added to this project.");

            return;
        }

        // ========== OUTPUT ==========

        for (ProjectTask task : tasks) {

            System.out.println(
                    "\n--------------------------------------");

            System.out.println(
                    "Task ID: T-" + task.getTaskId());

            System.out.println(
                    "Task: " + task.getTaskName());

            System.out.println(
                    "Description: " + task.getDescription());

            System.out.println(
                    "Status: " + task.getStatus());
        }
    }

    // ========================================================
    // 9. UPDATING PROJECT TASK STATUS
    // ========================================================

    private void updateTaskStatus() {

        // ========== DECLARATIONS ==========

        int projectId;
        int taskId;
        String statusChoice;
        String newStatus;
        String result;

        // ========== PROCESSING ==========

        viewProjects();

        try {

            System.out.print(
                    "\nEnter the project ID number: ");

            projectId = Integer.parseInt(
                    input.nextLine().trim());

            if (!projectService.projectExists(projectId)) {

                System.out.println("Project not found.");
                return;
            }

            // I show the tasks so I can choose
            // the correct task to update.
            displayTasks(projectId);

            System.out.print(
                    "\nEnter the task ID number: ");

            taskId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.println("\nAvailable Task Statuses:");
            System.out.println("1. Not Started");
            System.out.println("2. In Progress");
            System.out.println("3. Completed");

            System.out.print("\nChoose the new status: ");
            statusChoice = input.nextLine().trim();

            // ========== VALIDATION ==========

            // I convert the menu selection into
            // a status supported by my database.
            newStatus = switch (statusChoice) {

                case "1" -> "Not Started";
                case "2" -> "In Progress";
                case "3" -> "Completed";
                default -> null;
            };

            if (newStatus == null) {

                System.out.println(
                        "Invalid task status selection.");

                return;
            }

            // ========== PROCESSING ==========

            result = taskService.changeTaskStatus(
                    projectId,
                    taskId,
                    newStatus
            );

            // ========== OUTPUT ==========

            System.out.println(result);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter valid ID numbers.");

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Error updating task: " + e.getMessage());
        }
    }

    // ========================================================
    // 10. VIEWING PROJECT COMPLETION PROGRESS
    // ========================================================

    private void viewProgress() {

        // ========== DECLARATIONS ==========

        int projectId;
        int progress;

        // ========== PROCESSING ==========

        viewProjects();

        try {

            System.out.print(
                    "\nEnter the project ID number: ");

            projectId = Integer.parseInt(
                    input.nextLine().trim());

            // I calculate how many tasks are completed.
            progress = taskService.getProjectProgress(
                    projectId
            );

            // ========== OUTPUT ==========

            System.out.println(
                    "\n========== PROJECT PROGRESS ==========");

            System.out.println(
                    "Project ID: P-" + projectId);

            System.out.println(
                    "Tasks completed: " + progress + "%");

            // I remind myself that finishing tasks
            // does not prove the quality of my assessment.
            System.out.println(
                    "Note: Progress measures completed tasks, "
                    + "not assessment quality.");

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid project ID.");

        } catch (SQLException | IOException
                 | IllegalArgumentException e) {

            System.out.println(
                    "Error viewing progress: " + e.getMessage());
        }
    }
}
