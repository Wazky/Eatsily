/* ==========================================================================
 * RECIPE CARD COMPONENTS
 * ========================================================================== */

import { useTranslation } from "react-i18next";

// ─── Image Section ────────────────────────────────────────────────────────

export function PlaceholderIcon() {
    return <i className="bi bi-image recipe-card__image-placeholder" />;
}

const DIFFICULTY_CLASSES = {
    EASY:   'badge--difficulty-easy',
    MEDIUM: 'badge--difficulty-medium',
    HARD:   'badge--difficulty-hard',
};

const DIFFICULTY_LABELS = {
    EASY:   'recipeLabels.difficulty.easy',
    MEDIUM: 'recipeLabels.difficulty.medium',
    HARD:   'recipeLabels.difficulty.hard',
};

export function DifficultyBadge({ difficulty }) {
    const { t } = useTranslation("recipes");
    const cls   = DIFFICULTY_CLASSES[difficulty] ?? 'badge--difficulty-easy';
    const label = DIFFICULTY_LABELS[difficulty]  ?? difficulty;

    return (
        <span className={`badge ${cls}`} >
            {t(label)}
        </span>
    );
}

// ─── Body Section ────────────────────────────────────────────────────────

export function ClockIcon() {
    return <i className="bi bi-clock meta-icon" />;
}

export function ServingsIcon() {
    return <i className="bi bi-people meta-icon" />;
}

// ─── Footer Section ────────────────────────────────────────────────────────  

export function LockIcon() {
    return <i className="bi bi-lock meta-icon" />;
}

export function UnlockIcon() {
    return <i className="bi bi-unlock meta-icon" />;
}


