
export const validationMessage = (t, error) => {
    if (!error) return null;

    return t(`common:${error.code}`, error.params);
};


export const upperFirst = (str) => {
    if  (!str || typeof str !== "string") return '';

    return str.charAt(0).toUpperCase() + str.slice(1);
}