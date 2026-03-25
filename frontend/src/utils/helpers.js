export const validationMessage = (t, error) => {
    if (!error) return null;

    const translatedParams = error.params
        ? (Object.fromEntries(
            Object.entries(error.params).map(([key, value]) => [
                key,
                t(`common:${value}`) || value
            ])
        ))
        : undefined;

    return t(`common:${error.code}`, translatedParams);
};


export const upperFirst = (str) => {
    if  (!str || typeof str !== "string") return '';

    return str.charAt(0).toUpperCase() + str.slice(1);
}

export const formatDate = (dateObj) => {
    if (!dateObj) return 'No disponible';
    
    const { dayOfMonth, monthValue, year } = dateObj;
    return `${dayOfMonth.toString().padStart(2, '0')}/${monthValue.toString().padStart(2, '0')}/${year}`;
};