
export const validationMessage = (t, error) => {
    if (!error) return null;

    return t(`common:${error.code}`, error.params);
};

