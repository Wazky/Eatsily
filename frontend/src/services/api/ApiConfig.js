import $ from 'jquery';

const BACKEND_URL = 'Eatsily/rest';

$.ajaxSetup({
    contentType: 'application/json',
    dataType: 'json',
    cache: false,

});

// Utility function to construct full API URLs
export const url = (endpoint) => `${BACKEND_URL}${endpoint}`;

export default $;