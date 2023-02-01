public class Constants {
    public final static String GITHUB_USERNAME = "matthewgrosman";

    // Repository where the README.md containing the last commit tracker element is located.
    public final static String GITHUB_README_REPOSITORY_NAME = "matthewgrosman/matthewgrosman";
    public final static String README_FILENAME = "README.md";

    // Message that is displayed as commit message for bot automatic pushes to GitHub
    // with README changes.
    public final static String COMMIT_MESSAGE = "[COMMIT-BOT] Updated last commit tracker with latest commit repository.";

    // Used to locate (and then replace) the last commit tracker element in the markdown.
    public final static String README_LAST_COMMIT_REGEX = "(!\\[last-commit\\]\\(https:\\/\\/img\\.shields\\.io\\/github\\/last-commit\\/matthewgrosman\\/)([\\w-]+)(.svg\\))";
}
