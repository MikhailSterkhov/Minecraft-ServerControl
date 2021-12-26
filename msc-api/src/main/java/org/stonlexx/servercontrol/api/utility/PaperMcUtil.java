package org.stonlexx.servercontrol.api.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

@UtilityClass
public class PaperMcUtil {

    public final String PAPER_API_URL = "https://papermc.io/api";

    public final String GET_PROJECTS_URL = "/v2/projects";
    public final String GET_PROJECT_VERSIONS_URL = "/v2/projects/%s";
    public final String GET_PROJECT_BUILDS_URL = "/v2/projects/%s/versions/%s";


    /**
     * Получить список билдов и проектов
     * от сообщества Paper
     */
    public GetPaperProjects getPaperProjects() {

        return getPaperJson((PAPER_API_URL + GET_PROJECTS_URL),
                GetPaperProjects.class);
    }

    /**
     * Получить список версий и билдов
     * определенного проекта от Paper
     *
     * @param projectName - имя проекта
     */
    public GetPaperVersionsProject getPaperVersions(@NonNull String projectName) {

        return getPaperJson(String.format(PAPER_API_URL + GET_PROJECT_VERSIONS_URL,
                projectName), GetPaperVersionsProject.class);
    }

    /**
     * Получить список билдов определенного
     * проекта от Paper
     *
     * @param projectName    - имя проекта
     * @param projectVersion - версия проекта
     */
    public GetPaperBuildsProject getPaperBuilds(@NonNull String projectName,
                                                @NonNull String projectVersion) {

        return getPaperJson(String.format(PAPER_API_URL + GET_PROJECT_BUILDS_URL,
                projectName, projectVersion), GetPaperBuildsProject.class);
    }


    protected <T> T getPaperJson(@NonNull String parsedUrl,
                                 @NonNull Class<T> jsonClass) {

        try {
            URLConnection urlConnection = new URL(parsedUrl).openConnection();
            urlConnection.connect();

            try (InputStream inputStream = urlConnection.getInputStream();
                 Scanner scanner = new Scanner(inputStream)) {

                return JsonUtil.fromJson(scanner.nextLine(), jsonClass);
            }

        } catch (Exception ignored) {

            return null;
        }
    }


    public class GetPaperProjects {
        public String[] projects;
    }

    public class GetPaperVersionsProject {
        public String project_id;
        public String project_name;

        public String[] version_groups;
        public String[] versions;
    }

    public class GetPaperBuildsProject {
        public String project_id;
        public String project_name;
        public String version;

        public int[] builds;
    }

}
