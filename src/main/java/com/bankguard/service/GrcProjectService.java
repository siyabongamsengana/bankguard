
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.0 - GRC PROJECT SERVICE
// ============================================================

// I use this service to apply my business rules
// before creating or retrieving GRC projects.

import com.bankguard.model.GrcProject;
import com.bankguard.repository.GrcProjectRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GrcProjectService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use my repository to communicate with SQLite.
    private final GrcProjectRepository projectRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public GrcProjectService() {

        // I prepare my repository when the service starts.
        projectRepository = new GrcProjectRepository();
    }

    // ========================================================
    // 3. CREATING A NEW GRC PROJECT
    // ========================================================

    public String createProject(
            String projectName,
            String description,
            String category) {

        // ========== VALIDATION ==========

        // I require a meaningful project name.
        if (projectName == null || projectName.isBlank()) {

            return "Project name is required.";
        }

        // I require a description of the work.
        if (description == null || description.isBlank()) {

            return "Project description is required.";
        }

        // I require a category to classify my GRC work.
        if (category == null || category.isBlank()) {

            return "Project category is required.";
        }

        // I limit field lengths to keep records manageable.
        if (projectName.trim().length() > 150) {

            return "Project name cannot exceed 150 characters.";
        }

        if (description.trim().length() > 2000) {

            return "Project description cannot exceed "
                    + "2000 characters.";
        }

        if (category.trim().length() > 100) {

            return "Project category cannot exceed "
                    + "100 characters.";
        }

        // ========== PROCESSING ==========

        try {

            // I send the validated information
            // to my project repository.
            int rowsInserted = projectRepository.createProject(
                    projectName.trim(),
                    description.trim(),
                    category.trim()
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "GRC project created successfully!";
            }

            return "Project was not created.";

        } catch (SQLException | IOException e) {

            return "Project creation failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING ALL MY GRC PROJECTS
    // ========================================================

    public List<GrcProject> getAllProjects()
            throws SQLException, IOException {

        // I retrieve the projects saved in my database.
        return projectRepository.findAllProjects();
    }

    // ========================================================
    // 5. CHECKING WHETHER A PROJECT EXISTS
    // ========================================================

    public boolean projectExists(int projectId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (projectId <= 0) {

            return false;
        }

        // ========== OUTPUT ==========

        return projectRepository.projectExists(projectId);
    }
}

