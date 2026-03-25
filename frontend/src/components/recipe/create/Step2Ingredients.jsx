import { useTranslation } from "react-i18next";
import { validationMessage } from "../../../utils/helpers";
import useMeasurementUnits from "../../../hooks/catalog/useMeasurementUnits";
import IngredientAutocomplete from "./IngredientAutocomplete";

export default function Step2Ingredients({
    formData,
    errors,
    ingredientFieldErrors,
    addIngredient,
    updateIngredient,
    removeIngredient
}) {
    const { t } = useTranslation("recipes");
    const { units, loading: unitsLoading } = useMeasurementUnits();

    return (
    <div className="wizard-step-content">

        {/* Ingredients Error (if any) */}
        {errors.ingredients && (
            <div className="aa-alert aa-alert--error mb-3">
                <i className="bi bi-exclamation-circle"></i>
                {validationMessage(t, errors.ingredients)}
            </div>
        )}

        {/* Column Headers */}
        {(formData.ingredients.length > 0) && (
            <div className="ingredient-row ingredient-row-header">
                <span className="aa-label">
                    {t("ingredientLabels.name")}
                    <span className="aa-required">*</span>    
                </span>
                <span className="aa-label">
                    {t("ingredientLabels.quantity")}
                    <span className="aa-required">*</span>    
                </span>
                <span className="aa-label">
                    {t("ingredientLabels.unit")}
                    <span className="aa-required">*</span>    
                </span>
                <span className="aa-label">
                    {t("ingredientLabels.note")}
                </span>                
            </div>
        )}

        {/* Ingredient Rows */}
        {formData.ingredients.map((ingredient, index) => {
            return <IngredientRow 
                key={index}
                index={index}
                ingredient={ingredient}
                units={units}
                unitsLoading={unitsLoading}
                ingredientFieldErrors={ingredientFieldErrors}
                updateIngredient={updateIngredient}
                removeIngredient={removeIngredient}                
            />;
        })}

        <button
            type="button"
            className="wizard-add-btn"
            onClick={addIngredient}
        >
            <i className="bi bi-plus-lg"></i>
            {t('recipeCreate.wizard.actions.addIngredient')}
        </button>

    </div>
    );
}

export function IngredientRow({
    index,
    ingredient,
    units,
    unitsLoading,
    ingredientFieldErrors,
    updateIngredient,
    removeIngredient
}) {
    const { t } = useTranslation("recipes");
    const fieldError = ingredientFieldErrors?.[index];

    const TYPE_LABELS = { WEIGHT: 'Weight', VOLUME: 'Volume', UNIT: 'Unit', OTHER: 'Other' };
    const unitsByType = Object.groupBy(units, unit => unit.type);
    const selectedMeasurementUnit = ingredient.unitId ? units.find(u => u.id === ingredient.unitId) : null;
    const isUnitType = selectedMeasurementUnit?.type === 'UNIT';

    return (
    <div className="ingredient-row">

        {/* Ingredient Name - autocomplete */}
        <IngredientAutocomplete 
            value={ingredient.ingredientName}
            isInvalid={!!fieldError?.ingredientId}
            onChange={(val) => updateIngredient(index, "ingredientName", val)}
            onSelect={({ ingredientId, ingredientName}) => {
                updateIngredient(index, 'ingredientId', ingredientId);
                updateIngredient(index, 'ingredientName', ingredientName);
            }}
        />

        {/* Quantity */}
        <input 
            type="number"
            className={`form-control aa-input 
                ${fieldError?.quantity ? 'is-invalid' : ''}`
            }
            placeholder="0"
            min={isUnitType ? 1 : 0.01}
            step={isUnitType ? 1 : 0.01}
            value={ingredient.quantity}
            onChange={e => updateIngredient(index, "quantity", e.target.value)}
        />

        {/* Unit */}
        <select
            className={`form-select aa-input 
                ${(fieldError?.unitId) ? "is-invalid" : ''}`
            }
            value={ingredient.unitId ?? ''}
            onChange={e => updateIngredient(index, 'unitId', Number(e.target.value))}
            disabled={unitsLoading}
        >
            <option value="">
                {unitsLoading ? '...' : t('recipeCreate.wizard.form.placeholders.unit')}
            </option>
            {Object.entries(unitsByType).map(([type, typeUnits]) => {
                return (
                    <optgroup key={type} label={TYPE_LABELS[type] ? TYPE_LABELS[type] : type}>
                        {typeUnits.map(unit => (
                            <option key={unit.id} value={unit.id}>
                                {unit.name} ({unit.abbreviation})
                            </option>
                        ))}
                    </optgroup>
                );
            })}
        </select>

        {/* Notes */}
        <input 
            type="text"
            className="form-control aa-input"
            placeholder={t('recipeCreate.wizard.form.placeholders.note')}
            value={ingredient.notes}
            onChange={e => updateIngredient(index, "notes", e.target.value)}
        />

        {/* Remove Button */}
        <button
            type="button"
            className="aa-btn-icon aa-btn-icon--danger"
            onClick={() => removeIngredient(index)}
            title={t('recipeCreate.wizard.actions.remove')}
        >
            <i className="bi bi-trash"></i>
        </button>

    </div>
    );

}

