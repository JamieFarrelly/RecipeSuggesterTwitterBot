
public class Recipe {

	private String recipeUrl;
	private String recipeName;

	public Recipe(String recipeUrl, String recipeName) {
		super();
		this.recipeUrl = recipeUrl;
		this.recipeName = recipeName;
	}

	public String getRecipeUrl() {
		return recipeUrl;
	}

	public void setRecipeUrl(String recipeUrl) {
		this.recipeUrl = recipeUrl;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

}
