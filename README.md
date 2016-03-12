# RecipeSuggesterTwitterBot

Recipe suggester is a pretty simple twitter bot that allows people to tweet the bot ingredients and then get a reply with a recipe that they can make using all of those ingredients.

You can view the bot in action [here on Twitter](https://www.twitter.com/recipesuggester)

Long story short, the bot searches for new tweets in the format "@RecipeSuggester suggest ingredient1, ingredient2" every 10 minutes. It then loops through each tweet and fires off an API call to get a recipe to tweet back to the user.

At the moment if anything goes wrong (doesn't find a recipe or user doesn't include the suggest keyword for example) it doesn't tweet back the user but on the Twitter profile it's explained what needs to be done in order to get a reply.
