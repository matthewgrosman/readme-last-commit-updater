import org.kohsuke.github.*;

import java.io.IOException;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;


public class LastCommitUpdater {
    public static GitHub github;

    private static PagedIterable<GHEventInfo> getUserEvents(GitHub github) throws IOException {
        return github.getMyself().listEvents();
    }

    private static GHCommit getLatestCommit(GHRepository repository) {
        PagedIterable<GHCommit> commits = repository.listCommits();
        return commits.iterator().next();
    }

    private static boolean checkCommitIsValid(GHEventInfo event) throws IOException {
        String actionType = event.getType().name();
        String actionUser = event.getActorLogin();
        String latestCommitMessage = getLatestCommit(event.getRepository()).getCommitShortInfo().getMessage();

        System.out.println(latestCommitMessage);

        return actionType.equals(GHEvent.PUSH.name())
                && actionUser.equals(Constants.GITHUB_USERNAME)
                && !latestCommitMessage.equals(Constants.COMMIT_MESSAGE);
    }

    private static Date getCommitDate() {
        Date input = new Date();
        return Date.from(input.toInstant());
    }

    public static void main(String[] args) throws IOException {
        github = GitHubBuilder.fromPropertyFile().build();
        PagedIterable<GHEventInfo> events = getUserEvents(github);

        for (GHEventInfo event : events) {
            if(checkCommitIsValid(event)) {
                GHRepository repository = github.getRepository(Constants.GITHUB_README_REPOSITORY_NAME);
                String latestCommitSha = getLatestCommit(repository).getSHA1();

                //System.out.println(repository.getFileContent("README.md").getDownloadUrl());

                System.out.println("here\n");
                GHContent content = repository.getFileContent("test.md", latestCommitSha);
                // content.update(LocalDateTime.now().toString(), Constants.COMMIT_MESSAGE);

//                GHCommitBuilder commitBuilder = repository.createCommit();
//                GHCommit newCommit = commitBuilder
//                        .author(Constants.GITHUB_USERNAME, Credentials.GITHUB_EMAIL, getCommitDate())
//                        .committer(Constants.GITHUB_USERNAME, Credentials.GITHUB_EMAIL, getCommitDate())
//                        .message(Constants.COMMIT_MESSAGE)
//                        .parent(latestCommitSha)
//                        .create();

                // repository.createCommit();

                break;
            }
        }

    }
}
