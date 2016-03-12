import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class RecipeSuggester {

    private static final long TEN_MINUTES_IN_MILLIS = 600000;
    private static final int TWEET_MAX_CHARACTERS = 140;

    /*
     *  hardcoded since the bot was rewritten from scratch, this was the last
     *  tweet replied to years ago
     */
    private static long LAST_TWEET_ID_REPLIED_TO = 496774295121440769l;
    // Only getting tweets sent in the format @RecipeSuggester suggest
    private static final String TWITTER_ACCOUNT_NAME = "RecipeSuggester";
    private static final String KEYWORD_SCANNING_FOR = "suggest";

    private static final String FOOD_TO_FORK_API_KEY = "CHANGE_ME";
    private static final String FOOD_TO_FORK_URL = "http://food2fork.com/api/search?";

    public static void main(String... args) {
        suggestRecipe();
    }

    private static void suggestRecipe() {
        // access the twitter API using the twitter4j.properties file
        Twitter twitter = TwitterFactory.getSingleton();

        // keep the bot running all the time
        while (true) {
            // create a new search
            Query query = new Query("to:" + TWITTER_ACCOUNT_NAME + " " + KEYWORD_SCANNING_FOR);
            query.setSinceId(LAST_TWEET_ID_REPLIED_TO);

            try {
                // get the results from that search
                QueryResult queryResult = twitter.search(query);

                // loop through the new tweets that haven't been replied to yet
                for (Status tweet : queryResult.getTweets()) {

                    /*
                     * do this before we tweet, in case something goes wrong we
                     * don't want to try it again
                     */
                    LAST_TWEET_ID_REPLIED_TO = tweet.getId();

                    Recipe recipe = getRecipeSuggested(tweet.getText());

                    String tweetToBeSent = "@" + tweet.getUser().getScreenName() + " Your recipe is "
                            + recipe.getRecipeName() + " " + recipe.getRecipeUrl();
                    /*
                     * In reality this shouldn't really happen often, but if it
                     * does we'll show the url before the recipe name, and then
                     * make it shorter enough for a tweet
                     */
                    if (tweetToBeSent.length() > TWEET_MAX_CHARACTERS) {
                        tweetToBeSent = "@" + tweet.getUser().getScreenName() + " " + recipe.getRecipeUrl()
                                + " Your recipe is " + recipe.getRecipeName();

                        tweetToBeSent = tweetToBeSent.substring(0, TWEET_MAX_CHARACTERS);
                    }

                    StatusUpdate statusUpdate = new StatusUpdate(tweetToBeSent);
                    statusUpdate.inReplyToStatusId(tweet.getId());
                    twitter.updateStatus(statusUpdate);
                }
            } catch (TwitterException te) {
                te.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(TEN_MINUTES_IN_MILLIS);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    private static Recipe getRecipeSuggested(String tweetText) throws Exception {

        // get the tweet text without account name or keyword in it
        tweetText = tweetText.replace("@" + TWITTER_ACCOUNT_NAME, "");
        tweetText = tweetText.replace(KEYWORD_SCANNING_FOR, "");
        tweetText = tweetText.replace(",", " ");
        String ingredients = tweetText.replace(" ", "%20");

        // http://food2fork.com/api/search?key=YourApiKey&q=ham%20chicken
        String endpoint = FOOD_TO_FORK_URL + "key=" + FOOD_TO_FORK_API_KEY + "&q=" + ingredients;

        String json = readUrl(endpoint);

        // if there's multiple recipes, just use the first
        JSONObject jsonReturned = new JSONObject(json);
        String recipeUrl = jsonReturned.getJSONArray("recipes").getJSONObject(0).getString("f2f_url");
        String recipeName = jsonReturned.getJSONArray("recipes").getJSONObject(0).getString("title");

        return new Recipe(recipeUrl, recipeName);
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}
