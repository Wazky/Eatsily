import $, { url } from '../api/ApiConfig';
import { ENDPOINTS } from '../api/endpoints';

class RecipeService {

    async getRecipe(recipeId, locale = null) {
        try {
            const params = {};
            if (locale) params.locale = locale;
            
            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.BY_ID(recipeId)),
                method: 'GET',
                data: params                
            });

            console.log('Fetched recipe data:', response);
            return {
                success: true,
                data: response
            };
        

        } catch (error) {
            console.error('FAXXXXXOOOOOOO', error);
            return {
                success: false,
                error: error.responseJSON
            };
        }
    }

    async listRecipes(locale = null) {
        try {
            const params = {};
            if (locale) params.locale = locale;

            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.BASE),
                method: 'GET',
                data: params
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

    async listMyRecipes(locale = null) {
        try {
            const params = {};
            if (locale) params.locale = locale;

            const response = await $.ajax({
                url: url(ENDPOINTS.RECIPES.MY_RECIPES),
                method: 'GET',
                data: params
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
                method: 'PUT',
                data: JSON.stringify({ public: newVisibility }),
                dataType: 'text' ,
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