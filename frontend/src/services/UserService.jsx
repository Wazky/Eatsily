import $, { url } from "./api/ApiConfig";
import { ENDPOINTS } from "./api/endpoints";
import tokenManager from "./auth/tokenManager";

class UserService {

    /**
     * Get user profile
     */
    async getProfile() {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.USERS.PROFILE),
                method: 'GET'                
            });

            console.log('User profile response:', response);
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
}

export default new UserService();