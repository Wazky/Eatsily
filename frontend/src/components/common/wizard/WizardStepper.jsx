import { useTranslation } from "react-i18next";

export default function WizardStepper({
    currentStep,
    totalSteps,
    stepLabels
}) {

    return (
        <div className="wizard-stepper">
            {stepLabels.map((label, index) => { 
                return <WizardStep 
                    key={index} 
                    stepNumber={index + 1} 
                    currentStep={currentStep} 
                    stepLabel={label} 
                />;
            })}
        </div>
    );
}

export function WizardStep({ stepNumber, currentStep, stepLabel }) {
    const { t } = useTranslation("recipes");
    const isDone   = stepNumber < currentStep;
    const isActive = stepNumber === currentStep;
    
    return (
    <div 
        key={stepNumber} 
        className={`wizard-step 
            ${isActive ? 'wizard-step--active' : ''} 
            ${isDone ? 'wizard-step--done' : ''}`
        }
    > 
        <div className="wizard-step__circle">
            {isDone 
                ? <i className="bi bi-check-lg"></i>
                : stepNumber
            } 
        </div>
        
        <span className="wizard-step__label">{t(stepLabel)}</span>

    </div>
    );
}