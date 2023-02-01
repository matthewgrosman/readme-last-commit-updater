import org.apache.commons.io.IOUtils;
import org.kohsuke.github.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class LastCommitUpdater {
    /**
     * Returns an PagedIterable containing all of the Github users contribution activity in order
     * from most recent to least recent.
     *
     * @param github    A GitHub object that lets us interact with the GitHub API Java wrapper.
     * @return          A PagedIterable<GHEventInfo> object containing all of the Github users
     *                  contribution activity in order from most recent to least recent.
     * @throws IOException  Can throw IOException due to .getMyself() function call.
     */
    private static PagedIterable<GHEventInfo> getUserEvents(GitHub github) throws IOException {
        return github.getMyself().listEvents();
    }

    /**
     * Gets the latest commit within a requested Github repository.
     *
     * @param repository    The repository that we would like to find the latest commit of.
     * @return              The latest commit within the requested Github repository.
     */
    private static GHCommit getLatestCommit(GHRepository repository) {
        PagedIterable<GHCommit> commits = repository.listCommits();
        return commits.iterator().next();
    }

    /**
     * We only want to update our README's last commit status if the type of action
     * logged by GitHub was a code push by the GitHub user. I am using this last
     * commit status to track my last "significant" code commit, but feel free to
     * adjust this to show for any kind of action.
     *
     * In this case, the action is valid and we will return true (meaning: the last
     * commit element gets updated) if  the action was a git push, it came from my
     * username, and it wasn't a commit generated from this bot (this would lead to
     * using the bots' commits as my last commit). If any of those conditions aren't
     * met, we return false and the last commit status isn't updated as a result.
     *
     * @param event         A GHEventInfo containing all info pertaining to a GHEvent.
     * @return              A boolean denoting if the action is a valid push that will
     *                      trigger updating the last commit element.
     * @throws IOException  Can throw IOException from .getActorLogin() and .getRepository()
     *                      function calls.
     */
    private static boolean checkActionIsValidPush(GHEventInfo event) throws IOException {
        String actionType = event.getType().name();
        String actionUser = event.getActorLogin();
        String latestCommitMessage = getLatestCommit(event.getRepository()).getCommitShortInfo().getMessage();

        return actionType.equals(GHEvent.PUSH.name())
                && actionUser.equals(Constants.GITHUB_USERNAME)
                && !latestCommitMessage.equals(Constants.COMMIT_MESSAGE);
    }

    /**
     * Update the README.md markdown to point the last commit element to the repository
     * from my profile with the most recent commit.
     *
     * @param readmeString      A String containing the current markdown for the README.md
     * @param repositoryName    A String containing the name of repository from my profile
     *                          with the most recent commit.
     * @return                  A String containing the updated README.md markdown that now
     *                          points the last commit tracker to the correct directory.
     */
    private static String createUpdatedReadmeMarkdown(String readmeString, String repositoryName) {
        return readmeString.replaceAll(Constants.README_LAST_COMMIT_REGEX, "$1" + repositoryName + "$3");
    }

    /**
     * Updates the README.md last commit tracker to point to the directory with the
     * most recent commit
     *
     * @param github    A GitHub object that lets us interact with the GitHub API Java wrapper.
     * @param event     A GHEventInfo containing all info pertaining to a GHEvent
     * @throws IOException  Can throw an IOException due to .read(), .getRepository(), and
     *                      .getFileContents() function calls.
     */
    private static void updateReadme(GitHub github, GHEventInfo event) throws IOException {
        // Grab README.md content so we edit it.
        GHRepository repository = github.getRepository(Constants.GITHUB_README_REPOSITORY_NAME);
        GHContent content = repository.getFileContent(Constants.README_FILENAME);

        // Convert README.md content into a String so we can use RegEx to match the last commit
        // element and replace it with the most recently updated repository.
        String readmeMarkdown = IOUtils.toString(content.read(), StandardCharsets.UTF_8);
        String updatedReadmeMarkdown = createUpdatedReadmeMarkdown(readmeMarkdown, event.getRepository().getName());

        // This pushes a commit of the README.md changes we just made.
        content.update(updatedReadmeMarkdown, Constants.COMMIT_MESSAGE);
    }

    public static void main(String[] args) throws IOException {
        GitHub github = GitHubBuilder.fromPropertyFile().build();
        PagedIterable<GHEventInfo> events = getUserEvents(github);

        for (GHEventInfo event : events) {
            if(checkActionIsValidPush(event)) {
                updateReadme(github, event);

                // We only want to update the last commit repository based on the latest GitHub push,
                // so we break after the first hit
                break;
            }
        }

    }
}
