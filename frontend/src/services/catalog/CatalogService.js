import $, { url } from '../api/ApiConfig';
import { ENDPOINTS } from '../api/endpoints';

class CatalogService {

    async listUnits(type = null) {
        try {
            const params = {};
            
            if (type) {
                params.type = type;
            }

            const response = await $.ajax({
                url: url(ENDPOINTS.CATALOG.UNITS),
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
            }
        }
    }


    async searchIngredients(search = null) {
        try {
            const params = {};

            if (search) {
                params.search = search;
            }

            const response = await $.ajax({
                url: url(ENDPOINTS.CATALOG.INGREDIENTS),
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

    async listCategories() {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.CATALOG.CATEGORIES),
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

}

export default new CatalogService();