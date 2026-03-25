
export const ROUTES = {

// -UNPROTECTED ROUTES

    // Auth routes
    LOGIN: "/login",
    REGISTER: "/register",

    // Error routes
    ERROR: "*",

// -PROTECTED ROUTES
    
    // Dashboard route 
    HOME: "/",
    PROFILE: "/profile",  
    SETTINGS: "/settings",      

    // Recipes routes
    RECIPES: "/recipes",
    MY_RECIPES: "/my-recipes",
    RECIPE_DETAIL: "/recipes/:id",
    RECIPE_CREATE: "/recipes/new",
    RECIPE_EDIT: "/recipes/:id/edit",
    
}