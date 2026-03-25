package com.metahrms.employee_management.dto.response.task.taskstatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.metahrms.employee_management.dto.response.task.task.TaskResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Task statistics for dashboard")
public class TaskStatsResponse {

    @Schema(description = "Total tasks", example = "156")
    Long totalTasks;

    @Schema(description = "Active tasks", example = "98")
    Long activeTasks;

    @Schema(description = "Completed tasks", example = "45")
    Long completedTasks;

    @Schema(description = "Overdue tasks", example = "13")
    Long overdueTasks;

    @Schema(description = "Tasks by status")
    Map<String, Long> tasksByStatus;

    @Schema(description = "Tasks by priority")
    Map<String, Long> tasksByPriority;

    @Schema(description = "Tasks by department")
    List<DepartmentTaskStats> tasksByDepartment;

    @Schema(description = "Upcoming deadline tasks (next 7 days)")
    List<TaskResponse> upcomingTasks;

    @Schema(description = "My urgent tasks")
    List<TaskResponse> myUrgentTasks;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long totalTasks;
        private Long activeTasks;
        private Long completedTasks;
        private Long overdueTasks;
        private Map<String, Long> tasksByStatus;
        private Map<String, Long> tasksByPriority;
        private List<DepartmentTaskStats> tasksByDepartment;
        private List<TaskResponse> upcomingTasks;
        private List<TaskResponse> myUrgentTasks;

        public Builder totalTasks(Long totalTasks) { this.totalTasks = totalTasks; return this; }
        public Builder activeTasks(Long activeTasks) { this.activeTasks = activeTasks; return this; }
        public Builder completedTasks(Long completedTasks) { this.completedTasks = completedTasks; return this; }
        public Builder overdueTasks(Long overdueTasks) { this.overdueTasks = overdueTasks; return this; }
        public Builder tasksByStatus(Map<String, Long> tasksByStatus) { this.tasksByStatus = tasksByStatus; return this; }
        public Builder tasksByPriority(Map<String, Long> tasksByPriority) { this.tasksByPriority = tasksByPriority; return this; }
        public Builder tasksByDepartment(List<DepartmentTaskStats> tasksByDepartment) { this.tasksByDepartment = tasksByDepartment; return this; }
        public Builder upcomingTasks(List<TaskResponse> upcomingTasks) { this.upcomingTasks = upcomingTasks; return this; }
        public Builder myUrgentTasks(List<TaskResponse> myUrgentTasks) { this.myUrgentTasks = myUrgentTasks; return this; }

        public TaskStatsResponse build() {
            TaskStatsResponse response = new TaskStatsResponse();
            response.totalTasks = this.totalTasks;
            response.activeTasks = this.activeTasks;
            response.completedTasks = this.completedTasks;
            response.overdueTasks = this.overdueTasks;
            response.tasksByStatus = this.tasksByStatus;
            response.tasksByPriority = this.tasksByPriority;
            response.tasksByDepartment = this.tasksByDepartment;
            response.upcomingTasks = this.upcomingTasks;
            response.myUrgentTasks = this.myUrgentTasks;
            return response;
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DepartmentTaskStats {
        @Schema(description = "Department ID", example = "1")
        Integer departmentId;

        @Schema(description = "Department name", example = "IT Department")
        String departmentName;

        @Schema(description = "Total tasks", example = "45")
        Long totalTasks;

        @Schema(description = "Active tasks", example = "30")
        Long activeTasks;

        @Schema(description = "Completed tasks", example = "15")
        Long completedTasks;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Integer departmentId;
            private String departmentName;
            private Long totalTasks;
            private Long activeTasks;
            private Long completedTasks;

            public Builder departmentId(Integer departmentId) { this.departmentId = departmentId; return this; }
            public Builder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
            public Builder totalTasks(Long totalTasks) { this.totalTasks = totalTasks; return this; }
            public Builder activeTasks(Long activeTasks) { this.activeTasks = activeTasks; return this; }
            public Builder completedTasks(Long completedTasks) { this.completedTasks = completedTasks; return this; }

            public DepartmentTaskStats build() {
                DepartmentTaskStats stats = new DepartmentTaskStats();
                stats.departmentId = this.departmentId;
                stats.departmentName = this.departmentName;
                stats.totalTasks = this.totalTasks;
                stats.activeTasks = this.activeTasks;
                stats.completedTasks = this.completedTasks;
                return stats;
            }
        }
    }
}
