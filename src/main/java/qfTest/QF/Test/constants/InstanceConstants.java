package qfTest.QF.Test.constants;

import io.github.cdimascio.dotenv.Dotenv;

public class InstanceConstants {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static final String sourceInstance = dotenv.get("JIRA_SOURCE_INSTANCE");
    public static final String destInstance = dotenv.get("JIRA_DEST_INSTANCE");
    public static final String email = dotenv.get("JIRA_EMAIL");
    public static final String token = dotenv.get("JIRA_API_TOKEN");

    public static final String sourceProkectKey = dotenv.get("JIRA_SOURCE_PROJECT_KEY");
    public static final String targetProjectKey = dotenv.get("JIRA_TARGET_PROJECT_KEY");

    public static final String attachmentPath = "/Users/rohankumar/Desktop/attachements/";   // '/Users/rohankumar/Desktop/attachements '

}
