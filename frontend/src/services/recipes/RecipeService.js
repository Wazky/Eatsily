import $, { url } from '../api/ApiConfig';
import { ENDPOINTS } from '../api/endpoints';

class RecipeService {

    async listRecipes() {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.BASE),
                method: 'GET'        
            });

            return {
                success: true,
                data: response
            };

        } catch (error) {
            return {
                success: false,
                error: error.responseJSON
            };
        }
    }

    async listMyRecipes() {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.MY_RECIPES),
                method: 'GET'
            });

            return {
                success: true,
                data: response
            };

        } catch (error) {
            return {
                success: false,
                error: error.responseJSON
            };
        }
    }

    async createRecipe(recipeData) {
        console.log('Creating recipe with data:', recipeData);
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.BASE),
                method: 'POST',
                data: JSON.stringify(recipeData)
            });

            return {
                success: true,
                data: response
            };
        
        } catch (error) {
            return {
                success: false,
                error: error.responseJSON
            };
        }
    }

    async updateVisibility(recipeId, newVisibility) {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.VISIBILITY(recipeId)),
                method: 'UPDATE',
                data: JSON.stringify({ public: newVisibility })
            });

            return {
                success: true,
                data: response
            };
        
        } catch (error) {
            return {
                success: false,
                error: error.responseJSON
            };
        }
    }

    async deleteRecipe(recipeId) {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.BY_ID(recipeId)),
                method: 'DELETE'
            });

            return {
                success: true,
                data: response
            };
        } catch (error) {
            return {
                success: false,
                error: error.responseJSON
            };
        }
    }

}

export default new RecipeService();