import os
import re

backend_dir = r"E:\SBVIA-AppWeb\backend\src\main\java\com\sbvia\backend"

classes_needing_no_args = [
    "AuthResponse", "DrivingResultDTO", "FeedbackIaResponse", "ScenarioDTO",
    "AccessHistory", "AiEvaluation", "AiModel", "Decision", "DifficultyLevel",
    "DrivingBehavior", "EventType", "Feedback", "Infraction", "MetricType",
    "PerformanceMetric", "RoadEvent", "RoadType", "Role", "Scenario",
    "SeverityLevel", "Simulation"
]

for root, dirs, files in os.walk(backend_dir):
    for file in files:
        if file.endswith(".java"):
            filepath = os.path.join(root, file)
            with open(filepath, "r", encoding="utf-8") as f:
                content = f.read()
            
            original_content = content
            
            # Remove the injected constructor
            pattern = r"\s+/\*\* Default constructor for \w+\. \*/\n\s+public \w+\(\) \{\}\n"
            content = re.sub(pattern, "", content)
            
            # Restore @NoArgsConstructor if needed
            class_name = file.replace(".java", "")
            if class_name in classes_needing_no_args and "@NoArgsConstructor" not in content:
                # insert it before public class
                content = re.sub(r"(public class " + class_name + ")", r"@lombok.NoArgsConstructor\n\1", content)
                
            if original_content != content:
                with open(filepath, "w", encoding="utf-8") as f:
                    f.write(content)
                print(f"Fixed {file}")
