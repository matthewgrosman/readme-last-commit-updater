import org.apache.commons.io.IOUtils;
import org.kohsuke.github.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class LastCommitUpdater {
    private static PagedIterable<GHEventInfo> getUserEvents(GitHub github) throws IOException {
        return github.getMyself().listEvents();
    }

    private static GHCommit getLatestCommit(GHRepository repository) {
        PagedIterable<GHCommit> commits = repository.listCommits();
        return commits.iterator().next();
    }

    private static boolean checkCommitIsManualPush(GHEventInfo event) throws IOException {
        String actionType = event.getType().name();
        String actionUser = event.getActorLogin();
        String latestCommitMessage = getLatestCommit(event.getRepository()).getCommitShortInfo().getMessage();

        return actionType.equals(GHEvent.PUSH.name())
                && actionUser.equals(Constants.GITHUB_USERNAME)
                && !latestCommitMessage.equals(Constants.COMMIT_MESSAGE);
    }

    private static String getUpdatedReadme(String readmeString, String repositoryName) {
        return readmeString.replaceAll(Constants.README_LAST_COMMIT_REGEX, "$1" + repositoryName + "$3");
    }

    public static void main(String[] args) throws IOException {
        GitHub github = GitHubBuilder.fromPropertyFile().build();
        PagedIterable<GHEventInfo> events = getUserEvents(github);

        for (GHEventInfo event : events) {
            if(checkCommitIsManualPush(event)) {
                // Grab README.md content so we edit it.
                GHRepository repository = github.getRepository(Constants.GITHUB_README_REPOSITORY_NAME);
                GHContent content = repository.getFileContent(Constants.README_FILENAME);

                // Convert README.md content into a String so we can use RegEx to match the last commit
                // element and replace it with the most recently updated repository.
                String readmeMarkdown = IOUtils.toString(content.read(), StandardCharsets.UTF_8);
                String updatedReadmeMarkdown = getUpdatedReadme(readmeMarkdown, event.getRepository().getName());

                // This pushes a commit of the README.md changes we just made.
                content.update(updatedReadmeMarkdown, Constants.COMMIT_MESSAGE);

                // We only want to update the last commit repository based on the latest GitHub push.
                break;
            }
        }

    }
}
